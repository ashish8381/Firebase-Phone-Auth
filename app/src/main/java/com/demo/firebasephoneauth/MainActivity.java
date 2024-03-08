package com.demo.firebasephoneauth;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
        String mVerificationId="";
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    FirebaseAuth auth;
    TextInputEditText mphone,motp;
    LinearLayout mphonelayout,mphonelayout2;
    Button msendotpbtn,msubmitotp;

    ProgressBar mprogress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        mphone=findViewById(R.id.signup_phone);

        msendotpbtn=findViewById(R.id.signup_btn);


        mprogress=findViewById(R.id.verification_loading);

        mphonelayout=findViewById(R.id.LinearLayout_Phone);
        msubmitotp=findViewById(R.id.signup_confirm);
        motp=findViewById(R.id.otp_field);

        mphonelayout2=findViewById(R.id.LinearLayout_PinView);

        msendotpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mprogress.setVisibility(View.VISIBLE);
                String number=mphone.getText().toString();
                sendotp(number);
            }
        });

        msubmitotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mprogress.setVisibility(View.VISIBLE);
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
                            mprogress.setVisibility(View.GONE);
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
                            checkdeviceId(user.getUid());
                            mprogress.setVisibility(View.GONE);
//                            updateUI();
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

    private void checkdeviceId(String uid) {
        String  device_id= Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("FirebasePhoneAuth").child("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(uid)) {

                    String device = dataSnapshot.child(uid).child("deviceid").getValue().toString();

                    if (device.equals(device_id)) {
                        Intent intet = new Intent(MainActivity.this, ProfileDisplay.class);
                        intet.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intet.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intet);
                        finish();
                    }
                    else{
                        AlertDialog.Builder altBx = new AlertDialog.Builder(MainActivity.this);
                        altBx.setTitle("Another User Logged In");
                        altBx.setMessage("Do You want to Logout this User..");
                        altBx.setIcon(R.mipmap.ic_launcher);

                        altBx.setPositiveButton("Keep me LogIn & Logout other User", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                logout_user(uid,device_id);
                            }
                        });
                        altBx.setNeutralButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                                FirebaseAuth.getInstance().signOut();
                                mphonelayout.setVisibility(View.VISIBLE);
                                mphonelayout2.setVisibility(View.GONE);
                            }

                        });
                        altBx.show();
                    }
                }
                else{
                    updateUI();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void logout_user(String uid, String deviceId) {

        Log.e("uid",uid);
        Log.e("uid","devideId=== "+deviceId);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

//        databaseReference.child("FirebasePhoneAuth").child("user");
        databaseReference.child("FirebasePhoneAuth").child("user")
                .child(uid).child("deviceid").setValue(deviceId).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(MainActivity.this, "User Logged out Successfully", Toast.LENGTH_SHORT).show();
                Intent intet = new Intent(MainActivity.this, ProfileDisplay.class);
                intet.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intet.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intet);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Something went Wrong!!", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                mphonelayout.setVisibility(View.VISIBLE);
                mphonelayout2.setVisibility(View.GONE);
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