package com.cobe.bestdarnalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //get extra strings from intent
        String onOff = intent.getExtras().getString("extra");

        //create intent for ringtone service
        Intent alarmReceiverIntent = new Intent(context, RingtonePlayingService.class);

        //put string from MainActivity to RingtonePlayingService
        alarmReceiverIntent.putExtra("extra", onOff);

        //start ringtone service
        context.startService(alarmReceiverIntent);

    }

}
