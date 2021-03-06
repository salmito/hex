package com.salmito.hex.engine.things.geometry;

import com.salmito.hex.engine.Thing;
import com.salmito.hex.programs.camera.CameraProgram;

/**
 * Created by Tiago on 16/09/2015.
 */
public class Vector3f implements Thing {

    private final Point3f origin;
    private final Point3f point;

    public Vector3f(Point3f p) {
        this.origin = new Point3f(0f, 0f, 0f);
        this.point = new Point3f(p);
    }

    public float getX() {
        return point.getX()-origin.getX();
    }
    public float getY() {
        return point.getY()-origin.getY();
    }
    public float getZ() {
        return point.getZ()-origin.getZ();
    }

    public Point3f getPoint() {
        return point;
    }

    @Override
    public void draw(long time, CameraProgram program) {

    }

    @Override
    public void clean() {

    }
}
