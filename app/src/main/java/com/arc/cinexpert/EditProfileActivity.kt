package com.arc.cinexpert

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var editTextName: EditText
    private lateinit var editTextSurname: EditText
    private lateinit var editTextUsername: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var buttonSaveProfile: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        editTextName = findViewById(R.id.editTextName)
        editTextSurname = findViewById(R.id.editTextSurname)
        editTextUsername = findViewById(R.id.editTextUsername)
        editTextEmail = findViewById(R.id.editTextEmail)
        buttonSaveProfile = findViewById(R.id.buttonSaveProfile)

        loadUserProfile()

        buttonSaveProfile.setOnClickListener {
            if (validateInputs()) {
                saveUserProfile()
            }
        }
    }

    private fun loadUserProfile() {
        val user = auth.currentUser
        if (user != null) {
            firestore.collection("usuarios").document(user.uid).get().addOnSuccessListener { document ->
                if (document != null) {
                    editTextName.setText(document.getString("nombre"))
                    editTextSurname.setText(document.getString("apellidos"))
                    editTextUsername.setText(document.getString("usuario"))
                    editTextEmail.setText(document.getString("correo"))
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        val name = editTextName.text.toString().trim()
        val surname = editTextSurname.text.toString().trim()
        val username = editTextUsername.text.toString().trim()
        val email = editTextEmail.text.toString().trim()

        if (name.isEmpty()) {
            editTextName.error = "El nombre no puede estar vacío"
            editTextName.requestFocus()
            return false
        }

        if (surname.isEmpty()) {
            editTextSurname.error = "El apellido no puede estar vacío"
            editTextSurname.requestFocus()
            return false
        }

        if (username.isEmpty()) {
            editTextUsername.error = "El nombre de usuario no puede estar vacío"
            editTextUsername.requestFocus()
            return false
        }

        if (email.isEmpty()) {
            editTextEmail.error = "El correo no puede estar vacío"
            editTextEmail.requestFocus()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.error = "Por favor, introduce un correo válido"
            editTextEmail.requestFocus()
            return false
        }

        return true
    }

    private fun saveUserProfile() {
        val user = auth.currentUser
        if (user != null) {
            val updatedUser = hashMapOf(
                "nombre" to editTextName.text.toString(),
                "apellidos" to editTextSurname.text.toString(),
                "usuario" to editTextUsername.text.toString(),
                "correo" to editTextEmail.text.toString()
            )

            firestore.collection("usuarios").document(user.uid).set(updatedUser)
                .addOnSuccessListener {
                    Toast.makeText(this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al actualizar el perfil: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
