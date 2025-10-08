package com.example.aplicativohub.bloconotas

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicativohub.R
import com.example.aplicativohub.bloconotas.Nota
import com.example.aplicativohub.bloconotas.NotaAdapter
import com.example.aplicativohub.hub.MainActivity
import com.example.aplicativohub.placar.PlacarActivity

class BlocoNotasActivity : AppCompatActivity() {

    private lateinit var edtTitulo: EditText
    private lateinit var edtConteudo: EditText
    private lateinit var btnSalvar: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView

    private val listaNotas = mutableListOf<Nota>()
    private lateinit var adapter: NotaAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bloco_notas)

        edtTitulo = findViewById(R.id.edtTitulo)
        edtConteudo = findViewById(R.id.edtConteudo)
        btnSalvar = findViewById(R.id.btnSalvarNota)
        recyclerView = findViewById(R.id.recyclerNotas)
        searchView = findViewById(R.id.searchNotas)

        adapter = NotaAdapter(listaNotas) { nota ->
            listaNotas.remove(nota)
            adapter.notifyDataSetChanged()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnSalvar.setOnClickListener {
            val titulo = edtTitulo.text.toString()
            val conteudo = edtConteudo.text.toString()
            if (titulo.isNotEmpty()) {
                listaNotas.add(0, Nota(titulo, conteudo))
                adapter.notifyDataSetChanged()
                edtTitulo.text.clear()
                edtConteudo.text.clear()
            }
        }

        // Filtro de busca
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarNotas(newText ?: "")
                return true
            }
        })

        edtTitulo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                edtTitulo.setTypeface(null, Typeface.BOLD) // aplica negrito
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        val btnVoltar: Button = findViewById(R.id.Voltar)

        btnVoltar.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun filtrarNotas(texto: String) {
        val filtradas = listaNotas.filter { it.titulo.contains(texto, ignoreCase = true) }
        adapter = NotaAdapter(filtradas.toMutableList()) { nota ->

            listaNotas.remove(nota)
            adapter.notifyDataSetChanged()
        }
        recyclerView.adapter = adapter
    }





}
