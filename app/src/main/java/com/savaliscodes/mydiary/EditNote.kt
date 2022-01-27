package com.savaliscodes.mydiary

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.savaliscodes.mydiary.databinding.ActivityEditNoteBinding

class EditNote : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityEditNoteBinding
    //values
    lateinit var docID : String
    lateinit var userId : String
    lateinit var lTitle : String
    lateinit var lContents : String
    lateinit var position : String
    //views
    lateinit var titleEd : EditText
    lateinit var contentsED : EditText
    //db
    private lateinit var db : FirebaseFirestore
    //get ref to list
    private lateinit var logsList : ArrayList<DiaryData>
    private lateinit var logsAdapter : LogsAdapter
    private lateinit var work : ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        //onclick on nav icon
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        //get views
        titleEd = findViewById(R.id.log_ttle_ed)
        contentsED = findViewById(R.id.log_conts_ed)

        //get values from intent
        docID = intent.getStringExtra("id").toString()
        userId = intent.getStringExtra("user").toString()
        lTitle = intent.getStringExtra("title").toString()
        lContents = intent.getStringExtra("contents").toString()
        position = intent.getStringExtra("position").toString()

        //all values set to EditText field have to be editable
        fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

        //set text to views
        titleEd.text = lTitle.toEditable()
        contentsED.text = lContents.toEditable()
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
            updateLog()
        }
        //cancel option
        updateDoa.setNeutralButton("Cancel"){dialogInterface, which ->
            Toast.makeText(applicationContext,"clicked cancel\n operation canceled", Toast.LENGTH_SHORT).show()
        }
        //perform negative action
        updateDoa.setNegativeButton("No"){dialogInterface, which ->
            Toast.makeText(applicationContext,"clicked No", Toast.LENGTH_LONG).show()
            finish()
        }
        //create the alert dialog
        val alertDialog : AlertDialog = updateDoa.create()
        //other dialog policies
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun updateLog() {
        db = FirebaseFirestore.getInstance()
        //get new values
        val newTitle = titleEd.text.toString()
        val newContents = contentsED.text.toString()
        //put values in hashmap
        val updates = hashMapOf<String, Any>(
            "LogTitle" to newTitle,
            "LogContents" to newContents
        )
        if (newTitle.equals(lTitle) && newContents.equals(lContents)) {
            Toast.makeText(applicationContext, "You Have not made any changes", Toast.LENGTH_SHORT)
                .show()
            finish()
        } else {
            //do the update
            db.collection("Diary Logs").document(docID)
                .update(updates)
                .addOnCompleteListener { task->
                    if(task.isSuccessful){
                        Log.d(ContentValues.TAG, "DocumentSnapshot successfully updated!")
                        Toast.makeText(
                            applicationContext,
                            "Diary Log Updated Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        eventChangeListener()
                       finish()
                        //stop listening for db changes
                        work.remove()
                    }else{
                        Log.d(ContentValues.TAG, "Error Updating document")
                        Toast.makeText(
                            applicationContext,
                            "Diary Log Update Failed. Try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun eventChangeListener() {
        db = FirebaseFirestore.getInstance()

        // Create a reference to the cities collection
        val logsRef = db.collection("Diary Logs")

        // Create a query against the collection.
        val query = logsRef.whereEqualTo("UserId", userId)

        work = query.orderBy("LogTime", Query.Direction.DESCENDING)
            //read both realtime and local data
            .addSnapshotListener(
                MetadataChanges.INCLUDE,
                object : EventListener<QuerySnapshot> {
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?
                    ) {
                        if(error != null){
                            Log.e("Firestore Read Error", error.message.toString())
                            Toast.makeText(applicationContext, "No Diary Logs Yet", Toast.LENGTH_SHORT).show()
                            return
                        }
                        if(value?.isEmpty == true){
                            Toast.makeText(applicationContext, "Welcome. Add Your First Log.\n Press the + button", Toast.LENGTH_LONG).show()
                        }
                        for(dc : DocumentChange in value?.documentChanges!!){
                            if(dc.type == DocumentChange.Type.ADDED){
                                logsList.add(dc.document.toObject(DiaryData::class.java))
                            }
                            if(dc.type == DocumentChange.Type.REMOVED){
                                logsList.remove(dc.document.toObject(DiaryData::class.java))
                            }
                            if(dc.type == DocumentChange.Type.MODIFIED){
                                logsList.add(dc.document.toObject(DiaryData::class.java))
                            }
                        }
                        logsAdapter.notifyDataSetChanged()
                    }

                })

    }

    private fun cancelDialog() {
       val cancelDao = AlertDialog.Builder(this)
        //set title for alert dialog
        cancelDao.setTitle("Cancel")
        //set message for alert dialog
        cancelDao.setMessage("Are you sure you want to cancel updating this Log?")
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
