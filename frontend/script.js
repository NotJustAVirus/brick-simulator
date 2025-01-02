import * as THREE from 'three';

import { GLTFLoader } from 'three/addons/loaders/GLTFLoader.js';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';

let camera, scene, renderer;

init();

function init() {
    scene = new THREE.Scene();
    scene.background = new THREE.Color(0x222222);

    scene.add(new THREE.AmbientLight(0xffffff, 2.7));

    camera = new THREE.PerspectiveCamera(35, window.innerWidth / window.innerHeight, 1, 500);

    camera.up.set(0, 0, 1);
    camera.position.set(0, -18, 12);

    camera.add(new THREE.PointLight(0xffffff, 50));

    scene.add(camera);

    // const grid = new THREE.GridHelper(50, 50, 0xffffff, 0x555555);
    // grid.rotateOnAxis(new THREE.Vector3(1, 0, 0), 90 * (Math.PI / 180));
    // scene.add(grid);

    renderer = new THREE.WebGLRenderer({ antialias: true });
    renderer.setPixelRatio(window.devicePixelRatio);
    renderer.setSize(window.innerWidth, window.innerHeight);
    document.body.appendChild(renderer.domElement);

    const loader = new GLTFLoader();
    loader.load('model/red_brick_low.glb', function (gltf) {
        let brick = gltf.scene.children[0];
        // brick.position.set(0.8, -0.1, 0);
        brick.rotation.x = Math.PI;
        brick.scale.set(20, 20, 20);
        scene.add(brick);
        render();
    }, undefined, function (error) {
        console.error(error);
    });

    const controls = new OrbitControls(camera, renderer.domElement);
    controls.addEventListener('change', render);
    controls.target.set(0, 0, 0);
    // controls.enableZoom = false;
    controls.maxDistance = 100;
    controls.minDistance = 5;
    controls.update();
    controls.autoRotate = true;

    setInterval(() => {
        controls.update();
    }, 1000 / 30);
    window.addEventListener('resize', onWindowResize);

}

function onWindowResize() {
    camera.aspect = window.innerWidth / window.innerHeight;
    camera.updateProjectionMatrix();

    renderer.setSize(window.innerWidth, window.innerHeight);

    render();
}

function render() {
    renderer.render(scene, camera);
}

class Timer {
    time = 0;
    speed = 1;
    interval = 200;
    started = false;

    updatedTime = null;
    updatedTimeTime = 0;

    constructor() { }

    start(time) {
        this.time = time;
        this.timer = setInterval(() => {
            if (this.updatedTimeTime !== 0) {
                this.time = this.updatedTime + (Date.now() - this.updatedTimeTime);
                this.updatedTime = null;
                this.updatedTimeTime = 0;
            } else {
                this.time += this.interval * this.speed;
            }
            if (this.callback) {
                this.callback(this.time);
            }
        }, this.interval);
        this.started = true;
    }

    updateTime(time) {
        this.updatedTime = time;
        this.updatedTimeTime = Date.now();
    }
}

let timer = new Timer();
let globalTimer = new Timer();
timer.callback = function (time) {
    document.getElementById("timer").innerHTML = timeToString(time);
}

globalTimer.callback = function (time) {
    document.getElementById("global-timer").innerHTML = timeToString(time);
}

let websocket = new WebSocket("/ws2");

websocket.onopen = function (event) {
    console.log("Connection established");
};

websocket.onmessage = function (event) {
    let data = JSON.parse(event.data);
    if (!timer.started) {
        timer.start(parseInt(data.time));
        globalTimer.start(parseInt(data.totalTime));
    } else {
        timer.updateTime(parseInt(data.time));
        globalTimer.updateTime(parseInt(data.totalTime));
    }
};

websocket.onclose = function (event) {
    console.log("Connection closed");
};

setInterval(function () {
    websocket.send("Hello from client");
}, 5000);

function timeToString(time) {
    let milliseconds = time;
    let seconds = Math.floor(milliseconds / 1000);
    let minutes = Math.floor(seconds / 60);
    let hours = Math.floor(minutes / 60);
    let days = Math.floor(hours / 24);
    let years = Math.floor(days / 365);
    // milliseconds = milliseconds % 1000;
    seconds = seconds % 60;
    minutes = minutes % 60;
    hours = hours % 24;
    days = days % 365;
    // milliseconds = milliseconds.toString().padStart(3, "0");
    // seconds = seconds.toString().padStart(2, "0");
    // minutes = minutes.toString().padStart(2, "0");
    // hours = hours.toString().padStart(2, "0");

    let out = [];

    let str = "";
    if (years > 0) {
        str = "year";
    } else if (days > 0) {
        str = "day";
    } else if (hours > 0) {
        str = "hour";
    } else if (minutes > 0) {
        str = "minute";
    } else {
        str = "second";
    }

    switch (str) {
        case "year":
            out.push(formatUnit(years, "year"));
        case "day":
            out.push(formatUnit(days, "day"));
        case "hour":
            out.push(formatUnit(hours, "hour"));
        case "minute":
            out.push(formatUnit(minutes, "minute"));
        default:
            out.push(formatUnit(seconds, "second"));
            break;
    }
    str = "";
    for (let i = 0; i < out.length; i++) {
        str += out[i];
        if (i < out.length - 2) {
            str += ", ";
        } else if (i < out.length - 1) {
            str += " and ";
        }
    }

    return str;
}

function formatUnit(value, unit) {
    if (value !== 1) {
        unit += "s";
    }
    return value + " " + unit;
}