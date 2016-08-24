package cv;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class Main extends PApplet {

	private static int WIDTH = 800;
	private static int HEIGHT = 800;

	private ParticleSystem particleSystem;
	private PImage img;

	public void settings() {
		size(WIDTH, HEIGHT, P2D);
	}

	public static void main(String[] args) {
		PApplet.main(Main.class.getName());
	}

	public void setup() {
		super.setup();
		loop();

		img = loadImage("fire2.png");
		particleSystem = new ParticleSystem(new PVector(WIDTH / 2, HEIGHT / 2), img);
	}

	public void draw() {
		background(0);
		blendMode(ADD);
		particleSystem.addParticle(5);
		// particleSystem.applyForce(new PVector(0, -0.0125f));
		particleSystem.applyForce(new PVector(map(mouseX, 0, WIDTH, -0.2f, 0.2f), map(mouseY, 0, HEIGHT, -0.2f, 0.2f)));
		particleSystem.run(getGraphics());
	}
}
