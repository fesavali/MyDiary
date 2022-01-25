package com.savaliscodes.mydiary

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.savaliscodes.mydiary.databinding.ActivityDiaryViewBinding

class DiaryView : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityDiaryViewBinding
    lateinit var nTitle : TextView
    lateinit var contents : TextView
    //from Click
    var userId : String = ""
    var docId :String =""
    //db
    private lateinit var db : FirebaseFirestore
//    private lateinit var work : ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDiaryViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        //get views
        nTitle = findViewById(R.id.log_ttle)
        contents = findViewById(R.id.log_conts)

        //values from intent
        userId = intent.getStringExtra("user").toString()
        docId = intent.getStringExtra("id").toString()

        val title = intent.getStringExtra("title")
        val contents1 = intent.getStringExtra("contents")

        //set text to fields
        nTitle.text = title
        contents.text = contents1

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_del, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.del_log ->{
                showDeleteDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showDeleteDialog() {
        val delDialog = AlertDialog.Builder(this)
        delDialog.setTitle("Delete Diary Log")
        delDialog.setMessage("Are you sure You want to delete this log")
        delDialog.setIcon(android.R.drawable.ic_dialog_alert)
        delDialog.setPositiveButton("Yes"){dialogInterface, which ->
            deleteLog()
        }
        delDialog.setNeutralButton("Cancel"){dialogInterface , which ->
            Toast.makeText(applicationContext,"clicked cancel\n operation canceled",Toast.LENGTH_SHORT).show()
        }
        delDialog.setNegativeButton("No"){dialogInterface, which ->
            Toast.makeText(applicationContext,"clicked No",Toast.LENGTH_LONG).show()
        }
        val alertDialog : AlertDialog = delDialog.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun deleteLog() {
        db = FirebaseFirestore.getInstance()

        db.collection("Diary Logs").document(docId)
            .delete()
            .addOnSuccessListener { task->
                Log.d(TAG, "DocumentSnapshot successfully deleted!")
                Toast.makeText(applicationContext, "Log Deleted Successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error deleting document", e)
                Toast.makeText(applicationContext, "Log Delete Failed. Try Again", Toast.LENGTH_SHORT).show()
            }
    }
}