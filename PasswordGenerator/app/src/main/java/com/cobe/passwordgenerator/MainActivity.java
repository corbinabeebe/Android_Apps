package com.cobe.passwordgenerator;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //declare variables
    TextView passwordTextView;
    TextView passwordLengthTextView;
    SeekBar seekBar;
    Button generateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        passwordTextView = findViewById(R.id.passwordTextView); //initialize passwordTextView
        passwordLengthTextView = findViewById(R.id.passwordLengthTextView); //initialize passwordLengthTextView
        seekBar = findViewById(R.id.seekBar); //initialize seekBar
        generateButton = findViewById(R.id.generateButton); //initialize generateButton

        seekBar.setMax(25); //sets seekBar max to 25
        seekBar.setProgress(5); //sets seekBar position to 5
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { //seekBar change listener to accept input when seekbar length is changed
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                passwordLengthTextView.setText("Length: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        generateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String password = PasswordGenerator.process(seekBar.getProgress()); //generates a new random password
                passwordTextView.setText(password); //sets textView with new password
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
