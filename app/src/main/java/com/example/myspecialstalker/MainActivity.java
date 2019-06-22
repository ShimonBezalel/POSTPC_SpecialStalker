package com.example.myspecialstalker;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import androidx.appcompat.app.AppCompatActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;

import android.Manifest;
import android.os.Bundle;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.MutableLiveData;


public class MainActivity extends AppCompatActivity {
    private static final int CODE_PERMISSION_REQ    = 1546;

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
        final TextView viewNumber = (TextView) findViewById(R.id.editReceiverNumber);
        textMessage.setText(sharedPreferences.getString(messageContent, "Message: "));
        textNumber.setText(sharedPreferences.getString(receiverNumber, "To: "));

        // If a number was entered to the number field, we can now commence
        if(!textNumber.toString().equals("")){
            String text ="Sending an SMS to  " + textNumber.toString();
            viewNumber.setText(text);
        }

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
                    viewNumber.setText(readySignal);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        CharSequence channel = getString( R.string.channel);
        NotificationChannel notificationChannel = new NotificationChannel( getString(R.string.channel_id),
                channel, NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.setDescription("sdf");

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);
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






