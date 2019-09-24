/**
 * MainActivity.java
 * Hosts MainActivityFragment on a phone and both the
 * MainActivityFragment and SettingsActivityFragment on a tablet
 *
 **/

package com.cobe.roadsigntest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;

import com.cobe.roadsigntest.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    public static final String CHOICES = "pref_numberOfChoices";
    public static final String TYPES = "pref_typesToInclude";

    private boolean phoneDevice = true; //used to force portrait mode
    private boolean preferencesChanged = true; //did preferences change?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //set default values in the app's SharedPreferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        //register listener for SharedPreferences changes
        PreferenceManager.getDefaultSharedPreferences(this).
                registerOnSharedPreferenceChangeListener(preferencesChangeListener);

        //determine screen size
        int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

        //if the device is a tablet set phoneDevice to false
        if(screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE || screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            phoneDevice = false; //tells us its not a phone sized device
        }

        //if running on a phone-sized device. allow only portrait orientation
        if(phoneDevice) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }



    @Override
    protected void onStart() {
        super.onStart();

        if(preferencesChanged) {
            //default preferences have been set,
            //initialize MainActivityFragment and start the quiz
            MainActivityFragment testFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.testFragment);
            testFragment.updateGuessRows(
                    PreferenceManager.getDefaultSharedPreferences(this));
            testFragment.updateTypes(
                    PreferenceManager.getDefaultSharedPreferences(this));
            testFragment.resetQuiz();
            preferencesChanged = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //get device's current orientation
        int orientation = getResources().getConfiguration().orientation;

        //display the app's menu only in portrait mode
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            //inflate the menu
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent preferencesIntent = new Intent(this, SettingsActivity.class);
        startActivity(preferencesIntent);
        return super.onOptionsItemSelected(item);
    }

    private SharedPreferences.OnSharedPreferenceChangeListener preferencesChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            preferencesChanged = true; //user changed app settings

            MainActivityFragment testFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.testFragment);

            if (key.equals(CHOICES)) {// number of choices to display changed
                testFragment.updateGuessRows(sharedPreferences);
                testFragment.resetQuiz();
            } else if (key.equals(TYPES)) { //types to include changed
                Set<String> signTypes = sharedPreferences.getStringSet(TYPES, null);

                if (signTypes != null && signTypes.size() > 0) {
                    testFragment.updateTypes(sharedPreferences);
                    testFragment.resetQuiz();
                } else {
                    //must select one type -- set Warning_Signs as default
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    signTypes.add(getString(R.string.default_type));
                    editor.putStringSet(TYPES, signTypes);
                    editor.apply();

                    Toast.makeText(MainActivity.this, R.string.default_sign_type_message, Toast.LENGTH_SHORT).show();
                }
            }
            Toast.makeText(MainActivity.this, R.string.restarting_quiz, Toast.LENGTH_SHORT).show();
        }
    };

}
