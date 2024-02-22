package com.demo.firebasephoneauth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

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
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
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
                    Intent intet = new Intent(SplashActivity.this, ProfileDisplay.class);
                    intet.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intet.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intet);
                    finish();

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