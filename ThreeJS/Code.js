/* global THREE*/
var camera, scene, renderer;
var geometry, material, mesh;
var controls, light, box;
var mirrorCubeCamera, mirrorCube;
var mirrorSphereCamera, mirrorSphere;
var refractSphereCamera, refractSphere;
var bubbleCamera;

init();
animate();

function init() {

    camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 1, 10000);
    camera.position.z = 1000;

    scene = new THREE.Scene();

    geometry = new THREE.BoxGeometry(200, 200, 200);
    material = new THREE.MeshBasicMaterial({
        color: 0xff0000,
        wireframe: true
    });

    mesh = new THREE.Mesh(geometry, material);
    scene.add(mesh);

    renderer = new THREE.WebGLRenderer();
    renderer.setSize(window.innerWidth, window.innerHeight);

    document.body.appendChild(renderer.domElement);

    window.addEventListener("resize", function() {
        camera.aspect = window.innerWidth / window.innerHeight;
        camera.updateProjectionMatrix();
        renderer.setSize(window.innerWidth, window.innerHeight);
    }, false);

    controls = new THREE.OrbitControls(camera, renderer.domElement);

    scene.add(new THREE.AxisHelper(500));
    var images = ["posx.jpg", "negx.jpg",
        "posy.jpg", "negy.jpg",
        "posz.jpg", "negz.jpg"
    ]
    var loader = new THREE.CubeTextureLoader();
    loader.path = "https://raw.githubusercontent.com/mrdoob/three.js/dev/examples/textures/cube/Park2/";
    loader.crossOrigin = "";
    var skyTexture = loader.load(images);

    var shader = THREE.ShaderLib["cube"];
    shader.uniforms["tCube"].value = skyTexture;
    var skyMaterial = new THREE.ShaderMaterial({
        fragmentShader: shader.fragmentShader,
        vertexShader: shader.vertexShader,
        uniforms: shader.uniforms,
        depthWrite: false,
        side: THREE.BackSide
    });

    var skybox = new THREE.Mesh(
        new THREE.BoxGeometry(5000, 5000, 5000),
        skyMaterial
    );
    scene.add(skybox);

    box = new THREE.Mesh(
        new THREE.CubeGeometry(100, 100, 100),
        new THREE.MeshLambertMaterial({
            color: 0x0000ff
        })
    );
    scene.add(box);

    var sphere = new THREE.Mesh(
        new THREE.SphereGeometry(50),
        new THREE.MeshLambertMaterial({
            color: 0xff0000,
            transparent: true,
            opacity: 0.5
        })
    );
    sphere.position.set(100, 100, 100);
    scene.add(sphere);

    light = new THREE.PointLight(0xffffff);
    light.position.set(250, 250, 250);
    scene.add(light);

    mirrorCubeCamera = new THREE.CubeCamera(0.1, 5000, 512);
    scene.add(mirrorCubeCamera);
    mirrorCube = new THREE.Mesh(
        new THREE.CubeGeometry(300, 300, 300),
        new THREE.MeshBasicMaterial({
            envMap: mirrorCubeCamera.renderTarget
        })
    );
    mirrorCube.position.set(-500, 0, 0);
    mirrorCubeCamera.position = mirrorCube.position;
    scene.add(mirrorCube);

    mirrorSphereCamera = new THREE.CubeCamera(0.1, 5000, 512);
    scene.add(mirrorSphereCamera);
    mirrorSphere = new THREE.Mesh(
        new THREE.SphereGeometry(200, 64, 64),
        new THREE.MeshBasicMaterial({
            envMap: mirrorSphereCamera.renderTarget
        })
    );
    mirrorSphere.position.set(500, 0, 0);
    mirrorSphereCamera.position = mirrorSphere.position;
    scene.add(mirrorSphere);

    refractSphereCamera = new THREE.CubeCamera(0.1, 5000, 512);
    refractSphereCamera.renderTarget.mapping = THREE.CubeRefractionMapping;
    scene.add(refractSphereCamera);
    refractSphere = new THREE.Mesh(
        new THREE.SphereGeometry(200, 64, 64),
        new THREE.MeshBasicMaterial({
            envMap: refractSphereCamera.renderTarget,
            color: 0xffffff,
            refractionRatio: 0.99,
            reflectivity: 0.9
        })
    );
    refractSphere.position.set(0, 500, 0);
    refractSphereCamera.position = refractSphere.position;
    scene.add(refractSphere);

    bubbleCamera = new THREE.CubeCamera(0.1, 5000, 512);
    scene.add(bubbleCamera);

    var fShader = THREE.FresnelShader;
    console.log(fShader);
    var uniforms = THREE.UniformsUtils.clone(fShader.uniforms);
    uniforms['tCube'].value = bubbleCamera.renderTarget;

    bubble = new THREE.Mesh(
        new THREE.SphereGeometry(100, 64, 64),
        new THREE.ShaderMaterial({
            fragmentShader: fShader.fragmentShader,
            vertexShader: fShader.vertexShader,
            uniforms: uniforms
        })
    );
    bubble.position.set(200, 200, 200);
    bubbleCamera.position = bubble.position;
    scene.add(bubble);
}

function animate() {
    requestAnimationFrame(animate);
    controls.update();

    // mesh.rotation.x += 0.01;
    // mesh.rotation.y += 0.02;

    box.rotation.x += 0.01;
    box.rotation.y += 0.02;
    box.rotation.z += 0.03;

    mirrorCube.visible = false;
    mirrorCubeCamera.updateCubeMap(renderer, scene);
    mirrorCube.visible = true;

    mirrorSphere.visible = false;
    mirrorSphereCamera.updateCubeMap(renderer, scene);
    mirrorSphere.visible = true;

    refractSphere.visible = false;
    refractSphereCamera.updateCubeMap(renderer, scene);
    refractSphere.visible = true;

    renderer.render(scene, camera);
}
