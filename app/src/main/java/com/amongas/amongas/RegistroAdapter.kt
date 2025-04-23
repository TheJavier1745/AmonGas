package com.amongas.amongas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.threeten.bp.format.DateTimeFormatter

class RegistroAdapter(private val registros: List<RegistroGas>) :
    RecyclerView.Adapter<RegistroAdapter.RegistroViewHolder>() {

    class RegistroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val hora: TextView = view.findViewById(R.id.tvHora)
        val valor: TextView = view.findViewById(R.id.tvValor)
        val estado: TextView = view.findViewById(R.id.tvEstado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegistroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_registro, parent, false)
        return RegistroViewHolder(view)
    }

    override fun onBindViewHolder(holder: RegistroViewHolder, position: Int) {
        val registro = registros[position]
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        val fechaStr = registro.fechaHora.format(formatter)
        holder.hora.text = "Fecha: $fechaStr"
        holder.valor.text = "Valor: ${registro.valor} ppm"

        when {
            registro.valor <= 249 -> {
                holder.estado.text = "Estado: Normal"
                holder.estado.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.green))
            }
            registro.valor in 250..499 -> {
                holder.estado.text = "Estado: Medio"
                holder.estado.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.yellow))
            }
            else -> {
                holder.estado.text = "Estado: Emergencia"
                holder.estado.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
            }
        }
    }

    override fun getItemCount() = registros.size
}
