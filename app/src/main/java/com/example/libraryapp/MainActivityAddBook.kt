package com.example.libraryapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import java.io.File

class MainActivityAddBook : AppCompatActivity() {
    private lateinit var dbHelper: DbHelper
    private val PICK_PDF_REQUEST = 1
    private lateinit var categoria: String
    private lateinit var nombreLibro: String
    private lateinit var sinopsis: String
    private var userId: Long = -1L // Asigna un valor predeterminado al userId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_add_book)
        dbHelper = DbHelper(this)

        val imageButton = findViewById<ImageButton>(R.id.imgButton)

        imageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf"
            startActivityForResult(
                Intent.createChooser(intent, "Seleccionar archivo PDF"),
                PICK_PDF_REQUEST
            )
        }

        val buttonSave = findViewById<Button>(R.id.buttonSave)
        // Configurar el clic del botón de guardar
        buttonSave.setOnClickListener {
            // Obtener los datos del formulario
            val categoria1 = findViewById<TextView>(R.id.textViewCategoria)
            val nombreLibro1 = findViewById<TextView>(R.id.nombreLibro)
            val sinopsis1 = findViewById<TextView>(R.id.sinopsis)

            categoria = categoria1.text.toString()
            nombreLibro = nombreLibro1.text.toString()
            sinopsis = sinopsis1.text.toString()

            // Suponiendo que ya tienes el ID del usuario que está subiendo el libro
            userId = intent.getLongExtra("USER_ID", -1L)

            // Verificar si algún campo está vacío
            if (categoria.isEmpty() || nombreLibro.isEmpty() || sinopsis.isEmpty()) {
                Toast.makeText(this, "Por favor ingresa todos los campos", Toast.LENGTH_SHORT)
                    .show()
            } else {
                // Aquí no necesitas llamar a insertLibro, ya que lo harás en onActivityResult
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == PICK_PDF_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val selectedFileUri = data.data
            val selectedFilePath: String = selectedFileUri?.let { getPath(it) } ?: ""
            Log.d("MainActivityAddBook", "Ruta del archivo seleccionado: $selectedFilePath")

            // Verificar si el archivo seleccionado existe antes de intentar copiarlo
            val selectedFile = File(selectedFilePath)
            if (!selectedFile.exists()) {
                Toast.makeText(this, "El archivo seleccionado no existe", Toast.LENGTH_SHORT).show()
                return
            }

            // Aquí guardas el archivo PDF en el sistema de archivos
            val file = File(selectedFilePath)
            // Luego, puedes mover el archivo a una ubicación de tu elección
            // (por ejemplo, al directorio de almacenamiento interno o externo de tu aplicación)
            // Esto es solo un ejemplo básico:
            val destination = File(filesDir, "nombre_archivo.pdf")
            file.copyTo(destination, overwrite = true)

            // Guarda la ruta del archivo en la base de datos
            val filePathInDatabase = destination.absolutePath
            // Aquí debes modificar tu método de inserción de libro para que también acepte la ruta del archivo
            val idLibro = dbHelper.insertLibro(categoria, nombreLibro, sinopsis, userId, filePathInDatabase)

            // Muestra un mensaje de confirmación
            if (idLibro != -1L) {
                Toast.makeText(this, "Libro guardado exitosamente!", Toast.LENGTH_SHORT).show()
                // Aquí puedes hacer cualquier otra acción necesaria después de guardar el libro
            } else {
                Toast.makeText(this, "Error al guardar el libro", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getPath(uri: Uri): String {
        var filePath = ""
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME)
                    filePath = cursor.getString(columnIndex)
                }
            }
        } else {
            // Manejar otros tipos de URI según sea necesario
        }
        return filePath
    }
}


//class MainActivityAddBook : AppCompatActivity() {
//    private lateinit var dbHelper: DbHelper
//    private val PICK_PDF_REQUEST = 1
//    private lateinit var categoria: String
//    private lateinit var nombreLibro: String
//    private lateinit var sinopsis: String
//    private var userId: Long = -1L
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main_add_book)
//        dbHelper = DbHelper(this)
//
//        val imageButton = findViewById<ImageButton>(R.id.imgButton)
//
//        imageButton.setOnClickListener {
//            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.type = "application/pdf"
//            startActivityForResult(
//                Intent.createChooser(intent, "Seleccionar archivo PDF"),
//                PICK_PDF_REQUEST
//            )
//        }
//
//        val buttonSave = findViewById<Button>(R.id.buttonSave)
//        // Configurar el clic del botón de guardar
//        buttonSave.setOnClickListener {
//            // Obtener los datos del formulario
//            val categoria1 = findViewById<TextView>(R.id.textViewCategoria)
//            val nombreLibro1 = findViewById<TextView>(R.id.nombreLibro)
//            val sinopsis1 = findViewById<TextView>(R.id.sinopsis)
//
//            val categoria = categoria1.text.toString()
//            val nombreLibro = nombreLibro1.text.toString()
//            val sinopsis = sinopsis1.text.toString()
//
//
//            // Suponiendo que ya tienes el ID del usuario que está subiendo el libro
//            val userId: Long = intent.getLongExtra("USER_ID", -1L)
//
//            // Verificar si algún campo está vacío
//            if (categoria.isEmpty() || nombreLibro.isEmpty() || sinopsis.isEmpty()) {
//                Toast.makeText(this, "Por favor ingresa todos los campos", Toast.LENGTH_SHORT)
//                    .show()
//            } else {
//                // Guardar el libro en la base de datos
//                val idLibro = dbHelper.insertLibro(categoria, nombreLibro, sinopsis, userId)
//                if (idLibro != -1L) {
//                    Toast.makeText(this, "Libro guardado exitosamente!", Toast.LENGTH_SHORT).show()
//                    // Aquí puedes hacer cualquier otra acción necesaria después de guardar el libro
//                } else {
//                    Toast.makeText(this, "Error al guardar el libro", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == PICK_PDF_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
//            val selectedFileUri = data.data
//            val selectedFilePath: String = selectedFileUri?.let { getPath(it) } ?: ""
//
//            // Aquí guardas el archivo PDF en el sistema de archivos
//            val file = File(selectedFilePath)
//            // Luego, puedes mover el archivo a una ubicación de tu elección
//            // (por ejemplo, al directorio de almacenamiento interno o externo de tu aplicación)
//            // Esto es solo un ejemplo básico:
//            val destination = File(filesDir, "nombre_archivo.pdf")
//            file.copyTo(destination, overwrite = true)
//
//            // Guarda la ruta del archivo en la base de datos
//            val filePathInDatabase = destination.absolutePath
//            // Aquí debes modificar tu método de inserción de libro para que también acepte la ruta del archivo
//            val idLibro = dbHelper.insertLibro(categoria, nombreLibro, sinopsis, userId, filePathInDatabase)
//
//            // Muestra un mensaje de confirmación
//            if (idLibro != -1L) {
//                Toast.makeText(this, "Libro guardado exitosamente!", Toast.LENGTH_SHORT).show()
//                // Aquí puedes hacer cualquier otra acción necesaria después de guardar el libro
//            } else {
//                Toast.makeText(this, "Error al guardar el libro", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun getPath(uri: Uri): String {
//        val cursor = contentResolver.query(uri, null, null, null, null)
//        var filePath = ""
//        cursor.use {
//            it?.let { cursor ->
//                if (cursor.moveToFirst()) {
//                    val columnIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
//                    filePath = if (columnIndex != -1) {
//                        cursor.getString(columnIndex)
//                    } else {
//                        ""
//                    }
//                }
//            }
//        }
//        return filePath
//    }
//}
