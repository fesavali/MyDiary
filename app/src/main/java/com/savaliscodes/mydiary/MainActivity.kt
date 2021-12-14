package com.savaliscodes.mydiary

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp

import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
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

       //manage user sessions
        val mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        if (user != null) {
            // User is signed in
            val user: String? = intent.getStringExtra("uReg")
        } else {
            //user is not signed in direct to login
           val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        //handle fab on click
        binding.fab.setOnClickListener { view ->
           val intent = Intent(this, AddNote::class.java)
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
    }
}