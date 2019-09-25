package com.cobe.bestdarnalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DigitalClock;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    //Declare variables needed for alarm manager
    TimePicker alarmTimePicker;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    Button setAlarmButton;
    Button alarmOffButton;
    long time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        alarmTimePicker = findViewById(R.id.timePicker);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        final Intent intent = new Intent(this, AlarmReceiver.class);
        final Context context = this;


        //todo
        //onclick change listener to turn on alarm
        setAlarmButton = findViewById(R.id.setAlarmButton);
        setAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "ALARM ON", Toast.LENGTH_SHORT).show();
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getHour());
                calendar.set(Calendar.MINUTE, alarmTimePicker.getMinute());

                pendingIntent = pendingIntent.getBroadcast(context, 0, intent, 0);
                time = (calendar.getTimeInMillis() - (calendar.getTimeInMillis() % 60000));
                if(System.currentTimeMillis() > time) {
                    if(calendar.AM_PM == 0) {
                        time = time + (1000 * 60 * 60 * 12);
                    } else {
                        time = time + (1000 * 60 * 60 * 24);
                    }
                }
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 10000, pendingIntent);

            }
        });
        //todo
        //onclick listener to turn off alarm
        alarmOffButton = findViewById(R.id.alarmOffButton);
        alarmOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pendingIntent = pendingIntent.getBroadcast(context, 0, intent, 0);
                alarmManager.cancel(pendingIntent);
                pendingIntent.cancel();
                Toast.makeText(MainActivity.this, "ALARM OFF", Toast.LENGTH_SHORT).show();
                Ringtone ringtone = RingtoneManager.getRingtone(context, null);
                ringtone.stop();
            }
        });
    }

}
