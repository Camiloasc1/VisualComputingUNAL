package cv.culling;

import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;
import remixlab.dandelion.core.Camera;
import remixlab.dandelion.core.Eye;
import remixlab.dandelion.geom.Vec;
import remixlab.proscene.Scene;

import java.util.*;

//HSR
public class HiddenSurfaceRemoval extends PApplet {

    public static final int WIDTH = 600;
    public static final int HEIGHT = 600;
    private Scene scene, auxScene;
    private List<PShape> shapes;
    private Map<PShape, AABB> aabbs;

    @Override
    public void settings() {
        size(WIDTH, HEIGHT, P3D);
    }

    public void setup() {
        frameRate(30);

        scene = new Scene(this, createGraphics(WIDTH, HEIGHT / 2, P3D));
        scene.enableBoundaryEquations();

        auxScene = new Scene(this, createGraphics(WIDTH, HEIGHT / 2, P3D), 0, HEIGHT / 2);
        auxScene.setRadius(500);
        auxScene.showAll();

        buildComplexScene();
        // Vec p1 = new Vec(-500, -250, -250);
        // Vec p2 = new Vec(500, 250, 250);

        // TODO: To build Octree

    }

    private void buildComplexScene() {
        shapes = new LinkedList<>();
        aabbs = new HashMap<>();

        Random random = new Random();
        int numx = 50, numy = 25;
        for (int i = 0; i < numx; i++) {
            for (int j = 0; j < numy; j++) {
                int x = 20 * (i - numx / 2);
                int y = 20 * (j - numy / 2);
                createBox(x, y, 0, 10, random.nextInt(20) + 5);
            }
        }
    }

    private void createBox(int x, int y, int z, int w, int h) {
        PShape[] box = new PShape[6];

        // For closed polygons, use normal vectors facing outward
        box[0] = createShape();
        box[0].beginShape(QUAD);
        box[0].vertex(x, y + w, z + h);
        box[0].vertex(x, y, z + h);
        box[0].vertex(x + w, y, z + h);
        box[0].vertex(x + w, y + w, z + h);
//        box[0].normal(0, 0, 1);
        box[0].endShape();
        box[0].setNormal(0, 0, 0, 1);

        box[1] = createShape();
        box[1].beginShape(QUAD);
        box[1].vertex(x + w, y, z + h);
        box[1].vertex(x + w, y, z);
        box[1].vertex(x + w, y + w, z);
        box[1].vertex(x + w, y + w, z + h);
//        box[1].normal(1, 0, 0);
        box[1].endShape();
        box[1].setNormal(0, 1, 0, 0);

        box[2] = createShape();
        box[2].beginShape(QUAD);
        box[2].vertex(x + w, y + w, z);
        box[2].vertex(x, y + w, z);
        box[2].vertex(x, y + w, z + h);
        box[2].vertex(x + w, y + w, z + h);
//        box[2].normal(0, 1, 0);
        box[2].endShape();
        box[2].setNormal(0, 0, 1, 0);

        box[3] = createShape();
        box[3].beginShape(QUAD);
        box[3].vertex(x, y + w, z);
        box[3].vertex(x + w, y + w, z);
        box[3].vertex(x + w, y, z);
        box[3].vertex(x, y, z);
//        box[3].normal(0, 0, -1);
        box[3].endShape();
        box[3].setNormal(0, 0, 0, -1);

        box[4] = createShape();
        box[4].beginShape(QUAD);
        box[4].vertex(x, y + w, z + h);
        box[4].vertex(x, y + w, z);
        box[4].vertex(x, y, z);
        box[4].vertex(x, y, z + h);
//        box[4].normal(-1, 0, 0);
        box[4].endShape();
        box[4].setNormal(0, -1, 0, 0);

        box[5] = createShape();
        box[5].beginShape(QUAD);
        box[5].vertex(x, y, z);
        box[5].vertex(x + w, y, z);
        box[5].vertex(x + w, y, z + h);
        box[5].vertex(x, y, z + h);
//        box[5].normal(0, -1, 0);
        box[5].endShape();
        box[5].setNormal(0, 0, -1, 0);

        for (PShape edge : box) {
            shapes.add(edge);
            aabbs.put(edge, new AABB(edge.getVertex(0), edge.getVertex(2)));
        }
    }

