package com.demo.firebasephoneauth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    FirebaseUser currentUser ;
    FirebaseAuth mAuth;

    String device_id;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        device_id= Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentUser == null) {
                    updateUI();
                }else {
                    userId = currentUser.getUid();
                   checkdata();
                }
            }
        }, 3000);

    }

    private void checkdata() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("FirebasePhoneAuth").child("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userId)) {

                    String device=dataSnapshot.child(userId).child("deviceid").getValue().toString();

                    if(device.equals(device_id)){
                        Intent intet = new Intent(SplashActivity.this, ProfileDisplay.class);
                        intet.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intet.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intet);
                        finish();
                    }else{

                        Toast.makeText(SplashActivity.this, "Another Device is loggedIn"+device, Toast.LENGTH_SHORT).show();
                        updateUI();
//
                    }



                } else {
                    Intent intet = new Intent(SplashActivity.this, ProfileActivity.class);
                    intet.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intet.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intet);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }

    private void updateUI()
    {
            Intent intet = new Intent(SplashActivity.this, MainActivity.class);
            intet.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intet.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intet);
            finish();

    }
}