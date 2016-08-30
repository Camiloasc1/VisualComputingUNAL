package cv.flocking;

import processing.core.PApplet;
import processing.core.PVector;
import remixlab.dandelion.core.Trackable;
import remixlab.dandelion.geom.Quat;
import remixlab.dandelion.geom.Vec;
import remixlab.proscene.InteractiveFrame;
import remixlab.proscene.MouseAgent;
import remixlab.proscene.Scene;

import java.util.ArrayList;

public class Main3D extends PApplet {
    private static int WIDTH = 640;
    private static int HEIGHT = 360;

    Scene scene;
    Trackable lastAvatar;
    //flock bounding box
    int flockWidth = 1280;
    int flockHeight = 720;
    int flockDepth = 600;
    int initBoidNum = 300; // amount of boids to start the program with
    ArrayList<Boid> flock;
    boolean avoidWalls = true;
    float hue = 255;
    boolean triggered;
    boolean inThirdPerson;
    boolean changedMode;

    public void settings() {
        size(640, 360, P3D);
    }

    public static void main(String[] args) {
        PApplet.main(Main3D.class.getName());
    }

    public void setup() {
        scene = new Scene(this);
        scene.mouseAgent().setPickingMode(MouseAgent.PickingMode.CLICK);
        scene.setAxesVisualHint(false);
        scene.setGridVisualHint(false);
        scene.setBoundingBox(new Vec(0, 0, 0), new Vec(flockWidth, flockHeight, flockDepth));
        scene.showAll();
        // create and fill the list of boids
        flock = new ArrayList();
        for (int i = 0; i < initBoidNum; i++)
            flock.add(new Boid(scene, new PVector(flockWidth / 2, flockHeight / 2, flockDepth / 2)));
        scene.startAnimation();
    }

    public void draw() {
        background(0);
        if (inThirdPerson && scene.avatar() == null) {
            inThirdPerson = false;
            adjustFrameRate();
        } else if (!inThirdPerson && scene.avatar() != null) {
            inThirdPerson = true;
            adjustFrameRate();
        }
        ambientLight(128, 128, 128);
        directionalLight(255, 255, 255, 0, 1, -100);
        noFill();
        stroke(255);

        line(0, 0, 0, 0, flockHeight, 0);
        line(0, 0, flockDepth, 0, flockHeight, flockDepth);
        line(0, 0, 0, flockWidth, 0, 0);
        line(0, 0, flockDepth, flockWidth, 0, flockDepth);

        line(flockWidth, 0, 0, flockWidth, flockHeight, 0);
        line(flockWidth, 0, flockDepth, flockWidth, flockHeight, flockDepth);
        line(0, flockHeight, 0, flockWidth, flockHeight, 0);
        line(0, flockHeight, flockDepth, flockWidth, flockHeight, flockDepth);

        line(0, 0, 0, 0, 0, flockDepth);
        line(0, flockHeight, 0, 0, flockHeight, flockDepth);
        line(flockWidth, 0, 0, flockWidth, 0, flockDepth);
        line(flockWidth, flockHeight, 0, flockWidth, flockHeight, flockDepth);

        triggered = scene.timer().trigggered();
        for (Boid boid : flock) {
            if (triggered)
                boid.run(flock);
            boid.render();
        }
    }

    void adjustFrameRate() {
        if (scene.avatar() != null)
            frameRate(1000 / scene.animationPeriod());
        else
            frameRate(60);
        if (scene.animationStarted())
            scene.restartAnimation();
    }

    public void keyPressed() {
        switch (key) {
            case 't':
                scene.shiftTimers();
            case 'p':
                println("Frame rate: " + frameRate);
                break;
            case 'v':
                avoidWalls = !avoidWalls;
                break;
            case '+':
                scene.setAnimationPeriod(scene.animationPeriod() - 2, false);
                adjustFrameRate();
                break;
            case '-':
                scene.setAnimationPeriod(scene.animationPeriod() + 2, false);
                adjustFrameRate();
                break;
            case ' ':
                if (scene.avatar() == null && lastAvatar != null)
                    scene.setAvatar(lastAvatar);
                else
                    lastAvatar = scene.resetAvatar();
                break;
        }
    }

