package com.cobe.fortuneteller;


import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private String[] fortuneArray = {
            "Don't count on it",
            "Ask again later",
            "You can rely on it",
            "Without a doubt",
            "Outlook is not so good",
            "It's decidedly so",
            "Signs point to yes",
            "Yes, definitely",
            "Yes",
            "My sources say NO",
            "Have you done your homework?",
            "I can not confirm nor deny!"
    };

    private TextView fortuneText;
    private ImageButton fortuneImageButton;

    Animation shake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fortuneText = findViewById(R.id.fortuneText);
        fortuneImageButton = findViewById(R.id.fortuneImageButton);
        fortuneImageButton.setOnClickListener(fortuneChangeListener);

        shake = AnimationUtils.loadAnimation(this, R.anim.shake_anim);

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

    private View.OnClickListener fortuneChangeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Random random = new Random();
            int index = random.nextInt(fortuneArray.length);
            fortuneText.setText(fortuneArray[index]);
            fortuneImageButton.startAnimation(shake);

        }
    };
}
