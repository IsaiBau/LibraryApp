package com.example.libraryapp
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.cardview.widget.CardView
data class Libro(val titulo: String, val descripcion: String,val id: Long)
class LibraryAdapter(private val libros: List<Libro>, private val userId: Int) : RecyclerView.Adapter<LibraryAdapter.LibroViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibroViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view_libro, parent, false)
        return LibroViewHolder(view)
    }

    override fun onBindViewHolder(holder: LibroViewHolder, position: Int) {
        val libro = libros[position]
        holder.libroTitulo.text = libro.titulo
        holder.libroDescripcion.text = libro.descripcion
        holder.libroTitulo.setOnClickListener { // Establece el detector de clics en toda la vista de tarjeta
            val libroId = libro.id  // Obtiene el ID del libro del objeto Libro
            val intent = Intent(holder.libroTitulo.context, MainActivityDetailBook::class.java).apply {
                putExtra("ID_USUARIO", userId)
                putExtra("LIBRO_ID", libroId)
                val a =  putExtra("LIBRO_ID", libroId)
                Log.d("adaptador", "$a")
            }

            holder.libroTitulo.context.startActivity(intent)
        }
    }

    override fun getItemCount() = libros.size

    class LibroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val libroTitulo: TextView = itemView.findViewById(R.id.libroTitulo)
        val libroDescripcion: TextView = itemView.findViewById(R.id.libroDescripcion)
        // Referencias a otros elementos del cardView (opcional)
    }

}
