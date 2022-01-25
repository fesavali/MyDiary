package com.savaliscodes.mydiary

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.savaliscodes.mydiary.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), LogsAdapter.onLogClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var logsList : ArrayList<DiaryData>
    private lateinit var logsAdapter : LogsAdapter
    private lateinit var db : FirebaseFirestore
    private lateinit var work : ListenerRegistration
    private lateinit var sp: SharedPreferences
    var keepSigned :Boolean = false
    var alwaysSign : Boolean = true
    var prefUname : String = "Set Name in Settings"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        refreshToolbar()
        //pref
        sp = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        prefUname = sp.getString("u_sign", "").toString()
        //check prefs
        keepSigned = sp.getBoolean("always", false)
        alwaysSign = sp.getBoolean("password_s", true)
        //set Preferred App Title
        if(prefUname.isNotEmpty()){
            refreshToolbar()
        }

        recyclerView = findViewById(R.id.rc_logs)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        logsList = arrayListOf()

        logsAdapter = LogsAdapter(logsList, this)

        recyclerView.adapter = logsAdapter

        eventChangeListener()

        //get extras from login and register activities
        val user = intent.getStringExtra("uReg")
        val uMail = intent.getStringExtra("mail")

        //handle fab on click
        binding.fab.setOnClickListener {
           val intent = Intent(this, AddNote::class.java)
            intent.putExtra("uId", user)
            intent.putExtra("uEmail", uMail)
            startActivity(intent)
        }
    }

    private fun refreshToolbar() {
        binding.toolbar.title = prefUname;
        setSupportActionBar(binding.toolbar)
    }
    //this causes triple entry reading
//                override fun onResume() {
//                    super.onResume()
//                    refreshToolbar()
//                }
//                override fun onStart() {
//                    super.onStart()
//                    refreshToolbar()
//                }

    override fun onLogClick(position: Int) {
//        Toast.makeText(this, "Item $position clicked", Toast.LENGTH_SHORT).show()
        val clickedLog : DiaryData = logsList[position]
        val id = clickedLog.DiaryLogID
        val title = clickedLog.LogTitle
        val contents = clickedLog.LogContents
        val  user = clickedLog.UserId
        val intent = Intent(this, DiaryView::class.java)
        intent.putExtra("id", id)
        intent.putExtra("title", title)
        intent.putExtra("contents", contents)
        intent.putExtra("user", user)
        startActivity(intent)
    }

    private fun eventChangeListener() {
        db = FirebaseFirestore.getInstance()

        val userId = intent.getStringExtra("uReg")

        // Create a reference to the cities collection
        val logsRef = db.collection("Diary Logs")

        // Create a query against the collection.
        val query = logsRef.whereEqualTo("UserId", userId)

        work = query.orderBy("LogTime", Query.Direction.DESCENDING)
                //read both realtime and local data
            .addSnapshotListener(MetadataChanges.INCLUDE,
                    object : EventListener<QuerySnapshot>{
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
            R.id.delete_account ->{
                deleteAccountDialog()
                true
            }
            R.id.log_out ->{
                logoutDialog()
                true
            }
            R.id.settings ->{
                launchPreferences()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun launchPreferences() {
        val intent = Intent(this, Settings::class.java)
        startActivity(intent)
    }

    private fun logoutDialog() {
        val outDialog = AlertDialog.Builder(this)
        //set title for alert dialog
        outDialog.setTitle("Logout")
        //set message for alert dialog
        outDialog.setMessage("Are you sure you want to log out?")
        outDialog.setIcon(android.R.drawable.ic_lock_power_off)
        //perform positive action
        outDialog.setPositiveButton("Yes"){dialogInterface, which ->
            //signOut user
            FirebaseAuth.getInstance().signOut()
            //redirect to login
            val intent = Intent(this, Login::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
        //cancel option
        outDialog.setNeutralButton("Cancel"){dialogInterface, which ->
            Toast.makeText(applicationContext,"clicked cancel\n operation canceled",Toast.LENGTH_SHORT).show()
        }
        //perform negative action
        outDialog.setNegativeButton("No"){dialogInterface, which ->
            Toast.makeText(applicationContext,"clicked No",Toast.LENGTH_LONG).show()
        }
        //create the alert dialog
        val alertDialog : AlertDialog = outDialog.create()
        //other dialog policies
        alertDialog.setCancelable(false)
        alertDialog.show()

    }

    private fun deleteAccountDialog() {
        val delDialog = AlertDialog.Builder(this)
        //set title for alert dialog
        delDialog.setTitle("Delete Account")
        //set message for alert dialog
        delDialog.setMessage("Are You Sure you want to delete your account? This will delete all your saved Data.")
        delDialog.setIcon(android.R.drawable.ic_menu_delete)
        //performing positive action
        delDialog.setPositiveButton("Yes"){dialogInterface, which ->
            val user = FirebaseAuth.getInstance().currentUser
            user?.delete()
                ?.addOnCompleteListener { task->
                    if(task.isSuccessful){
                        Log.d(TAG, "User account deleted.")
                        Toast.makeText(applicationContext,
                            "You have successfully deleted your account.", Toast.LENGTH_SHORT).show()
                        val name = FirebaseAuth.getInstance().currentUser
                        val name1 = name?.displayName.toString()
                        //send user to reg activity
                        val intent = Intent(this, Register::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.putExtra("del", 112)
                        intent.putExtra("name", name1)
                        startActivity(intent)

                    }else{
                        Toast.makeText(applicationContext,
                            "Account Deletion Failed. Try Again.", Toast.LENGTH_SHORT).show()
                    }
                }

        }
        //performing cancel action
        delDialog.setNeutralButton("Cancel"){dialogInterface , which ->
            Toast.makeText(applicationContext,"clicked cancel\n operation canceled",Toast.LENGTH_SHORT).show()
        }
        //performing negative action
        delDialog.setNegativeButton("No"){dialogInterface, which ->
            Toast.makeText(applicationContext,"clicked No",Toast.LENGTH_LONG).show()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = delDialog.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()

    }

    //sign out user on pressing back button
    override fun onBackPressed() {
        if(keepSigned){
            finishAffinity()
            System.exit(0)
        }
        //pref logics
        if(alwaysSign){
            //signOut user
            FirebaseAuth.getInstance().signOut()

            //stop listening for db changes
            work.remove()
            //start Login activity
            val intent = Intent(this, Login::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }

        if(!alwaysSign && !keepSigned){
            showSecDialog()
        }

    }

    private fun showSecDialog() {
        val secDialog = AlertDialog.Builder(this)
        secDialog.setTitle("Update Your Security Preferences")
        secDialog.setMessage("Please update your Preferences Before you close App")
        secDialog.setIcon(android.R.drawable.ic_dialog_alert)
        secDialog.setPositiveButton("Yes"){dialogInterface, which ->
            val intent = Intent(applicationContext, Settings::class.java)
            var out1 : String = "2"
            intent.putExtra("close", out1)
            startActivity(intent)
        }
        secDialog.setNeutralButton("Cancel"){dialogInterface , which ->
            Toast.makeText(applicationContext,"clicked cancel\n operation canceled",Toast.LENGTH_SHORT).show()
        }
        secDialog.setNegativeButton("Updated"){dialogInterface, which ->
            finishAffinity()
            System.exit(0)
            Toast.makeText(applicationContext,"clicked No",Toast.LENGTH_LONG).show()
        }
        val alertDialog : AlertDialog = secDialog.create()
        alertDialog.setCancelable(false)
        alertDialog.show()

//            Toast.makeText(this,"Please update your Preferences Before you close App", Toast.LENGTH_SHORT)
    }

}