package com.example.findyourwg

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.auth.FirebaseAuth

/**
 * regelt die Login Seite
 */

//TODO check for speed, integrate AuthListener (see other project)
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

    }

    override fun onResume() {
        super.onResume()
        findViewById<AppCompatButton>(R.id.login_button_login).setOnClickListener {
            val email = findViewById<EditText>(R.id.email_edittext_login).text.toString()
            val password = findViewById<EditText>(R.id.password_edittext_login).text.toString()

            Log.d("Login", "Attempt login with email/pw: $email/***")

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }


            //show error message if login not successful
            FirebaseAuth.getInstance()!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    this@LoginActivity
                ) { task ->
                    if (!task.isSuccessful) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Login nicht erfolgreich, überprüfe Emailadresse und Passwort",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

        }

    }
}
