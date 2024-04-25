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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var autenticacion: FirebaseAuth
    private lateinit var edtUsuario: EditText
    private lateinit var edtContrasena: EditText
    private lateinit var btnIniciarSesion: Button
    private lateinit var txtRegistrarse: TextView
    private lateinit var googleSignInClient: GoogleSignInClient
    companion object { private const val RC_SIGN_IN = 9001 }

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

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Botón para iniciar sesión con Google
        val btnSignInGoogle = findViewById<Button>(R.id.btnGoogle)
        btnSignInGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Error de Google Sign In: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        autenticacion.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Inicio de sesión exitoso, navegar al MainActivity
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Finaliza esta actividad para que el usuario no pueda volver a ella
                } else {
                    // Si falla el inicio de sesión, mostrar mensaje al usuario.
                    Toast.makeText(this, "Error de inicio de sesión: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
