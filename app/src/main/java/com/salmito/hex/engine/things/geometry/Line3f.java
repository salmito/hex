package com.salmito.hex.engine.things.geometry;

import android.opengl.GLES20;

import com.salmito.hex.engine.Thing;
import com.salmito.hex.programs.camera.CameraProgram;
import com.salmito.hex.util.Constants;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Tiago on 16/09/2015.
 */
public class Line3f implements Thing {

    private final Point3f p1;
    private final Point3f p2;

    private final FloatBuffer vertices;
    private final ShortBuffer index;
    private final FloatBuffer color;

    public Line3f(Point3f p1, Point3f p2) {
        vertices = ByteBuffer.allocateDirect(6 * Constants.bytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer().put(new float[]{
                p1.getX(), p1.getY(), p1.getZ(),
                p2.getX(), p2.getY(), p2.getZ()
        });
        color = ByteBuffer.allocateDirect(8 * Constants.bytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer().put(new float[]{
                1f, 0f, 0f, 1f,
                0f, 0f, 1f, 1f
        });
        index = ByteBuffer.allocateDirect(2 * Constants.bytesPerShort).order(ByteOrder.nativeOrder()).asShortBuffer().put(new short[]{
                0, 1
        });
        this.p1 = new Point3f(vertices, 0);
        this.p2 = new Point3f(vertices, 3);
    }

    public Point3f interpolate(float t) {
        return interpolate(p1, p2, t);
    }

    /**
     * Interpolate a point along a line
     *
     * @param p1
     * @param p2
     * @param t
     * @return
     */
    public static Point3f interpolate(Point3f p1, Point3f p2, float t) {
        float x = p1.getX() + (p2.getX() - p1.getX()) * t;
        float y = p1.getY() + (p2.getY() - p1.getY()) * t;
        float z = p1.getZ() + (p2.getZ() - p1.getZ()) * t;
        return new Point3f(x, y, z);
    }


    public void setEnd(Point3f end) {
        p2.set(end);
    }

    @Override
    public void draw(long time, CameraProgram p) {
        index.rewind();
        color.rewind();
        vertices.rewind();
        p.drawBuffer(vertices, color, index, GLES20.GL_LINE_STRIP);
    }

    public void setStart(Point3f start) {
        p1.set(start);
    }

    @Override
    public void clean() {

    }
}
