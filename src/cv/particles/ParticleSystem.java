package cv.particles;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class ParticleSystem {
    private List<Particle> particles;
    private List<Attractor> attractors;
    private Gravity gravity;
    private PVector pos;
    private PImage img;

    public ParticleSystem(PVector pos, PImage img) {
        super();
        this.pos = pos;
        this.img = img;
        particles = new ArrayList<Particle>();
        attractors = new ArrayList<Attractor>();
        gravity = new Gravity();
    }

    public PVector getPos() {
        return pos;
    }

    public void setPos(PVector pos) {
        this.pos = pos;
    }

    public List<Particle> getParticles() {
        return particles;
    }

    public boolean addParticle(Particle p) {
        return particles.add(p);
    }

    public boolean addAttractor(Attractor r) {
        return attractors.add(r);
    }

    public List<Attractor> getRepellers() {
        return attractors;
    }

    public void addParticle() {
        particles.add(new Particle(pos, img));
    }

    public void addParticle(int count) {
        for (int i = 0; i < count; i++) {
            addParticle();
        }
    }

    public PVector getGravity() {
        return gravity.getGravity();
    }

    public void setGravity(PVector gravity) {
        this.gravity.setGravity(gravity);
    }

    public void applyForce(PVector force) {
        for (Particle particle : particles) {
            particle.applyForce(force);
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
        for (Attractor a : attractors) {
            a.run(pg, particles);
        }
        gravity.run(pg, particles);
    }

}
