package com.demo.firebasephoneauth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    FirebaseUser currentUser ;
    FirebaseAuth mAuth;
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
                    sendToStart();
                }else {
                    Intent intet = new Intent(SplashActivity.this, ProfileActivity.class);
                    intet.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intet.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intet);
                    finish();
                }
            }
        }, 3000);

    }
    private void sendToStart()
    {   Intent intet = new Intent(SplashActivity.this, MainActivity.class);
        intet.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intet.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intet);
        finish();
    }
}