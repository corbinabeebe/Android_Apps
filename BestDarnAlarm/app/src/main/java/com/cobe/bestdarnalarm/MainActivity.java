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
    Context context;
    long time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initiate timepicker
        alarmTimePicker = findViewById(R.id.timePicker);

        //initiate alarm manager service
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //create intent
        final Intent intent = new Intent(this, AlarmReceiver.class);
        this.context = this;

        //onclick change listener to turn on alarm
        setAlarmButton = findViewById(R.id.setAlarmButton);
        setAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "ALARM ON", Toast.LENGTH_SHORT).show();
                Calendar calendar = Calendar.getInstance();

                //setting calendar instance  on time picker
                calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getHour());
                calendar.set(Calendar.MINUTE, alarmTimePicker.getMinute());

                //extra string into intent to tell clock that alarm on button was pressed
                intent.putExtra("extra", "alarm on");

                pendingIntent = pendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
                time = (calendar.getTimeInMillis() - (calendar.getTimeInMillis() % 60000));
                if(System.currentTimeMillis() > time) {
                    if(calendar.AM_PM == 0) {
                        time = time + (1000 * 60 * 60 * 12);
                    } else {
                        time = time + (1000 * 60 * 60 * 24);
                    }
                }
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

            }
        });
        //onclick listener to turn off alarm
        alarmOffButton = findViewById(R.id.alarmOffButton);
        alarmOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //message to display that alarm is off
                Toast.makeText(MainActivity.this, R.string.alarm_off_message, Toast.LENGTH_SHORT).show();

                //Cancels the alarm
                alarmManager.cancel(pendingIntent);

                //putExtra boolean into my intent - tells clock alarm off button was pressed
                intent.putExtra("extra", "alarm off");

                //stop ringtone
                sendBroadcast(intent);

            }
        });
    }
}
