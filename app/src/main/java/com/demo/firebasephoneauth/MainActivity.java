package com.demo.firebasephoneauth;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
        String mVerificationId="";
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    FirebaseAuth auth;
    TextInputEditText mphone,motp;
    LinearLayout mphonelayout,mphonelayout2;
    Button msendotpbtn,msubmitotp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        mphone=findViewById(R.id.signup_phone);

        msendotpbtn=findViewById(R.id.signup_btn);

        mphonelayout=findViewById(R.id.LinearLayout_Phone);
        msubmitotp=findViewById(R.id.signup_confirm);
        motp=findViewById(R.id.otp_field);

        mphonelayout2=findViewById(R.id.LinearLayout_PinView);

        msendotpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number=mphone.getText().toString();
                sendotp(number);
            }
        });

        msubmitotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp=motp.getText().toString();
                verifyPhoneNumberWithCode(mVerificationId,otp);
            }
        });
    }

    private void sendotp(String phonenumber){

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phonenumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(@NonNull String verificationId,
                                           @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {

                            Log.d("otp", "onCodeSent:" + verificationId);

                            // Save verification ID and resending token so we can use them later
                            mVerificationId = verificationId;
                            mResendToken = forceResendingToken;
                            mphonelayout.setVisibility(View.GONE);
                            mphonelayout2.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        mphonelayout2.setVisibility(View.GONE);
                        mphonelayout.setVisibility(View.VISIBLE);
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            updateUI();
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void updateUI() {
        Intent intet = new Intent(MainActivity.this, ProfileActivity.class);
        intet.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intet.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intet);
        finish();

    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

}