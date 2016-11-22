package cv.painter;

import processing.core.PShape;
import processing.core.PVector;
import remixlab.dandelion.core.Camera;

import java.util.Arrays;
import java.util.Comparator;

public class PainterComparator implements Comparator<PShape> {

    private Camera camera;

    public PainterComparator(Camera camera) {
        super();
        this.camera = camera;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    @Override
    public int compare(PShape s1, PShape s2) {
        // 1
        if (overlap(s1, s2)) {
            // 2
            if (behind(s1, s2)) {
                return -1;
            }
            if (behind(s2, s1)) {
                return 1;
            }
            // 3
            if (ahead(s1, s2)) {
                return 1;
            }
            if (ahead(s2, s1)) {
                return -1;
            }
//            System.out.println("failed " + s1.getName() + " - " + s2.getName());
        }
        return 0;
    }

    private boolean overlap(PShape s1, PShape s2) {
        if (minX(s2) > maxX(s1) || minX(s1) > maxX(s2))
            return false;
        if (minY(s2) > maxY(s1) || minY(s1) > maxY(s2))
            return false;
        return true;
    }

    private boolean behind(PShape s1, PShape s2) {
        return minZ(s2) > maxZ(s1);
    }

    private boolean ahead(PShape s1, PShape s2) {
//        System.out.println(s1.getName());
//        System.out.println(s2.getName());
        PVector v = Util.cameraEyeCoordinatesOf(camera, s2.getVertex(0));
        PVector n = Util.cameraEyeCoordinatesOf(camera, s2.getNormal(0).add(Util.toPVector(camera.position())));
//        System.out.println(s2.getNormal(0));
//        System.out.println(n);
//        System.out.println(n.mag());
//        System.out.println(n.normalize());
        if (n.z < 0)
            n = n.mult(-1);
//        System.out.println(n);

        PVector[] p = new PVector[s1.getVertexCount()];
        for (int i = 0; i < p.length; i++) {
            p[i] = Util.cameraEyeCoordinatesOf(camera, s1.getVertex(i)).sub(v);
        }

        for (float angle : anglesFromNormal(n, p)) {
            if (angle > 90.1) {
                return false;
            }
        }
        return true;
    }

    private float[] anglesFromNormal(PVector n, PVector[] p) {
        float[] angles = new float[p.length];
        for (int i = 0; i < p.length; i++) {
            if (Float.compare(p[i].mag(), 0) == 0) {
                angles[i] = 90;
                continue;
            }
            angles[i] = (float) Math.toDegrees(PVector.angleBetween(n, p[i]));
        }
//        System.out.println(Arrays.toString(angles));
        return angles;
    }

    public float minX(PShape s) {
        return Util.minX(s, v -> Util.cameraEyeCoordinatesOf(camera, v));
    }

    private float maxX(PShape s) {
        return Util.maxX(s, v -> Util.cameraEyeCoordinatesOf(camera, v));
    }

    public float minY(PShape s) {
        return Util.minY(s, v -> Util.cameraEyeCoordinatesOf(camera, v));
    }

    public float maxY(PShape s) {
        return Util.maxY(s, v -> Util.cameraEyeCoordinatesOf(camera, v));
    }

    public float minZ(PShape s) {
        return Util.minZ(s, v -> Util.cameraEyeCoordinatesOf(camera, v));
    }

    public float maxZ(PShape s) {
        return Util.maxZ(s, v -> Util.cameraEyeCoordinatesOf(camera, v));
    }
}