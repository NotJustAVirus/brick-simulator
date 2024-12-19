import * as THREE from 'three';

import { GLTFLoader } from 'three/addons/loaders/GLTFLoader.js';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';

let camera, scene, renderer;

init();

function init() {
    scene = new THREE.Scene();
    scene.background = new THREE.Color( 0x222222 );

    scene.add( new THREE.AmbientLight( 0xffffff, 2.7 ) );

    camera = new THREE.PerspectiveCamera( 35, window.innerWidth / window.innerHeight, 1, 500 );

    // Z is up for objects intended to be 3D printed.

    camera.up.set( 0, 0, 1 );
    camera.position.set( 0, - 9, 6 );

    camera.add( new THREE.PointLight( 0xffffff, 50 ) );

    scene.add( camera );

    const grid = new THREE.GridHelper( 50, 50, 0xffffff, 0x555555 );
    grid.rotateOnAxis( new THREE.Vector3( 1, 0, 0 ), 90 * ( Math.PI / 180 ) );
    scene.add( grid );

    renderer = new THREE.WebGLRenderer( { antialias: true } );
    renderer.setPixelRatio( window.devicePixelRatio );
    renderer.setSize( window.innerWidth, window.innerHeight );
    document.body.appendChild( renderer.domElement );

    const loader = new GLTFLoader();
    loader.load( 'model/red_brick_low.glb', function (gltf) {
        let brick = gltf.scene.children[0];
        // brick.position.set(0.8, -0.1, 0);
        brick.rotation.x = Math.PI;
        brick.scale.set(20, 20, 20);
        scene.add(brick);
        render();
    }, undefined, function (error) {
        console.error(error);
    });

    const controls = new OrbitControls( camera, renderer.domElement );
    controls.addEventListener( 'change', render );
    controls.target.set( 0, 0, 0 );
    // controls.enableZoom = false;
    controls.maxDistance = 100;
    controls.minDistance = 5;
    controls.update();

    window.addEventListener( 'resize', onWindowResize );

}

function onWindowResize() {
    camera.aspect = window.innerWidth / window.innerHeight;
    camera.updateProjectionMatrix();

    renderer.setSize( window.innerWidth, window.innerHeight );

    render();
}

function render() {
    renderer.render( scene, camera );
}

let websocket = new WebSocket("ws://localhost:8080/ws");

websocket.onopen = function (event) {
    console.log("Connection established");
};

websocket.onmessage = function (event) {
    document.getElementById("timer").innerHTML = event.data;
};

websocket.onclose = function (event) {
    console.log("Connection closed");
};

setInterval(function () {
    websocket.send("Hello from client");
}, 5000);