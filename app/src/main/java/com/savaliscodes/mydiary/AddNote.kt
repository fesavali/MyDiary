package com.savaliscodes.mydiary

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class AddNote : AppCompatActivity() {
    lateinit var userId: String
    lateinit var userEmail: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)
//       //get user data from main activity
         userId = intent.getStringExtra("uId").toString()
        userEmail = intent.getStringExtra("uEmail").toString()
        val btn = findViewById<Button>(R.id.save)
        // handle save button click
        btn.setOnClickListener {
            saveNote()
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
        val db = Firebase.firestore

        val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z")
        val currentDateAndTime: String = simpleDateFormat.format(Date())

        val key = db.collection("Diary Logs").document().id;

        //put values on hashmap 123
        val diaryLog = hashMapOf(
            "Diary Log ID" to key,
            "User Id" to userId,
            "Log Title" to title,
            "Log Contents" to contents,
            "Log Time" to currentDateAndTime,
            "User Email" to userEmail
        )
        progress.isVisible = true
        db.collection("Diary Logs").document(key)
            .set(diaryLog, SetOptions.merge())
            .addOnSuccessListener {
                progress.isInvisible = true
                Log.d(TAG, "DocumentSnapshot successfully written!")
                Toast.makeText(this, "Your Log was Saved Successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                progress.isInvisible = true
                Log.w(TAG, "Error writing document", e)
                Toast.makeText(this, "Your Log Failed to Save. Try Again. Reason $e", Toast.LENGTH_LONG).show()
            }

    }
}