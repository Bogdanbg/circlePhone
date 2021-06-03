package com.example.circledialerphone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private RotaryDialerView rotaryDialerView;
    private EditText digits;
    private Button backSpace,callButton,starButton,barsButton;
    private static final int REQUEST_CALL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rotaryDialerView = findViewById(R.id.dialer);
        digits = findViewById(R.id.digits);
        callButton = findViewById(R.id.call_button);
        starButton = findViewById(R.id.star_button);
        barsButton = findViewById(R.id.bars_button);

        rotaryDialerView.addDialListener(number -> digits.append(String.valueOf(number)));

        backSpace = findViewById(R.id.btn_delete);
        backSpace.setOnClickListener(view -> {
            if(digits.getText().toString().length() > 0)
            {
                digits.getText().delete(digits.getText().length()-1,digits.getText().length());
            }
        });

        backSpace.setOnLongClickListener(view -> {
            if (digits.getText().toString().length() > 0)
            {
                digits.getText().clear();
                return true;
            }
            return false;
        });

        starButton.setOnClickListener(view -> digits.append("*"));
        barsButton.setOnClickListener(view -> digits.append("#"));
        callButton.setOnClickListener(view -> makeCall());



    }

    private void makeCall()
    {
        String number  = digits.getText().toString();
        number = number.replace("#",Uri.encode("#"));
        if (number.trim().length() > 0)
        {
            if(ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL);
            }
            else{
                    String dial = "tel:" + number;
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                }

        }else {
            Toast.makeText(MainActivity.this, "Enter number",   Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                makeCall();
            }else{
                Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show();
            }
        }
    }
}