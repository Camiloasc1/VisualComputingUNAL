package cv.shader;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.opengl.PShader;

public class Shader1 extends PApplet {

	private static int WIDTH = 600;
	private static int HEIGHT = 600;

	private PShader shader;

	public void settings() {
		size(WIDTH, HEIGHT, P2D);
	}

	public static void main(String[] args) {
		PApplet.main(Shader1.class.getName());
	}

	public void setup() {
		super.setup();
		loop();

		shader = loadShader("res/glsl/frag.glsl", "res/glsl/vert.glsl");
	}

	public void draw() {
		background(0);
		
		shader(shader);
		shader.set("c", 1f);

		beginShape(QUAD);
		fill(0, 0, 255);
		vertex(0, 0);
		vertex(WIDTH, 0);

		fill(255, 0, 0);
		vertex(WIDTH, HEIGHT);
		fill(0, 255, 0);
		vertex(0, HEIGHT);
		endShape();
		
		resetShader();

	}
}