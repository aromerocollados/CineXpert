package com.arc.cinexpert

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.arc.cinexpert.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            // No hay usuario logueado, redirige a LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        // Si hay usuario, permanece en esta actividad o redirige a otra actividad principal
    }
}
