package com.example.erronka4

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
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
    private lateinit var cbIsAdmin: CheckBox

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
        cbIsAdmin = findViewById(R.id.cbIsAdmin) // Vinculamos checkbox

        // Inicializar la base de datos
        myDb = DatabaseHelper(this)

        btnRegister.setOnClickListener {

            val username = editTextRegisterUsername.text.toString()
            val password = editTextRegisterPassword.text.toString()
            val isAdmin = cbIsAdmin.isChecked

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

            // Obtenemos el hash de la contraseña
            val passwordHash = Hasher.hash(password.toCharArray())

            // Insertamos el nuevo suuario
            val id = myDb.insertUser(username, passwordHash, isAdmin)

            if (id > -1) {
                Toast.makeText(this, "Erabiltzailea egoki gorde da", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Errorea erabiltzailea gordetzerako garaian", Toast.LENGTH_SHORT).show()
            }

        }
    }
}