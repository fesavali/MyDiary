package com.savaliscodes.mydiary

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.savaliscodes.mydiary.databinding.ActivitySettingsBinding
import kotlin.system.exitProcess

class Settings : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    lateinit var fromMain : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        fromMain = intent.getStringExtra("close").toString()

        //set onclick listener for nav icon
        binding.toolbar.setNavigationOnClickListener {
//           val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
            if(2.equals(fromMain)){
                finishAffinity()
                System.exit(0)
            }else{
                Toast.makeText(this, "Your Preferences will Update on Next Launch", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        //Declaring fragment manager from making data
        // transactions using the custom fragment
        val mFragmentManager = supportFragmentManager
        val mFragmentTransaction = mFragmentManager.beginTransaction()
        val mFragment = SettingsFragment()
        val mBundle = Bundle()
//        mBundle.putString("mText",mEditText.text.toString())
        mFragment.arguments = mBundle
        mFragmentTransaction.add(R.id.frameLayout, mFragment).commit()
    }

    override fun onBackPressed() {
        if(2.equals(fromMain)){
            finishAffinity()
            exitProcess(0)
        }else{
            finish()
        }
    }

}