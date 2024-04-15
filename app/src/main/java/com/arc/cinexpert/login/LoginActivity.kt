package com.arc.cinexpert.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.arc.cinexpert.MainActivity
import com.arc.cinexpert.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var autenticacion: FirebaseAuth
    private lateinit var edtUsuario: EditText
    private lateinit var edtContrasena: EditText
    private lateinit var btnIniciarSesion: Button
    private lateinit var txtRegistrarse: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        autenticacion = FirebaseAuth.getInstance()

        edtUsuario = findViewById(R.id.edtUsuario)
        edtContrasena = findViewById(R.id.edtPassword)
        btnIniciarSesion = findViewById(R.id.btnLogin)
        txtRegistrarse = findViewById(R.id.btnRegistro)

        btnIniciarSesion.setOnClickListener {
            val usuario = edtUsuario.text.toString().trim()
            val contrasena = edtContrasena.text.toString().trim()
            if (usuario.isNotEmpty() && contrasena.isNotEmpty()) {
                iniciarSesion(usuario, contrasena)
            } else {
                Toast.makeText(this, "Por favor, ingresa tus credenciales.", Toast.LENGTH_SHORT).show()
            }
        }

        txtRegistrarse.setOnClickListener {
            // Navegar a RegistroActivity
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun iniciarSesion(correo: String, contrasena: String) {
        autenticacion.signInWithEmailAndPassword(correo, contrasena).addOnCompleteListener(this) { tarea ->
            if (tarea.isSuccessful) {
                Toast.makeText(this, "Inicio de sesión exitoso.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                // Aquí puedes iniciar la actividad principal de tu app o realizar otra acción
            } else {
                Toast.makeText(this, "Error de inicio de sesión: ${tarea.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
