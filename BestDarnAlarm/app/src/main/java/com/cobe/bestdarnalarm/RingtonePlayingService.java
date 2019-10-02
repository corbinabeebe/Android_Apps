package com.cobe.bestdarnalarm;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class RingtonePlayingService extends Service {

    MediaPlayer mSong;
    boolean isRunning;
    int id;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //get extra values
        String onOffState = intent.getExtras().getString("extra");

        //assert used to prevent app from having a null pointer
        assert onOffState != null;

        //switch statement to find state of alarm
        switch (onOffState) {
            case "alarm on":
                startId = 1;
                break;
            case "alarm off":
                startId = 0;
                break;
            default:
                startId = 0;
                break;
        }


        //if else statements to see when music is or isn't playing
        if(!this.isRunning && startId == 1) {
            //create media player
            mSong = MediaPlayer.create(this, R.raw.analogwatch);
            //start media
            mSong.start();
            Toast.makeText(RingtonePlayingService.this, "Wake up!! Wake up!!! Wake up!", Toast.LENGTH_LONG).show();
            this.isRunning = true;
            this.id = 0;

        } else if(this.isRunning && startId == 0) {
            mSong.stop(); //stop media player
            mSong.reset(); //reset media player
            this.isRunning = false;
            this.id = 0;

        } else if(!this.isRunning && startId == 0) {
            this.isRunning = false;
            this.id = 0;
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, R.string.alarm_off_message, Toast.LENGTH_SHORT).show();

        super.onDestroy();
        this.isRunning = false;
    }
}
