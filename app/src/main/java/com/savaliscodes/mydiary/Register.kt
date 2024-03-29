package com.savaliscodes.mydiary


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth

class Register : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //handle account delete with appropriate toast
        val toast = intent.getStringExtra("del")
        val name = intent.getStringExtra("name")
        if(112.equals(toast)){
            Toast.makeText(this, "Its sad to see you leave.$name" +
                    "Would you like to create another Account?", Toast.LENGTH_LONG).show()
        }
        val regBtn = findViewById<Button>(R.id.btnReg)
        //register User
        regBtn.setOnClickListener {
            registerUser()
        }

    }

    private fun registerUser() {

        val userName = findViewById<EditText>(R.id.Username)
        val password = findViewById<EditText>(R.id.pass)
        val password1 = findViewById<EditText>(R.id.pass1)

        val progress = findViewById<ProgressBar>(R.id.bar1)

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
        progress.isVisible = true
        mAuth.createUserWithEmailAndPassword(uEmail, uPass)
            .addOnCompleteListener(this){ task->
                if(task.isSuccessful){
                    //hide progress bar
                    progress.isInvisible = true
                    //get current user
                    val user = mAuth.currentUser
                    val userID = user?.uid.toString()
                    val uMail = user?.email.toString()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("uReg", userID)
                    intent.putExtra("mail", uMail)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }  else {
                    progress.isInvisible = true
                    finish()
                    Toast.makeText(this,task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }

    override fun onBackPressed() {
        finish()
    }
}

