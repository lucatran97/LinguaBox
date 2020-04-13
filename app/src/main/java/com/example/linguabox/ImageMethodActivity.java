package com.example.linguabox;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;

public class ImageMethodActivity extends AppCompatActivity {
    Button camera, gallery;
    private static final int PICK_IMAGE = 100;
    Uri imageUri = null;
    FirebaseVisionImage image;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_method);
        camera = findViewById(R.id.camera_method);
        gallery = findViewById(R.id.gallery_method);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraRecognition = new Intent(getApplicationContext(), ImageToTextActivity.class);
                startActivity(cameraRecognition);
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });




    }

    private void openGallery()
    {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE) {
                imageUri = data.getData();
                try {
                    image = FirebaseVisionImage.fromFilePath(getApplicationContext(), imageUri);
                    FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                            .getOnDeviceTextRecognizer();
                    Task<FirebaseVisionText> result =
                            detector.processImage(image)
                                    .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                                        @Override
                                        public void onSuccess(FirebaseVisionText firebaseVisionText) {
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
                                                    // Task failed with an exception
                                                    Log.i("FAILURE", e.toString());
                                                    e.printStackTrace();
                                                    finish();
                                                }
                                            });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
