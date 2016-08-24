package cv;

import java.util.ArrayList;
import java.util.List;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

public class ParticleSystem {
	private List<Particle> particles;
	private PVector pos;
	private PImage img;

	public ParticleSystem(PVector pos, PImage img) {
		super();
		this.pos = pos;
		this.img = img;
		particles = new ArrayList<>();
	}

	public void applyForce(PVector force) {
		for (Particle particle : particles) {
			particle.applyForce(force);
		}
	}

	public void addParticle() {
		particles.add(new Particle(pos, img));
	}

	public void addParticle(int count) {
		for (int i = 0; i < count; i++) {
			addParticle();
		}
	}

	public void run(PGraphics pg) {
		for (int i = 0; i < particles.size(); i++) {
			Particle p = particles.get(i);
			if (!p.isAlive())
				particles.remove(i--);
			else
				p.run(pg);
		}
	}

}
