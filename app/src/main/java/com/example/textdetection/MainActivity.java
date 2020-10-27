package com.example.textdetection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

public class MainActivity extends AppCompatActivity {

    private Button cameraBtn;
    private final static int REQUEST_CAMERA_CAPTURE = 124;
    private FirebaseVisionTextRecognizer textRecognizer;
    FirebaseVisionImage image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        cameraBtn = findViewById(R.id.camera_button);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(takePictureIntent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(takePictureIntent, REQUEST_CAMERA_CAPTURE);

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CAMERA_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");

            recogniseMyText(bitmap);

        } }

    private void recogniseMyText(Bitmap bitmap) {
        try {
            image = FirebaseVisionImage.fromBitmap(bitmap);
            textRecognizer = FirebaseVision
                    .getInstance()
                    .getOnDeviceTextRecognizer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        textRecognizer.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        String resultText = firebaseVisionText.getText();
                        if(resultText.isEmpty()){
                            Toast.makeText(MainActivity.this,"No text Detected",Toast.LENGTH_SHORT).show();
                        }else{
                            Intent intent = new Intent(MainActivity.this,ResultActivity.class);
                            intent.putExtra(TextRecognisation.RESULT_TEXT,resultText);
                            startActivity(intent);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }
}