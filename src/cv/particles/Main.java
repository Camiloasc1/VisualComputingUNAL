package cv.particles;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.event.MouseEvent;

public class Main extends PApplet {

	private static int WIDTH = 800;
	private static int HEIGHT = 800;
	private static float WINDSCALE = 0.05f;
	private static int PARTICLECOUNT = 5;

	private PImage img;
	private ParticleSystem particleSystem;
	private float mouseForce;

	public void settings() {
		size(WIDTH, HEIGHT, P2D);
	}

	public static void main(String[] args) {
		PApplet.main(Main.class.getName());
	}

	public void setup() {
		super.setup();
		loop();

		img = loadImage("res/fire2.png");
		particleSystem = new ParticleSystem(new PVector(WIDTH / 2, HEIGHT / 2), img);
		particleSystem.addAttractor(new Attractor(new PVector(0, 0), -100f));
		particleSystem.addAttractor(new Attractor(new PVector(WIDTH / 4, HEIGHT / 3), 50f));
		particleSystem.setGravity(new PVector(0, 0.25f));
		mouseForce = 0.25f;
	}

	public void draw() {
		background(0);
		blendMode(ADD);
		particleSystem.addParticle(PARTICLECOUNT);
		// particleSystem.applyForce(new PVector(0, -0.0125f));
		particleSystem.applyForce(new PVector(map(mouseX, 0, WIDTH, -mouseForce, mouseForce),
				map(mouseY, 0, HEIGHT, -mouseForce, mouseForce)));
		particleSystem.run(getGraphics());
	}

	@Override
	public void mouseWheel(MouseEvent event) {
		mouseForce -= WINDSCALE * event.getCount();
	}
}
