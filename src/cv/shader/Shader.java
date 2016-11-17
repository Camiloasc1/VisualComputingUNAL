package cv.shader;

import processing.core.PApplet;
import processing.core.PVector;
import processing.opengl.PShader;

public class Shader extends PApplet {

    public static final float SPEED = 100f;
    private static int WIDTH = 600;
    private static int HEIGHT = 600;

    private PShader flatShader;
    private PShader phongShader;

    public void settings() {
        size(WIDTH, HEIGHT, P3D);
    }

    public static void main(String[] args) {
        PApplet.main(Shader.class.getName());
    }

    public void setup() {
        super.setup();
        loop();

        flatShader = loadShader("res/glsl/flat/frag.glsl", "res/glsl/flat/vert.glsl");
        phongShader = loadShader("res/glsl/phong/frag.glsl", "res/glsl/phong/vert.glsl");

    }

    public void draw() {
        background(0);
        noStroke();

        ambientLight(255, 255, 255);
        pointLight(255, 255, 255, width / 2, height / 2, 0);
        lightSpecular(255, 255, 255);
        pointLight(255, 255, 255, width / 2, 0, 200);

        int detail = mouseY / 10 + 3;
        float shininess = mouseX / 25 + 0.1f;
        surface.setTitle("Detail: " + detail + '\t' + "Shininess: " + shininess);

        sphereDetail(detail);

        shader(flatShader);
        flatShader.set("matAmbient", new PVector(1, 0, 0));
        flatShader.set("matDiffuse", new PVector(0, 1, 0));
        flatShader.set("matSpecular", new PVector(0, 0, 1));
        flatShader.set("matShininess", shininess);
        phongShader.set("cameraPosition", new PVector(WIDTH / 2, HEIGHT / 2, 465));

        pushMatrix();
        {
            translate(WIDTH * 1f / 4f, HEIGHT / 2);
            rotateY(frameCount / SPEED);
            sphere(100);
        }
        popMatrix();

        resetShader();


        shader(phongShader);
        phongShader.set("matAmbient", new PVector(1, 0, 0));
        phongShader.set("matDiffuse", new PVector(0, 1, 0));
        phongShader.set("matSpecular", new PVector(0, 0, 1));
        phongShader.set("matShininess", shininess);
        phongShader.set("cameraPosition", new PVector(WIDTH / 2, HEIGHT / 2, 465));

        pushMatrix();
        {
            translate(WIDTH * 3f / 4f, HEIGHT / 2);
            rotateY(frameCount / SPEED);
            sphere(100);
        }
        popMatrix();

        resetShader();
    }
}