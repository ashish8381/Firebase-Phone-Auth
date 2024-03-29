package com.demo.firebasephoneauth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.demo.firebasephoneauth.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileDisplay extends AppCompatActivity {

    TextView mname,memail,maddress;
    CircleImageView mimage;

    FirebaseAuth mauth;

    Button mlogout;

    ProgressBar mprogress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_display);

        mname=findViewById(R.id.name);
        memail=findViewById(R.id.email);
        maddress=findViewById(R.id.address);

        mprogress=findViewById(R.id.profile_display_loading);

        mimage=findViewById(R.id.image);

        mlogout=findViewById(R.id.logout);

        String  device_id= Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("FirebasePhoneAuth");

        String userId = firebaseAuth.getCurrentUser().getUid();


        databaseReference.child("user").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);

                    if(device_id.equals(user.getDeviceid())){

                    }else{
                        updateui();
                    }

                    if (user != null) {
                        String name = user.getName();
                        String address = user.getAddress();
                        String email = user.getEmail();
                        String imageBase64 = user.getImage();

                        byte[] decodedString = Base64.decode(imageBase64, Base64.DEFAULT);
                        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        mimage.setImageBitmap(decodedBitmap);
                        mname.setText(name);
                        memail.setText(email);
                        maddress.setText(address);

                        mprogress.setVisibility(View.GONE);

                    }
                } else {
                    mprogress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mprogress.setVisibility(View.GONE);
            }
        });



        mlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateui();

            }
        });


    }

    private void updateui() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(ProfileDisplay.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    public static Bitmap convertBase64ToImage(String base64String) {
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

}