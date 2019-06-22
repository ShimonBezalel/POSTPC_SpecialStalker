package com.example.myspecialstalker;


import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import java.util.Objects;

public class OutgoingBroadcastReceiver extends BroadcastReceiver {

    private static final String TEXT = "TEXT";
    private static final String PHONE_NUMBER = "PHONE_NUMBER";
    public static final String CHANNEL_ID = "14445";
    private Context context;


    @Override
    public void onReceive(Context context, Intent intent) {
    this.context = context;
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
        final int id                    = (int) R.string.channel_id;
        final String INTENT_DELIVERED   = "SMS_DELIVERED";
        final String INTENT_SENT        = "SMS_SENT";

        PendingIntent sendIntent = PendingIntent.getBroadcast(context, 0, new Intent(INTENT_SENT), 0);
        PendingIntent deliveredIntent = PendingIntent.getBroadcast(context, 0, new Intent(INTENT_DELIVERED), 0);

        this.context.registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Notification ntfc = new NotificationCompat.Builder(context, CHANNEL_ID)
                                .setContentTitle("Received!").setSmallIcon(R.drawable.ic_launcher_background)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .build();
                        NotificationManagerCompat.from(context).notify(id, ntfc);
                        break;

                    case Activity.RESULT_CANCELED:
                        Toast.makeText(context, "Did not receive.",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(INTENT_DELIVERED));

        this.context.registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Notification ntfc = new NotificationCompat.Builder(context, CHANNEL_ID)
                                .setContentTitle("Sent!").setSmallIcon(R.drawable.ic_launcher_background)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .build();
                        NotificationManagerCompat.from(context).notify(id, ntfc);
                        break;

                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(context, "Cannot send. No Service",
                                Toast.LENGTH_SHORT).show();
                        break;

                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(context, "Cannot send. Failure.",
                                Toast.LENGTH_SHORT).show();
                        break;

                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context, "Cannot send. No PDU",
                                Toast.LENGTH_SHORT).show();
                        break;

                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(context, "Cannot send. Radio is off.",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(INTENT_SENT));


        Notification ntfc = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("sending message..").setSmallIcon(R.drawable.ic_launcher_background)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();
        NotificationManagerCompat.from(context).notify(id, ntfc);

        SmsManager.getDefault().sendTextMessage(
                number,
                null,
                textMessage,
                sendIntent,
                deliveredIntent);
    }


}
