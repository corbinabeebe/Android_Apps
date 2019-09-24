package com.cobe.roadsigntest;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * SettingsActivityFragment.java
 * Subclass of PreferenceFragment for managing app settings
 */
public class SettingsActivityFragment extends PreferenceFragment {

    //creates preferences GUI from preferences.xml file in res/xml
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.preferences); //load from XML
    }
}
