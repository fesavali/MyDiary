package com.savaliscodes.mydiary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //check if user is signed in
        var mAuthUser = FirebaseAuth.getInstance().currentUser

        if(mAuthUser != null){
            val userId = mAuthUser.uid.toString()
            Toast.makeText(this,"You Are Logged in", Toast.LENGTH_SHORT).show()
            val intent = Intent(this,MainActivity::class.java)
            intent.putExtra("uReg", userId)
            startActivity(intent)
        }
        val login = findViewById<Button>(R.id.btnLogin)
//        val googleBtn = findViewById<Button>(R.id.googleLog)
        val reg = findViewById<TextView>(R.id.regTxt)

        //send user to register page if not registered
        reg.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        //login user
        login.setOnClickListener {
            logInUser()
        }

    }

    private fun logInUser() {

        val userName = findViewById<EditText>(R.id.userName)
        val pass = findViewById<EditText>(R.id.password)

        val progress = findViewById<ProgressBar>(R.id.bar)

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
                    startActivity(intent)
                }else{
                    progress.isInvisible = true
                    Toast.makeText(this, task.exception?.message,
                        Toast.LENGTH_SHORT).show()
                }

            }

    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}