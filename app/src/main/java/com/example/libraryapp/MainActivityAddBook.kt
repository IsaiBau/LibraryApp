package com.example.libraryapp

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText


class MainActivityAddBook : AppCompatActivity() {
    private lateinit var dbHelper: DbHelper
    private val PICK_PDF_REQUEST = 1
    private lateinit var categoria: String
    private lateinit var nombreLibro: String
    private lateinit var sinopsis: String
    private lateinit var url: String
    private var selectedFileUri: Uri? = null
    private var userId: Long = -1L // Asigna un valor predeterminado al userId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_add_book)
        dbHelper = DbHelper(this)

        val categorias = listOf("Comedia", "Romance", "Drama", "Acción")
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.textViewCategoria)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categorias)
        autoCompleteTextView.setAdapter(adapter)
        val usu = intent.getIntExtra("ID_USUARIO", -1)
        Log.d("MainActivity", "se pudo: $usu")
        val usu3 = intent.getIntExtra("usu", -1)
        Log.d("MainActivity", "se pudo: $usu3")


        //imageButton.setOnClickListener {
        //    val intent = Intent(Intent.ACTION_GET_CONTENT)
        //    intent.type = "application/pdf"
        //    startActivityForResult(
        //        Intent.createChooser(intent, "Seleccionar archivo PDF"),
        //        PICK_PDF_REQUEST
        //    )
        //}

        val buttonSave = findViewById<Button>(R.id.buttonSave)
        // Configurar el clic del botón de guardar
        buttonSave.setOnClickListener {
            // Obtener los datos del formulario
            Log.d("MainActivity", "se pudo: $usu")
            val usu2 = usu.toLong()
            val categoria1 = findViewById<TextView>(R.id.textViewCategoria)
            val nombreLibro1 = findViewById<TextView>(R.id.nombreLibro)
            val sinopsis1 = findViewById<TextView>(R.id.sinopsis)
            val imageButton = findViewById<EditText>(R.id.imgButton)

            categoria = categoria1.text.toString()
            nombreLibro = nombreLibro1.text.toString()
            sinopsis = sinopsis1.text.toString()
            url = imageButton.text.toString()
            // Suponiendo que ya tienes el ID del usuario que está subiendo el libro
            userId = intent.getLongExtra("USER_ID", -1L)
            Log.d("id", "id: $userId")
            // Verificar si algún campo está vacío
            if (categoria.isEmpty() || nombreLibro.isEmpty() || sinopsis.isEmpty()) {
                Toast.makeText(this, "Por favor ingresa todos los campos", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val uri = selectedFileUri.toString()
                val idLibro = dbHelper.insertLibro(categoria, nombreLibro, sinopsis, usu2,url)

                // Muestra un mensaje de confirmación
                if (idLibro != -1L) {
                    Toast.makeText(this, "Libro guardado exitosamente!", Toast.LENGTH_SHORT).show()
                    // Aquí puedes hacer cualquier otra acción necesaria después de guardar el libro
                    Log.d("MainActivityAddBook", "se pudo")
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Error al guardar el libro", Toast.LENGTH_SHORT).show()
                    Log.d("MainActivityAddBook", "error")
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == PICK_PDF_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            Log.d("MainActivityAddBook", "URI del archivo seleccionado: ${data.data}")
            //esta uri es  content://com.android.providers.media.documents/document/document%3A358711
            //este es el bueno te quiero mucho, esto fue frustrante, sigo frustrada
            //pero quiero aprovechar para decirte que te quiero un montoooon
            //resulta que no vi que habia salto de linea  y pense que estaba completo, me tarde toda la noche porque quise solo poe no leer aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
            selectedFileUri = data.data

            val selectedFilePath: String = selectedFileUri?.let { getPath(it) } ?: ""
            Log.d("MainActivityAddBook", "Ruta del archivo seleccionado: $selectedFilePath")
            //esta solo es el nombre: Documento A4 Catálogo tienda de ropa minimalista gris y blanco.pdf
            // Verificar si el archivo seleccionado existe antes de intentar copiarlo
            val selectedFile = File(selectedFilePath)
            val selectedFilePath1: String = selectedFile.absolutePath


            // Aquí guardas el archivo PDF en el sistema de archivos
            val file = File(selectedFilePath)
            // Luego, puedes mover el archivo a una ubicación de tu elección
            // (por ejemplo, al directorio de almacenamiento interno o externo de tu aplicación)
            // Esto es solo un ejemplo básico:

            val destination = File(filesDir, selectedFilePath)
            if (!destination.exists()) {
                destination.mkdirs() // Crea el directorio y sus subdirectorios si no existen
            }

            file.copyTo(destination, overwrite = true)

            // Guarda la ruta del archivo en la base de datos
            val filePathInDatabase = destination.absolutePath
            // Aquí debes modificar tu método de inserción de libro para que también acepte la ruta del archivo

        }
    }

    private fun getPath(uri: Uri): String {
        Log.d("MainActivityAddBook", "URI recibida en getPath: $uri")
        //aqui fue donde me di ceunt aque estaba cortada la ruta y si estaba bein xddddd
        // content://com.android.providers.media.documents/document/document%3A358711
        var filePath = ""

        val scheme = uri.scheme
        if (scheme == "file") {
            var filePath: String? = null
            filePath = uri.path
            Log.d("MainActivityAddBook", "filepath recibida en getPath: $filePath")
            //este no imprime nada puedes quitarlo
        } else if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME)
                    filePath = cursor.getString(columnIndex)
                    Log.d("MainActivityAddBook", "filepath del cursor recibida en getPath: $filePath")
                    //imprime solo nombre, no sirve este
                    //Documento A4 Catálogo tienda de ropa minimalista gris y blanco.pdf
                }
            }
        } else {
            // Manejar otros tipos de URI según sea necesario
        }
        return filePath
    }
/*
    private fun checkPermission(): Boolean {
        val permission1 = ContextCompat.checkSelfPermission(applicationContext, WRITE_EXTERNAL_STORAGE)
        val permission2 = ContextCompat.checkSelfPermission(applicationContext, READ_EXTERNAL_STORAGE)
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE),
            200
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 200) {
            if(grantResults.size > 0) {
                val writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED

                if(writeStorage && readStorage) {
                    Toast.makeText(this, "Permisos concedidos", Toast.LENGTH_LONG)
                } else {
                    Toast.makeText(this, "Permisos rechazados", Toast.LENGTH_LONG)
                    finish()
                }
            }
        }
    }*/
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
