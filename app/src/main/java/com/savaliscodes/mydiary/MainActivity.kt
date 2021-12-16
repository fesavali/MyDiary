package com.savaliscodes.mydiary

import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

import androidx.navigation.ui.AppBarConfiguration


import android.view.Menu
import android.view.MenuItem

import com.google.firebase.auth.FirebaseAuth
import com.savaliscodes.mydiary.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //connect toolbar to main activity
        setSupportActionBar(binding.toolbar)

//        get extras from login and register activities
        val user = intent.getStringExtra("uReg")
        val uMail = intent.getStringExtra("mail")

        //handle fab on click
        binding.fab.setOnClickListener {
           val intent = Intent(this, AddNote::class.java)
            intent.putExtra("uId", user)
            intent.putExtra("uEmail",uMail)
            startActivity(intent)
        }
    }
    //handle menu inflater
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    //on options selected case
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
    //sign out user on pressing back button
    override fun onBackPressed() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
    }
}