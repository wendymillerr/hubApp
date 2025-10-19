package com.example.aplicativohub.placar

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.aplicativohub.R
import com.example.aplicativohub.hub.MainActivity
import com.example.aplicativohub.utils.AppLogger

class PlacarActivity : AppCompatActivity() {

    // Variáveis de pontuação
    private var pontuacaoTimeA: Int = 0
    private var pontuacaoTimeB: Int = 0
    private var ultimaPontuacao: Int = 0
    private var ultimoTime: String = ""

    // TextViews do placar
    private lateinit var pTimeA: TextView
    private lateinit var pTimeB: TextView

    // Preferências para salvar o tema
    private var isDarkMode = false
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppLogger.i("PlacarActivity iniciada")

        // Pega as preferências salvas (pra lembrar o tema escolhido)
        prefs = getSharedPreferences("config", MODE_PRIVATE)
        isDarkMode = prefs.getBoolean("darkMode", false)

        if (isDarkMode) {
            AppLogger.i("Modo escuro ativado")
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppLogger.i("Modo claro ativado")
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        setContentView(R.layout.activity_placar)

        // Ligando os TextViews
        pTimeA = findViewById(R.id.placarTimeA)
        pTimeB = findViewById(R.id.placarTimeB)

        // Ligando os botões
        val bTresPontosA: Button = findViewById(R.id.tresPontosA)
        val bDoisPontosA: Button = findViewById(R.id.doisPontosA)
        val bLivreA: Button = findViewById(R.id.tiroLivreA)
        val bTresPontosB: Button = findViewById(R.id.tresPontosB)
        val bDoisPontosB: Button = findViewById(R.id.doisPontosB)
        val bLivreB: Button = findViewById(R.id.tiroLivreB)
        val bAnular: Button = findViewById(R.id.anularPonto)
        val bReiniciar: Button = findViewById(R.id.reiniciarPartida)
        val bTrocarTema: Button = findViewById(R.id.trocarTema)
        val btnVoltar: Button = findViewById(R.id.Voltar)

        // Clique para o Time A
        bTresPontosA.setOnClickListener { adicionarPontos(3, "A") }
        bDoisPontosA.setOnClickListener { adicionarPontos(2, "A") }
        bLivreA.setOnClickListener { adicionarPontos(1, "A") }

        // Clique para o Time B
        bTresPontosB.setOnClickListener { adicionarPontos(3, "B") }
        bDoisPontosB.setOnClickListener { adicionarPontos(2, "B") }
        bLivreB.setOnClickListener { adicionarPontos(1, "B") }

        // Anular último ponto registrado
        bAnular.setOnClickListener {
            AppLogger.d("Botão 'Anular' clicado")
            removerPontos(ultimaPontuacao, ultimoTime)
        }

        // Reiniciar placar com popup personalizado
        bReiniciar.setOnClickListener {
            AppLogger.w("Usuário solicitou reinício da partida")
            mostrarDialogoReiniciar()
        }

        bTrocarTema.setOnClickListener {
            try {
                isDarkMode = !isDarkMode
                prefs.edit().putBoolean("darkMode", isDarkMode).apply()

                if (isDarkMode) {
                    AppLogger.i("Tema alterado para escuro")
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppLogger.i("Tema alterado para claro")
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }

                // SIMULA ERRO PROPOSITAL PARA MAU FUNCIONAMENTO - TIRAR O COMENTÁRIO PARA TESTE
               // throw Exception("Falha ao aplicar tema (simulado)")

                recreate()
            } catch (e: Exception) {
                AppLogger.e("Erro ao trocar tema", e)
                Toast.makeText(this, "Erro ao aplicar tema!", Toast.LENGTH_SHORT).show()
            }
        }


        // Voltar para o Hub
        btnVoltar.setOnClickListener {
            AppLogger.i("Voltando para o Hub principal")
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    // Adicionar pontos
    private fun adicionarPontos(pontos: Int, time: String) {
        AppLogger.v("Adicionando $pontos ponto(s) para o time $time")

        if (time == "A") {
            pontuacaoTimeA += pontos
            atualizarPlacar("A")
            Toast.makeText(this, "✅ Time A ganhou $pontos ponto(s)!", Toast.LENGTH_SHORT).show()
        } else {
            pontuacaoTimeB += pontos
            atualizarPlacar("B")
            Toast.makeText(this, "✅ Time B ganhou $pontos ponto(s)!", Toast.LENGTH_SHORT).show()
        }

        ultimaPontuacao = pontos
        ultimoTime = time
        AppLogger.i("Última pontuação registrada: +$pontos para o time $time")
    }

    // Remover pontos
    private fun removerPontos(pontos: Int, time: String) {
        AppLogger.v("Tentando anular $pontos ponto(s) do time $time")

        if (ultimaPontuacao != 0) {
            if (time == "A") {
                pontuacaoTimeA -= pontos
                atualizarPlacar("A")
                AppLogger.i("Ponto removido do time A: -$pontos")
            } else {
                pontuacaoTimeB -= pontos
                atualizarPlacar("B")
                AppLogger.i("Ponto removido do time B: -$pontos")
            }
        } else {
            AppLogger.w("Nenhum ponto para anular — ação ignorada")
            mostrarDialogoImpossivelAnular()
        }
        ultimaPontuacao = 0
    }

    // Atualizar o placar na tela
    private fun atualizarPlacar(time: String) {
        try {
            if (time == "A") {
                pTimeA.text = pontuacaoTimeA.toString()
            } else {
                pTimeB.text = pontuacaoTimeB.toString()
            }
            AppLogger.d("Placar atualizado: A=$pontuacaoTimeA | B=$pontuacaoTimeB")
        } catch (e: Exception) {
            AppLogger.e("Erro ao atualizar o placar", e)
        }
    }

    // Mostrar diálogo quando não há ponto para anular
    private fun mostrarDialogoImpossivelAnular() {
        AppLogger.w("Tentativa de anular ponto inexistente")
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_impossivelanular, null)

        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnOK = dialogView.findViewById<Button>(R.id.btnOK)
        btnOK.setOnClickListener {
            AppLogger.d("Usuário fechou o diálogo de erro de anulação")
            dialog.dismiss()
        }

        dialog.show()
    }

    // Mostrar diálogo de confirmação para reiniciar partida
    private fun mostrarDialogoReiniciar() {
        AppLogger.v("Exibindo diálogo de confirmação para reinício")
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_reiniciar, null)

        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnCancelar = dialogView.findViewById<Button>(R.id.btnCancelar)
        val btnConfirmar = dialogView.findViewById<Button>(R.id.btnConfirmar)

        btnCancelar.setOnClickListener {
            AppLogger.d("Usuário cancelou o reinício da partida")
            dialog.dismiss()
            Toast.makeText(this, "Reinício cancelado", Toast.LENGTH_SHORT).show()
        }

        btnConfirmar.setOnClickListener {
            AppLogger.i("Usuário confirmou o reinício da partida")
            reiniciarPartida()
            dialog.dismiss()
        }

        dialog.show()
    }

    // Reiniciar o placar
    private fun reiniciarPartida() {
        pontuacaoTimeA = 0
        pontuacaoTimeB = 0
        pTimeA.text = "0"
        pTimeB.text = "0"
        ultimaPontuacao = 0
        AppLogger.i("Placar reiniciado com sucesso: A=0 | B=0")
        Toast.makeText(this, "Placar reiniciado com sucesso!", Toast.LENGTH_SHORT).show()
    }
}
