package com.mili.placerecognitionapp.filters;

import android.content.Context;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;

import java.io.IOException;
import java.util.List;

/**
 * Created by mili on 5/5/16.
 */
public class RecognitionFilter implements Filter {
    // The reference descriptors.
    private List<Mat> mReferenceDescriptors;


    // Features of the scene (the current frame).
    private final MatOfKeyPoint mSceneKeypoints =
            new MatOfKeyPoint();
    // Descriptors of the scene's features.
    private final Mat mSceneDescriptors = new Mat();

    // A grayscale version of the scene.
    private final Mat mGraySrc = new Mat();

    // Tentative matches of scene features and reference features.
    private final MatOfDMatch mMatches = new MatOfDMatch();

    // A feature detector, which finds features in images.
    private FeatureDetector mFeatureDetector;
    // A descriptor extractor, which creates descriptors of features.
    private  DescriptorExtractor mDescriptorExtractor;
    // A descriptor matcher, which matches features based on their descriptors.
    private DescriptorMatcher mDescriptorMatcher;

    // The colors.
    private final Scalar mGreenColor = new Scalar(0, 255, 0);
    private final Scalar mBlueColor = new Scalar(255, 0, 0);
    private final Scalar mRedColor = new Scalar(0, 0, 255);


    public RecognitionFilter(final Context context, final int featureMode) throws IOException {
        if (featureMode == 0) {
            //sift

        } else if (featureMode == 1) {
            //surf

        } else if (featureMode == 2) {
            //orb

            // Load the reference orb descriptors


            mFeatureDetector = FeatureDetector.create(FeatureDetector.ORB);
            mDescriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
            mDescriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMINGLUT);

            // Detect the reference features and compute their descriptors.
            mFeatureDetector.detect(referenceImageGray,
                    mReferenceKeypoints);
            mDescriptorExtractor.compute(referenceImageGray,
                    mReferenceKeypoints, mReferenceDescriptors);
        }




    }

    @Override
    public void apply(Mat src, Mat dst) {

    }
}
