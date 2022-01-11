package com.savaliscodes.mydiary

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.savaliscodes.mydiary.databinding.ActivitySettingsBinding

class Settings : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
           finish()
        }
//         Declaring fragment manager from making data
        // transactions using the custom fragment
        val mFragmentManager = supportFragmentManager
        val mFragmentTransaction = mFragmentManager.beginTransaction()
        val mFragment = SettingsFragment()
        val mBundle = Bundle()
//        mBundle.putString("mText",mEditText.text.toString())
        mFragment.arguments = mBundle
        mFragmentTransaction.add(R.id.frameLayout, mFragment).commit()
    }
}