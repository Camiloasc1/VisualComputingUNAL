package cv.particles;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

import java.util.Random;

public class Particle {

    private float m;
    private float size;
    private float life;
    private PVector pos;
    private PVector vel;
    private PVector acc;
    private PImage img;

    public Particle(PVector pos, PImage img) {
        super();
        Random random = new Random();

        m = random.nextFloat();
        size = m * 100;
        life = 1.0f;
        this.img = img;

        this.pos = pos.copy();
        vel = new PVector((float) random.nextGaussian() * 0.25f, (float) random.nextGaussian() * 0.25f - 1.0f);
        acc = new PVector();
    }

    public PVector getPos() {
        return pos.copy();
    }

    public void setPos(PVector pos) {
        this.pos = pos;
    }

    public void applyForce(PVector force) {
        acc.add(PVector.mult(force, 1.0f / m));
        // acc.add(PVector.mult(force, m));
    }

    public boolean isAlive() {
        return life > 0.0;
    }

    void run(PGraphics pg) {
        vel.add(acc);
        pos.add(vel);
        acc.mult(0);
        life -= 0.005;

        pg.tint(255, 255, 255 * m, life * 128);
        pg.image(img, pos.x, pos.y, size, size);
        // pg.noStroke();
        // pg.fill(255, 255, 255, life * 128);
        // pg.ellipse(pos.x, pos.y, size, size);
    }
}
