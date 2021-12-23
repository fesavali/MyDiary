package com.savaliscodes.mydiary

import android.content.Intent
import android.os.Bundle
import android.util.Log

import androidx.appcompat.app.AppCompatActivity



import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

import com.savaliscodes.mydiary.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var logsList : ArrayList<DiaryData>
    private lateinit var logsAdapter : LogsAdapter
    private lateinit var db : FirebaseFirestore
//    private lateinit var userId : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //connect toolbar to main activity
        setSupportActionBar(binding.toolbar)

        recyclerView = findViewById(R.id.rc_logs)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        logsList = arrayListOf()

        logsAdapter = LogsAdapter(logsList)

        recyclerView.adapter = logsAdapter

        eventChangeListener()

        //get extras from login and register activities
        val user = intent.getStringExtra("uReg")
        val uMail = intent.getStringExtra("mail")

        //update recyclerview on adding a new note
        val newDoc = intent.getStringExtra("docUID")
        if(newDoc != null){
            logsAdapter.notifyDataSetChanged()
        }

        //handle fab on click
        binding.fab.setOnClickListener {
           val intent = Intent(this, AddNote::class.java)
            intent.putExtra("uId", user)
            intent.putExtra("uEmail", uMail)
            startActivity(intent)
        }
    }

    private fun eventChangeListener() {
        db = FirebaseFirestore.getInstance()

        val userId = intent.getStringExtra("uReg")

        // Create a reference to the cities collection
        val logsRef = db.collection("Diary Logs")

        // Create a query against the collection.
        val query = logsRef.whereEqualTo("UserId", userId)

        query.orderBy("LogTime", Query.Direction.DESCENDING)
            .addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(
                value: QuerySnapshot?,
                error: FirebaseFirestoreException?
            ) {
                if(error != null){
                    Log.e("Firestore Read Error", error.message.toString())

                    return
                }
                for(dc : DocumentChange in value?.documentChanges!!){
                    if(dc.type == DocumentChange.Type.ADDED){
                        logsList.add(dc.document.toObject(DiaryData::class.java))
                    }
                    if(dc.type == DocumentChange.Type.REMOVED){
                        logsList.remove(dc.document.toObject(DiaryData::class.java))
                    }
                }
                logsAdapter.notifyDataSetChanged()
            }

        })

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