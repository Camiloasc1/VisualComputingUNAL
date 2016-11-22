package cv.painter;

import processing.core.PShape;
import remixlab.dandelion.core.Camera;

import java.util.Comparator;

class zComparator implements Comparator<PShape> {

    private Camera camera;

    public zComparator(Camera camera) {
        super();
        this.camera = camera;
    }

    @Override
    public int compare(PShape s1, PShape s2) {
        return Float.compare(maxZ(s1), maxZ(s2));
    }

    public float minZ(PShape s) {
        return Util.minZ(s, v -> Util.cameraEyeCoordinatesOf(camera, v));
    }

    public float maxZ(PShape s) {
        return Util.maxZ(s, v -> Util.cameraEyeCoordinatesOf(camera, v));
    }
}