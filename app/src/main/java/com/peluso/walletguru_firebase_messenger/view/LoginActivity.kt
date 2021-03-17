package com.peluso.walletguru_firebase_messenger.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.peluso.walletguru_firebase_messenger.R

const val UNAME_KEY = "UNAME"

class LoginActivity : AppCompatActivity() {

    private lateinit var editText: EditText
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        editText = findViewById(R.id.login_edittext)
        button = findViewById(R.id.login_button)
        button.setOnClickListener {
            if (editText.text.isBlank()) {
                Toast.makeText(this, "Need a username to continue", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val username = editText.text.toString()
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra(UNAME_KEY, username)
            }
            startActivity(intent)
        }
    }

}