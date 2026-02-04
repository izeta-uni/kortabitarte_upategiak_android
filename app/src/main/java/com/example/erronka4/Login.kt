package com.example.erronka4

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

const val PREFS_NAME = "settings"

class Login : AppCompatActivity() {

    private lateinit var myDb: DatabaseHelper
    private lateinit var btnLogin: Button
    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var btnGoToRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Logica de auto login (shared preferences)
        val settings: SharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Verificamos si existe la clave "username"
        if (settings.contains("username")) {
            // Si existe, vamos directo a Incidencias sin pedir contraseña
            val intent = Intent(this, Incidencias::class.java)
            startActivity(intent)
            finish() // Cerramos Login para que no pueda volver atrás
        }

        // Vincular vistas
        btnLogin = findViewById(R.id.btnCreateAcount)
        editTextUsername = findViewById(R.id.editTextRegisterUsername)
        editTextPassword = findViewById(R.id.editTextRegisterPassword)
        btnGoToRegister = findViewById(R.id.btnGoToRegister)

        // Inicializar la base de datos
        myDb = DatabaseHelper(this)

        // Establecer el listener del botón
        btnLogin.setOnClickListener {
            val username = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()

            val user = myDb.getUser(username)

            if (user != null) {
                if (Hasher.verify(password.toCharArray(), user.passwordHash)) {

                    // Guardar sesion al entrar
                    val editor = settings.edit()
                    editor.putString("username", username)
                    editor.apply()

                    val intent = Intent(this, Incidencias::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Pasahitza okerra", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Erabiltzailea hau ez dago erregistratuta", Toast.LENGTH_SHORT).show()
            }
        }

        btnGoToRegister.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
    }
}