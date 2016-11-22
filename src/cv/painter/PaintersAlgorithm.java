package cv.painter;

import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;
import remixlab.dandelion.core.Camera;
import remixlab.proscene.Scene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PaintersAlgorithm extends PApplet {

    private Random random;
    private Scene scene;
    private PainterComparator painterComparator;
    private zComparator zComparator;
    private List<PShape> shapes;
    private boolean enableZBuffer;

    @Override
    public void settings() {
        size(640, 480, P3D);
    }

    @Override
    public void setup() {
        random = new Random();
        scene = new Scene(this);
        zComparator = new zComparator(scene.camera());
        painterComparator = new PainterComparator(scene.camera());

        shapes = new ArrayList<>();
        int numx = 1, numy = 1, numz = 1;
        for (int i = 0; i < numx; i++) {
            for (int j = 0; j < numy; j++) {
                for (int k = 0; k < numz; k++) {
                    int x = 20 * (i - numx / 2);
                    int y = 20 * (j - numy / 2);
                    int z = 20 * (k - numz / 2);
                    createPyramid(x, y, z);
                }
            }
        }

        enableZBuffer = true;
        surface.setTitle("ENABLE_DEPTH_TEST");
        hint(ENABLE_DEPTH_TEST);

        noStroke();

        scene.camera().setType(Camera.Type.ORTHOGRAPHIC);
    }

    public void createPyramid(int x, int y, int z) {
        PVector p1 = new PVector(x + random.nextInt(20), y + random.nextInt(10), z);
        PVector p2 = new PVector(x + 10 + random.nextInt(10), y + 10 + random.nextInt(10), z);
        PVector p3 = new PVector(x + random.nextInt(10), y + 10 + random.nextInt(10), z);
        PVector p4 = new PVector(x + random.nextInt(20), y + random.nextInt(20), z + 1 + random.nextInt(10));

        PShape face1 = createShape();
        face1.beginShape(TRIANGLE);
        face1.setName("White");
        face1.stroke(255, 255, 255);
        face1.fill(255, 255, 255);
        face1.vertex(p1.x, p1.y, p1.z);
        face1.vertex(p2.x, p2.y, p2.z);
        face1.vertex(p3.x, p3.y, p3.z);
        face1.endShape();
        shapes.add(face1);

        PShape face2 = createShape();
        face2.beginShape(TRIANGLE);
        face2.setName("Red");
        face2.stroke(255, 0, 0);
        face2.fill(255, 0, 0);
        face2.vertex(p1.x, p1.y, p1.z);
        face2.vertex(p2.x, p2.y, p2.z);
        face2.vertex(p4.x, p4.y, p4.z);
        face2.endShape();
        shapes.add(face2);

        PShape face3 = createShape();
        face3.beginShape(TRIANGLE);
        face3.setName("Green");
        face3.stroke(0, 255, 0);
        face3.fill(0, 255, 0);
        face3.vertex(p1.x, p1.y, p1.z);
        face3.vertex(p3.x, p3.y, p3.z);
        face3.vertex(p4.x, p4.y, p4.z);
        face3.endShape();
        shapes.add(face3);

        PShape face4 = createShape();
        face4.beginShape(TRIANGLE);
        face4.setName("Blue");
        face4.stroke(0, 0, 255);
        face4.fill(0, 0, 255);
        face4.vertex(p2.x, p2.y, p2.z);
        face4.vertex(p4.x, p4.y, p4.z);
        face4.vertex(p3.x, p3.y, p3.z);
        face4.endShape();
        shapes.add(face4);
    }

    @Override
    public void draw() {
        background(0);
//        Collections.shuffle(shapes);
        Collections.sort(shapes, zComparator);
        Collections.sort(shapes, painterComparator);

//        StringBuilder str = new StringBuilder();
//        str.append("Draw Order:");
        for (PShape shape : shapes) {
//            str.append(" ").append(shape.getName());
            shape(shape);

//            PVector start = shape.getVertex(0);
//            PVector end = start.copy().add(shape.getNormal(0));
//            stroke(shape.getStroke(0));
//            fill(shape.getFill(0));
//            strokeWeight(10);
//            line(start.x, start.y, start.z, end.x, end.y, end.z);
        }
//        System.out.println(str.toString());
    }

    @Override
    public void keyPressed() {
        enableZBuffer = !enableZBuffer;
        if (enableZBuffer) {
            surface.setTitle("ENABLE_DEPTH_TEST");
            hint(ENABLE_DEPTH_TEST);
        } else {
            surface.setTitle("DISABLE_DEPTH_TEST");
            hint(DISABLE_DEPTH_TEST);
        }
    }

    public static void main(String args[]) {
        PApplet.main(PaintersAlgorithm.class.getName());
    }

}