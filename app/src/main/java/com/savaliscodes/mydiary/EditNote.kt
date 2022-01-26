package com.savaliscodes.mydiary

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.savaliscodes.mydiary.databinding.ActivityEditNoteBinding

class EditNote : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityEditNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        //onclick on nav icon
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.edt_cancel ->{
                cancelDialog()
                true
            }
            R.id.edt_save ->{
                updateDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun updateDialog() {
        val updateDoa = AlertDialog.Builder(this)
        //set title for alert dialog
        updateDoa.setTitle("Update Diary Log")
        //set message for alert dialog
        updateDoa.setMessage("Are you sure you want to update this Log?")
        updateDoa.setIcon(android.R.drawable.ic_input_add)
        //perform positive action
        updateDoa.setPositiveButton("Yes"){dialogInterface, which ->
            Toast.makeText(this, "Log Updated", Toast.LENGTH_SHORT).show()
        }
        //cancel option
        updateDoa.setNeutralButton("Cancel"){dialogInterface, which ->
            Toast.makeText(applicationContext,"clicked cancel\n operation canceled", Toast.LENGTH_SHORT).show()
        }
        //perform negative action
        updateDoa.setNegativeButton("No"){dialogInterface, which ->
            Toast.makeText(applicationContext,"clicked No", Toast.LENGTH_LONG).show()
        }
        //create the alert dialog
        val alertDialog : AlertDialog = updateDoa.create()
        //other dialog policies
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun cancelDialog() {
       val cancelDao = AlertDialog.Builder(this)
        //set title for alert dialog
        cancelDao.setTitle("Cancel")
        //set message for alert dialog
        cancelDao.setMessage("Are you sure you want to cancel this update this Log?")
        cancelDao.setIcon(android.R.drawable.ic_dialog_alert)
        //perform positive action
        cancelDao.setPositiveButton("Yes"){dialogInterface, which ->
            Toast.makeText(this, "Log Update Canceled", Toast.LENGTH_SHORT).show()
            finish()
        }
        //cancel option
        cancelDao.setNeutralButton("Cancel"){dialogInterface, which ->
            Toast.makeText(applicationContext,"clicked cancel\n operation canceled", Toast.LENGTH_SHORT).show()
        }
        //perform negative action
        cancelDao.setNegativeButton("No"){dialogInterface, which ->
            Toast.makeText(applicationContext,"clicked No", Toast.LENGTH_LONG).show()
        }
        //create the alert dialog
        val alertDialog : AlertDialog = cancelDao.create()
        //other dialog policies
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}
