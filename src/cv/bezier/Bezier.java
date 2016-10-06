package cv.bezier;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import processing.core.PVector;
import processing.event.MouseEvent;

public class Bezier extends PApplet {

	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;
	private static final int FLAGS = 3;
	private static final int MODE = 1;

	private Flag[] flags;
	private float rotation = PI / 4;
	private float offset = WIDTH / 2;

	public void settings() {
		size(WIDTH, HEIGHT, P3D);
	}

	public static void main(String[] args) {
		PApplet.main(Bezier.class.getName());
	}

	public void setup() {
		super.setup();
		loop();

		flags = new Flag[FLAGS];
		int width = WIDTH / FLAGS;
		for (int i = 0; i < FLAGS; i++) {
			// Line l1 = new Line(width * i, HEIGHT / 2, width * i, i % 2 == 0 ?
			// HEIGHT / 4 : HEIGHT * 3f / 4f);
			// Line l2 = new Line(width * (i + 1), HEIGHT / 2, width * (i + 1),
			// i % 2 == 1 ? HEIGHT / 4 : HEIGHT * 3f / 4f);
			Line l1 = new Line(width * i, HEIGHT / 2, width * (i + 0.5f), HEIGHT / 2);
			Line l2 = new Line(width * (i + 1), HEIGHT / 2, width * (i + 0.5f), HEIGHT / 2);
			if (i % 2 == 0 || MODE == 0)
				flags[i] = new Flag(l1, l2);
			else
				flags[i] = new Flag(l2, l1);
		}
	}

	public void draw() {
		background(0);
		translate(offset, 0, 0);
		rotateY(rotation);
		for (int i = 0; i < FLAGS; i++) {
			flags[i].draw();
		}
	}

	@Override
	public void mouseWheel(MouseEvent event) {
		rotation += -PI / 20 * event.getCount();
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		int button = event.getButton();
		switch (button) {
		case 37: {
			offset += WIDTH / 20;
			break;
		}
		case 39: {
			offset -= WIDTH / 20;
			break;
		}
		}
	}

	class Line {
		public PVector start;
		public PVector end;

		public Line(float x1, float y1, float x2, float y2) {
			start = new PVector(x1, y1, 0);
			end = new PVector(x2, y2, 0);
		}

		public void offset(float x, float y) {
			PVector offset = new PVector(x, y);
			start.add(offset);
			end.add(offset);
		}

		public void drawLine() {
			pushStyle();
			{
				stroke(255);
				fill(255);
				line(start.x, start.y, start.z, end.x, end.y, end.z);
			}
			popStyle();
		}
	}

	class BezierCurve {
		public Line l1;
		public Line l2;

		public BezierCurve(Line l1, Line l2) {
			super();
			this.l1 = l1;
			this.l2 = l2;
		}

		public void offset(float x, float y) {
			l1.offset(x, y);
			l2.offset(x, y);
		}

		public void drawBezier() {
			pushStyle();
			{
				stroke(255);
				noFill();
				bezier(l1.start.x, l1.start.y, l1.end.x, l1.end.y, l2.end.x, l2.end.y, l2.start.x, l2.start.y);
			}
			popStyle();
		}

		public PVector[] bezierPoints(int steps) {
			PVector[] points = new PVector[steps];
			float t;
			for (int i = 0; i < steps; i++) {
				t = (float) i / (steps - 1);
				points[i] = new PVector(bezierPoint(l1.start.x, l1.end.x, l2.end.x, l2.start.x, t),
						bezierPoint(l1.start.y, l1.end.y, l2.end.y, l2.start.y, t),
						bezierPoint(l1.start.z, l1.end.z, l2.end.z, l2.start.z, t));
			}
			return points;
		}
	}

	class Flag {
		private static final int STEPS = 1000;
		private static final int PERIOD = MODE == 0 ? 16 : 8;
		private static final int AMPLITUDE = MODE == 0 ? 16 : 8;

		private Line l1;
		private Line l2;
		private BezierCurve curve;
		private PVector[][] points;
		private int wave;

		public Flag(Line l1, Line l2) {
			super();
			this.l1 = l1;
			this.l2 = l2;
			curve = new BezierCurve(l1, l2);
			points = new PVector[3][STEPS];
			calcPoints();
		}

		private void calcPoints() {
			points[0] = getPoints(0, -HEIGHT / 4, STEPS);
			points[1] = getPoints(0, 0, STEPS);
			points[2] = getPoints(0, HEIGHT / 4, STEPS);
		}

		public void draw() {
			wave = ++wave % PERIOD;
			l1.end.z += wave >= PERIOD / 2 ? -AMPLITUDE : AMPLITUDE;
			l2.end.z += wave >= PERIOD / 2 ? AMPLITUDE : -AMPLITUDE;
			if (MODE == 1) {
				l1.start.z += wave >= PERIOD / 2 ? -AMPLITUDE : AMPLITUDE;
				l2.start.z += wave >= PERIOD / 2 ? AMPLITUDE : -AMPLITUDE;
			}
			calcPoints();
			for (int i = 1; i < STEPS; i++) {
				stroke(255, 0, 0);
				fill(255, 0, 0);
				drawShape(points[0][i - 1], points[1][i]);
				stroke(0, 255, 0);
				fill(0, 255, 0);
				drawShape(points[1][i - 1], points[2][i]);
			}
		}

		private void drawCurve(int x, int y) {
			curve.offset(x, y);
			curve.drawBezier();
			curve.offset(-x, -y);
		}

		private void drawShape(PVector start, PVector end) {
			beginShape();
			vertex(start.x, start.y, start.z);
			vertex(start.x, end.y, start.z);
			vertex(end.x, end.y, end.z);

			vertex(start.x, end.y, start.z);
			vertex(end.x, end.y, end.z);
			vertex(end.x, start.y, end.z);
			endShape();
		}

		private PVector[] getPoints(int x, int y, int steps) {
			PVector[] points;
			curve.offset(x, y);
			points = curve.bezierPoints(steps);
			curve.offset(-x, -y);
			return points;
		}
	}
}
