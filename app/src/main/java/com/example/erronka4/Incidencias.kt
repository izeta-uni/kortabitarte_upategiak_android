package com.example.erronka4

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button // Importante
import android.widget.ImageButton
import android.widget.TextView
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
    private lateinit var tvEmptyView: TextView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var btnRegisterUser: Button

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

        // Inicializamos las vistas
        rvIncidencias = findViewById(R.id.rvIncidencias)
        tvEmptyView = findViewById(R.id.tvEmptyView)
        btnRegisterUser = findViewById(R.id.btnRegisterUser)

        // Comprobar si es admin
        val settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isAdmin = settings.getBoolean("isAdmin", false)

        if (isAdmin) {
            btnRegisterUser.visibility = View.VISIBLE
            btnRegisterUser.setOnClickListener {
                val intent = Intent(this, Register::class.java)
                startActivity(intent)
            }
        } else {
            btnRegisterUser.visibility = View.GONE
        }

        rvIncidencias.layoutManager = LinearLayoutManager(this)

        val btnAddIncidencia = findViewById<FloatingActionButton>(R.id.btnAddIncidencia)
        btnAddIncidencia.setOnClickListener {
            val intent = Intent(this, CreateIncidencia::class.java)
            startActivity(intent)
        }

        val btnLogout = findViewById<ImageButton>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            settings.edit().clear().apply()

            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        refreshListData()
    }

    private fun refreshListData() {
        val listData = dbHelper.getAllIncidencias()

        if (listData.isEmpty()) {
            tvEmptyView.visibility = View.VISIBLE
            rvIncidencias.visibility = View.GONE
        } else {
            tvEmptyView.visibility = View.GONE
            rvIncidencias.visibility = View.VISIBLE
        }

        val adapter = IncidenciasAdapter(listData) { incidentToDelete ->
            showDeleteConfirmationDialog(incidentToDelete)
        }

        rvIncidencias.adapter = adapter
    }

    private fun showDeleteConfirmationDialog(incidencia: Incidencia) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Ezabatu gorabehera?")
        builder.setMessage("Ziur zaude '${incidencia.titulo}' ezabatu nahi duzula? Ekintza hau ezin da desegin.")

        builder.setPositiveButton("Bai, ezabatu") { dialog, _ ->
            val deletedRows = dbHelper.deleteIncidencia(incidencia.id)
            if (deletedRows > 0) {
                Toast.makeText(this, "Gorabehera ezabatua", Toast.LENGTH_SHORT).show()
                refreshListData()
            } else {
                Toast.makeText(this, "Errorea ezabatzerakoan", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Ez") { dialog, _ ->
            dialog.dismiss()
        }

        val alert = builder.create()
        alert.show()
    }
}