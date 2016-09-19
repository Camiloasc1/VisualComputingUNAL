package cv.physics;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import java.util.HashSet;
import java.util.Set;

public class CircleRaycast extends PApplet {

    private static int WIDTH = 600;
    private static int HEIGHT = 600;

    private PhysicCircle circles;
    private PImage img;

    public void settings() {
        size(WIDTH, HEIGHT, P2D);
    }

    public static void main(String[] args) {
        PApplet.main(CircleRaycast.class.getName());
    }

    public void setup() {
        super.setup();
        loop();

        img = loadImage("res/porky.jpg");
        circles = new PhysicCircle(new PVector(WIDTH / 2, HEIGHT / 2), 256);
        circles.add(new PhysicCircle(new PVector(WIDTH / 2 - 128, HEIGHT / 2), 128));
        circles.add(new PhysicCircle(new PVector(WIDTH / 2 + 128, HEIGHT / 2), 128));
//		circles.add(new PhysicCircle(new PVector(WIDTH / 2, HEIGHT / 2 - 160), 64));
        circles.add(new PhysicCircle(new PVector(WIDTH / 2, HEIGHT / 2 + 160), 64));
    }

    public void draw() {
        background(0);
        image(img, WIDTH / 2 - img.width / 2, HEIGHT / 2 - img.height / 2);
        Ray ray = new Ray(new PVector(0, HEIGHT), new PVector(mouseX, mouseY));
        circles.draw(ray);
        ray.draw();
    }

    class PhysicCircle {
        private Set<PhysicCircle> circles;
        private PVector pos;
        private float r;

        public PhysicCircle(PVector pos, float r) {
            super();
            circles = new HashSet<>();
            this.pos = pos;
            this.r = r;
        }

        public boolean add(PhysicCircle c) {
            return circles.add(c);
        }

        public void draw(Ray ray) {
            boolean collides = rayCast(ray);
            pushStyle();
            {
                stroke(255);
                fill(collides ? 128 : 0, 0);
                ellipse(pos.x, pos.y, 2 * r, 2 * r);
            }
            popStyle();
            for (PhysicCircle c : circles) {
                c.draw(collides ? ray : null);
            }
        }

        public boolean rayCast(Ray ray) {
            if (ray == null)
                return false;

            PVector d = ray.getVector();
            PVector e = ray.getStart().sub(pos);

            float a = d.dot(d);
            float b = 2 * e.dot(d);
            float c = e.dot(e) - r * r;

            float discriminant = b * b - 4 * a * c;
            if (discriminant < 0)
                return false;
            else {
                discriminant = sqrt(discriminant);
                float t1 = (-b - discriminant) / (2 * a);
                float t2 = (-b + discriminant) / (2 * a);
                boolean b1 = 0 <= t1 && t1 <= 1;
                boolean b2 = 0 <= t2 && t2 <= 1;

                if (b1) {
                    pushStyle();
                    {
                        stroke(255, 0, 0);
                        fill(255, 0, 0);
                        PVector v = d.copy().mult(t1);
                        v.y += HEIGHT;
                        ellipse(v.x, v.y, 10, 10);
                    }
                    popStyle();
                }

                if (b2) {
                    pushStyle();
                    {
                        stroke(255, 0, 0);
                        fill(255, 0, 0);
                        PVector v = d.copy().mult(t2);
                        v.y += HEIGHT;
                        ellipse(v.x, v.y, 10, 10);
                    }
                    popStyle();
                }
                return b1 || b2;
            }
        }
    }

    class Ray {
        private PVector start;
        private PVector end;

        public Ray(PVector start, PVector end) {
            super();
            this.start = start;
            this.end = end;
        }

        public PVector getStart() {
            return start.copy();
        }

        public PVector getEnd() {
            return end.copy();
        }

        public PVector getVector() {
            return end.copy().sub(start);
        }

        public void draw() {
            pushStyle();
            {
                stroke(255);
                fill(0);
                line(start.x, start.y, end.x, end.y);
            }
            popStyle();
        }
    }
}