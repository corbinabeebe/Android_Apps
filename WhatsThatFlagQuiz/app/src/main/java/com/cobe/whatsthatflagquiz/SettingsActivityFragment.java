package com.cobe.whatsthatflagquiz;

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

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.preferences); //load xml
    }
}
