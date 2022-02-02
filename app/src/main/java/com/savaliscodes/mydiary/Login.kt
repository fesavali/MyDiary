package com.savaliscodes.mydiary

import android.content.ContentValues.TAG
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
import android.hardware.biometrics.BiometricManager.Authenticators.*
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.*
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import java.util.concurrent.Executor

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
    var fingerPrint: Boolean = false

    //fingerprints
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //prefs reference
        sp= PreferenceManager.getDefaultSharedPreferences(applicationContext)

        //get pref value
        fingerPrint = sp.getBoolean("finger_print", false)

        //check if user is signed in
        var mAuthUser = FirebaseAuth.getInstance().currentUser
        if(fingerPrint){
            //showFingerprint()
            checkIfPhoneHasFingerprint()
        }

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
    private fun checkIfPhoneHasFingerprint() {
//        Toast.makeText(this, "inafika hapa", Toast.LENGTH_SHORT).show()
        val biometricManager = BiometricManager.from(this)
            when (biometricManager.canAuthenticate().and(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
                BIOMETRIC_SUCCESS ->{
                    Toast.makeText(this, "Biometrics checked", Toast.LENGTH_SHORT).show()
                    Log.d("MY_APP_TAG", "App can authenticate using biometrics.")
                    showFingerprint()
                }
                BIOMETRIC_ERROR_NO_HARDWARE ->{
                    Toast.makeText(this, "No fingerprint hardware found", Toast.LENGTH_SHORT).show()
                    Log.e("MY_APP_TAG", "No biometric features available on this device.")
                }
                BIOMETRIC_ERROR_HW_UNAVAILABLE ->{
                    Toast.makeText(this,"Fingerprint is busy. Try again.", Toast.LENGTH_SHORT).show()
                    Log.e("MY_APP_TAG", "Biometric features are currently unavailable.")
                }
            }
    }

    private fun showFingerprint() {
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int,
                                                   errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext,
                        "Authentication error: $errString", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(applicationContext,
                        "Authentication succeeded!", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build()

        // Prompt appears when user clicks "Log in".
        // Consider integrating with the keystore to unlock cryptographic operations,
        // if needed by your app.
//        val biometricLoginButton =
//            findViewById<Button>(R.id.biometric_login)
//        biometricLoginButton.setOnClickListener {
//            biometricPrompt.authenticate(promptInfo)
//        }
        Toast.makeText(this, "Bioscreen checked", Toast.LENGTH_SHORT).show()
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