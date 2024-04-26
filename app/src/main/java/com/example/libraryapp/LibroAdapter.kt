package com.example.libraryapp

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Libros(val titulo: String, val descripcion: String,val id: Long)
class LibrosAdapter(private val libros: List<Libro>, private val userId: Int) : RecyclerView.Adapter<LibrosAdapter.LibroViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibroViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.libro_item, parent, false)
        return LibroViewHolder(view)
    }

    override fun onBindViewHolder(holder: LibroViewHolder, position: Int) {
        val libro = libros[position]
        holder.libroTitulo.text = libro.titulo
        holder.libroCat.text = libro.descripcion
        holder.libroSel.setOnClickListener { // Establece el detector de clics en toda la vista de tarjeta
            val libroId = libro.id  // Obtiene el ID del libro del objeto Libro
            val intent = Intent(holder.libroSel.context, MainActivityDetailBook::class.java).apply {
                putExtra("ID_USUARIO", userId)
                putExtra("LIBRO_ID", libroId)
                val a =  putExtra("LIBRO_ID", libroId)
                Log.d("adaptador", "$a")
            }

            holder.libroSel.context.startActivity(intent)
        }
    }

    override fun getItemCount() = libros.size

    class LibroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val libroTitulo: TextView = itemView.findViewById(R.id.titulo)
        val libroCat: TextView = itemView.findViewById(R.id.categoria)
        val libroSel: ImageView = itemView.findViewById(R.id.sel)
        // Referencias a otros elementos del cardView (opcional)
    }

}
