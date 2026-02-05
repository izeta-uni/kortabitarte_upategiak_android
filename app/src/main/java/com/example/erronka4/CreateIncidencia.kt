package com.example.erronka4

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CreateIncidencia : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etDescription: EditText
    private lateinit var etDate: EditText
    private lateinit var ivImage: ImageView
    private lateinit var btnSave: Button
    private lateinit var dbHelper: DatabaseHelper

    private var selectedImageUri: String = ""
    private var tempImageUri: Uri? = null // Para guardar la uri temporal de la cámara

    // Launcher para la galeria
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) {
            val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
            contentResolver.takePersistableUriPermission(uri, flag)
            selectedImageUri = uri.toString()
            ivImage.setImageURI(uri)
        }
    }

    // Launcher para la camara
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess && tempImageUri != null) {
            // Si la foto se sacó bien, la mostramos y guardamos la URI
            selectedImageUri = tempImageUri.toString()
            ivImage.setImageURI(tempImageUri)
        }
    }

    // Launcher para pedir permisos de la camara
    private val requestCameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(this, "Kamera baimena behar da", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_incidencia)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)
        etTitle = findViewById(R.id.editTextTitle)
        etDescription = findViewById(R.id.editTextDescription)
        etDate = findViewById(R.id.editTextDate)
        ivImage = findViewById(R.id.imageView)
        btnSave = findViewById(R.id.btnCreateIncidencia)

        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        etDate.setText(currentDate)

        // Al hacer clic en la imagen, mostramos el diálogo de selección
        ivImage.setOnClickListener {
            showImageSelectionDialog()
        }

        btnSave.setOnClickListener {
            saveIncidencia()
        }

        // Mostrar el calendario al hacer click en el input de fecha
        etDate.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun showImageSelectionDialog() {
        val options = arrayOf("Kamera", "Galeria")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Aukeratu irudia")
        builder.setItems(options) { _, which ->
            if (which == 0) {
                // Opción 0: Cámara -> Comprobar permisos
                checkCameraPermission()
            } else {
                // Opción 1: Galería -> Abrir directo
                // (OpenDocument no necesita permiso explícito en Android moderno, es automático y seguro)
                pickImageLauncher.launch(arrayOf("image/*"))
            }
        }
        builder.show()
    }

    private fun showDatePickerDialog() {
        val calendar = java.util.Calendar.getInstance()
        val year = calendar.get(java.util.Calendar.YEAR)
        val month = calendar.get(java.util.Calendar.MONTH)
        val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)

        android.app.DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            // Formateamos la fecha a dd/MM/yyyy
            val formattedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear)
            etDate.setText(formattedDate)
        }, year, month, day).show()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Ya tenemos permiso
            openCamera()
        } else {
            // Pedimos permiso
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openCamera() {
        // Crear un archivo temporal vacío para guardar la foto
        val photoFile = File.createTempFile(
            "IMG_${System.currentTimeMillis()}_",
            ".jpg",
            cacheDir
        )

        // Guardamos la URI en la variable global
        tempImageUri = FileProvider.getUriForFile(
            this,
            "${packageName}.provider",
            photoFile
        )

        // Lanzar la cámara de fomra segura
        // Usamos ?.let para asegurarnos de que la uri no es nula
        tempImageUri?.let { uri ->
            takePictureLauncher.launch(uri)
        }
    }

    private fun saveIncidencia() {
        val title = etTitle.text.toString()
        val description = etDescription.text.toString()
        val date = etDate.text.toString()

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Bete titulu eta deskripzio eremuak", Toast.LENGTH_SHORT).show()
            return
        }

        val result = dbHelper.insertIncidencia(title, description, date, selectedImageUri)

        if (result > -1) {
            Toast.makeText(this, "Gorabehera egoki sortu da", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Errorea", Toast.LENGTH_SHORT).show()
        }
    }
}