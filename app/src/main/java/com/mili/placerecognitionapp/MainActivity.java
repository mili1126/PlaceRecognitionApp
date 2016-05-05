package com.mili.placerecognitionapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    /************Permission************/
    private static final String[] INITIAL_PERMS = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int INITIAL_REQUEST = 0;

    private boolean permissionAllGranted() {
        for (String perm : INITIAL_PERMS) {
            if (!hasPermission(perm)) return false;
        }
        return true;
    }

    private boolean hasPermission(String perm) {
        return (PackageManager.PERMISSION_GRANTED ==
                ContextCompat.checkSelfPermission(this, perm));
    }
    /***********Permission*************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Check and ask permission
        if (!permissionAllGranted()) {
            Log.d(TAG, "request for permissions");
            ActivityCompat.requestPermissions(this, INITIAL_PERMS, INITIAL_REQUEST);
        }
    }
}
