package cv.physics.box2d.ragdoll;

import fisica.*;
import processing.core.PApplet;
import processing.core.PVector;

public class Main extends PApplet {

    private static int WIDTH = 800;
    private static int HEIGHT = 800;

    private FWorld world;

    public void settings() {
        size(WIDTH, HEIGHT, P2D);
    }

    public static void main(String[] args) {
        PApplet.main(Main.class.getName());
    }

    public void setup() {
        super.setup();
        loop();

        createBody();

    }

    private void createBody() {
        boolean s = false;

        Fisica.init(this);
        world = new FWorld();
        world.setEdges();
        FCircle head = new FCircle(30);
        head.setPosition(WIDTH / 2, HEIGHT / 2);
        head.setStatic(s);
        world.add(head);

        FBox chest1 = new FBox(40, 15);
        chest1.setPosition(head.getX(), head.getY() + head.getSize() / 2 + chest1.getHeight() / 2);
        chest1.setStatic(s);
        world.add(chest1);

        FBox chest2 = new FBox(40, 15);
        chest2.setPosition(head.getX(), chest1.getY() + chest1.getHeight() / 2 + chest2.getHeight() / 2);
        chest2.setStatic(s);
        world.add(chest2);

        FBox chest3 = new FBox(40, 15);
        chest3.setPosition(head.getX(), chest2.getY() + chest2.getHeight() / 2 + chest3.getHeight() / 2);
        chest3.setStatic(s);
        world.add(chest3);

        FBox leftleg1 = new FBox(15, 25);
        leftleg1.setPosition(chest3.getX() - chest3.getWidth() / 2 + leftleg1.getWidth() / 2,
                chest3.getY() + chest2.getHeight() / 2 + leftleg1.getHeight() / 2);
        leftleg1.setStatic(s);
        world.add(leftleg1);

        FBox leftleg2 = new FBox(15, 25);
        leftleg2.setPosition(leftleg1.getX(), leftleg1.getY() + leftleg1.getHeight() / 2 + leftleg2.getHeight() / 2);
        leftleg2.setStatic(s);
        world.add(leftleg2);

        FBox rightleg1 = new FBox(15, 25);
        rightleg1.setPosition(chest3.getX() + chest3.getWidth() / 2 - rightleg1.getWidth() / 2,
                chest3.getY() + chest2.getHeight() / 2 + rightleg1.getHeight() / 2);
        rightleg1.setStatic(s);
        world.add(rightleg1);

        FBox rightleg2 = new FBox(15, 25);
        rightleg2.setPosition(rightleg1.getX(),
                rightleg1.getY() + rightleg1.getHeight() / 2 + rightleg2.getHeight() / 2);
        rightleg2.setStatic(s);
        world.add(rightleg2);

        FBox leftarm1 = new FBox(20, 15);
        leftarm1.setPosition(chest1.getX() - chest1.getWidth() / 2 - leftarm1.getWidth() / 2, chest1.getY());
        leftarm1.setStatic(s);
        world.add(leftarm1);

        FBox leftarm2 = new FBox(20, 15);
        leftarm2.setPosition(leftarm1.getX() - leftarm1.getWidth() / 2 - leftarm2.getWidth() / 2, leftarm1.getY());
        leftarm2.setStatic(s);
        world.add(leftarm2);

        FBox rightarm1 = new FBox(20, 15);
        rightarm1.setPosition(chest1.getX() + chest1.getWidth() / 2 + rightarm1.getWidth() / 2, chest1.getY());
        rightarm1.setStatic(s);
        world.add(rightarm1);

        FBox rightarm2 = new FBox(20, 15);
        rightarm2.setPosition(rightarm1.getX() + rightarm1.getWidth() / 2 + rightarm2.getWidth() / 2, rightarm1.getY());
        rightarm2.setStatic(s);
        world.add(rightarm2);

        createJoint(head, chest1, new PVector(0, 0), new PVector(0, 0), false);
        createJoint(chest1, chest2, new PVector(0, 0.5f), new PVector(0, -0.5f), false);
        createJoint(chest2, chest3, new PVector(0, 0.5f), new PVector(0, -0.5f), false);

        createJoint(chest3, leftleg1, new PVector(-0.3f, 0.5f), new PVector(0, -0.5f), false);
        createJoint(leftleg1, leftleg2, new PVector(0, 0.5f), new PVector(0, -0.5f), true);

        createJoint(chest3, rightleg1, new PVector(0.3f, 0.5f), new PVector(0, -0.5f), false);
        createJoint(rightleg1, rightleg2, new PVector(0, 0.5f), new PVector(0, -0.5f), true);

        createJoint(chest1, leftarm1, new PVector(-0.5f, 0), new PVector(0.5f, 0), false);
        createJoint(leftarm1, leftarm2, new PVector(-0.5f, 0), new PVector(0.5f, 0), true);

        createJoint(chest1, rightarm1, new PVector(0.5f, 0), new PVector(-0.5f, 0), false);
        createJoint(rightarm1, rightarm2, new PVector(0.5f, 0), new PVector(-0.5f, 0), true);
    }

    private void createJoint(FBody body1, FBody body2, PVector anchor1, PVector anchor2, boolean articulate) {
        if (body1 instanceof FCircle) {
            anchor1.x *= ((FCircle) body1).getSize();
            anchor1.y *= ((FCircle) body1).getSize();
        }
        if (body1 instanceof FBox) {
            anchor1.x *= ((FBox) body1).getWidth();
            anchor1.y *= ((FBox) body1).getHeight();
        }
        if (body2 instanceof FCircle) {
            anchor2.x *= ((FCircle) body2).getSize();
            anchor2.y *= ((FCircle) body2).getSize();
        }
        if (body2 instanceof FBox) {
            anchor2.x *= ((FBox) body2).getWidth();
            anchor2.y *= ((FBox) body2).getHeight();
        }

        // FDistanceJoint joint;
        // joint = new FDistanceJoint(body1, body2);
        // joint.setAnchor1(anchor1.x, anchor1.y);
        // joint.setAnchor2(anchor2.x, anchor2.y);
        // joint.setDamping(1e14f);
        // joint.setFrequency(1e15f);
        // joint.setFill(255);
        // joint.setCollideConnected(!articulate);
        // joint.calculateLength();

        float angle = articulate ? PI / 2 : PI / 16;

        FRevoluteJoint joint;
        joint = new FRevoluteJoint(body1, body2, body1.getX() + anchor1.x, body1.getY() + anchor1.y);
        joint.setCollideConnected(false);
        joint.setEnableLimit(true);
        joint.setLowerAngle(-angle);
        joint.setUpperAngle(angle);
        world.add(joint);
    }

    public void draw() {
        background(0);

        world.step();
        world.draw();
    }
}
