package com.arc.cinexpert.login

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.arc.cinexpert.R
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var autenticacion: FirebaseAuth
    private lateinit var edtCorreo: EditText
    private lateinit var edtContrasena: EditText
    private lateinit var btnConfirmarRegistro: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        autenticacion = FirebaseAuth.getInstance()

        edtCorreo = findViewById(R.id.edtCorreo)
        edtContrasena = findViewById(R.id.edtPassword)
        btnConfirmarRegistro = findViewById(R.id.btnConfirmarRegistro)

        btnConfirmarRegistro.setOnClickListener {
            val correo = edtCorreo.text.toString().trim()
            val contrasena = edtContrasena.text.toString().trim()
            if (correo.isNotEmpty() && contrasena.isNotEmpty()) {
                registrarUsuario(correo, contrasena)
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registrarUsuario(correo: String, contrasena: String) {
        autenticacion.createUserWithEmailAndPassword(correo, contrasena).addOnCompleteListener(this) { tarea ->
            if (tarea.isSuccessful) {
                // Registro exitoso
                Toast.makeText(this, "Registro exitoso.", Toast.LENGTH_SHORT).show()
                finish() // O redirige al usuario donde quieras
            } else {
                // Error de registro
                Toast.makeText(this, "Error de registro: ${tarea.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
