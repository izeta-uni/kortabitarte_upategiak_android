package com.example.erronka4

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Register : AppCompatActivity() {

    private lateinit var myDb: DatabaseHelper
    private lateinit var btnRegister: Button
    private lateinit var editTextRegisterUsername: EditText
    private lateinit var editTextRegisterPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Vincular vistas
        btnRegister = findViewById<Button>(R.id.btnCreateAcount)
        editTextRegisterUsername = findViewById<EditText>(R.id.editTextRegisterUsername)
        editTextRegisterPassword = findViewById<EditText>(R.id.editTextRegisterPassword)

        // Inicializar la base de datos
        myDb = DatabaseHelper(this)

        btnRegister.setOnClickListener {

            val username = editTextRegisterUsername.text.toString()
            val password = editTextRegisterPassword.text.toString()

            if (username.isEmpty()) {
                editTextRegisterUsername.error = "Username is required"
                editTextRegisterUsername.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                editTextRegisterPassword.error = "Password is required"
                editTextRegisterPassword.requestFocus()
                return@setOnClickListener
            }

            val passwordHash = Hasher.hash(password.toCharArray())

            val id = myDb.insertUser(username, passwordHash)
            if (id > -1) {
                Toast.makeText(this, "Usuario Guardado correctamente", Toast.LENGTH_SHORT).show()
                editTextRegisterUsername.text.clear()
                editTextRegisterPassword.text.clear()
            } else {
                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
            }

        }
    }
}