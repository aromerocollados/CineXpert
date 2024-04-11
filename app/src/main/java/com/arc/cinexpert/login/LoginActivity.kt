package com.arc.cinexpert.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.arc.cinexpert.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var autenticacion: FirebaseAuth
    private lateinit var edtNombreUsuario: EditText
    private lateinit var edtContrasena: EditText
    private lateinit var btnIniciarSesion: Button
    private lateinit var txtRegistrarse: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        autenticacion = FirebaseAuth.getInstance()

        edtNombreUsuario = findViewById(R.id.edtUsuario)
        edtContrasena = findViewById(R.id.edtPassword)
        btnIniciarSesion = findViewById(R.id.btnLogin)
        txtRegistrarse = findViewById(R.id.txvRegistro)

        btnIniciarSesion.setOnClickListener {
            val nombreUsuario = edtNombreUsuario.text.toString().trim()
            val contrasena = edtContrasena.text.toString().trim()
            if (nombreUsuario.isNotEmpty() && contrasena.isNotEmpty()) {
                // Aquí deberías buscar el correo electrónico asociado al nombre de usuario en tu base de datos
                // y luego llamar a iniciarSesion(correo, contrasena). Este paso se omite por simplicidad.
            } else {
                Toast.makeText(this, "Por favor, ingresa tus credenciales.", Toast.LENGTH_SHORT).show()
            }
        }

        txtRegistrarse.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
