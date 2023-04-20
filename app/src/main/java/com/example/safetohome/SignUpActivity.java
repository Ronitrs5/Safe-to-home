package com.example.safetohome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity {
    CountryCodePicker ccp;
    EditText phone, otp;
    TextView textView;
    Button phonebtn, otpbtn;
    ProgressBar progressBar;
    String phoneNumber;
    String otpId;
    ImageView imageView;
    FirebaseAuth mAuth;
    public static final String SHARED_PREFS= "sharedPrefs";

    TextView permissionCall, permissionMessage, permissionLocation;

    private String[] PERMISSIONS;

    TextView askPermissionButton;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth= FirebaseAuth.getInstance();
        textView= findViewById(R.id.signupTxt);
        phone= findViewById(R.id.signupPhone);
        ccp= (CountryCodePicker) findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(phone);
        imageView= findViewById(R.id.imageView);

        otp= findViewById(R.id.signupOtp);
        phonebtn= findViewById(R.id.signupphonebtn);
        otpbtn= findViewById(R.id.signupotpbtn);
        progressBar= findViewById(R.id.progressBar);

        checkBox();

        permissionCall= findViewById(R.id.permissionCall);
        permissionMessage= findViewById(R.id.permissionMessage);
        permissionLocation= findViewById(R.id.permissionLocation);
        askPermissionButton= findViewById(R.id.askPermission);

        askPermissionButton.setOnClickListener(view -> {

            if (!hasPermissions(getApplicationContext(),PERMISSIONS)){
                ActivityCompat.requestPermissions(SignUpActivity.this, PERMISSIONS, 1);
            }else {
                Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show();
            }

        });

        PERMISSIONS = new String[]{

                Manifest.permission.CALL_PHONE,
                Manifest.permission.SEND_SMS,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION

        };

        if (!hasPermissions(getApplicationContext(),PERMISSIONS)){
            ActivityCompat.requestPermissions(SignUpActivity.this, PERMISSIONS, 1);
        }

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phone.setVisibility(View.VISIBLE);
                ccp.setVisibility(View.VISIBLE);
                phonebtn.setVisibility(View.VISIBLE);
                otp.setVisibility(View.INVISIBLE);
                otpbtn.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.INVISIBLE);
            }
        });

        phonebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(phone.getText().toString())){
                    phone.setError("Enter a valid phone number");
                }
                else{
                    ccp.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.VISIBLE);
                    phone.setVisibility(View.INVISIBLE);
                    phonebtn.setVisibility(View.INVISIBLE);
                    otpbtn.setVisibility(View.VISIBLE);
                    otp.setVisibility(View.VISIBLE);
                    String numb= ccp.getFullNumberWithPlus().replace(" ", "");
                    initiateotp(numb);
                }
            }
        });


        otpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(otp.getText().toString())){
                    otp.setError("Cannot be empty");
                }

                else{
                    PhoneAuthCredential credential= PhoneAuthProvider.getCredential(otpId, otp.getText().toString());
                    signInWithPhoneAuthCredential(credential);
                    imageView.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);

                }
            }
        });



    }

    private void checkBox() {
        SharedPreferences sharedPreferences= getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String check = sharedPreferences.getString("name", "" );

        if(check.equals("true")){
            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
            finish();
        }
    }

    private void initiateotp(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
    mCallbacks=
    new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            otpId=s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            signInWithPhoneAuthCredential(phoneAuthCredential);

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            SharedPreferences sharedPreferences= getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                            SharedPreferences.Editor editor= sharedPreferences.edit();
                            editor.putString("name", "true");
                            editor.apply();
                            startActivity(new Intent(SignUpActivity.this, InfoActivity.class));
                            finish();

                        }
                        else {
                            progressBar.setVisibility(View.INVISIBLE);
                            imageView.setVisibility(View.VISIBLE);
                            Toast.makeText(SignUpActivity.this, "Something went wrong! Please try again", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private boolean hasPermissions(Context context, String... PERMISSIONS){
        if (context!=null &&PERMISSIONS!=null){
            for ( String permission: PERMISSIONS){
                if (ActivityCompat.checkSelfPermission(context,permission)!= PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode==1){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                permissionCall.setText("‣ Make calls: GRANTED");
            }else {
                permissionCall.setText("‣ Make calls: NOT GRANTED");
            }


            if (grantResults[1]==PackageManager.PERMISSION_GRANTED){
                permissionMessage.setText("‣ Send SMS: GRANTED");
            }else {
                permissionMessage.setText("‣ Send SMS: NOT GRANTED");
            }


            if (grantResults[2]==PackageManager.PERMISSION_GRANTED){
                permissionLocation.setText("‣ Location: PARTIALLY GRANTED");
            }else {
                permissionLocation.setText("‣ Location: NOT GRANTED");
            }

            if (grantResults[3]==PackageManager.PERMISSION_GRANTED){
                permissionLocation.setText("‣ Location: GRANTED");
            }else {
                permissionLocation.setText("‣ Location: NOT GRANTED");
            }


        }
    }
}