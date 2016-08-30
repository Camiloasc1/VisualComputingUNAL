package cv.particles;

import processing.core.PGraphics;
import processing.core.PVector;

import java.util.List;

public class Gravity {
    private PVector gravity;

    public Gravity() {
        super();
        gravity = new PVector(0, 0);
    }

    public Gravity(PVector gravity) {
        super();
        this.gravity = gravity;
    }

    public void run(PGraphics pg, List<Particle> particles) {
        for (Particle p : particles) {
            p.applyForce(gravity);
        }
    }

    public PVector getGravity() {
        return gravity;
    }

    public void setGravity(PVector gravity) {
        this.gravity = gravity;
    }

}
