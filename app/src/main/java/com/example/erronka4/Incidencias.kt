package com.example.erronka4

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View // Importante añadir esto para View.VISIBLE/GONE
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
    private lateinit var tvEmptyView: TextView // Variable para el mensaje
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

        // Inicializamos las vistas
        rvIncidencias = findViewById(R.id.rvIncidencias)
        tvEmptyView = findViewById(R.id.tvEmptyView) // Inicializamos el TextView nuevo

        rvIncidencias.layoutManager = LinearLayoutManager(this)

        val btnAddIncidencia = findViewById<FloatingActionButton>(R.id.btnAddIncidencia)
        btnAddIncidencia.setOnClickListener {
            val intent = Intent(this, CreateIncidencia::class.java)
            startActivity(intent)
        }

        val btnLogout = findViewById<ImageButton>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            val settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            settings.edit().clear().apply()

            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }

        // Si borramos la aplicacion se pierden los registros. Esto se usa para popular la lista.
        // populateIncidencesDatabase()


    }

    private fun populateIncidencesDatabase() {
        val tituluak = listOf(
            "Botila apurtuta",
            "Robotaren errorea",
            "Tenperatura altuegia",
            "Bidalketa atzeratua (UK)",
            "Mahastian onddoak",
            "Etiketa okerrak",
            "Biltegiko argiak",
            "Uraren filtrazioa",
            "Kaxa galduak",
            "Sistemaren hutsegitea"
        )

        val deskripzioak = listOf(
            "Biltegian kaxa bat erori da eta 6 botila apurtu dira.",
            "Etiketatzeko makina gelditu da 3. linean.",
            "Hartzigarriaren tenperatura 28ºC-ra igo da, arriskutsua.",
            "Londresera doan kamioia ez da iritsi orduan.",
            "Ourenseko mahastian arazoak ikusi dira hostoetan.",
            "Gourmet Ardoaren etiketak gaizki inprimatu dira.",
            "Pasabide nagusiko argiak funditu dira.",
            "Bulego nagusian ura sartzen ari da euriteagatik.",
            "Inbentarioan 10 kaxa falta dira B2 sekzioan.",
            "Zerbitzaria erori da eta ezin da eskaerarik sartu."
        )

        // Insertamos 10 incidencias
        for (i in tituluak.indices) {
            dbHelper.insertIncidencia(
                titulo = tituluak[i],
                descripcion = deskripzioak[i],
                fecha = "04/02/2026",
                uri = "" // Sin imagen
            )
        }

        // Refrescamos la lista para verlo al momento
        refreshListData()
        Toast.makeText(this, "10 gorabehera sortu dira!", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        refreshListData()
    }

    private fun refreshListData() {
        // Obtenemos los datos
        val listData = dbHelper.getAllIncidencias()

        // Logica de vista vacia
        if (listData.isEmpty()) {
            // Si no hay datos muestra mensaje y oculta lista
            tvEmptyView.visibility = View.VISIBLE
            rvIncidencias.visibility = View.GONE
        } else {
            // Si hay datos oculta mensaje y muestra lista
            tvEmptyView.visibility = View.GONE
            rvIncidencias.visibility = View.VISIBLE
        }
        // -----------------------------

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
                refreshListData() // Al recargar, se actualizará el mensaje si la lista queda vacía
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