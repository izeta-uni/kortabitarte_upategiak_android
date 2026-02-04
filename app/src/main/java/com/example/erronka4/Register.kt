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
        btnRegister = findViewById(R.id.btnCreateAcount)
        editTextRegisterUsername = findViewById(R.id.editTextRegisterUsername)
        editTextRegisterPassword = findViewById(R.id.editTextRegisterPassword)

        // Inicializar la base de datos
        myDb = DatabaseHelper(this)

        btnRegister.setOnClickListener {

            val username = editTextRegisterUsername.text.toString()
            val password = editTextRegisterPassword.text.toString()

            if (username.isEmpty()) {
                editTextRegisterUsername.error = "Erabiltzailea derrigorrezkoa da"
                editTextRegisterUsername.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                editTextRegisterPassword.error = "Pasahitza derrigorrezkoa da"
                editTextRegisterPassword.requestFocus()
                return@setOnClickListener
            }

            val passwordHash = Hasher.hash(password.toCharArray())

            val id = myDb.insertUser(username, passwordHash)
            if (id > -1) {
                Toast.makeText(this, "Erabiltzailea egoki gorde da", Toast.LENGTH_SHORT).show()
                editTextRegisterUsername.text.clear()
                editTextRegisterPassword.text.clear()
            } else {
                Toast.makeText(this, "Errorea erabiltzailea gordetzerako garaian", Toast.LENGTH_SHORT).show()
            }

        }
    }
}