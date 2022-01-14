package com.savaliscodes.mydiary

import android.app.PendingIntent.getActivity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.*
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import android.content.SharedPreferences
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import com.savaliscodes.mydiary.SettingsFragment.Companion.prefName


class Login : AppCompatActivity() {
    lateinit var userName : EditText
    lateinit var pass : EditText
    lateinit var progress : ProgressBar
    lateinit var login : Button
    lateinit var reg : TextView
    lateinit var forgot : TextView
    lateinit var mail : EditText
    lateinit var rest : Button

    //read preferences
    lateinit var sp : SharedPreferences
    var userPrefName : String = ""
    var requirePass: Boolean = false
    var keepSigned : Boolean = false
    var fingerPrint: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //prefs reference
        sp= PreferenceManager.getDefaultSharedPreferences(applicationContext)

        //get pref value
        userPrefName = sp.getString("u_sign", "").toString()
        requirePass = sp.getBoolean("password_s", true)
        keepSigned = sp.getBoolean("always", false)
        fingerPrint = sp.getBoolean("finger_print", false)


        //check if user is signed in
        var mAuthUser = FirebaseAuth.getInstance().currentUser

        if(mAuthUser != null){
            val userId = mAuthUser.uid.toString()
            Toast.makeText(this,"You Are Logged in", Toast.LENGTH_SHORT).show()
            val intent = Intent(this,MainActivity::class.java)
            intent.putExtra("uReg", userId)
            startActivity(intent)
        }
        //Initialise views
        userName = findViewById(R.id.userName)
        pass = findViewById(R.id.password)
        progress = findViewById(R.id.bar)
        login = findViewById(R.id.btnLogin)
        //val googleBtn = findViewById<Button>(R.id.googleLog)
        reg = findViewById(R.id.regTxt)
        forgot = findViewById(R.id.fogt)

        //send user to register page if not registered
        reg.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        //login user
        login.setOnClickListener {
            logInUser()
        }

        //process user password reset
        forgot.setOnClickListener{
            sendEmailPassReset()
        }

    }

    private fun logInUser() {
        //Get Firebase Auth Instance
        val mAuth = FirebaseAuth.getInstance()
        //get user inputs
        val uName:String = userName.text.toString().trim()
        val uPass:String = pass.text.toString().trim()
        //validate user inputs
        if(uName.isEmpty()){
            userName.error = "User Email is required"
            userName.requestFocus()
            return
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(uName).matches()){
            userName.error = "Use a Valid Email Address"
            userName.requestFocus()
            return
        }
        if(uPass.isEmpty()){
            pass.error = "Password id Required"
            pass.requestFocus()
            return
        }
        //signIn user
        progress.isVisible = true
        mAuth.signInWithEmailAndPassword(uName,uPass)
            .addOnCompleteListener(this){ task->
                if(task.isSuccessful){
                    //hide progress bar
                    progress.isInvisible = true
                    //get logged in user
                    val user = mAuth.currentUser
                    val userID = user?.uid.toString()
                    val uMail = user?.email.toString()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("uReg", userID)
                    intent.putExtra("mail", uMail)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }else{
                    progress.isInvisible = true
                    Toast.makeText(this, task.exception?.message,
                        Toast.LENGTH_SHORT).show()
                }

            }

    }

    private fun sendEmailPassReset() {
        //set all other views as invisible
        viewsInvisible()
        //get views
        mail = findViewById<EditText>(R.id.resetEmail)
        rest = findViewById<Button>(R.id.reset)
        mail.isVisible = true
        rest.isVisible = true

        rest.setOnClickListener {
            val email = mail.text.toString().trim()
            //validate input
            if(email.isEmpty()){
                mail.error = "Email is Required."
                mail.requestFocus()
                return@setOnClickListener
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                mail.error = "Enter a valid Email"
                mail.requestFocus()
                return@setOnClickListener
            }
            progress.isVisible = true
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener { task->
                        if(task.isSuccessful){
                            progress.isInvisible = true
                            Log.d(TAG, "Email sent.")
                            Toast.makeText(applicationContext,
                                    "Password Reset Successful. Check Your email.", Toast.LENGTH_LONG).show()
                            //set other views visible for login
                            viewsVisible()
                        }else{
                            Log.d(TAG, "Email sent Failed.")
                            Toast.makeText(applicationContext,
                                    task.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
        }

    }

    private fun viewsVisible() {
        userName.isVisible = true
        pass.isVisible = true
        login.isVisible = true
        reg.isVisible = true
        forgot.isVisible = true
        mail.isInvisible = true
        rest.isInvisible = true
    }

    private fun viewsInvisible() {
        userName.isInvisible = true
        pass.isInvisible = true
        login.isInvisible = true
        reg.isInvisible = true
        forgot.isInvisible = true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}