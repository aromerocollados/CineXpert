package com.arc.cinexpert.login

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.arc.cinexpert.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var autenticacion: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var edtCorreo: EditText
    private lateinit var edtContrasena: EditText
    private lateinit var edtNombre: EditText
    private lateinit var edtApellidos: EditText
    private lateinit var edtEdad: EditText
    private lateinit var btnRegistrar: Button
    private lateinit var spnProvincia: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        autenticacion = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        edtCorreo = findViewById(R.id.edtCorreo)
        edtContrasena = findViewById(R.id.edtPassword)
        edtNombre = findViewById(R.id.edtNombre)
        edtApellidos = findViewById(R.id.edtApellidos)
        edtEdad = findViewById(R.id.edtEdad)
        btnRegistrar = findViewById(R.id.btnConfirmarRegistro)

        // Inicializa el Spinner
        spnProvincia = findViewById(R.id.spnProvincia)
        // Suponiendo que tienes un array de provincias en strings.xml
        ArrayAdapter.createFromResource(
            this,
            R.array.provincias_espanolas,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spnProvincia.adapter = adapter
        }

        btnRegistrar.setOnClickListener {
            val correo = edtCorreo.text.toString().trim()
            val contrasena = edtContrasena.text.toString().trim()
            val nombre = edtNombre.text.toString().trim()
            val apellidos = edtApellidos.text.toString().trim()
            val edad = edtEdad.text.toString().trim().toIntOrNull()
            val provincia = spnProvincia.selectedItem.toString()


            if (correo.isNotEmpty() && contrasena.isNotEmpty() && nombre.isNotEmpty() && apellidos.isNotEmpty() && edad != null) {
                registrarUsuario(correo, contrasena, nombre, apellidos, edad, provincia)
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos correctamente.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registrarUsuario(correo: String, contrasena: String, nombre: String, apellidos: String, edad: Int, provincia: String) {
        autenticacion.createUserWithEmailAndPassword(correo, contrasena).addOnCompleteListener(this) { tarea ->
            if (tarea.isSuccessful) {
                val userId = autenticacion.currentUser?.uid
                val usuario = hashMapOf(
                    "nombre" to nombre,
                    "apellidos" to apellidos,
                    "correo" to correo,
                    "edad" to edad,
                    "provincia" to provincia // Agrega la provincia aquí
                )

                userId?.let {
                    db.collection("usuarios").document(it)
                        .set(usuario)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Registro exitoso.", Toast.LENGTH_SHORT).show()
                            finish() // Finaliza esta actividad y vuelve a LoginActivity
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al guardar los datos: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                Toast.makeText(this, "Error de registro: ${tarea.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
