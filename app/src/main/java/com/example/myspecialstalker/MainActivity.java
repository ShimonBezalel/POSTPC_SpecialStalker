package com.example.myspecialstalker;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MutableLiveData;

public class MainActivity extends AppCompatActivity {
    private static final int CODE_PERMISSION_REQ = 1546;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Save reference to this activity for later use
        final MainActivity activity = this;

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);

        boolean[] permission_results = {
                false,
                false,
                false
        };

        String[] permission_requests = {
                Manifest.permission.SEND_SMS,
                Manifest.permission.PROCESS_OUTGOING_CALLS,
                Manifest.permission.READ_PHONE_STATE
        };
        int i = 0;
        for (String permission_type : permission_requests) {
            permission_results[i] = ActivityCompat.checkSelfPermission(activity, permission_type)
                    ==
                    PackageManager.PERMISSION_GRANTED;
            i += 1;
        }

        // If all permissions are acquired we can commence with the app's logic
        boolean all_permissions = true;
        for (i = 0; i < 3; i++) {
            all_permissions &= permission_results[i];
        }
        if (all_permissions) {
            setContentView(R.layout.activity_main);
        }

        // Otherwise, permissions must be obtained manually.
        else {
            i = 0;
            for (boolean permission_result : permission_results) {
                if (!permission_result) {
                    ActivityCompat.requestPermissions(
                            activity,
                            new String[]{permission_requests[i]},
                            CODE_PERMISSION_REQ);
                }
                i += 1;
            }
            setContentView(R.layout.activity_main);
        }

    }
}