package com.example.aplicativohub.bloconotas

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicativohub.R
import com.example.aplicativohub.hub.MainActivity
import com.example.aplicativohub.utils.AppLogger

class BlocoNotasActivity : AppCompatActivity() {

    private lateinit var edtTitulo: EditText
    private lateinit var edtConteudo: EditText
    private lateinit var btnSalvar: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView

    private val listaNotas = mutableListOf<Nota>()
    private lateinit var adapter: NotaAdapter
    private var inicio: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bloco_notas)

        AppLogger.i("Bloco de Notas iniciado")

        if (listaNotas.isEmpty()){
            AppLogger.i("Lista de notas vazia ao iniciar")
            inicio = false
        }

        edtTitulo = findViewById(R.id.edtTitulo)
        edtConteudo = findViewById(R.id.edtConteudo)
        btnSalvar = findViewById(R.id.btnSalvarNota)
        recyclerView = findViewById(R.id.recyclerNotas)
        searchView = findViewById(R.id.searchNotas)

        adapter = NotaAdapter(listaNotas) { nota ->
            AppLogger.w("Nota '${nota.titulo}' removida pelo usuário")
            listaNotas.remove(nota)
            adapter.notifyDataSetChanged()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnSalvar.setOnClickListener {
            val titulo = edtTitulo.text.toString()
            val conteudo = edtConteudo.text.toString()
            AppLogger.v("Tentando salvar nota: $titulo")

            if (titulo.isNotEmpty()) {
                listaNotas.add(0, Nota(titulo, conteudo))
                adapter.notifyDataSetChanged()
                AppLogger.i("Nota '$titulo' salva com sucesso")
                edtTitulo.text.clear()
                edtConteudo.text.clear()
            } else {
                AppLogger.w("Tentativa de salvar nota sem título")
            }
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                AppLogger.d("Filtro aplicado: $newText")
                filtrarNotas(newText ?: "")
                return true
            }
        })

        edtTitulo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                edtTitulo.setTypeface(null, Typeface.BOLD)
                AppLogger.v("Título alterado para: $s")
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        val btnVoltar: Button = findViewById(R.id.Voltar)
        btnVoltar.setOnClickListener {
            AppLogger.i("Voltando ao Hub principal")
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun filtrarNotas(texto: String) {
        try{
            if (texto.contains("%")) {
                throw Exception("Forçando erro para testar catch.")
            }
            val filtradas = listaNotas.filter { it.titulo.contains(texto, ignoreCase = true) }
            AppLogger.v("Quantidade de notas filtradas: ${filtradas.size}")
            adapter = NotaAdapter(filtradas.toMutableList()) { nota ->
                AppLogger.w("Nota '${nota.titulo}' removida após filtro")
                listaNotas.remove(nota)
                adapter.notifyDataSetChanged()
            }
            recyclerView.adapter = adapter
        } catch (e: Exception) {
            AppLogger.e("Erro ao filtrar notas", e)
        }
    }
}
