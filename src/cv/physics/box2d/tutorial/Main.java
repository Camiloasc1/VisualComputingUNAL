package cv.physics.box2d.tutorial;

import processing.core.PApplet;
import processing.core.PVector;

import org.jbox2d.dynamics.World;

import fisica.*;

public class Main extends PApplet {

	private static int WIDTH = 800;
	private static int HEIGHT = 800;

	private FWorld world;
	private FPoly poly;

	public void settings() {
		size(WIDTH, HEIGHT, P2D);
	}

	public static void main(String[] args) {
		PApplet.main(Main.class.getName());
	}

	public void setup() {
		super.setup();
		loop();

		Fisica.init(this);
		world = new FWorld();
		world.setEdges();
		{
			FCircle circle = new FCircle(30);
			circle.setPosition(WIDTH / 2, HEIGHT / 2);
			circle.setRestitution(0.8f);
			circle.setStatic(true);
			world.add(circle);
		}
		{
			FBox box = new FBox(100, 10);
			box.setPosition(1f / 4f * WIDTH, HEIGHT / 2);
			box.setStatic(true);
			world.add(box);
		}
		{
			FBox box = new FBox(100, 10);
			box.setPosition(3f / 4f * WIDTH, HEIGHT / 2);
			box.setStatic(true);
			world.add(box);
		}
		{
			FCompound compound = new FCompound();
			compound.setPosition(WIDTH / 2, HEIGHT / 2);
			{
				FCircle circle = new FCircle(40);
				circle.setPosition(0, 0);
				circle.setRestitution(0.8f);
				compound.addBody(circle);
			}
			{
				FCircle circle = new FCircle(30);
				circle.setPosition(0, -10);
				circle.setRestitution(0.8f);
				compound.addBody(circle);
			}
			{
				FCircle circle = new FCircle(20);
				circle.setPosition(0, -20);
				circle.setRestitution(0.8f);
				compound.addBody(circle);
			}
			world.add(compound);
		}
	}

	public void draw() {
		background(0);

		if (frameCount % 100 == 0) {
			FCircle circle = new FCircle(30);
			circle.setPosition(random(0, WIDTH), 30);
			circle.setRestitution(0.8f);
			world.add(circle);

			FBlob blob = new FBlob();
			int size = (int) random(10, 50);
			blob.setAsCircle(random(0, WIDTH), 30, size, size);
			blob.setFill(random(0, 255), random(0, 255), random(0, 255));
			world.add(blob);
		}

		world.step();
		world.draw();
	}

	@Override
	public void mouseDragged() {
		if (poly == null) {
			poly = new FPoly();
			poly.setPosition(WIDTH / 2, HEIGHT / 2 + 32);
			poly.setStatic(true);
			poly.vertex(mouseX, mouseY);
			world.add(poly);
		}
	}

	@Override
	public void mouseReleased() {
		if (poly != null) {
			poly.setStatic(false);
			poly = null;
		}
	}

}
