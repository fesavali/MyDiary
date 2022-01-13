package com.savaliscodes.mydiary


import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, prefName)
    }
    companion object{
        const val prefName = "SHARED_PREF"
    }
}