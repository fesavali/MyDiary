package com.savaliscodes.mydiary

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AddNote : AppCompatActivity() {
//    lateinit var userId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)
//       //get user data from main activity
//         userId = intent.getStringExtra("uId").toString()
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

        val user = FirebaseAuth.getInstance().currentUser

        val userId = user?.uid.toString()
        val userName = user?.displayName.toString()
        val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z")
        val currentDateAndTime: String = simpleDateFormat.format(Date())

        val diaryLog = hashMapOf(
            "Log Id" to userId,
            "Log Title" to title,
            "Log Contents" to contents,
            "Log Time" to currentDateAndTime
        )

        db.collection(userName).document(title)
            .set(diaryLog)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }

    }
}