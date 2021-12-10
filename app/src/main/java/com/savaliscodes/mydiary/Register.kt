package com.savaliscodes.mydiary


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class Register : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val regBtn = findViewById<Button>(R.id.btnReg)
        val googleSign = findViewById<Button>(R.id.googleBtn)
        //register User
        regBtn.setOnClickListener {
            registerUser()
        }

    }

    private fun registerUser() {

        val userName = findViewById<EditText>(R.id.Username)
        val password = findViewById<EditText>(R.id.pass)
        val password1 = findViewById<EditText>(R.id.pass1)

        //Get Firebase Auth Instance
        val mAuth = FirebaseAuth.getInstance()
        //get user inputs
        val uEmail:String = userName.text.toString().trim()
        val uPass:String = password.text.toString().trim()
        val uPass1:String = password1.text.toString().trim()
        //validate user inputs
        if(uEmail.isEmpty()){
            userName.error = "Email is required"
            userName.requestFocus()
            return
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(uEmail).matches()){
            userName.error = "Use a Valid Email Address"
            userName.requestFocus()
            return
        }
        if(uPass.isEmpty()){
            password.error = "Password is Required"
            password.requestFocus()
            return
        }
        if(uPass.length < 6){
            password.error = "Password length must be more than 6 characters"
            password.requestFocus()
            return
        }
        if(uPass1.isEmpty()){
            password1.error = "Confirm your Password"
            password1.requestFocus()
            return
        }
        if(uPass != uPass1){
            password1.error = "Passwords Don\'t Match"
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

