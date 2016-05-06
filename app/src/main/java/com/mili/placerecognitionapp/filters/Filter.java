package com.mili.placerecognitionapp.filters;

/**
 * Created by mili on 5/5/16.
 */
import org.opencv.core.Mat;

public interface Filter {
    public abstract int apply(final Mat src, final Mat dst);
}

