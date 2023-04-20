package com.example.safetohome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    TextView name1, name2, phone1, phone2, stringadd, timer;
    private  SensorManager sensorManager;
    Button button, button2;
    private FirebaseAuth firebaseAuth;
    public static final String SHARED_PREFS= "sharedPrefs";
    private static final int REQUEST_CALL=3;
    private static final int REQUEST_SMS=6;
    private static final int ACCESS_LOCATION=9;
    private int count=0;
    CardView cardView;
    Button cancel;

    private int duration=20;

    FusedLocationProviderClient fusedLocationProviderClient;



    @SuppressLint({"MissingInflatedId", "ServiceCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth= FirebaseAuth.getInstance();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        name1= findViewById(R.id.mainname1);
        name2= findViewById(R.id.mainname2);
        phone1= findViewById(R.id.mainphone1);
        phone2= findViewById(R.id.mainphone2);
        button= findViewById(R.id.mainbtnlogout);
        button.setBackgroundColor(Color.parseColor("#484848"));
        button2= findViewById(R.id.maincontactbutton);
        stringadd= findViewById(R.id.stringadd);
        timer= findViewById(R.id.timertxt);
        cardView= findViewById(R.id.mainCardView);
        cancel= findViewById(R.id.cancelbtn);

        String Name1= getIntent().getStringExtra("name1");
        String Name2= getIntent().getStringExtra("name2");
        String Phone1= getIntent().getStringExtra("phone1");
        String Phone2= getIntent().getStringExtra("phone2");

        name1.setText(Name1);
        name2.setText(Name2);
        phone1.setText(Phone1);
        String dial2= phone1.getText().toString();
        phone2.setText(Phone2);

        String number= phone1.getText().toString();
        String dial = "tel:"+ number;

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, InfoActivity.class));
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences= getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor= sharedPreferences.edit();
                editor.putString("name", "");
                editor.apply();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
                finish();
            }
        });



        sensorManager= (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor shake= sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SensorEventListener sensorEventListener= new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                if (sensorEvent!= null){
                    float x= sensorEvent.values[0];
                    float y=sensorEvent.values[1];
                    float z=sensorEvent.values[2];

                    float sum= Math.abs(x)+Math.abs(y)+Math.abs(z);

                            if (sum>150){
                                cardView.setVisibility(View.VISIBLE);
                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        finishAffinity();
                                    }
                                });
                                new CountDownTimer(20000, 1000) {
                                    @Override
                                    public void onTick(long l) {
                                        timer.setText(""+l/1000);
                                    }

                                    @Override
                                    public void onFinish() {
                                        timer.setText("Calling");
                                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));

                                    }
                                }.start();

                            }


                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        sensorManager.registerListener(sensorEventListener,shake,SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event){

        int action, keyCode;

        action= event.getAction();
        keyCode = event.getKeyCode();

        switch (keyCode)
        {
            case KeyEvent.KEYCODE_VOLUME_UP : {
                if (KeyEvent.ACTION_UP == 3 ) {

                        String number = phone1.getText().toString();
                }

            }

            case KeyEvent.KEYCODE_VOLUME_DOWN:
            {
                if (KeyEvent.ACTION_DOWN==action){

                    String number= phone1.getText().toString();
                    if(number.trim().length()>0){
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
                        }
                        else if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS},REQUEST_SMS);

                        }else if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},ACCESS_LOCATION);

                        }else {
                            String dial = "tel:"+ number;
                            String message="I think I am in danger!\nThis is not a joke!\nGET HELP IMMEDIATELY!";
                            SmsManager.getDefault().sendTextMessage(dial, null, message, null, null);
                            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location!=null){
                                        Geocoder geocoder= new Geocoder(MainActivity.this, Locale.getDefault());
                                        List<Address> addresses=null;

                                        try {
                                            addresses= geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                            stringadd.setText(addresses.get(0).getAddressLine(0));
                                            String patta= addresses.get(0).getAddressLine(0);
                                            String message2="This is my last location: "+patta;
                                            SmsManager.getDefault().sendTextMessage(dial, null, message2, null, null);

                                        }catch (IOException e){
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });

                            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));

                        }
                    }
                };
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void GetLastLocation(int number) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location!=null){
                        Geocoder geocoder= new Geocoder(MainActivity.this, Locale.getDefault());
                        List<Address> addresses=null;

                        try {
                            addresses= geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            stringadd.setText(addresses.get(0).getAddressLine(0));
                            String patta= addresses.get(0).getAddressLine(0);
                            String message2="This is my last location: "+patta;


                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            });

        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode== REQUEST_CALL){
            if (grantResults.length>3 && grantResults[3]==PackageManager.PERMISSION_GRANTED &&count==3){

            }
        }

    }
}