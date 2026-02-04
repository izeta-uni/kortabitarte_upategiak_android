package com.example.erronka4

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class Incidencias : AppCompatActivity() {

    private lateinit var rvIncidencias: RecyclerView
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_incidencias)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)
        rvIncidencias = findViewById(R.id.rvIncidencias)
        rvIncidencias.layoutManager = LinearLayoutManager(this)

        // Boton añadir incidencia
        val btnAddIncidencia = findViewById<FloatingActionButton>(R.id.btnAddIncidencia)
        btnAddIncidencia.setOnClickListener {
            val intent = Intent(this, CreateIncidencia::class.java)
            startActivity(intent)
        }

        // Logica cerrar sesion
        val btnLogout = findViewById<ImageButton>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            // Borrar SharedPreferences
            val settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            settings.edit().clear().apply()

            // Volver al Login
            val intent = Intent(this, Login::class.java)
            startActivity(intent)

            // Cerrar esta actividad para que no puedan volver atrás
            finish()
        }

    }

    override fun onResume() {
        super.onResume()
        refreshListData()
    }

    private fun refreshListData() {
        val listData = dbHelper.getAllIncidencias()

        // Pasamos la función de borrar cada incidencia al adaptador
        val adapter = IncidenciasAdapter(listData) { incidentToDelete ->
            showDeleteConfirmationDialog(incidentToDelete)
        }

        rvIncidencias.adapter = adapter
    }

    // Dialogo de confirmacion
    private fun showDeleteConfirmationDialog(incidencia: Incidencia) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Ezabatu gorabehera?")
        builder.setMessage("Ziur zaude '${incidencia.titulo}' ezabatu nahi duzula? Ekintza hau ezin da desegin.")

        // Botón SÍ
        builder.setPositiveButton("Bai, ezabatu") { dialog, _ ->
            val deletedROws = dbHelper.deleteIncidencia(incidencia.id)
            if (deletedROws > 0) {
                Toast.makeText(this, "Gorabehera ezabatua", Toast.LENGTH_SHORT).show()
                refreshListData()
            } else {
                Toast.makeText(this, "Errorea ezabatzerakoan", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        // Botón NO
        builder.setNegativeButton("Ez") { dialog, _ ->
            dialog.dismiss()
        }

        val alert = builder.create()
        alert.show()
    }
}