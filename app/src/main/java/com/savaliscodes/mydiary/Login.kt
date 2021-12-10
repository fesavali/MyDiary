package com.savaliscodes.mydiary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    val userName = findViewById<EditText>(R.id.userName)
    val pass = findViewById<EditText>(R.id.password)
    val login = findViewById<Button>(R.id.btnLogin)
    val googleBtn = findViewById<Button>(R.id.googleLog)
    val reg = findViewById<TextView>(R.id.regTxt)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //send user to register page if not registered
        reg.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
        //handle google sign in
        googleBtn.setOnClickListener {
            launchGoogleAuthUI()
        }
        //login user
        login.setOnClickListener {
            logInUser()
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
            userName.setError("User Email is required")
            userName.requestFocus()
            return
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(uName).matches()){
            userName.setError("Use a Valid Email Address")
            userName.requestFocus()
            return
        }
        if(uPass.isEmpty()){
            pass.setError("Password id Required")
            pass.requestFocus()
            return
        }
        //signIn user
        mAuth.signInWithEmailLink(uName,uPass)
            .addOnCompleteListener(this){ task->
                if(task.isSuccessful){
                    val user = mAuth.currentUser
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("uName", user)
                    startActivity(intent)
                }else{
                    Toast.makeText(this, "Authentication failed. Try Again",
                        Toast.LENGTH_SHORT).show()
                }

            }

    }

    private fun launchGoogleAuthUI() {
        // See: https://developer.android.com/training/basics/intents/result
        val signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()){ res->
            this.onSignInResult(res)
        }
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build())

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            val intent = Intent(this,MainActivity::class.java)
            intent.putExtra("uName", user)
            startActivity(intent)
        } else {
            // Sign in failed. If response is null the user canceled the process
            finish()
            Toast.makeText(this, "Login with Google Failed. Try again", Toast.LENGTH_SHORT).show()

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}