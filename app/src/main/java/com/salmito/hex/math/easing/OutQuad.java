package com.salmito.hex.math.easing;

/**
 * Created by tiago on 9/16/2015.
 */
public class OutQuad implements EasingFunction {
    @Override
    public float f(float t) {
        return t * (2 - t);
    }
}
