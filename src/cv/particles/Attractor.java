package cv.particles;

import processing.core.PGraphics;
import processing.core.PVector;

import java.util.List;

public class Attractor {
    private PVector pos;
    private float strength;

    public Attractor(PVector pos, float strength) {
        super();
        this.pos = pos;
        this.strength = strength;
    }

    public void run(PGraphics pg, List<Particle> particles) {
        float d;
        PVector pPos;
        PVector f;
        for (Particle p : particles) {
            pPos = p.getPos();
            d = PVector.dist(pos, pPos);
            f = pPos.sub(pos);
            f.mult(-strength);
            f.div(d * d);
            p.applyForce(f);
        }
        pg.ellipse(pos.x, pos.y, strength, strength);
    }
}