    // The Boid class
    class Boid {
        Scene scene;
        InteractiveFrame frame;
        Quat q;
        int grabsMouseColor;// color
        int avatarColor;

        // fields
        PVector pos, vel, acc, ali, coh, sep; // pos, velocity, and acceleration
        // in
        // a vector datatype
        float neighborhoodRadius; // radius in which it looks for fellow boids
        float maxSpeed = 4; // maximum magnitude for the velocity vector
        float maxSteerForce = .1f; // maximum magnitude of the steering vector
        float sc = 3; // scale factor for the render of the boid
        float flap = 0;
        float t = 0;

        // constructors
        Boid(Scene scn, PVector inPos) {
            scene = scn;
            grabsMouseColor = color(0, 0, 255);
            avatarColor = color(255, 0, 0);
            pos = new PVector();
            pos.set(inPos);
            frame = new InteractiveFrame(scene);
            frame.setPosition(new Vec(pos.x, pos.y, pos.z));
            frame.setTrackingEyeAzimuth(-PApplet.HALF_PI);
            frame.setTrackingEyeInclination(PApplet.PI * (4 / 5));
            frame.setTrackingEyeDistance(scene.radius() / 10);
            vel = new PVector(random(-1, 1), random(-1, 1), random(1, -1));
            acc = new PVector(0, 0, 0);
            neighborhoodRadius = 100;
        }

        void run(ArrayList bl) {
            t += .1;
            flap = 10 * PApplet.sin(t);
            // acc.add(steer(new PVector(mouseX,mouseY,300),true));
            // acc.add(new PVector(0,.05,0));
            if (avoidWalls) {
                acc.add(PVector.mult(avoid(new PVector(pos.x, flockHeight, pos.z), true), 5));
                acc.add(PVector.mult(avoid(new PVector(pos.x, 0, pos.z), true), 5));
                acc.add(PVector.mult(avoid(new PVector(flockWidth, pos.y, pos.z), true), 5));
                acc.add(PVector.mult(avoid(new PVector(0, pos.y, pos.z), true), 5));
                acc.add(PVector.mult(avoid(new PVector(pos.x, pos.y, 0), true), 5));
                acc.add(PVector.mult(avoid(new PVector(pos.x, pos.y, flockDepth), true), 5));
            }
            flock(bl);
            move();
            checkBounds();
            // render();
        }

        // ///-----------behaviors---------------
        void flock(ArrayList bl) {
            ali = alignment(bl);
            coh = cohesion(bl);
            sep = seperation(bl);
            acc.add(PVector.mult(ali, 1));
            acc.add(PVector.mult(coh, 3));
            acc.add(PVector.mult(sep, 1));
        }

        void scatter() {
        }

        // //------------------------------------

        void move() {
            vel.add(acc); // add acceleration to velocity
            vel.limit(maxSpeed); // make sure the velocity vector magnitude does
            // not
            // exceed maxSpeed
            pos.add(vel); // add velocity to position
            frame.setPosition(new Vec(pos.x, pos.y, pos.z));
            acc.mult(0); // reset acceleration
        }

        void checkBounds() {
            if (pos.x > flockWidth)
                pos.x = 0;
            if (pos.x < 0)
                pos.x = flockWidth;
            if (pos.y > flockHeight)
                pos.y = 0;
            if (pos.y < 0)
                pos.y = flockHeight;
            if (pos.z > flockDepth)
                pos.z = 0;
            if (pos.z < 0)
                pos.z = flockDepth;
        }

        // check if this boid's frame is the avatar
        boolean isAvatar() {
            return scene.avatar() == null ? false : scene.avatar().equals(frame) ? true : false;
        }

