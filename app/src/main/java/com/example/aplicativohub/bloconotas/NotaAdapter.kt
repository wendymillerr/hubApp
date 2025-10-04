package com.example.aplicativohub.bloconotas


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicativohub.R
import java.util.concurrent.TimeUnit

class NotaAdapter(
    private val listaNotas: MutableList<Nota>,
    private val onDeleteClick: (Nota) -> Unit
) : RecyclerView.Adapter<NotaAdapter.NotaViewHolder>() {

    inner class NotaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitulo: TextView = itemView.findViewById(R.id.tvTituloNota)
        val tvConteudo: TextView = itemView.findViewById(R.id.tvConteudoNota)
        val tvTempo: TextView = itemView.findViewById(R.id.tvTempoNota)
        val btnApagar: Button = itemView.findViewById(R.id.btnApagarNota)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_nota, parent, false)
        return NotaViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotaViewHolder, position: Int) {
        val nota = listaNotas[position]
        holder.tvTitulo.text = nota.titulo
        holder.tvConteudo.text = nota.conteudo
        holder.tvTempo.text = tempoDesde(nota.timestamp)

        holder.btnApagar.setOnClickListener {
            onDeleteClick(nota)
        }
    }

    override fun getItemCount(): Int = listaNotas.size

    private fun tempoDesde(timestamp: Long): String {
        val diff = System.currentTimeMillis() - timestamp
        val minutos = TimeUnit.MILLISECONDS.toMinutes(diff)
        val horas = TimeUnit.MILLISECONDS.toHours(diff)
        val dias = TimeUnit.MILLISECONDS.toDays(diff)

        return when {
            minutos < 60 -> "$minutos min atrás"
            horas < 24 -> "$horas h atrás"
            dias < 7 -> "$dias d atrás"
            else -> "${dias / 7} sem atrás"
        }
    }
}
