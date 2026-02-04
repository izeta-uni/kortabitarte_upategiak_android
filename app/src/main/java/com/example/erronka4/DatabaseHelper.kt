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

    class DatabaseHelper(context: Context) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        companion object {
            // Nombre de la base de datos
            private const val DATABASE_NAME = "Erronka4.db"
            private const val DATABASE_VERSION = 1

            // Nombre de la tabla y columnas
            private const val TABLE_USERS = "users"
            private const val COL_ID = "id"
            private const val COL_USERNAME = "username"
            private const val COL_PASSWORD_HASH = "password_hash"
        }

        // Crear la tabla
        override fun onCreate(db: SQLiteDatabase) {
            val createTable = """
                CREATE TABLE $TABLE_USERS (
                    $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COL_USERNAME TEXT,
                    $COL_PASSWORD_HASH TEXT
                )
            """.trimIndent()
            db.execSQL(createTable)
        }

        // Actualizar la tabla (si cambias la versión de la BD)
        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
            onCreate(db)
        }

        // Insert user
        fun insertUser(username: String, passwordHash: String): Long {
            val db = this.writableDatabase
            val values = ContentValues().apply {
                put(COL_USERNAME, username)
                put(COL_PASSWORD_HASH, passwordHash)
            }
            // Retorna el ID del nuevo registro o -1 si hubo error
            val result = db.insert(TABLE_USERS, null, values)
            db.close() // Siempre cerrar la conexión
            return result
        }

        fun getUser(username: String): User? {

            val db = this.readableDatabase

            // 1. Condición: Buscar donde el username coincida
            val selection = "$COL_USERNAME = ?"
            val selectionArgs = arrayOf(username)

            // 2. Ejecutar la consulta
            val cursor = db.query(
                TABLE_USERS,
                null, // null significa "traer todas las columnas" (* en SQL)
                selection,
                selectionArgs,
                null,
                null,
                null
            )

            var user: User? = null

            // 3. Si el cursor tiene al menos una fila, extraemos los datos
            if (cursor.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID))
                val storedUsername = cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME))
                val storedHash = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD_HASH))

                // Creamos el objeto User
                user = User(id, storedUsername, storedHash)
            }

            // 4. Cerrar recursos
            cursor.close()
            db.close()

            return user
        }

    }