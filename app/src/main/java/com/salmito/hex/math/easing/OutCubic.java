package com.salmito.hex.math.easing;

/**
 * Created by tiago on 9/16/2015.
 */
public class OutCubic implements EasingFunction {
    @Override
    public float easy(float t) {
        return (--t) * t * t + 1;
    }
}
