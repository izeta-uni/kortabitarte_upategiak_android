package com.example.erronka4

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Modificamos el constructor para recibir la acción de borrar: (Incidencia) -> Unit
class IncidenciasAdapter(
    private val listaIncidencias: List<Incidencia>,
    private val onDeleteClick: (Incidencia) -> Unit
) : RecyclerView.Adapter<IncidenciasAdapter.IncidenciaViewHolder>() {

    class IncidenciaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitulo: TextView = itemView.findViewById(R.id.tvTitulo)
        val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        val ivIncidencia: ImageView = itemView.findViewById(R.id.ivIncidencia)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete) // Nuevo botón
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncidenciaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_incidencia, parent, false)
        return IncidenciaViewHolder(view)
    }

    override fun onBindViewHolder(holder: IncidenciaViewHolder, position: Int) {
        val item = listaIncidencias[position]

        holder.tvTitulo.text = item.titulo
        holder.tvFecha.text = item.fecha
        holder.tvDescripcion.text = item.descripcion

        // Lógica de Imagen
        if (item.imageUri.isNotEmpty()) {
            try {
                holder.ivIncidencia.visibility = View.VISIBLE
                holder.ivIncidencia.setImageURI(Uri.parse(item.imageUri))
            } catch (e: Exception) {
                holder.ivIncidencia.visibility = View.GONE
            }
        } else {
            holder.ivIncidencia.visibility = View.GONE
        }

        // Lógica del Botón Borrar
        holder.btnDelete.setOnClickListener {
            // Llamamos a la función que nos pasó el Activity
            onDeleteClick(item)
        }
    }

    override fun getItemCount(): Int = listaIncidencias.size
}