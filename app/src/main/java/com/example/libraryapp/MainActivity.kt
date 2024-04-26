package com.example.libraryapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryapp.R.id.addButton

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: DbHelper
    private var libroId: Long = -1
    private val librosList: MutableList<Libro> = mutableListOf()
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dbHelper = DbHelper(this)
        val usu = intent.getIntExtra("ID_USUARIO", -1)
        val libroID = intent.getIntExtra("LIBRO_ID", -1)
        Log.d("MainActivity", "se pudo: $usu")

        val buttonAdd = findViewById<ImageButton>(R.id.addButton)
        buttonAdd.setOnClickListener {
            val intent = Intent(this, MainActivityAddBook::class.java).apply {
                putExtra("ID_USUARIO", intent.getIntExtra("ID_USUARIO", -1))
                putExtra("usu", usu)
            }
            startActivity(intent)
        }
        refreshUl()

        val buttonV = findViewById<Button>(R.id.buttonView)
        buttonV.setOnClickListener {
            val intent = Intent(this, MainActivityDetailBook::class.java).apply {
                putExtra("LIBRO_ID", libroId)
                putExtra("ID_USUARIO", usu)
            }
            startActivity(intent)
        }
        val buttonCat = findViewById<Button>(R.id.buttonCat)
        buttonCat.setOnClickListener {
            val intent = Intent(this, MainActivityCategories::class.java).apply {
                putExtra("LIBRO_ID", libroID)
                putExtra("ID_USUARIO", usu)
            }
            startActivity(intent)
        }
        recyclerView = findViewById(R.id.recyclerViewMain)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = LibrosAdapter(librosList, usu)
        recyclerView.adapter = adapter
        val cursor = dbHelper.getAllBooks(usu)
        if (cursor != null) {
            librosList.clear() // Limpiar la lista antes de agregar nuevos datos
            cursor.moveToFirst() // Mueve el cursor al primer resultado
            while (!cursor.isAfterLast) { // Itera hasta que se haya alcanzado el último resultado
                val nombreIndex = cursor.getColumnIndex("nombre")
                val idIndex = cursor.getColumnIndex("id")
                val descripcionIndex = cursor.getColumnIndex("sinopsis")
                if (nombreIndex != -1 && descripcionIndex != -1) {
                    val isLibro = cursor.getString(idIndex)
                    val nombreLibro = cursor.getString(nombreIndex)
                    val descripcionLibro = cursor.getString(descripcionIndex)
                    librosList.add(Libro(nombreLibro, descripcionLibro, isLibro.toLong()))
                    // Dentro del bloque donde agregas libros a librosList
                    for (libros in librosList) {
                        Log.d("Libro", "Título: ${libros.titulo}, Descripción: ${libros.descripcion}")
                    }

                }
                cursor.moveToNext() // Mueve el cursor al siguiente resultado
            }
            adapter.notifyDataSetChanged()
            cursor.close() // Cerrar el cursor después de usarlo
        }
    }
    private fun refreshUl(){
        val usu = intent.getIntExtra("ID_USUARIO", -1)
        val id = usu.toLong()
        //carview grande donde aparece los añadidos recientemente
        val cursor = dbHelper.getUltimoLibroAñadidoPorUsuario(id)
        if (cursor != null && cursor.moveToFirst()) {
            val idColumnIndex = cursor.getColumnIndex("id")
            val nombreColumnIndex = cursor.getColumnIndex("nombre")
            val categoriaColumnIndex = cursor.getColumnIndex("categoria")
            val sinopsisColumnIndex = cursor.getColumnIndex("sinopsis")
            if (nombreColumnIndex != -1 && categoriaColumnIndex != -1) {
                libroId = cursor.getLong(idColumnIndex)
                val nombreLibro = cursor.getString(nombreColumnIndex)
                val categoria = cursor.getString(categoriaColumnIndex)
                val sinopsis = cursor.getString(sinopsisColumnIndex)

                // Ahora puedes mostrar los datos del libro en el CardView
                val libroTitulo = findViewById<TextView>(R.id.libroTitulo)
                libroTitulo.text = nombreLibro
                val sinopsiss= findViewById<TextView>(R.id.sinopsis)
                sinopsiss.text = sinopsis
                val buttonCategoria = findViewById<Button>(R.id.buttonCat)
                buttonCategoria.text = categoria
            } else {
                // Manejar el caso en el que las columnas no existen en el cursor
            }
        } else {
            // Maneja el caso en el que no se haya encontrado ningún libro añadido por el usuario en sesión
        }
        cursor?.close()
    }
}
//todos los libros intento
/*
val cursor1 = dbHelper.getAllBooks(id)
if (cursor1 != null && cursor1.moveToFirst()) {
    val cardView = findViewById<CardView>(R.id.cardView3)
    do {
        val idColumnIndex = cursor.getColumnIndex("id")
        val nombreColumnIndex = cursor.getColumnIndex("nombre")
        val categoriaColumnIndex = cursor.getColumnIndex("categoria")
        val sinopsisColumnIndex = cursor.getColumnIndex("sinopsis")

        if (nombreColumnIndex != -1 && categoriaColumnIndex != -1) {
            val libroId = cursor.getLong(idColumnIndex)
            val nombreLibro = cursor.getString(nombreColumnIndex)
            val categoria = cursor.getString(categoriaColumnIndex)
            val sinopsis = cursor.getString(sinopsisColumnIndex)

            // Inflar el diseño de la tarjeta interna
            val innerCardView = layoutInflater.inflate(R.layout.cardview3, cardView, false)

            // Obtener las vistas dentro de la tarjeta interna
            val libroTitulo = innerCardView.findViewById<TextView>(R.id.libroTitulo)
            val sinopsisTextView = innerCardView.findViewById<TextView>(R.id.sinopsis)
            val buttonCategoria = innerCardView.findViewById<Button>(R.id.buttonCat)

            // Establecer los valores del libro en las vistas
            libroTitulo.text = nombreLibro
            sinopsisTextView.text = sinopsis
            buttonCategoria.text = categoria

            // Agregar la tarjeta interna al CardView principal
            cardView.addView(innerCardView)

            // Asignar un clic al botón para ver el detalle del libro
            buttonCategoria.setOnClickListener {
                val intent = Intent(this, MainActivityDetailBook::class.java).apply {
                    putExtra("LIBRO_ID", libroId)
                    putExtra("ID_USUARIO", usu)
                }
                startActivity(intent)
            }
        } else {
            // Manejar el caso en el que las columnas no existen en el cursor
        }
    } while (cursor.moveToNext())
} else {
    // Maneja el caso en el que no se haya encontrado ningún libro añadido por el usuario en sesión
}
cursor?.close()
*/