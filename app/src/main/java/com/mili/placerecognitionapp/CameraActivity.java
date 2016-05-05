package com.mili.placerecognitionapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.Window;
import android.view.WindowManager;

import com.mili.placerecognitionapp.filters.Filter;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.io.IOException;
import java.util.List;

public class CameraActivity extends ActionBarActivity
        implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = "CameraActivity";
    private static final int FRAME_NUM = 200;

    // Keys for storing.
    private static final String STATE_IMAGE_SIZE_INDEX = "imageSizeIndex";
    private static final String STATE_RECOGNITION_FILTER_INDEX = "recognitionFilterIndex";
    private static int frame_index = 0;
    // An ID for items in the image size submenu.
    private static final int MENU_GROUP_ID_SIZE = 2;

    // The camera view.
    private CameraBridgeViewBase mCameraView;

    // The filter
    private Filter[] mRecognitionFilters;
    // The indices of the active filter.
    private int mRecognitionFilterIndex;

    // The image sizes supported by the active camera.
    private List<Camera.Size> mSupportedImageSizes;
    // The index of the active image size.
    private int mImageSizeIndex;

    // Whether an asynchronous menu action is in progress.
    // If so, menu interaction should be disabled.
    private boolean mIsMenuLocked;

    // The OpenCV loader callback.
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(final int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    Log.d(TAG, "OpenCV loaded successfully");
                    mCameraView.enableView();
                    //mCameraView.enableFpsMeter();

                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        final Window window = getWindow();
        window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (savedInstanceState != null) {
            mImageSizeIndex = savedInstanceState.getInt(STATE_IMAGE_SIZE_INDEX, 0);
            mRecognitionFilterIndex = savedInstanceState.getInt(STATE_RECOGNITION_FILTER_INDEX, 0);
        } else {
            mImageSizeIndex = 0;
            mRecognitionFilterIndex = 0;
        }

        final Camera camera;
        camera = Camera.open();
        final Camera.Parameters parameters = camera.getParameters();
        camera.release();
        mSupportedImageSizes = parameters.getSupportedPreviewSizes();
        final Camera.Size size = mSupportedImageSizes.get(mImageSizeIndex);

        mCameraView = new JavaCameraView(this, 0);
        mCameraView.setMaxFrameSize(size.width, size.height);
        mCameraView.setCvCameraViewListener(this);
        setContentView(mCameraView);
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.activity_camera, menu);

        int numSupportedImageSizes = mSupportedImageSizes.size();
        if (numSupportedImageSizes > 1) {
            final SubMenu sizeSubMenu = menu.addSubMenu(
                    R.string.menu_image_size);
            for (int i = 0; i < numSupportedImageSizes; i++) {
                final Camera.Size size = mSupportedImageSizes.get(i);
                sizeSubMenu.add(MENU_GROUP_ID_SIZE, i, Menu.NONE,
                        String.format("%dx%d", size.width,
                                size.height));
            }
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the current image size index.
        savedInstanceState.putInt(STATE_IMAGE_SIZE_INDEX, mImageSizeIndex);
        // Save the current recognition filter index.
        savedInstanceState.putInt(STATE_RECOGNITION_FILTER_INDEX, mRecognitionFilterIndex);
        super.onSaveInstanceState(savedInstanceState);
    }

    // Suppress backward incompatibility errors because we provide
    // backward-compatible fallbacks.
    @SuppressLint("NewApi")
    @Override
    public void recreate() {
        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.HONEYCOMB) {
            super.recreate();
        } else {
            finish();
            startActivity(getIntent());
        }
    }

    @Override
    public void onPause() {
        if (mCameraView != null) {
            mCameraView.disableView();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0,
                this, mLoaderCallback);
        mIsMenuLocked = false;
    }

    @Override
    public void onDestroy() {
        if (mCameraView != null) {
            mCameraView.disableView();
        }
        super.onDestroy();
    }

    // Suppress backward incompatibility errors because we provide
    // backward-compatible fallbacks (for recreate).
    @SuppressLint("NewApi")
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (mIsMenuLocked) {
            return true;
        }
        if (item.getGroupId() == MENU_GROUP_ID_SIZE) {
            mImageSizeIndex = item.getItemId();
            recreate();

            return true;
        }
        switch (item.getItemId()) {
            case R.id.menu_sift:
                Log.d(TAG, "SIFT clicked");
                return true;
            case R.id.menu_surf:
                Log.d(TAG, "SURF clicked");
                return true;
            case R.id.menu_orb:
                Log.d(TAG, "ORB clicked");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCameraViewStarted(final int width,
                                    final int height) {
    }

    @Override
    public void onCameraViewStopped() {
    }

    @Override
    public Mat onCameraFrame(final CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        final Mat rgba = inputFrame.rgba();

        // Apply the active filters.
        if (mRecognitionFilters != null) {
            mRecognitionFilters[mRecognitionFilterIndex].apply(
                    rgba, rgba);
        }
        

        return rgba;
    }

}
