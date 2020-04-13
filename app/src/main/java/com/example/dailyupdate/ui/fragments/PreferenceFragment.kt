package com.example.dailyupdate.ui.fragments

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import androidx.preference.*
import com.example.dailyupdate.R

class PreferenceFragment : PreferenceFragmentCompat(), OnSharedPreferenceChangeListener {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_general, rootKey)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        // Figure out which preference was changed, if there is any preference
        val preference = findPreference<Preference>(key)
        if (null != preference) {
            // Update the summary for the preference
            if (preference is SwitchPreferenceCompat) {
                val switchValue = sharedPreferences.getBoolean(preference.key, false)
                preference.isChecked = switchValue
            } else {
                val value = sharedPreferences.getString(preference.key, "")
                setPreferenceSummary(preference, value)
            }
        }
    }

    private fun setPreferenceSummary(preference: Preference, value: String?) {
        if (preference is EditTextPreference) {
            preference.summary = value
        } else if (preference is ListPreference) {
            // For list preferences, figure out the label of the selected value
            val prefIndex = preference.findIndexOfValue(value)
            if (prefIndex >= 0) {
                // Set the summary to that label
                preference.summary = preference.entries[prefIndex]
            }
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
}