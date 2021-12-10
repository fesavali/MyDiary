package com.savaliscodes.mydiary

import android.app.Activity.RESULT_OK
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class Register : AppCompatActivity() {

    val userName = findViewById<EditText>(R.id.Username)
    val password = findViewById<EditText>(R.id.pass)
    val password1 = findViewById<EditText>(R.id.pass1)
    val regBtn = findViewById<Button>(R.id.btnReg)
    val googleSign = findViewById<Button>(R.id.googleBtn)

    // [START declare_auth]
    private lateinit var mAuth: FirebaseAuth
    // [END declare_auth]

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // handle continue with google button
        googleSign.setOnClickListener {
            launchGoogleAuthUI()
        }
        //register User
        regBtn.setOnClickListener {
            registerUser()
        }

    }

    private fun launchGoogleAuthUI() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        // Initialize Firebase Auth
        val mAuth = FirebaseAuth.getInstance()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    // [START auth_with_google]
    private fun firebaseAuthWithGoogle(idToken: String) {
        val mAuth = FirebaseAuth.getInstance()
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = mAuth.currentUser

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)

                }
            }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun registerUser() {
        //Get Firebase Auth Instance
        val mAuth = FirebaseAuth.getInstance()
        //get user inputs
        val uEmail:String = userName.text.toString().trim()
        val uPass:String = password.text.toString().trim()
        val uPass1:String = password1.text.toString().trim()
        //validate user inputs
        if(uEmail.isEmpty()){
            userName.setError("Email is required")
            userName.requestFocus()
            return
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(uEmail).matches()){
            userName.setError("Use a Valid Email Address")
            userName.requestFocus()
            return
        }
        if(uPass.isEmpty()){
            password.setError("Password is Required")
            password.requestFocus()
            return
        }
        if(uPass.length < 6){
            password.setError("Password length must be more than 6 characters")
            password.requestFocus()
            return
        }
        if(uPass1.isEmpty()){
            password1.setError("Confirm your Password")
            password1.requestFocus()
            return
        }
        if(!uPass.equals(uPass1)){
            password1.setError("Passwords Don\'t Match")
            password1.requestFocus()
            return
        }
        //register user
        mAuth.createUserWithEmailAndPassword(uEmail, uPass)
            .addOnCompleteListener(this){ task->
                if(task.isSuccessful){
                    val user = mAuth.currentUser
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("UReg", user)
                    startActivity(intent)
                }  else {
                    finish()
                    Toast.makeText(this,"User Registration Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }
    }

