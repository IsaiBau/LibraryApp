package com.example.libraryapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.libraryapp.R.id.addButton
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_ADD_BOOK = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Obtener el ID del libro desde el intent
        val libroId = intent.getLongExtra("LIBRO_ID", 1)

        // Verificar si se proporcionó un ID válido
        if (libroId != 0L) {
            // Acceder a la base de datos y obtener los detalles del libro
            val dbHelper = DbHelper(this)
            val libro = dbHelper.obtenerDetallesLibro(libroId)

            // Verificar si se encontró el libro
            if (libro != null) {
                // Actualizar los elementos de tu CardView con los detalles del libro
                findViewById<TextView>(R.id.libroTitulo).text = libro.nombre
                findViewById<TextView>(R.id.sinopsis).text = libro.sinopsis
                findViewById<Button>(R.id.buttonCat).text = libro.categoria

                // Agregar un listener al ImageButton para abrir el archivo PDF
// Agregar un listener al ImageButton para abrir el archivo PDF
                findViewById<ImageButton>(R.id.bookView).setOnClickListener {
                    // Obtener el filePath del libro seleccionado
                    val libro = dbHelper.obtenerDetallesLibro(libroId)
                    val filePath = libro?.filePath

                    // Verificar si el filePath no es nulo o vacío
                    if (!filePath.isNullOrEmpty()) {
                        // Copiar el archivo PDF a la memoria externa del dispositivo
                        val externalDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                        val destinationFile = File(externalDir, "temp.pdf")

                        try {
                            FileInputStream(File(filePath)).use { inputStream ->
                                FileOutputStream(destinationFile).use { outputStream ->
                                    inputStream.copyTo(outputStream)
                                }
                            }

                            // Crear un intent para abrir el archivo PDF desde la memoria externa
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.setDataAndType(Uri.parse(destinationFile.absolutePath), "application/pdf")
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

                            // Verificar si hay aplicaciones que puedan manejar el intent
                            val chooserIntent = Intent.createChooser(intent, "Abrir archivo PDF con:")
                            if (chooserIntent.resolveActivity(packageManager) != null) {
                                startActivity(chooserIntent)
                            } else {
                                // Si no se encontró ninguna aplicación, mostrar un mensaje al usuario
                                Toast.makeText(this, "No se encontró ninguna aplicación para abrir el archivo PDF", Toast.LENGTH_SHORT).show()
                            }

                        } catch (e: IOException) {
                            e.printStackTrace()
                            // Manejar cualquier excepción que pueda ocurrir durante la copia del archivo
                            Toast.makeText(this, "Error al copiar el archivo PDF", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Si el filePath está vacío o nulo, mostrar un mensaje de error
                        Toast.makeText(this, "No se encontró la ruta del archivo PDF", Toast.LENGTH_SHORT).show()
                    }
                }


            } else {
                Log.d("MainActivity", "No se encontró el libro con el ID: $libroId")
            }
        } else {
            Log.d("MainActivity", "No se proporcionó ningún ID de libro válido")
        }

        // Otros listeners y lógica de la actividad
        val usu = intent.getIntExtra("ID_USUARIO", -1)
        Log.d("MainActivity", "ID Usuario: $usu")

        val buttonAdd = findViewById<ImageButton>(R.id.addButton)
        buttonAdd.setOnClickListener {
            val intent = Intent(this, MainActivityAddBook::class.java).apply {
                putExtra("ID_USUARIO", usu)
            }
            startActivityForResult(intent, REQUEST_ADD_BOOK)
        }

        val buttonView = findViewById<Button>(R.id.buttonView)
        buttonView.setOnClickListener {
            val intent = Intent(this, MainActivityDetailBook::class.java)
            startActivity(intent)
        }

        val buttonCat = findViewById<Button>(R.id.buttonCat)
        buttonCat.setOnClickListener {
            val intent = Intent(this, MainActivityCategories::class.java)
            startActivity(intent)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ADD_BOOK && resultCode == RESULT_OK) {
            // Refrescar la actividad para mostrar el nuevo libro agregado
            recreate()
        }
    }
}