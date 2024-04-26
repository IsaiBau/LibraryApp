package com.example.libraryapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.example.libraryapp.R.id.buttonReturn
import com.example.libraryapp.databinding.ActivityMainActivitVisorBinding
import com.example.libraryapp.databinding.ActivityMainDetailBookBinding

class MainActivityDetailBook : AppCompatActivity() {
    private lateinit var dbHelper: DbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_detail_book)
        dbHelper = DbHelper(this)
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.cat)
        val categorias = listOf("Comedia", "Romance", "Drama", "Acción")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categorias)
        autoCompleteTextView.setAdapter(adapter)
        val usu = (intent.getIntExtra("ID_USUARIO", -1)).toLong()
        var idL = intent.getLongExtra("LIBRO_ID", -1)

        val buttonDelete = findViewById<ImageButton>(R.id.buttonDelete)
        buttonDelete.setOnClickListener {
            dbHelper.deleteBook(idL)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val cursor = dbHelper.getLibroPorId(usu, idL)
        if (cursor != null && cursor.moveToFirst()) {
            val idColumnIndex = cursor.getColumnIndex("id")
            val filePathIndex = cursor.getColumnIndex("file_path")
            val filePath = cursor.getString(filePathIndex)
            val uri = Uri.parse(filePath)
            val nombreColumnIndex = cursor.getColumnIndex("nombre")
            val categoriaColumnIndex = cursor.getColumnIndex("categoria")
            val sinopsisColumnIndex = cursor.getColumnIndex("sinopsis")
            if (nombreColumnIndex != -1 && categoriaColumnIndex != -1) {
                idL = cursor.getLong(idColumnIndex)
                val nombreLibro = cursor.getString(nombreColumnIndex)
                val categoria = cursor.getString(categoriaColumnIndex)
                val sinopsis = cursor.getString(sinopsisColumnIndex)

                val categoria1 = findViewById<TextView>(R.id.cat)
                categoria1.text = categoria
                val nombreLibro1 = findViewById<TextView>(R.id.libroTitulo)
                val nom= findViewById<TextView>(R.id.nom)
                nombreLibro1.text = nombreLibro
                nom.text = nombreLibro
                val sinopsis1 = findViewById<TextView>(R.id.sin)
                val sinopsisL = findViewById<TextView>(R.id.sinopsisL)
                sinopsisL.text = sinopsis

            } else {
                // Manejar el caso en el que las columnas no existen en el cursor
            }
        } else {
            // Manejar el caso en el que no se encuentre el libro
        }
        cursor?.close()

        val buttonReturn = findViewById<Button>(R.id.buttonReturn)
        buttonReturn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        val ver = findViewById<ImageButton>(R.id.ver)
        ver.setOnClickListener {
            val intent = Intent(this, MainActivitVisor::class.java).apply {
                putExtra("LIBRO_ID", idL)
                putExtra("ID_USUARIO", usu)
            }
            startActivity(intent)
        }
    }
}