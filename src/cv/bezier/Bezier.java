package cv.bezier;

import processing.core.PApplet;
import processing.core.PVector;

public class Bezier extends PApplet {

    private static int WIDTH = 800;
    private static int HEIGHT = 600;

    public void settings() {
        size(WIDTH, HEIGHT, P2D);
    }

    public static void main(String[] args) {
        PApplet.main(Bezier.class.getName());
    }

    public void setup() {
        super.setup();
        loop();
    }

    public void draw() {
        background(0);
        Line l1 = new Line(0, HEIGHT / 2, 0, HEIGHT / 4);
        Line l2 = new Line(WIDTH, HEIGHT / 2, WIDTH, HEIGHT * 3f / 4f);
//        l1.drawLine();
//        l2.drawLine();

        drawCurve(l1, l2, 0, -HEIGHT / 4);
        drawCurve(l1, l2, 0, 0);
        drawCurve(l1, l2, 0, HEIGHT / 4);
    }

    private void drawCurve(Line l1, Line l2, int x, int y) {
        pushMatrix();
        {
            translate(x, y);
            l1.drawBezier(l2);
        }
        popMatrix();
    }

    class Line {
        public PVector start;
        public PVector end;

        public Line(float x1, float y1, float x2, float y2) {
            start = new PVector(x1, y1);
            end = new PVector(x2, y2);
        }

        public void drawLine() {
            pushStyle();
            {
                stroke(255);
                fill(255);
                line(start.x, start.y, end.x, end.y);
            }
            popStyle();
        }

        public void drawBezier(Line l2) {
            pushStyle();
            {
                stroke(255);
                noFill();
                bezier(start.x, start.y, end.x, end.y, l2.end.x, l2.end.y, l2.start.x, l2.start.y);
            }
            popStyle();
        }
    }
}
