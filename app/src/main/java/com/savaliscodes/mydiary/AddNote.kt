package com.savaliscodes.mydiary

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.google.firebase.firestore.SetOptions
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.firestore.FirebaseFirestore

class AddNote : AppCompatActivity() {
    lateinit var userId: String
    lateinit var userEmail: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)


     //get user data from main activity
        userId = intent.getStringExtra("uId").toString()
        userEmail = intent.getStringExtra("uEmail").toString()
        val btn = findViewById<Button>(R.id.save)
        // handle save button click
        btn.setOnClickListener {
            saveNote()
        }
    }
    //handle menu inflater
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_addnote, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId){
            R.id.cancel -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun saveNote() {
        // ref to edit texts
        val tited = findViewById<EditText>(R.id.title_log)
        val cont = findViewById<EditText>(R.id.log_cont)

        val progress = findViewById<ProgressBar>(R.id.bar2)
        //get user input
        val title = tited.text.toString().trim()
        val contents = cont.text.toString().trim()
        //validate user input
        if(title.isEmpty()){
            tited.error = "Diary Log Title is required."
            tited.requestFocus()
            return
        }
        if(contents.isEmpty()){
            cont.error = "Diary Log Contents are required."
            cont.requestFocus()
            return
        }
        val db = FirebaseFirestore.getInstance()

        val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z")
        val currentDateAndTime: String = simpleDateFormat.format(Date())

        val key = db.collection("Diary Logs").document().id;

        //put values on hashmap 123
        val diaryLog = hashMapOf(
            "DiaryLogID" to key,
            "UserId" to userId,
            "LogTitle" to title,
            "LogContents" to contents,
            "LogTime" to currentDateAndTime,
            "UserEmail" to userEmail
        )
        //show progressBar
        progress.isVisible = true
        //save data to cloud firestore
        db.collection("Diary Logs").document(key)
            .set(diaryLog, SetOptions.merge())
            .addOnSuccessListener {
                //hide progress bar
                progress.isInvisible = true
                Log.d(TAG, "DocumentSnapshot successfully written!")
                //redirect home
                Toast.makeText(this, "Your Log was Saved Successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                //on fail hide progress
                progress.isInvisible = true
                //log error
                Log.w(TAG, "Error writing document", e)
                Toast.makeText(this, "Your Log Failed to Save. Try Again. Reason $e", Toast.LENGTH_LONG).show()
            }

    }


}