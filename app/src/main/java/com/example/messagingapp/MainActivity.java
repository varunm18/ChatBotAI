package com.example.messagingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    BroadcastReceiverClass brc;
    IntentFilter filter;
    static TextView t1, t2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        t1 = findViewById(R.id.text);
        t2 = findViewById(R.id.text2);

        checkForSmsPermission();
    }

    private void checkForSmsPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            Log.d("FAIL", "failed");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        brc = new BroadcastReceiverClass();
        filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(brc, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(brc);
    }
}