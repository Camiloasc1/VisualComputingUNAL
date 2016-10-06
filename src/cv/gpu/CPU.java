package cv.gpu;

import processing.core.PApplet;
import remixlab.proscene.Scene;

public class CPU extends PApplet {
	private static final int WIDTH = 800;
	private static final int HEIGHT = 800;

	private static final int SIZE = 1000;
	private int drawn;
	private Scene scene;
	private float angle[];
	private float speed[];

	public void settings() {
		size(WIDTH, HEIGHT, P3D);
	}

	public static void main(String[] args) {
		PApplet.main(CPU.class.getName());
	}

	public void setup() {
		super.setup();
		loop();

		scene = new Scene(this);
		scene.setAxesVisualHint(false);
		scene.setGridVisualHint(false);
		// scene.setBoundingBox(new Vec(0, 0, 0), new Vec(fWidth, fHeight,
		// fDepth));
		// scene.showAll();

		angle = new float[SIZE];
		speed = new float[SIZE];
		for (int i = 0; i < SIZE; i++) {
			speed[i] = random(0.05f);
		}
	}

	public void draw() {
		background(50);
		noStroke();

		sphere(10);
		for (int i = 0; i < SIZE; i++) {
			angle[i] += speed[i];
		}
		for (int i = 0; i < drawn; i++) {
			pushMatrix();
			{
				rotate(angle[i]);
				translate(20 * (i + 1), 0);
				sphere(10);
			}
			popMatrix();
		}

		surface.setTitle("Drawn: " + drawn + " - " + "FPS: " + frameRate);

		if (keyPressed)
			switch (key) {
			case '+':
				drawn += 25;
				break;
			case '-':
				drawn -= 25;
				break;

			default:
				break;
			}
		if (drawn > SIZE)
			drawn = SIZE;
		if (drawn < 0)
			drawn = 0;
	}

	// @Override
	// public void mouseWheel(MouseEvent event) {
	// drawn += -event.getCount();
	// if (drawn > SIZE)
	// drawn = SIZE;
	// if (drawn < 0)
	// drawn = 0;
	// }
}
