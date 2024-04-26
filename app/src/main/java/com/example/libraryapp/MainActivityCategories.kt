package com.example.libraryapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.example.libraryapp.databinding.ActivityMainCategoriesBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryapp.LibraryAdapter

class MainActivityCategories : AppCompatActivity() {
    private lateinit var dbHelper: DbHelper
    private lateinit var recyclerView: RecyclerView
    private val librosList: MutableList<Libro> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_categories)
        val userId = intent.getIntExtra("ID_USUARIO", -1)
        val usu = intent.getIntExtra("ID_USUARIO", -1).toLong()
        val libroID = intent.getIntExtra("LIBRO_ID", -1)
        Log.d("DETAIL", "ID_LIBRO: $libroID")
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = LibraryAdapter(librosList, userId)
        recyclerView.adapter = adapter

        dbHelper = DbHelper(this)
        //dbHelper = DbHelper(this)
        //val buttonView = findViewById<Button>(R.id.buttonView)
        //buttonView.setOnClickListener {
        //    val intent = Intent(this, MainActivityDetailBook::class.java)
        //    startActivity(intent)
        //}

        val grupoRadio = findViewById<RadioGroup>(R.id.grupo_radio)
        var categoria: String? = null
        val rb7 = findViewById<RadioButton>(R.id.radioBtn7)
        val rb8 = findViewById<RadioButton>(R.id.radioBtn8)
        val rb9 = findViewById<RadioButton>(R.id.radioBtn9)
        val rb10 = findViewById<RadioButton>(R.id.radioBtn10)
        rb10.setTextColor(resources.getColor(R.color.white))
        grupoRadio.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioBtn7 -> {
                    categoria = "Comedia"
                    Log.d("MainComedia", "listado")
                    rb7.setTextColor(resources.getColor(R.color.white))
                    rb8.setTextColor(resources.getColor(R.color.black))
                    rb9.setTextColor(resources.getColor(R.color.black))
                    rb10.setTextColor(resources.getColor(R.color.black))
                }

                R.id.radioBtn8 -> {
                    categoria = "Romance"
                    rb7.setTextColor(resources.getColor(R.color.black))
                    rb8.setTextColor(resources.getColor(R.color.white))
                    rb9.setTextColor(resources.getColor(R.color.black))
                    rb10.setTextColor(resources.getColor(R.color.black))
                }

                R.id.radioBtn9 -> {
                    categoria = "Tragedia"
                    rb7.setTextColor(resources.getColor(R.color.black))
                    rb8.setTextColor(resources.getColor(R.color.black))
                    rb9.setTextColor(resources.getColor(R.color.white))
                    rb10.setTextColor(resources.getColor(R.color.black))
                }

                R.id.radioBtn10 -> {
                    categoria = "Terror"
                    rb7.setTextColor(resources.getColor(R.color.black))
                    rb8.setTextColor(resources.getColor(R.color.black))
                    rb9.setTextColor(resources.getColor(R.color.black))
                    rb10.setTextColor(resources.getColor(R.color.white))
                }
            }
            categoria?.let { cat ->
                val cursor = dbHelper.getBooksByCategory(usu, cat)
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
                            for (libro in librosList) {
                                Log.d("Libro", "Título: ${libro.titulo}, Descripción: ${libro.descripcion}")
                            }

                        }
                        cursor.moveToNext() // Mueve el cursor al siguiente resultado
                    }
                    adapter.notifyDataSetChanged()
                    cursor.close() // Cerrar el cursor después de usarlo
                }
            }

        }


    }
}