package com.example.libraryapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(private val context: Context) : SQLiteOpenHelper(context, DBNAME, null, DB_VERSION) {

    companion object {
        private const val DBNAME = "App.db"
        private const val DB_VERSION = 1
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
    fun getLibrosByUserId(userId: Long): Cursor? {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM LIBROS WHERE user_id = ?", arrayOf(userId.toString()))
    }
    fun getUltimoLibroAñadidoPorUsuario(userId: Long): Cursor? {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM LIBROS WHERE user_id = ? ORDER BY id DESC LIMIT 1", arrayOf(userId.toString()))
    }
    fun getLibroPorId(userId: Long, libroId: Long): Cursor? {
        val db = readableDatabase
        return db.rawQuery("SELECT id, categoria, nombre, sinopsis, file_path FROM LIBROS WHERE user_id = ? AND id = ?", arrayOf(userId.toString(), libroId.toString()))
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
        return writableDatabase.insert("LIBROS", null, values)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // No se realiza ninguna acción específica en este momento
        // Puedes dejar este método vacío o agregar código de actualización del esquema de la base de datos aquí si es necesario.
    }
}
