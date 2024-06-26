package com.example.libraryapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(private val context: Context) : SQLiteOpenHelper(context, DBNAME, null, DB_VERSION) {

    companion object {
        private const val DBNAME = "App.db"
        private const val DB_VERSION = 2
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val queryLibros = (
                "CREATE TABLE LIBROS(" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "categoria TEXT, " +
                        "nombre TEXT, " +
                        "sinopsis TEXT, " +
                        "file_path TEXT, " +
                        "user_id INTEGER, " +
                        "FOREIGN KEY(user_id) REFERENCES USERS(id))"
                )
        db?.execSQL(queryLibros)

        val queryUsuarios = ("CREATE TABLE USERS(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "usuario TEXT, " +
                "password TEXT)")

        db?.execSQL(queryUsuarios)
    }

    fun insertUser(
        username: String,
        password: String,
    ): Long {
        val values = ContentValues().apply {
            put("usuario", username)
            put("password", password)
        }
        return writableDatabase.insert("USERS", null, values)
    }

    fun leerusuario(username: String, password: String): Long {
        val args = arrayOf(username, password)
        val consulta = readableDatabase.query(
            "USERS", arrayOf("id"),
            "usuario = ? AND password = ?", args, null, null, null
        )
        var userId: Long = -1
        if (consulta.moveToFirst()) {
            userId = consulta.getLong(0)
        }
        consulta.close()
        return userId
    }


    fun leer(sql: String): Cursor? {
        val db = readableDatabase
        return db.rawQuery(sql, null)
    }

    fun eliminarUsuario(userId: Long): Int {
        val db = writableDatabase
        return if (userId != null) {
            db.delete("USERS", "id = ?", arrayOf(userId.toString()))
        } else {
            db.delete("USERS", null, null)
        }
    }

    fun actualizarUsuario(
        userId: Long,
        username: String,
        password: String,
        apellidos: String,
        numero: String,
        email: String
    ): Int {
        val values = ContentValues().apply {
            put("username", username)
            put("password", password)
            put("apellidos", apellidos)
            put("numero", numero)
            put("email", email)
        }

        val whereClause = "id = ?"
        val whereArgs = arrayOf(userId.toString())

        return writableDatabase.update("USERS", values, whereClause, whereArgs)
    }
    fun obtenerIdUsuario(username: String, password: String): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id FROM USERS WHERE usuario = ? AND password = ?", arrayOf(username, password))
        return if (cursor.moveToFirst()) {
            val idUsuario = cursor.getInt(0)
            cursor.close()
            idUsuario
        } else {
            cursor.close()
            -1
        }
    }
    fun insertLibro(
        categoria: String,
        nombreLibro: String,
        sinopsis: String,
        userId: Long,
        filePath: String
    ): Long {
        val values = ContentValues().apply {
            put("categoria", categoria)
            put("nombre", nombreLibro)
            put("sinopsis", sinopsis)
            put("user_id", userId)
            put("file_path", filePath)
        }
        val libroId = writableDatabase.insert("LIBROS", null, values)
        // Devolver el ID del libro insertado
        return libroId
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // No se realiza ninguna acción específica en este momento
        // Puedes dejar este método vacío o agregar código de actualización del esquema de la base de datos aquí si es necesario.
    }

    data class Libro(
        val id: Long,
        val categoria: String,
        val nombre: String,
        val sinopsis: String,
        val filePath: String,
        val userId: Long
    )

    fun obtenerDetallesLibro(libroId: Long): Libro? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM LIBROS WHERE id = ?", arrayOf(libroId.toString()))
        return if (cursor.moveToFirst()) {
            val categoriaIndex = cursor.getColumnIndex("categoria")
            val nombreIndex = cursor.getColumnIndex("nombre")
            val sinopsisIndex = cursor.getColumnIndex("sinopsis")
            val filePathIndex = cursor.getColumnIndex("file_path")
            val userIdIndex = cursor.getColumnIndex("user_id")

            if (categoriaIndex == -1 || nombreIndex == -1 || sinopsisIndex == -1 || filePathIndex == -1 || userIdIndex == -1) {
                cursor.close()
                throw IllegalArgumentException("Columna no encontrada en los resultados de la consulta SQL.")
            }

            val categoria = cursor.getString(categoriaIndex)
            val nombre = cursor.getString(nombreIndex)
            val sinopsis = cursor.getString(sinopsisIndex)
            val filePath = cursor.getString(filePathIndex)
            val userId = cursor.getLong(userIdIndex)
            cursor.close()
            Libro(libroId, categoria, nombre, sinopsis, filePath, userId)
        } else {
            cursor.close()
            null
        }
    }
}
