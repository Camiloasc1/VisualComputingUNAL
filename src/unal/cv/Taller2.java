package unal.cv;


import processing.core.PApplet;
import processing.core.PImage;
import processing.event.MouseEvent;

public class Taller2 extends PApplet {
    private static int WIDTH = 800;
    private static int HEIGHT = 800;
    private static int MAPWIDTH = 250;
    private static int MAPHEIGHT = MAPWIDTH / 2;
    public static final int SCALEFACTOR = 100;
    private static float ZOOMSTEP = 0.1f;
    private static float ROTATIONSTEP = 0.1f;

    PImage map;
    float zoom = 1.0f;
    float rotation = 0.0f;

    public void settings() {
        size(WIDTH, HEIGHT);
    }

    public static void main(String[] args) {
        PApplet.main(Taller2.class.getName());
    }

    public void setup() {
        super.setup();
        map = loadImage("map.jpg");
        loop();
    }

    public void draw() {
        background(GRAY);
        float x = mouseX;
        float y = mouseY;
        if (x < MAPWIDTH && y < MAPHEIGHT) {
            x = -x / MAPWIDTH * map.width;
            y = -y / MAPHEIGHT * map.height;
        } else {
            x = 0;
            y = 0;
        }
        pushMatrix();
        pushStyle();
        {
            translate(WIDTH / 2, WIDTH / 2);
            rotate(rotation);
            scale(zoom);
            translate(x, y);
            image(map, 0, 0);
        }
        popStyle();
        popMatrix();
        image(map, 0, 0, MAPWIDTH, MAPHEIGHT);
        pushMatrix();
        pushStyle();
        {
            stroke(255, 255, 255);
            fill(0, 0, 0, 0);
            translate(mouseX, mouseY);
            rotate(rotation);
            float scale = SCALEFACTOR / zoom;
            rect(-scale / 2, -scale / 2, scale, scale);
        }
        popStyle();
        popMatrix();
    }

    @Override
    public void mouseWheel(MouseEvent event) {
        zoom += -ZOOMSTEP * event.getCount();
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        int button = event.getButton();
        switch (button) {
            case 37: {
                rotation += ROTATIONSTEP;
                break;
            }
            case 39: {
                rotation -= ROTATIONSTEP;
                break;
            }
        }
    }
}