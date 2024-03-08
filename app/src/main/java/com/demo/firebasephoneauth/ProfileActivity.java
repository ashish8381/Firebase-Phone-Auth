package com.demo.firebasephoneauth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.demo.firebasephoneauth.model.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {


    TextInputEditText mname,memail,maddress;
    int PICK_IMAGE_REQUEST=1;
    CircleImageView mimg;
    Button msubmit;

    ProgressBar mprogress;

    byte[] imageData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        mname=findViewById(R.id.profile_name);
        memail=findViewById(R.id.profile_email);
        maddress=findViewById(R.id.profile_address);

        mprogress=findViewById(R.id.profile_loading);

        msubmit=findViewById(R.id.create_profile);

        mimg=findViewById(R.id.profile_image);

        mimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
            }
        });


        msubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=mname.getText().toString();
                String email=memail.getText().toString();
                String address=maddress.getText().toString();

                if (name.isEmpty() || email.isEmpty() || address.isEmpty()) {
                    Log.e("errorlog","fields are empty");
                }else{
                    mprogress.setVisibility(View.VISIBLE);
                    updatedata(name,email,address,imageData);

                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                mimg.setImageBitmap(bitmap);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                imageData = baos.toByteArray();
                // Use the byte array as needed
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String convertBitmapToBase64(byte[] imageBytes) {

        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }




    private void updatedata(String name, String email, String address, byte[] imageBytes) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("FirebasePhoneAuth");

        // Get the user ID of the currently authenticated user
        String userId = firebaseAuth.getCurrentUser().getUid();
        String imageBase64 = convertBitmapToBase64(imageBytes);
        String device_id= Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        User user = new User(name, address, email, imageBase64,device_id);

        // Upload user data to Firebase Realtime Database
        databaseReference.child("user").child(userId).setValue(user)
                .addOnSuccessListener(aVoid ->{
                    mprogress.setVisibility(View.GONE);
                    Log.d("FirebaseUtils", "User data uploaded successfully");
                    Intent intent = new Intent(ProfileActivity.this, ProfileDisplay.class);
                    startActivity(intent);


                })
                .addOnFailureListener(e ->{
                    mprogress.setVisibility(View.GONE);
                    Log.e("FirebaseUtils", "Error uploading user data: " + e.getMessage());
                });


    }
}