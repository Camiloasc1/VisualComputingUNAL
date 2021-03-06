package cv.flocking;

import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;
import remixlab.dandelion.geom.Vec;
import remixlab.proscene.Scene;

import java.util.ArrayList;
import java.util.List;

public class Main extends PApplet {
    private static int WIDTH = 800;
    private static int HEIGHT = 800;

    private static int fWidth = 500;
    private static int fHeight = 500;
    private static int fDepth = 500;
    private Scene scene;
    private Flock flock;
    private PShape bird;

    public void settings() {
        size(WIDTH, HEIGHT, P3D);
    }

    public static void main(String[] args) {
        PApplet.main(Main.class.getName());
    }

    public void setup() {
        super.setup();
        loop();

        scene = new Scene(this);
        scene.setAxesVisualHint(false);
        scene.setGridVisualHint(false);
        scene.setBoundingBox(new Vec(0, 0, 0), new Vec(fWidth, fHeight, fDepth));
        scene.showAll();
        flock = new Flock();
        // Add an initial set of boids into the system
        for (int i = 0; i < 150; i++) {
            flock.addBoid(new Boid(fWidth / 2, fHeight / 2, fDepth / 2));
        }
        bird = loadShape("res/bird.obj");
    }

    public void draw() {
        background(50);
        flock.run();

        noFill();
        stroke(255);

        line(0, 0, 0, 0, fHeight, 0);
        line(0, 0, fDepth, 0, fHeight, fDepth);
        line(0, 0, 0, fWidth, 0, 0);
        line(0, 0, fDepth, fWidth, 0, fDepth);

        line(fWidth, 0, 0, fWidth, fHeight, 0);
        line(fWidth, 0, fDepth, fWidth, fHeight, fDepth);
        line(0, fHeight, 0, fWidth, fHeight, 0);
        line(0, fHeight, fDepth, fWidth, fHeight, fDepth);

        line(0, 0, 0, 0, 0, fDepth);
        line(0, fHeight, 0, 0, fHeight, fDepth);
        line(fWidth, 0, 0, fWidth, 0, fDepth);
        line(fWidth, fHeight, 0, fWidth, fHeight, fDepth);
    }

    // The Flock (a list of Boid objects)

    class Flock {
        ArrayList<Boid> boids; // An ArrayList for all the boids

        Flock() {
            boids = new ArrayList<Boid>(); // Initialize the ArrayList
        }

        void run() {
            for (Boid b : boids) {
                b.run(boids); // Passing the entire list of boids to each boid
                // individually
            }
        }

        void addBoid(Boid b) {
            boids.add(b);
        }

    }

    // The Boid class

    class Boid {

        PVector location;
        PVector velocity;
        PVector acceleration;
        float r;
        float maxforce; // Maximum steering force
        float maxspeed; // Maximum speed

        Boid(float x, float y, float z) {
            acceleration = new PVector(0, 0);

            // This is a new PVector method not yet implemented in JS
            // velocity = PVector.random2D();

            // Leaving the code temporarily this way so that this example runs
            // in JS
            float angle = random(TWO_PI);
            velocity = new PVector(random(-1, 1), random(-1, 1), random(1, -1));

            location = new PVector(x, y, z);
            r = 2.0f;
            maxspeed = 2;
            maxforce = 0.03f;
        }

        void run(ArrayList<Boid> boids) {
            flock(boids);
            update();
            borders();
            render();
        }

        void applyForce(PVector force) {
            // We could add mass here if we want A = F / M
            acceleration.add(force);
        }

        // We accumulate a new acceleration each time based on three rules
        void flock(ArrayList<Boid> boids) {
            PVector sep = separate(boids); // Separation
            PVector ali = align(boids); // Alignment
            PVector coh = cohesion(boids); // Cohesion
            // Arbitrarily weight these forces
            sep.mult(1.5f);
            ali.mult(1.0f);
            coh.mult(1.0f);
            // Add the force vectors to acceleration
            applyForce(sep);
            applyForce(ali);
            applyForce(coh);
        }

        // Method to update location
        void update() {
            // Update velocity
            velocity.add(acceleration);
            // Limit speed
            velocity.limit(maxspeed);
            location.add(velocity);
            // Reset accelertion to 0 each cycle
            acceleration.mult(0);
        }

        // A method that calculates and applies a steering force towards a
        // target
        // STEER = DESIRED MINUS VELOCITY
        PVector seek(PVector target) {
            PVector desired = PVector.sub(target, location); // A vector
            // pointing from
            // the location
            // to the target
            // Scale to maximum speed
            desired.normalize();
            desired.mult(maxspeed);

            // Above two lines of code below could be condensed with new PVector
            // setMag() method
            // Not using this method until Processing.js catches up
            // desired.setMag(maxspeed);

            // Steering = Desired minus Velocity
            PVector steer = PVector.sub(desired, velocity);
            steer.limit(maxforce); // Limit to maximum steering force
            return steer;
        }

