/*
 * Mathematik
 *
 * Copyright (C) 2012 Patrick Kochlik + Dennis Paul
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * {@link http://www.gnu.org/licenses/lgpl.html}
 *
 */

package cv.bspline;

import java.util.Vector;

import processing.core.PApplet;
import processing.core.PVector;
import remixlab.proscene.InteractiveFrame;
import remixlab.proscene.Scene;

/**
 * b-spline source code from: Tim Lambert 'nice page on curves, splines etc.'
 * http://www.cse.unsw.edu.au/~lambert/splines/
 */

public abstract class BSpline {

	/* the basis function for a cubic B spline */
	private static float b(int i, float t) {
		switch (i) {
		case -2:
			return (((-t + 3) * t - 3) * t + 1) / 6;
		case -1:
			return (((3 * t - 6) * t) * t + 4) / 6;
		case 0:
			return (((-3 * t + 3) * t + 3) * t + 1) / 6;
		case 1:
			return (t * t * t) / 6;
		}
		return 0; // we only get here if an invalid i is specified
	}

	/* evaluate a point on the B spline */
	private static PVector p(Vector<PVector> thePoints, int i, float t) {
		PVector p = new PVector();
		for (int j = -2; j <= 1; j++) {
			p.x += b(j, t) * thePoints.get(i + j).x;
			p.y += b(j, t) * thePoints.get(i + j).y;
			p.z += b(j, t) * thePoints.get(i + j).z;
		}
		return p;
	}

	public static Vector<PVector> curve(Vector<PVector> thePoints, int theSteps, Vector<PVector> theResult) {
		for (int i = 2; i < thePoints.size() - 1; i++) {
			for (int j = 1; j <= theSteps; j++) {
				theResult.add(p(thePoints, i, j / (float) theSteps));
			}
		}
		return theResult;
	}

	public static Vector<PVector> curve(Vector<PVector> thePoints, int theSteps) {
		return curve(thePoints, theSteps, new Vector<PVector>());
	}

	public static Vector<PVector> closeCurve(Vector<PVector> thePoints) {
		/* copy points */
		Vector<PVector> myClosedPoints = new Vector<PVector>();
		for (int i = 0; i < thePoints.size(); i++) {
			myClosedPoints.add(thePoints.get(i));
		}

		/* repeat first three points */
		if (thePoints.size() > 2) {
			myClosedPoints.add(thePoints.get(0));
			myClosedPoints.add(thePoints.get(1));
			myClosedPoints.add(thePoints.get(2));
		}
		return myClosedPoints;
	}

	public static class SketchTestBSpline extends PApplet {

		private Vector<PVector> _myPoints = new Vector<PVector>();

		private InteractiveFrame[] frames = new InteractiveFrame[4];

		public void settings() {
			size(640, 480, P3D);
		}

		public void setup() {
			smooth();
			Scene scene = new Scene(this);
			scene.setVisualHints(scene.PICKING);
			for (int i = 0; i < frames.length; i++) {
				frames[i] = new InteractiveFrame(scene);
				frames[i].setPosition(0, 0);
				frames[i].setMotionBinding(LEFT, "translate");
			}
		}

		// public void keyPressed() {
		// }

		// public void mousePressed() {
		// _myPoints.add(new PVector(mouseX, mouseY));
		// }

		public void draw() {
			background(0);
			_myPoints.clear();
			stroke(255, 0, 0);
			for (int i = 0; i < frames.length; i++) {
				PVector pos = getIFPos(i);
				_myPoints.add(pos);
				if (i == 0)
					line(getIFPos(frames.length - 1).x, getIFPos(frames.length - 1).y, getIFPos(frames.length - 1).z,
							pos.x, pos.y, pos.z);
				else
					line(getIFPos(i - 1).x, getIFPos(i - 1).y, getIFPos(i - 1).z, pos.x, pos.y, pos.z);
			}
			if (!_myPoints.isEmpty()) {
				/* control points */
				stroke(255, 0, 0);
				for (int i = 0; i < _myPoints.size(); i++) {
					cross(_myPoints.get(i), 10);
				}

				/* b-spline */
				Vector<PVector> myClosedPoints = BSpline.closeCurve(_myPoints);
				Vector<PVector> myResult = BSpline.curve(myClosedPoints, 20);

				/* closed */
				stroke(0, 0, 255);
				for (int i = 0; i < myResult.size(); i++) {
					final int myNextID = (i + 1) % myResult.size();
					line(myResult.get(i).x, myResult.get(i).y, myResult.get(i).z, myResult.get(myNextID).x,
							myResult.get(myNextID).y, myResult.get(myNextID).z);
				}
			}
		}

		private PVector getIFPos(int i) {
			return Scene.toPVector(frames[i].position());
		}

		private void cross(PVector p, float theSize) {
			line(p.x - theSize / 2, p.y, p.z, p.x + theSize / 2, p.y, p.z);
			line(p.x, p.y - theSize / 2, p.z, p.x, p.y + theSize / 2, p.z);
		}
	}

	public static void main(String[] args) {
		PApplet.main(new String[] { SketchTestBSpline.class.getName() });
	}
}
