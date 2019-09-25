package com.cobe.bestdarnalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;

import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    public AlarmReceiver() {
        super();
    }

    @Override
    public IBinder peekService(Context myContext, Intent service) {
        return super.peekService(myContext, service);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Wake up!! Wake up!!! Wake up!", Toast.LENGTH_LONG).show();
        Uri aUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        if(aUri == null) {
            aUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        Ringtone ringtone = RingtoneManager.getRingtone(context, aUri);
        ringtone.play();
    }


}
