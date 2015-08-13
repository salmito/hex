package com.salmito.hex.programs.hex.entities;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.salmito.hex.engine.Thing;
import com.salmito.hex.main.MainRenderer;
import com.salmito.hex.programs.hex.HexProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Hexagon implements Thing {
    public static final short[] indices = {0, 1, 2, 3, 4, 5, 6, 1};
    public static final ShortBuffer mHexagonIndices = ByteBuffer.allocateDirect(indices.length * MainRenderer.mBytesPerShort).order(ByteOrder.nativeOrder()).asShortBuffer().put(indices);
    public static final short[] indicesWire = {1, 2, 3, 4, 5, 6, 1};
    public static final ShortBuffer mHexagonIndicesWire = ByteBuffer.allocateDirect(indicesWire.length * MainRenderer.mBytesPerShort).order(ByteOrder.nativeOrder()).asShortBuffer().put(indicesWire);
    public static final int mPositionDataSize = 3;
    public static final int mStrideBytes = mPositionDataSize * MainRenderer.mBytesPerFloat;
    public static float radius = 1.0f;
    public static final float xOff = radius * (float) (Math.cos(Math.PI / 6));
    public static final float yOff = radius * (float) (Math.sin(Math.PI / 6));
    private static final float vertices[] = {
            0.0f, 0.0f, 0.0f,    //center
            (float) (radius * Math.cos(2 * Math.PI * (0 + 0.5f) / 6)), (float) (radius * Math.sin(2 * Math.PI * (0 + 0.5f) / 6)), 0.0f,    // top
            (float) (radius * Math.cos(2 * Math.PI * (1 + 0.5f) / 6)), (float) (radius * Math.sin(2 * Math.PI * (1 + 0.5f) / 6)), 0.0f,    // left top
            (float) (radius * Math.cos(2 * Math.PI * (2 + 0.5f) / 6)), (float) (radius * Math.sin(2 * Math.PI * (2 + 0.5f) / 6)), 0.0f,    // left bottom
            (float) (radius * Math.cos(2 * Math.PI * (3 + 0.5f) / 6)), (float) (radius * Math.sin(2 * Math.PI * (3 + 0.5f) / 6)), 0.0f,    // bottom
            (float) (radius * Math.cos(2 * Math.PI * (4 + 0.5f) / 6)), (float) (radius * Math.sin(2 * Math.PI * (4 + 0.5f) / 6)), 0.0f,    // right bottom
            (float) (radius * Math.cos(2 * Math.PI * (5 + 0.5f) / 6)), (float) (radius * Math.sin(2 * Math.PI * (5 + 0.5f) / 6)), 0.0f,    // right top
    };
    public static final FloatBuffer mHexagonVertices = ByteBuffer.allocateDirect(vertices.length * MainRenderer.mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer().put(vertices);
    public static float xF = 0.0f;
    public static float yF = 0.0f;
    private int color = HexColor.WHITE;

    private float rotateAngle = 90.0f;
    private boolean rotate = false;
    private int flipColor = -1;
    private int flipDirection = -1;
    private long lastFlip = 0L;
    private float upX = 1f, upY = 0f, upZ = 0f;

    private int i = 0;
    private HexMap.Coordinates coordinates;
    private HexProgram program;

    public Hexagon(HexProgram program, int r, int q) {
        this.program = program;
        this.coordinates = new HexMap.Coordinates(r, q);
    }

    public Hexagon(int color) {
        this.setColor(color);
    }

    public float getRotateAngle() {
        return rotateAngle;
    }

    public void flip(int color, int direction) {
        flipColor = color;
        rotateAngle = 0.0f;
        lastFlip = SystemClock.uptimeMillis();
        upX = (float) Math.cos(2 * Math.PI * (i++ % 6 + 0.5f) / 6);
        upY = (float) Math.sin(2 * Math.PI * (i++ % 6 + 0.5f) / 6);
        rotate = true;
    }

    public boolean isRotate() {
        return rotate;
    }

    public void setRotate(boolean rotate) {
        this.rotate = rotate;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public void draw(long dt) {
        mHexagonVertices.position(0);
        mHexagonIndices.position(0);
        mHexagonIndicesWire.position(0);
        GLES20.glVertexAttribPointer(program.getAttr("a_Position"), mPositionDataSize, GLES20.GL_FLOAT, false, mStrideBytes, mHexagonVertices);
        GLES20.glEnableVertexAttribArray(program.getAttr("a_Position"));

        float xf = xOff * coordinates.getQ() * 2;
        final float yf = yOff * coordinates.getR() * 3;
        if ((coordinates.getR() & 1) != 0)    // if the current line is not even
            xf += xOff;     // extra offset of half the width on xPos axis

        //if(xPos<=2 && yPos<=2) System.out.println("Hexagon ("+xPos+","+yPos+") "+"Center xPos="+xf+" yPos="+yf);


        Matrix.setIdentityM(program.getmModelMatrix(), 0);
        Matrix.translateM(program.getmModelMatrix(), 0, xf, yf, 0.0f);

        if (rotate) {
            long time = SystemClock.uptimeMillis() - lastFlip;
            if (time > 500L || rotateAngle > 360f) {
                rotate = false;
                rotateAngle = 0f;
            } else {
                rotateAngle += (360f * time) / 500f;
                if (rotateAngle >= 180f && flipColor >= 0) {
                    color = flipColor;
                    flipColor = -1;
                }
                Matrix.rotateM(program.getmModelMatrix(), 0, rotateAngle, upX, upY, upZ);
            }
        }
        HexColor.setColor(color);

        GLES20.glVertexAttribPointer(program.getAttr("a_Color"), HexColor.mColorDataSize, GLES20.GL_FLOAT, false, HexColor.mColorStrideBytes, HexColor.mHexagonColors);
        GLES20.glEnableVertexAttribArray(program.getAttr("a_Color"));

        Matrix.multiplyMM(program.getmMVPMatrix(), 0, program.getmViewMatrix(), 0, program.getmModelMatrix(), 0);
        Matrix.multiplyMM(program.getmMVPMatrix(), 0, program.getmProjectionMatrix(), 0, program.getmMVPMatrix(), 0);
        GLES20.glUniformMatrix4fv(program.getUniform("u_MVPMatrix"), 1, false, program.getmMVPMatrix(), 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLE_FAN, indices.length, GLES20.GL_UNSIGNED_SHORT, mHexagonIndices);

        HexColor.setColor(HexColor.WHITE);
        GLES20.glVertexAttribPointer(program.getAttr("a_Color"), HexColor.mColorDataSize, GLES20.GL_FLOAT, false, HexColor.mColorStrideBytes, HexColor.mHexagonColors);
        GLES20.glEnableVertexAttribArray(program.getAttr("a_Color"));
        GLES20.glDrawElements(GLES20.GL_LINE_LOOP, indicesWire.length, GLES20.GL_UNSIGNED_SHORT, mHexagonIndicesWire);
    }

    @Override
    public void clean() {

    }
}