    public void draw() {
        handleMouse();
        surface.setTitle("Frames: " + frameRate);

        culling();

        scene.pg().beginDraw();
        scene.beginDraw();
        mainDrawing(scene);
        scene.endDraw();
        scene.pg().endDraw();
        image(scene.pg(), 0, 0);

        auxScene.pg().beginDraw();
        auxScene.beginDraw();
        mainDrawing(auxScene);

        auxScene.pg().pushStyle();
        auxScene.pg().stroke(255, 255, 0);
        auxScene.pg().fill(255, 255, 0, 160);
        auxScene.drawEye(scene.eye());
        auxScene.pg().popStyle();

        auxScene.endDraw();
        auxScene.pg().endDraw();
        image(auxScene.pg(), auxScene.originCorner().x(), auxScene.originCorner().y());
    }

    private void mainDrawing(Scene s) {
        s.pg().background(0);

        for (PShape shape : shapes) {
            s.pg().shape(shape);
        }
    }

    private void handleMouse() {
        if (mouseY < 360) {
            scene.enableMotionAgent();
            scene.enableKeyboardAgent();
            auxScene.disableMotionAgent();
            auxScene.disableKeyboardAgent();
        } else {
            scene.disableMotionAgent();
            scene.disableKeyboardAgent();
            auxScene.enableMotionAgent();
            auxScene.enableKeyboardAgent();
        }
    }

    public static void main(String[] args) {
        PApplet.main(HiddenSurfaceRemoval.class.getCanonicalName());
    }

    private void culling() {
        Camera camera = scene.camera();
        boolean visible;
        for (PShape shape : shapes) {
            visible = true;

            // TODO: Back-Face Culling
            // scene.camera().isFaceFrontFacing(arg0, arg1);
            visible &= camera.isFaceFrontFacing(cameraToShape(camera, shape), getShapeNormal(shape));

            // TODO: View Frustum Culling
            // scene.camera().boxVisibility(arg0, arg1);
            visible &= scene.camera().boxVisibility(aabbs.get(shape).getP1(), aabbs.get(shape).getP2()) != Eye.Visibility.INVISIBLE;

            shape.setVisible(visible);
        }
    }

    private Vec getShapeNormal(PShape shape) {
        return Scene.toVec(shape.getNormal(0));
    }

    private PVector getShapeCenter(PShape shape) {
        PVector center = new PVector();
        int vertexCount = shape.getVertexCount();
        for (int i = 0; i < vertexCount; i++) {
            center.add(shape.getVertex(i));
        }
        center.div(vertexCount);
        return center;
    }

    private Vec cameraToShape(Camera camera, PShape shape) {
        Vec v;
//        v = Scene.toVec(getShapeCenter(shape));
        v = Scene.toVec(shape.getVertex(0));
        v.subtract(camera.position());
        return v;
    }
}

class AABB {
    private Vec p1;
    private Vec p2;

    public AABB(Vec p1, Vec p2) {
        this.p1 = p1;
        this.p2 = p2;
        fixup();
    }

    public AABB(PVector p1, PVector p2) {
        this.p1 = Scene.toVec(p1);
        this.p2 = Scene.toVec(p2);
        fixup();
    }

    public AABB(float x1, float y1, float z1, float x2, float y2, float z2) {
        p1 = new Vec(x1, y1, z1);
        p2 = new Vec(x2, y2, z2);
        fixup();
    }

    private void fixup() {
        p1.set(PApplet.min(p1.x(), p2.x()), PApplet.min(p1.y(), p2.y()), PApplet.min(p1.z(), p2.z()));
        p2.set(PApplet.max(p1.x(), p2.x()), PApplet.max(p1.y(), p2.y()), PApplet.max(p1.z(), p2.z()));
    }

    public Vec getP1() {
        return p1;
    }

    public void setP1(Vec p1) {
        this.p1 = p1;
    }

    public Vec getP2() {
        return p2;
    }

    public void setP2(Vec p2) {
        this.p2 = p2;
    }

    public Vec getVector() {
        Vec v = new Vec();
        v.set(p2);
        v.subtract(p1);
        return v;
    }

    public boolean collide(AABB other) {
        if (other.p1.x() > this.p2.x() || this.p1.x() > other.p2.x())
            return false;
        if (other.p1.y() > this.p2.y() || this.p1.y() > other.p2.y())
            return false;
        if (other.p1.z() > this.p2.z() || this.p1.z() > other.p2.z())
            return false;
        return true;
    }
}