        void render() {
            pushStyle();
            stroke(hue);
            noFill();
            noStroke();
            fill(hue);

            q = Quat.multiply(new Quat(new Vec(0, 1, 0), PApplet.atan2(-vel.z, vel.x)),
                    new Quat(new Vec(0, 0, 1), PApplet.asin(vel.y / vel.mag())));
            frame.setRotation(q);

            pushMatrix();
            // Multiply matrix to get in the frame coordinate system.
            frame.applyTransformation();

            // highlight boids under the mouse
            if (frame.checkIfGrabsInput(mouseX, mouseY))
                fill(grabsMouseColor);

            // setAvatar according to scene.motionAgent().inputGrabber()
            if (frame.grabsInput())
                if (!isAvatar())
                    scene.setAvatar(frame);

            // highlight the boid if its frame is the avatar
            if (isAvatar())
                fill(avatarColor);

            // draw boid
            beginShape(PApplet.TRIANGLES);
            vertex(3 * sc, 0, 0);
            vertex(-3 * sc, 2 * sc, 0);
            vertex(-3 * sc, -2 * sc, 0);

            vertex(3 * sc, 0, 0);
            vertex(-3 * sc, 2 * sc, 0);
            vertex(-3 * sc, 0, 2 * sc);

            vertex(3 * sc, 0, 0);
            vertex(-3 * sc, 0, 2 * sc);
            vertex(-3 * sc, -2 * sc, 0);

            vertex(-3 * sc, 0, 2 * sc);
            vertex(-3 * sc, 2 * sc, 0);
            vertex(-3 * sc, -2 * sc, 0);
            endShape();

            popMatrix();
            popStyle();
        }

        // steering. If arrival==true, the boid slows to meet the target. Credit
        // to
        // Craig Reynolds
        PVector steer(PVector target, boolean arrival) {
            PVector steer = new PVector(); // creates vector for steering
            if (!arrival) {
                steer.set(PVector.sub(target, pos)); // steering vector points
                // towards target (switch
                // target and pos for
                // avoiding)
                steer.limit(maxSteerForce); // limits the steering force to
                // maxSteerForce
            } else {
                PVector targetOffset = PVector.sub(target, pos);
                float distance = targetOffset.mag();
                float rampedSpeed = maxSpeed * (distance / 100);
                float clippedSpeed = PApplet.min(rampedSpeed, maxSpeed);
                PVector desiredVelocity = PVector.mult(targetOffset, (clippedSpeed / distance));
                steer.set(PVector.sub(desiredVelocity, vel));
            }
            return steer;
        }

        // avoid. If weight == true avoidance vector is larger the closer the
        // boid
        // is to the target
        PVector avoid(PVector target, boolean weight) {
            PVector steer = new PVector(); // creates vector for steering
            steer.set(PVector.sub(pos, target)); // steering vector points away
            // from
            // target
            if (weight)
                steer.mult(1 / PApplet.sq(PVector.dist(pos, target)));
            // steer.limit(maxSteerForce); //limits the steering force to
            // maxSteerForce
            return steer;
        }

        PVector seperation(ArrayList boids) {
            PVector posSum = new PVector(0, 0, 0);
            PVector repulse;
            for (int i = 0; i < boids.size(); i++) {
                Boid b = (Boid) boids.get(i);
                float d = PVector.dist(pos, b.pos);
                if (d > 0 && d <= neighborhoodRadius) {
                    repulse = PVector.sub(pos, b.pos);
                    repulse.normalize();
                    repulse.div(d);
                    posSum.add(repulse);
                }
            }
            return posSum;
        }

        PVector alignment(ArrayList boids) {
            PVector velSum = new PVector(0, 0, 0);
            int count = 0;
            for (int i = 0; i < boids.size(); i++) {
                Boid b = (Boid) boids.get(i);
                float d = PVector.dist(pos, b.pos);
                if (d > 0 && d <= neighborhoodRadius) {
                    velSum.add(b.vel);
                    count++;
                }
            }
            if (count > 0) {
                velSum.div((float) count);
                velSum.limit(maxSteerForce);
            }
            return velSum;
        }

        PVector cohesion(ArrayList boids) {
            PVector posSum = new PVector(0, 0, 0);
            PVector steer = new PVector(0, 0, 0);
            int count = 0;
            for (int i = 0; i < boids.size(); i++) {
                Boid b = (Boid) boids.get(i);
                float d = PApplet.dist(pos.x, pos.y, b.pos.x, b.pos.y);
                if (d > 0 && d <= neighborhoodRadius) {
                    posSum.add(b.pos);
                    count++;
                }
            }
            if (count > 0) {
                posSum.div((float) count);
            }
            steer = PVector.sub(posSum, pos);
            steer.limit(maxSteerForce);
            return steer;
        }
    }
}
