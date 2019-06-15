package com.example.myspecialstalker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import android.Manifest;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.os.Bundle;

import android.widget.TextView;
import android.widget.EditText;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MutableLiveData;

public class MainActivity extends AppCompatActivity {
    private static final int CODE_PERMISSION_REQ = 1546;
    public MutableLiveData<Boolean> messageLiveData;

    private static final String messageContent = "messageContent";
    private static final String receiverNumber = "receiverNumber";

    private static boolean isDomestic(String strNum) {
        return strNum.matches("\\d+");
    }

    private static boolean isInternational(String strNum) {
        return strNum.matches("\\+972\\d+");
    }

    private static boolean validNumber(CharSequence s){
        final int domesticNumberOfDigits = 10;
        final int internationalNumberOfDigits = 13;

        if (s.length() == domesticNumberOfDigits && isDomestic(s.toString())){
            return true;
        }
        return s.length() == internationalNumberOfDigits && isInternational(s.toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Save reference to this activity for later use
        final MainActivity activity = this;

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

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
        for (String permission_type : permission_requests){
            permission_results[i] = ActivityCompat.checkSelfPermission( activity, permission_type)
                    ==
                    PackageManager.PERMISSION_GRANTED;
            i += 1;
        }

        // If all permissions are acquired we can commence with the app's logic
        boolean all_permissions = true;
        for (i = 0; i < 3; i ++){
            all_permissions &= permission_results[i];
        }
        if (all_permissions) {
            setContentView(R.layout.activity_main);
        }

        // Otherwise, permissions must be obtained manually.
        else {
            i = 0;
            for (boolean permission_result: permission_results){
                if (!permission_result){
                    ActivityCompat.requestPermissions(
                            activity,
                            new String[]{permission_requests[i]},
                            CODE_PERMISSION_REQ);
                }
                i += 1;
            }
            setContentView(R.layout.activity_main);
        }

        EditText textMessage = (EditText) findViewById(R.id.editMessage);
        final EditText textNumber = (EditText) findViewById(R.id.editReceiver);
        final TextView textViewNumber = (TextView) findViewById(R.id.editReceiverNumber);
        textMessage.setText(sharedPreferences.getString(messageContent, "Message: "));
        textNumber.setText(sharedPreferences.getString(receiverNumber, "To: "));
        textMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(messageContent, s.toString());
                editor.apply();
            }
        });

        textNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (validNumber(s)) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(receiverNumber, s.toString());
                    editor.apply();
                    String readySignal ="Ready to send to: " + s.toString();
                    textViewNumber.setText(readySignal);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setContentView(R.layout.activity_main);
        } else { // access denied by user

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.SEND_SMS)) {


            }
        }
    }



}

