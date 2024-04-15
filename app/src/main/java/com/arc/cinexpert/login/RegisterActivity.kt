package com.arc.cinexpert.login

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.arc.cinexpert.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var autenticacion: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var edtCorreo: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtConfirmarPassword: EditText
    private lateinit var edtNombre: EditText
    private lateinit var edtApellidos: EditText
    private lateinit var edtUsuario: EditText
    private lateinit var btnRegistrar: Button
    private lateinit var txtTengoCuenta: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        autenticacion = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        edtNombre = findViewById(R.id.edtNombre)
        edtApellidos = findViewById(R.id.edtApellidos)
        edtUsuario = findViewById(R.id.edtUsuario)
        edtCorreo = findViewById(R.id.edtCorreo)
        edtPassword = findViewById(R.id.edtPassword)
        edtConfirmarPassword = findViewById(R.id.edtConfirmarPassword)
        btnRegistrar = findViewById(R.id.btnConfirmarRegistro)
        txtTengoCuenta = findViewById(R.id.txtTengoCuenta)

        btnRegistrar.setOnClickListener {
            val correo = edtCorreo.text.toString().trim()
            val usuario = edtUsuario.text.toString().trim()

            if (camposValidos(correo, usuario)) {
                registrarUsuario(correo, edtPassword.text.toString().trim(), edtNombre.text.toString().trim(),
                    edtApellidos.text.toString().trim(), usuario)
            }
        }

        txtTengoCuenta.setOnClickListener {
            finish() // Volver al inicio de sesión
        }
    }

    private fun camposValidos(correo: String, usuario: String): Boolean {
        var valid = true
        if (correo.isEmpty() || usuario.isEmpty() || edtPassword.text.toString().trim().isEmpty() ||
            edtNombre.text.toString().trim().isEmpty() || edtApellidos.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
            valid = false
        } else if (edtPassword.text.toString() != edtConfirmarPassword.text.toString()) {
            Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
            valid = false
        } else {
            db.collection("usuarios").whereEqualTo("correo", correo).get().addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    Toast.makeText(this, "El correo ya está en uso.", Toast.LENGTH_SHORT).show()
                    valid = false
                }
            }
            db.collection("usuarios").whereEqualTo("usuario", usuario).get().addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    Toast.makeText(this, "El nombre de usuario ya está en uso.", Toast.LENGTH_SHORT).show()
                    valid = false
                }
            }
        }
        return valid
    }

    private fun registrarUsuario(correo: String, password: String, nombre: String, apellidos: String, usuario: String) {
        if (correo.isNotEmpty() && password.isNotEmpty() && nombre.isNotEmpty() && apellidos.isNotEmpty() && usuario.isNotEmpty()) {
            autenticacion.createUserWithEmailAndPassword(correo, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = autenticacion.currentUser?.uid
                    val usuarioData = hashMapOf(
                        "nombre" to nombre,
                        "apellidos" to apellidos,
                        "usuario" to usuario,
                        "correo" to correo
                    )

                    userId?.let {
                        db.collection("usuarios").document(it)
                            .set(usuarioData)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Registro exitoso.", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error al guardar los datos: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(this, "Error de registro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}