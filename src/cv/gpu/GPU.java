package cv.gpu;

import processing.core.PApplet;
import processing.core.PShape;
import remixlab.proscene.Scene;

public class GPU extends PApplet {
	private static final int WIDTH = 800;
	private static final int HEIGHT = 800;

	private static final int SIZE = 5000;
	private int drawn;
	private Scene scene;
	private float angle[];
	private float speed[];
	private float rad[];
	private PShape sphere;

	public void settings() {
		size(WIDTH, HEIGHT, P3D);
	}

	public static void main(String[] args) {
		PApplet.main(GPU.class.getName());
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

		sphere = createShape(SPHERE, 10);
		
		angle = new float[SIZE];
		speed = new float[SIZE];
		rad = new float[SIZE];
		for (int i = 0; i < SIZE; i++) {
			speed[i] = random(0.05f);
			rad[i] = random(10f);
		}
	}

	public void draw() {
		background(255);
		noStroke();

		shape(sphere);
		for (int i = 0; i < SIZE; i++) {
			angle[i] += speed[i];
		}
		for (int i = 0; i < drawn; i++) {
			pushMatrix();
			{
				rotate(angle[i]);
				translate(20 * (i + 1), 0);
				scale(rad[i]);
				shape(sphere);
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
