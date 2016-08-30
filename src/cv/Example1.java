package cv;

import processing.core.PApplet;

public class Example1 extends PApplet {
    private static int WIDTH = 400;
    private static int HEIGHT = 400;

    int move;

    public void settings() {
        size(WIDTH, HEIGHT);
    }

    public static void main(String[] args) {
        PApplet.main(Example1.class.getName());
    }

    public void setup() {
        super.setup();
        loop();
    }

    public void draw() {
//        draw2();
        draw3();
    }

    public void draw2() {
        background(255, 255, 255);

        fill(255, 0, 0);
        rect(move, 0, 20, 20);
        move = (move + 10) % WIDTH;

        stroke(255, 255, 255);

        fill(255, 0, 0);
        rect(0, 0, 10, 10);

        fill(0, 255, 0);
        rect(10, 0, 10, 10);

        fill(0, 0, 255);
        rect(20, 0, 10, 10);

//        colorMode(HSB, 255);
        for (int i = 0; i < 255; i++) {
            for (int j = 0; j < 255; j++) {
                stroke(i, 0, 0, j);
                point(i, j + 10);
            }
        }
    }

    public void draw3() {
        background(255, 255, 255);
        stroke(255, 255, 255);
        fill(0, 0, 255, 128);

        translate(50, 50);
        ellipse(50, 0, 50, 30);
        pushMatrix();
        pushStyle();
        fill(255, 0, 0, 128);
        rotate(PI / 4);
        rect(0, 0, 100, 100);
        popStyle();
        popMatrix();
        scale(0.5f, 2);
        ellipse(50, 0, 50, 30);
    }
}