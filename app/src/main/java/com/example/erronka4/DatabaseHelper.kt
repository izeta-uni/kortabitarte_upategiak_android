package com.example.erronka4

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class User(
    val id: Int,
    val username: String,
    val passwordHash: String,
)

data class Incidencia(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val fecha: String,
    val imageUri: String
)

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "Erronka4.db"

        private const val DATABASE_VERSION = 2

        // --- TABLA USUARIOS ---
        private const val TABLE_USERS = "users"
        private const val COL_USER_ID = "id"
        private const val COL_USERNAME = "username"
        private const val COL_PASSWORD_HASH = "password_hash"

        // --- TABLA INCIDENCIAS ---
        private const val TABLE_INCIDENCIAS = "incidencias"
        private const val COL_INC_ID = "id"
        private const val COL_INC_TITULO = "titulo"
        private const val COL_INC_DESCRIPCION = "descripcion"
        private const val COL_INC_FECHA = "fecha"
        private const val COL_INC_IMAGE_URI = "image_uri"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // 1. Crear Tabla Usuarios
        val createTableUsers = """
            CREATE TABLE $TABLE_USERS (
                $COL_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USERNAME TEXT,
                $COL_PASSWORD_HASH TEXT
            )
        """.trimIndent()
        db.execSQL(createTableUsers)

        // 2. Crear Tabla Incidencias
        val createTableIncidencias = """
            CREATE TABLE $TABLE_INCIDENCIAS (
                $COL_INC_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_INC_TITULO TEXT,
                $COL_INC_DESCRIPCION TEXT,
                $COL_INC_FECHA TEXT,
                $COL_INC_IMAGE_URI TEXT
            )
        """.trimIndent()
        db.execSQL(createTableIncidencias)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // (En una app real profesional usaríamos ALTER TABLE para no perder datos)
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_INCIDENCIAS")
        onCreate(db)
    }

    // ==========================================
    // MÉTODOS PARA USUARIOS
    // ==========================================

    fun insertUser(username: String, passwordHash: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_USERNAME, username)
            put(COL_PASSWORD_HASH, passwordHash)
        }
        val result = db.insert(TABLE_USERS, null, values)
        db.close()
        return result
    }

    fun getUser(username: String): User? {
        val db = this.readableDatabase
        val selection = "$COL_USERNAME = ?"
        val selectionArgs = arrayOf(username)

        val cursor = db.query(TABLE_USERS, null, selection, selectionArgs, null, null, null)
        var user: User? = null

        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID))
            val storedUsername = cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME))
            val storedHash = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD_HASH))
            user = User(id, storedUsername, storedHash)
        }
        cursor.close()
        db.close()
        return user
    }

    // ==========================================
    // MÉTODOS PARA INCIDENCIAS
    // ==========================================

    fun insertIncidencia(titulo: String, descripcion: String, fecha: String, uri: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_INC_TITULO, titulo)
            put(COL_INC_DESCRIPCION, descripcion)
            put(COL_INC_FECHA, fecha)
            put(COL_INC_IMAGE_URI, uri)
        }
        val result = db.insert(TABLE_INCIDENCIAS, null, values)
        db.close()
        return result
    }

    fun getAllIncidencias(): List<Incidencia> {
        val lista = ArrayList<Incidencia>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_INCIDENCIAS ORDER BY $COL_INC_ID DESC" // Las nuevas primero

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_INC_ID))
                val titulo = cursor.getString(cursor.getColumnIndexOrThrow(COL_INC_TITULO))
                val desc = cursor.getString(cursor.getColumnIndexOrThrow(COL_INC_DESCRIPCION))
                val fecha = cursor.getString(cursor.getColumnIndexOrThrow(COL_INC_FECHA))
                val uri = cursor.getString(cursor.getColumnIndexOrThrow(COL_INC_IMAGE_URI))

                lista.add(Incidencia(id, titulo, desc, fecha, uri))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    fun deleteIncidencia(id: Int): Int {
        val db = this.writableDatabase
        // Borramos la fila donde el ID coincida
        val result = db.delete(TABLE_INCIDENCIAS, "$COL_INC_ID = ?", arrayOf(id.toString()))
        db.close()
        return result
    }
}