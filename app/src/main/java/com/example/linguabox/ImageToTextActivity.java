package com.example.linguabox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.FlashMode;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.io.File;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import android.media.Image;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;

public class ImageToTextActivity extends AppCompatActivity {
    private int REQUEST_CODE_PERMISSIONS = 10; //arbitrary number, can be changed accordingly
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA","android.permission.WRITE_EXTERNAL_STORAGE"};
    TextureView txView;
    Executor ex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        txView = findViewById(R.id.view_finder);
        ex = Executors.newSingleThreadExecutor();
        FirebaseApp.initializeApp(this.getApplicationContext());

        if(allPermissionsGranted()){
            startCamera(); //start camera if permission has been granted by user
        } else{
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    private void startCamera() {
        //make sure there isn't another camera instance running before starting
        CameraX.unbindAll();
        /* start preview */
        int aspRatioW = txView.getWidth(); //get width of screen
        int aspRatioH = txView.getHeight(); //get height
        Rational asp = new Rational (aspRatioW, aspRatioH); //aspect ratio
        Size screen = new Size(aspRatioW, aspRatioH); //size of the screen
        //config obj for preview/viewfinder thingy.
        @SuppressLint("RestrictedApi") PreviewConfig pConfig = new PreviewConfig.Builder().setTargetAspectRatioCustom(asp).setTargetResolution(screen).build();
        Preview preview = new Preview(pConfig); //lets build it
        preview.setOnPreviewOutputUpdateListener( new Preview.OnPreviewOutputUpdateListener() {
            //to update the surface texture we have to destroy it first, then re-add it
            @Override
            public void onUpdated(Preview.PreviewOutput output){
                ViewGroup parent = (ViewGroup) txView.getParent();
                parent.removeView(txView);
                parent.addView(txView, 0);
                txView.setSurfaceTexture(output.getSurfaceTexture());
                updateTransform();
            }});
        /* image capture */
        //config obj, selected capture mode
        ImageCaptureConfig imgCapConfig = new ImageCaptureConfig.Builder().setCaptureMode(ImageCapture.CaptureMode.MAX_QUALITY)
                .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).setFlashMode(FlashMode.AUTO).build();
        final ImageCapture imgCap = new ImageCapture(imgCapConfig);
        findViewById(R.id.capture_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgCap.takePicture(ex, new ImageCapture.OnImageCapturedListener() {
                    @Override
                    public void onCaptureSuccess(ImageProxy image, int rotationDegrees) {
                        if (image == null || image.getImage() == null) {
                            return;
                        }
                        Image mediaImage = image.getImage();
                        int rotation = degreesToFirebaseRotation(rotationDegrees);
                        FirebaseVisionImage vImage =
                                FirebaseVisionImage.fromMediaImage(mediaImage, rotation);
                        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                                .getOnDeviceTextRecognizer();
                        super.onCaptureSuccess(image, rotationDegrees);
                        Task<FirebaseVisionText> result =
                                detector.processImage(vImage)
                                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                                            @Override
                                            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                                // Task completed successfully
                                                String resultText = firebaseVisionText.getText();
                                                Log.i("RESULT", resultText);
                                                Intent imageTranslator = new Intent(getApplicationContext(),ImageToTranslationActivity.class);
                                                imageTranslator.putExtra("RESULT", resultText);
                                                startActivity(imageTranslator);
                                            }

                                        })
                                        .addOnFailureListener(
                                                new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.i("FAILURE", e.toString());
                                                        e.printStackTrace();
                                                        finish();
                                                    }
                                                });
                    }

                    @Override
                    public void onError(@NonNull ImageCapture.ImageCaptureError imageCaptureError, @NonNull String message, @Nullable Throwable cause) {
                        super.onError(imageCaptureError, message, cause);
                        finish();
                    }
                });
            }
        });
        //bind to lifecycle:
        CameraX.bindToLifecycle((LifecycleOwner)this, imgCap, preview);
    }

    private void updateTransform(){
        Matrix mx = new Matrix();
        float w = txView.getMeasuredWidth();
        float h = txView.getMeasuredHeight();
        float centreX = w / 2f; //calc centre of the viewfinder
        float centreY = h / 2f;
        int rotationDgr;
        int rotation = (int)txView.getRotation(); //cast to int bc switches don't like floats
        switch(rotation){ //correct output to account for display rotation
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
        mx.postRotate((float)rotationDgr, centreX, centreY);
        txView.setTransform(mx); //apply transformations to textureview
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //start camera when permissions have been granted otherwise exit app
        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(allPermissionsGranted()){
                startCamera();
            } else{
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private boolean allPermissionsGranted(){
        //check if req permissions have been granted
        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    int degreesToFirebaseRotation(int degrees) {
        switch (degrees) {
            case 0:
                return FirebaseVisionImageMetadata.ROTATION_0;
            case 90:
                return FirebaseVisionImageMetadata.ROTATION_90;
            case 180:
                return FirebaseVisionImageMetadata.ROTATION_180;
            case 270:
                return FirebaseVisionImageMetadata.ROTATION_270;
            default:
                throw new IllegalArgumentException(
                        "Rotation must be 0, 90, 180, or 270.");
        }
    }
}
