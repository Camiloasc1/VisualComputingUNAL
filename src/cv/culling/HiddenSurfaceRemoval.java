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
    private Collection<PShape> shapes;
    private Map<PShape, AABB> aabbs;
    private Octree octree;

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
        octree = new Octree(1024, 5); // Ends in 32x32.
        for (AABB aabb : aabbs.values()) {
            octree.addAABB(aabb);
        }
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
            aabbs.put(edge, new AABB(edge.getVertex(0), edge.getVertex(2), edge));
        }
    }

    public void draw() {
        handleMouse();
        surface.setTitle("Frames: " + frameRate);

        cull();

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

    private void cull() {
        boolean useOctree = false;

        Camera camera = scene.camera();

        if (useOctree) {
            for (PShape shape : shapes) {
                shape.setVisible(false);
            }
            octree.cull(camera);
        }

        for (PShape shape : shapes) {
            if (useOctree && !shape.isVisible())
                continue;
            shape.setVisible(isShapeVisible(camera, shape));
        }
    }

    private boolean isShapeVisible(Camera camera, PShape shape) {
        // TODO: Back-Face Culling
        // scene.camera().isFaceFrontFacing(arg0, arg1);
        if (camera.isFaceBackFacing(cameraToShape(camera, shape), getShapeNormal(shape)))
            return false;

        // TODO: View Frustum Culling
        // scene.camera().boxVisibility(arg0, arg1);
        if (scene.camera().boxVisibility(aabbs.get(shape).getP1(), aabbs.get(shape).getP2()) == Eye.Visibility.INVISIBLE)
            return false;

        return true;
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
//        v.subtract(camera.position()); Not needed!!!
        return v;
    }
}

class AABB {
    private Vec p1;
    private Vec p2;
    private PShape shape;

    public AABB(Vec p1, Vec p2) {
        this(p1, p2, null);
    }

    public AABB(Vec p1, Vec p2, PShape shape) {
        this.p1 = p1;
        this.p2 = p2;
        this.shape = shape;
        fixup();
    }

    public AABB(PVector p1, PVector p2) {
        this(p1, p2, null);
    }

    public AABB(PVector p1, PVector p2, PShape shape) {
        this.p1 = Scene.toVec(p1);
        this.p2 = Scene.toVec(p2);
        this.shape = shape;
        fixup();
    }

    private void fixup() {
        Vec a = new Vec();
        Vec b = new Vec();
        a.set(PApplet.min(p1.x(), p2.x()), PApplet.min(p1.y(), p2.y()), PApplet.min(p1.z(), p2.z()));
        b.set(PApplet.max(p1.x(), p2.x()), PApplet.max(p1.y(), p2.y()), PApplet.max(p1.z(), p2.z()));
        p1 = a;
        p2 = b;
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

    public PShape getShape() {
        return shape;
    }

    public void setShape(PShape shape) {
        this.shape = shape;
    }

    public Vec getVector() {
        Vec v = new Vec();
        v.set(p2);
        v.subtract(p1);
        return v;
    }

    public Vec getCenter() {
        Vec v = new Vec();
        v.add(p1);
        v.add(p2);
        v.divide(2);
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

    public boolean isVisibleOnCamera(Camera camera) {
        return camera.boxVisibility(p1, p2) != Eye.Visibility.INVISIBLE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AABB)) return false;

        AABB aabb = (AABB) o;

        if (p1 != null ? !p1.equals(aabb.p1) : aabb.p1 != null) return false;
        return p2 != null ? p2.equals(aabb.p2) : aabb.p2 == null;

    }

    @Override
    public int hashCode() {
        int result = p1 != null ? p1.hashCode() : 0;
        result = 31 * result + (p2 != null ? p2.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AABB{" +
                "p1=" + p1 +
                ", p2=" + p2 +
                ", shape=" + shape +
                '}';
    }
}

class Octree {
    private AABB root;
    private Map<AABB, Collection<AABB>> children;
    private Map<AABB, Collection<AABB>> shapes;

    public Octree(float size, int levels) {
        root = new AABB(
                new Vec(-size, -size, -size),
                new Vec(size, size, size),
                null);
        children = new HashMap<>();
        shapes = new HashMap<>();

        subdivide(root, ++levels);
    }

    private void subdivide(AABB node, int levels) {
        Vec center = node.getCenter();
        Collection<AABB> subnodes = new ArrayList<>(8);
        {
            subnodes.add(new AABB(center, node.getP2()));
            subnodes.add(new AABB(center, new Vec(node.getP2().x(), node.getP1().y(), node.getP2().z())));
            subnodes.add(new AABB(center, new Vec(node.getP1().x(), node.getP1().y(), node.getP2().z())));
            subnodes.add(new AABB(center, new Vec(node.getP1().x(), node.getP2().y(), node.getP2().z())));

            subnodes.add(new AABB(center, node.getP1()));
            subnodes.add(new AABB(center, new Vec(node.getP1().x(), node.getP2().y(), node.getP1().z())));
            subnodes.add(new AABB(center, new Vec(node.getP2().x(), node.getP2().y(), node.getP1().z())));
            subnodes.add(new AABB(center, new Vec(node.getP2().x(), node.getP1().y(), node.getP1().z())));
        }
        children.put(node, subnodes);
        if (--levels > 0)
            for (AABB child : subnodes) {
                subdivide(child, levels);
            }
    }

    public void addAABB(AABB box) {
        if (root.collide(box))
            addAABB(root, box);
    }

    private void addAABB(AABB node, AABB box) {
        for (AABB child : children.get(node)) {
            if (child.collide(box))
                if (isLeaf(child))
                    addShapeToLeaf(child, box);
                else
                    addAABB(child, box);
        }
    }

    public void cull(Camera camera) {
        if (root.isVisibleOnCamera(camera))
            cull(camera, root);
    }

    private void cull(Camera camera, AABB node) {
        for (AABB child : children.get(node)) {
            if (child.isVisibleOnCamera(camera))
                if (isLeaf(child))
                    cullLeaf(child);
                else
                    cull(camera, child);
        }
    }

    private boolean isLeaf(AABB node) {
        Collection<AABB> shapes = children.get(node);
        return shapes == null || shapes.isEmpty();
    }

    private void addShapeToLeaf(AABB node, AABB box) {
        shapes.putIfAbsent(node, new LinkedList<>());
        shapes.get(node).add(box);
    }

    private void cullLeaf(AABB node) {
        Collection<AABB> elements = shapes.get(node);
        if (elements == null)
            return;
        for (AABB aabb : elements) {
            aabb.getShape().setVisible(true);
        }
    }
}
