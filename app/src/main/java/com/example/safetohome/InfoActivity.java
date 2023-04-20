package com.example.safetohome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.LayoutTransition;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.safetohome.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

public class InfoActivity extends AppCompatActivity {

    TextView details;
    LinearLayout layout;

    ActivityMainBinding binding;
    TextView textView;
    CountryCodePicker ccp1,ccp2;
    EditText name1, name2, phone1, phone2;
    Button btn;
    ProgressBar progressBar;
    private FirebaseDatabase db;
    private DatabaseReference reference;

    private static final int REQUEST_CALL=100;
    private static final int REQUEST_SMS=200;
    private static final int ACCESS_LOCATION=300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        askPermissions();

        details= findViewById(R.id.details);
        layout= findViewById(R.id.layout);
        layout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        ccp1=(CountryCodePicker) findViewById(R.id.ccp1);
        ccp1=
        ccp2= (CountryCodePicker) findViewById(R.id.ccp2);

        name1= findViewById(R.id.infonamep1);
        name2= findViewById(R.id.infonamep2);
        phone1= findViewById(R.id.infophonep1);
        phone2= findViewById(R.id.infophonep2);
        btn= findViewById(R.id.infobtn);
        progressBar= findViewById(R.id.pb);
        textView= findViewById(R.id.infotxt);






        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((TextUtils.isEmpty(name1.getText().toString()) && TextUtils.isEmpty(phone1.getText().toString())) ){
                    View view1 = textView;
                    view1.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(() ->  view1.setVisibility(View.INVISIBLE), 3000);
                    View view2= btn;
                    view2.setVisibility(View.INVISIBLE);
                    new Handler().postDelayed(() ->  view2.setVisibility(View.VISIBLE), 3000);
                }

                else{
                    Intent intent= new Intent(InfoActivity.this, MainActivity.class);
                    intent.putExtra("name1", name1.getText().toString());
                    intent.putExtra("name2", name2.getText().toString());
                    intent.putExtra("phone1", phone1.getText().toString());
                    intent.putExtra("phone2", phone2.getText().toString());

                    startActivity(intent);
                    finish();
                }
            }
        });
    }


    public void expand(View view) {

        int v=(details.getVisibility()==View.GONE)? View.VISIBLE :View.GONE;

        TransitionManager.beginDelayedTransition(layout,new AutoTransition());
        details.setVisibility(v);
    }

    private void askPermissions() {
        if (ContextCompat.checkSelfPermission(InfoActivity.this, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(InfoActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
                if (ContextCompat.checkSelfPermission(InfoActivity.this, Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(InfoActivity.this, new String[]{Manifest.permission.SEND_SMS},REQUEST_SMS);
                     if (ContextCompat.checkSelfPermission(InfoActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(InfoActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},ACCESS_LOCATION);
                    }

            }
        }

    }

}