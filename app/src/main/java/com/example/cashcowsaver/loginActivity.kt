package com.example.cashcowsaver

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin)

        auth = FirebaseAuth.getInstance()
        val user = findViewById<EditText>(R.id.edtemailaddress)
        val pass = findViewById<EditText>(R.id.edtpass)
        val noaccount = findViewById<TextView>(R.id.txtregister)
        val guest = findViewById<LinearLayout>(R.id.guestsignin)
        val login = findViewById<Button>(R.id.btnsignin)

        //configure google sign-in//
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) //from firebase console//
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        //google sign-in button click//
        findViewById<LinearLayout>(R.id.googlesigin).setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
        guest.setOnClickListener {
            signInAnonymously()
        }

        //TabsignUp and TabsignIn//
        val tabSignUp = findViewById<TextView>(R.id.tab_sign_up)
        val tabSignIn = findViewById<TextView>(R.id.tab_sign_in)

        tabSignIn.setBackgroundResource(R.drawable.tab_selector)
        tabSignUp.setBackgroundColor(Color.TRANSPARENT)

        tabSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        login.setOnClickListener {
            val username = user.text.toString().trim()
            val password = pass.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Login Successful!!", Toast.LENGTH_LONG).show()
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "Login failed ${task.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please enter all the details", Toast.LENGTH_LONG).show()

            }
        }
        noaccount.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

//launch google sign in intent//
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
    startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    //HANDLE GOOGLE SIGN IN RESULT//
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            }
            catch (e: ApiException){
                Toast.makeText(this,"Login failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    //firebase authentication with google token//
    private fun firebaseAuthWithGoogle(idToken: String){
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful){
                   ///go to home page after successful login//
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
                else{
                    Toast.makeText(this,"Login Failed", Toast.LENGTH_LONG).show()
                }
            }
    }
    private fun signInAnonymously() {
        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val users = auth.currentUser
                    Toast.makeText(
                        this,
                        "Signed in as Guest (UID: ${users?.uid}",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Guest sign-in failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}