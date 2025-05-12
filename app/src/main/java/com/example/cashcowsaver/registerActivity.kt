package com.example.cashcowsaver

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val RC_SIGN_UP = 9001
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        auth = FirebaseAuth.getInstance()

        val name = findViewById<EditText>(R.id.edtname)
        val surname = findViewById<EditText>(R.id.edtsurname)
        val password1 = findViewById<EditText>(R.id.edtpass)
        val email = findViewById<EditText>(R.id.edtemailaddress)
        val confirmpass = findViewById<EditText>(R.id.edtconfirm)
        val register = findViewById<Button>(R.id.btnsign)

        //configure google sign in//
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))//from firebase//
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        //set click listener for sign-up//
        findViewById<LinearLayout>(R.id.googlesigin).setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_UP)
        }

        //TabsignUp and TabsignIn//
        val tabSignUp = findViewById<TextView>(R.id.tab_sign_up)
        val tabSignIn = findViewById<TextView>(R.id.tab_sign_in)

        tabSignUp.setBackgroundResource(R.drawable.tab_selector)
        tabSignIn.setBackgroundColor(Color.TRANSPARENT)

        tabSignIn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        register.setOnClickListener {
            val firstname = name.text.toString().trim()
            val lastname = surname.text.toString().trim()
            val mail = email.text.toString().trim()
            val pass1 = password1.text.toString().trim()
            val confirm = confirmpass.text.toString().trim()

            if(confirm != pass1){
                Toast.makeText(this,"This Password does not match!!!",Toast.LENGTH_LONG).show()
            }
            else{
                if(firstname.isNotEmpty() && lastname.isNotEmpty() && pass1.isNotEmpty() && confirm.isNotEmpty()){
                    auth.createUserWithEmailAndPassword(mail,pass1).addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            Toast.makeText(this,"Registration successful!!",Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                        else{
                            Toast.makeText(this,"Sign in failed: ${task.exception?.message}",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else{
                    Toast.makeText(this, "PLease fill in the details.",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_UP){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try{
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            }
            catch (e: ApiException){
                Toast.makeText(this,"Sign up failed ${e.message}",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String){
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful){
                    //after successful sign-up, go to login page//
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                else{
                    Toast.makeText(this, "Register failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}