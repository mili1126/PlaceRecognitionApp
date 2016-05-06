package com.mili.placerecognitionapp.filters;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.util.Log;

import com.mili.placerecognitionapp.MainActivity;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.*;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

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
    private DescriptorExtractor mDescriptorExtractor;
    // A descriptor matcher, which matches features based on their descriptors.
    private DescriptorMatcher mDescriptorMatcher;

    // The colors.
    private final Scalar mGreenColor = new Scalar(0, 255, 0);
    private final Scalar mBlueColor = new Scalar(255, 0, 0);
    private final Scalar mRedColor = new Scalar(0, 0, 255);


    public RecognitionFilter(final Context context, final int featureMode) throws IOException {
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

        for (int i = 1; i < 2; i++) {
            String filePath = DESCRIPTOR_FOLDERS.get(featureMode) + "/" + i + ".yml";

            BufferedReader reader = null;
            try {
                reader = new BufferedReader(
                        new InputStreamReader(mAssets.open(filePath)));

                // do reading, usually loop until end of file reading
                String mLine;
                while ((mLine = reader.readLine()) != null) {
                    //process line
                    if (mLine != null) {
                        Log.d(TAG, mLine);
                        break;
                    }
                }
            } catch (IOException e) {
                Log.d(TAG, "cannot read in the file");
            }


        }

        if (featureMode == 0) {
            //sift

        } else if (featureMode == 1) {
            //surf

        } else if (featureMode == 2) {
            //orb


            mFeatureDetector = FeatureDetector.create(FeatureDetector.ORB);
            mDescriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
            mDescriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMINGLUT);
        }


    }

    @Override
    public void apply(Mat src, Mat dst) {

    }
}