        void render() {
            fill(200, 100);
            stroke(255);
            pushMatrix();
            translate(location.x, location.y, location.z);
            rotateZ(PI);
            rotateY(new PVector(velocity.x, velocity.z).heading() - PI / 2);
            // beginShape(TRIANGLES);
            // vertex(0, -r * 2);
            // vertex(-r, r * 2);
            // vertex(r, r * 2);
            // endShape();
            scale(0.25f);
            shape(bird);
            popMatrix();
        }

        // Wraparound
        void borders() {
            float strength = -1;
            List<PVector> corners = new ArrayList<PVector>();
            corners.add(new PVector(0, location.y, location.z));
            corners.add(new PVector(location.x, 0, location.z));
            corners.add(new PVector(location.x, location.y, 0));
            corners.add(new PVector(fWidth, location.y, location.z));
            corners.add(new PVector(location.x, fHeight, location.z));
            corners.add(new PVector(location.x, location.y, fDepth));
            float d;
            PVector f;
            for (PVector p : corners) {
                d = PVector.dist(location, p);
                f = p.sub(location);
                f.mult(strength);
                f.div(d * d);
                applyForce(f);
            }
            // if (location.x < -r)
            // location.x = fWidth + r;
            // if (location.y < -r)
            // location.y = fHeight + r;
            // if (location.z < -r)
            // location.z = fDepth + r;
            // if (location.x > fWidth + r)
            // location.x = -r;
            // if (location.y > fHeight + r)
            // location.y = -r;
            // if (location.z > fDepth + r)
            // location.z = -r;
        }

        // Separation
        // Method checks for nearby boids and steers away
        PVector separate(ArrayList<Boid> boids) {
            float desiredseparation = 25.0f;
            PVector steer = new PVector(0, 0, 0);
            int count = 0;
            // For every boid in the system, check if it's too close
            for (Boid other : boids) {
                float d = PVector.dist(location, other.location);
                // If the distance is greater than 0 and less than an arbitrary
                // amount (0 when you are yourself)
                if ((d > 0) && (d < desiredseparation)) {
                    // Calculate vector pointing away from neighbor
                    PVector diff = PVector.sub(location, other.location);
                    diff.normalize();
                    diff.div(d); // Weight by distance
                    steer.add(diff);
                    count++; // Keep track of how many
                }
            }
            // Average -- divide by how many
            if (count > 0) {
                steer.div((float) count);
            }

            // As long as the vector is greater than 0
            if (steer.mag() > 0) {
                // First two lines of code below could be condensed with new
                // PVector setMag() method
                // Not using this method until Processing.js catches up
                // steer.setMag(maxspeed);

                // Implement Reynolds: Steering = Desired - Velocity
                steer.normalize();
                steer.mult(maxspeed);
                steer.sub(velocity);
                steer.limit(maxforce);
            }
            return steer;
        }

        // Alignment
        // For every nearby boid in the system, calculate the average velocity
        PVector align(ArrayList<Boid> boids) {
            float neighbordist = 50;
            PVector sum = new PVector(0, 0);
            int count = 0;
            for (Boid other : boids) {
                float d = PVector.dist(location, other.location);
                if ((d > 0) && (d < neighbordist)) {
                    sum.add(other.velocity);
                    count++;
                }
            }
            if (count > 0) {
                sum.div((float) count);
                // First two lines of code below could be condensed with new
                // PVector setMag() method
                // Not using this method until Processing.js catches up
                // sum.setMag(maxspeed);

                // Implement Reynolds: Steering = Desired - Velocity
                sum.normalize();
                sum.mult(maxspeed);
                PVector steer = PVector.sub(sum, velocity);
                steer.limit(maxforce);
                return steer;
            } else {
                return new PVector(0, 0);
            }
        }

        // Cohesion
        // For the average location (i.e. center) of all nearby boids, calculate
        // steering vector towards that location
        PVector cohesion(ArrayList<Boid> boids) {
            float neighbordist = 50;
            PVector sum = new PVector(0, 0); // Start with empty vector to
            // accumulate all locations
            int count = 0;
            for (Boid other : boids) {
                float d = PVector.dist(location, other.location);
                if ((d > 0) && (d < neighbordist)) {
                    sum.add(other.location); // Add location
                    count++;
                }
            }
            if (count > 0) {
                sum.div(count);
                return seek(sum); // Steer towards the location
            } else {
                return new PVector(0, 0);
            }
        }
    }
}
