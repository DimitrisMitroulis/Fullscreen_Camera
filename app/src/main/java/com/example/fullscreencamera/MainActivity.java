package com.example.fullscreencamera;

import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.core.VideoCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    private static final String TAG = "";
    //created by Dimitris mi
    //heavily inspired by https://www.youtube.com/watch?v=8ZD6_SDsKWI&ab_channel=NextGEN

    private final int REQUEST_CODE_PERMISSIONS = 101;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA"};

    private ImageCapture imageCapture;
    private VideoCapture videoCapture;

    TextureView textureView;
    Camera camera;
    FrameLayout frameLayout;
    ShowCamera showCamera;
    PreviewView preview_View;



    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        preview_View = (PreviewView) findViewById(R.id.preview_view);



        cameraProviderFuture = ProcessCameraProvider.getInstance(this);


        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCameraX(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        },getExecutor());







//        if (allPermissionGranted()) {
//            startCameraX();
//        } else
//            Log.d(TAG, "onCreate: ");
//            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);

    }


    private void startCamera(){
        camera = Camera.open();
        showCamera = new ShowCamera(this,camera);
        frameLayout.addView(showCamera);


    }


    private void startCameraX(ProcessCameraProvider cameraProvider) {
        Log.d(TAG, "here: ");
        cameraProvider.unbindAll();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        Preview preview = new Preview.Builder()
                .build();
        preview.setSurfaceProvider(preview_View.getSurfaceProvider());


        //bind to lifecycle:
        cameraProvider.bindToLifecycle((LifecycleOwner) this,cameraSelector ,preview);
    }


    private void updateTransform() {
        Matrix mx = new Matrix();
        float w = textureView.getMeasuredWidth();
        float h =textureView.getMeasuredHeight();

        float cx = w/2f;
        float cy = h/2f;
        int rotationDgr;
        int rotation = (int) textureView.getRotation();

        switch(rotation){
            case Surface.ROTATION_0:
                rotationDgr = 0;
                break;
            case Surface.ROTATION_90:
                rotationDgr = 90;
                break;
            case Surface.ROTATION_180:
                rotationDgr = 180;
                break;
            case Surface.ROTATION_270:
                rotationDgr = 270;
                break;
            default:
                return;
        }
        mx.postRotate((float)rotationDgr,cx,cy);
        textureView.setTransform(mx);



    }

    private boolean allPermissionGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }

        }
        return true;
    }


    @Override
    public void onClick(View v) {

    }

    Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }


}