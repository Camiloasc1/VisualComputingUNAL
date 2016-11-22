package cv.painter;

import processing.core.PShape;
import processing.core.PVector;
import remixlab.dandelion.core.Camera;
import remixlab.dandelion.geom.Vec;

import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Util {
    public enum COORDINATE {X, Y, Z}

    public enum OBJETIVE {MIN, MAX}

    private static Stream<PVector> getVertexStream(PShape shape) {
        return IntStream.range(0, shape.getVertexCount()).mapToObj(i -> shape.getVertex(i));
    }

    private static DoubleStream getValueStream(Stream<PVector> vectorStream, ToDoubleFunction<? super PVector> function) {
        return vectorStream.mapToDouble(function);
    }

    private static DoubleStream getCoordinateStream(PShape shape, COORDINATE coordinate, Function<? super PVector, ? extends PVector> trasform) {
        Stream<PVector> vertexStream = getVertexStream(shape).map(trasform);
        switch (coordinate) {
            case X:
                return getValueStream(vertexStream, v -> v.x);
            case Y:
                return getValueStream(vertexStream, v -> v.y);
            case Z:
                return getValueStream(vertexStream, v -> v.z);
        }
        return null;
    }

    private static double getCoordinateObjetive(PShape shape, COORDINATE coordinate, Function<? super PVector, ? extends PVector> transform, OBJETIVE objetive) {
        DoubleStream coordinateStream = Util.getCoordinateStream(shape, coordinate, transform);
        switch (objetive) {
            case MIN:
                return coordinateStream.min().getAsDouble();
            case MAX:
                return coordinateStream.max().getAsDouble();
        }
        return 0;
    }

    public static float minX(PShape s, Function<? super PVector, ? extends PVector> transform) {
        return (float) Util.getCoordinateObjetive(s, Util.COORDINATE.X, transform, OBJETIVE.MIN);
    }

    public static float maxX(PShape s, Function<? super PVector, ? extends PVector> transform) {
        return (float) Util.getCoordinateObjetive(s, Util.COORDINATE.X, transform, Util.OBJETIVE.MAX);
    }

    public static float minY(PShape s, Function<? super PVector, ? extends PVector> transform) {
        return (float) Util.getCoordinateObjetive(s, Util.COORDINATE.Y, transform, OBJETIVE.MIN);
    }

    public static float maxY(PShape s, Function<? super PVector, ? extends PVector> transform) {
        return (float) Util.getCoordinateObjetive(s, Util.COORDINATE.Y, transform, Util.OBJETIVE.MAX);
    }

    public static float minZ(PShape s, Function<? super PVector, ? extends PVector> transform) {
        return (float) Util.getCoordinateObjetive(s, Util.COORDINATE.Z, transform, OBJETIVE.MIN);
    }

    public static float maxZ(PShape s, Function<? super PVector, ? extends PVector> transform) {
        return (float) Util.getCoordinateObjetive(s, Util.COORDINATE.Z, transform, Util.OBJETIVE.MAX);
    }

    public static Vec toVec(PVector v) {
        return new Vec(v.x, v.y, v.z);
    }

    public static PVector toPVector(Vec v) {
        return new PVector(v.x(), v.y(), v.z());
    }

    public static PVector cameraEyeCoordinatesOf(Camera camera, PVector vector) {
        return toPVector(camera.eyeCoordinatesOf(toVec(vector)));
    }

    public static PVector projectedCoordinatesOf(Camera camera, PVector vector) {
        return toPVector(camera.projectedCoordinatesOf(toVec(vector)));
    }
}
