package com.mili.placerecognitionapp.filters;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.util.Log;

import com.mili.placerecognitionapp.MainActivity;
import com.mili.placerecognitionapp.R;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.*;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by mili on 5/5/16.
 */
public class RecognitionFilter implements Filter {
    private final static String TAG = "RecognitionFilter";
    private AssetManager mAssets;

    private List<String> DESCRIPTOR_FOLDERS = Arrays.asList(
            "sift",
            "surf",
            "orb"
    );

    // The reference imgaes;
    private List<Mat> mReferenceImgages = new ArrayList<>();

    // The reference image (this detector's target).
    private Mat mReferenceImage;

    // Descriptors of the reference image's features.
    private List<Mat> mReferenceDescriptors = new ArrayList<>();

    // Features of the scene (the current frame).
    private final MatOfKeyPoint mSceneKeypoints = new MatOfKeyPoint();
    // Descriptors of the scene's features.
    private final Mat mSceneDescriptor = new Mat();

    // Tentative matches of scene features and reference features.
    private final MatOfDMatch mMatches = new MatOfDMatch();

    // A feature detector, which finds features in images.
    public final FeatureDetector mFeatureDetector;
    // A descriptor extractor, which creates descriptors of features.
    public final DescriptorExtractor mDescriptorExtractor;
    // A descriptor matcher, which matches features based on their descriptors.
    public final DescriptorMatcher mDescriptorMatcher;

    // The colors.
    private final Scalar mGreenColor = new Scalar(0, 255, 0);
    private final Scalar mBlueColor = new Scalar(255, 0, 0);
    private final Scalar mRedColor = new Scalar(0, 0, 255);


    public RecognitionFilter( Context context, int featureMode) throws IOException {
        // Load the reference orb descriptors
        mAssets = context.getAssets();
        String[] descriptorNames;
        try {
            descriptorNames = mAssets.list(DESCRIPTOR_FOLDERS.get(featureMode));
            Log.i(TAG, "Found " + descriptorNames.length + " files");
        } catch (IOException ioe) {
            Log.e(TAG, "Could not list assets", ioe);
            return;
        }

        // Load the reference image from the app's resources.
        // It is loaded in BGR (blue, green, red) format.
        mReferenceImgages.add(Utils.loadResource(context, R.drawable.frame1, Imgcodecs.CV_LOAD_IMAGE_COLOR));
        mReferenceImgages.add(Utils.loadResource(context, R.drawable.frame2, Imgcodecs.CV_LOAD_IMAGE_COLOR));
        mReferenceImgages.add(Utils.loadResource(context, R.drawable.frame3, Imgcodecs.CV_LOAD_IMAGE_COLOR));
        mReferenceImgages.add(Utils.loadResource(context, R.drawable.frame4, Imgcodecs.CV_LOAD_IMAGE_COLOR));
        mReferenceImgages.add(Utils.loadResource(context, R.drawable.frame5, Imgcodecs.CV_LOAD_IMAGE_COLOR));
        mReferenceImgages.add(Utils.loadResource(context, R.drawable.frame6, Imgcodecs.CV_LOAD_IMAGE_COLOR));
        mReferenceImgages.add(Utils.loadResource(context, R.drawable.frame7, Imgcodecs.CV_LOAD_IMAGE_COLOR));
        mReferenceImgages.add(Utils.loadResource(context, R.drawable.frame8, Imgcodecs.CV_LOAD_IMAGE_COLOR));
        mReferenceImgages.add(Utils.loadResource(context, R.drawable.frame9, Imgcodecs.CV_LOAD_IMAGE_COLOR));
        mReferenceImgages.add(Utils.loadResource(context, R.drawable.frame10, Imgcodecs.CV_LOAD_IMAGE_COLOR));

        if (featureMode == 0) {
            //sift

        } else if (featureMode == 1) {
            //surf

        } else if (featureMode == 2) {
            //orb
            mFeatureDetector = FeatureDetector.create(FeatureDetector.ORB);
            mDescriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
            mDescriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMINGLUT);

            for (int i = 0; i < 10; i++) {
                MatOfKeyPoint mReferenceKeypoints = new MatOfKeyPoint();
                Mat mReferenceDescriptor = new Mat();
                mFeatureDetector.detect(mReferenceImgages.get(i), mReferenceKeypoints);
                mDescriptorExtractor.compute(mReferenceImgages.get(i), mReferenceKeypoints,
                        mReferenceDescriptor);
                mReferenceDescriptors.add(mReferenceDescriptor);
                Log.d(TAG, String.valueOf(mReferenceDescriptor.rows()));
            }
        }


    }

    @Override
    public int apply(Mat src, Mat dst) {
        MatOfKeyPoint mReferenceKeypoints = new MatOfKeyPoint();
        Mat mReferenceDescriptor = new Mat();
        mFeatureDetector.detect(mReferenceImgages.get(7), mReferenceKeypoints);
        mDescriptorExtractor.compute(mReferenceImgages.get(7), mReferenceKeypoints,
                mReferenceDescriptor);
        mReferenceDescriptors.add(mReferenceDescriptor);
        Log.d(TAG, "\n" + String.valueOf(mReferenceDescriptor.rows()));



        // Detect the scene features, compute their descriptors,
        // and match the scene descriptors to reference descriptors.
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2RGBA);
        mFeatureDetector.detect(src, mSceneKeypoints);
        mDescriptorExtractor.compute(src, mSceneKeypoints,
                mSceneDescriptor);

        int matchIndex = -1;
        int matchSize = 0;
        for (int i = 0; i < 10; i++) {

            mDescriptorMatcher.match(mSceneDescriptor,
                    mReferenceDescriptors.get(i), mMatches);
            int matches =mMatches.toList().size();
            if ( matches > matchSize) {
                matchIndex = i;
                matchSize = matches;
            }

        }

        return matchIndex;
    }

}
