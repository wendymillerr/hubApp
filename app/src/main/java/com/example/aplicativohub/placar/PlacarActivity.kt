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

        // pega as preferências salvas (pra lembrar o tema escolhido)
        prefs = getSharedPreferences("config", MODE_PRIVATE)
        isDarkMode = prefs.getBoolean("darkMode", false)

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        setContentView(R.layout.activity_placar) // usa o XML que você mostrou

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
        val bTrocarTema: Button = findViewById(R.id.trocarTema) // botão novo para mudar tema

        // clique para o Time A
        bTresPontosA.setOnClickListener { adicionarPontos(3, "A") }
        bDoisPontosA.setOnClickListener { adicionarPontos(2, "A") }
        bLivreA.setOnClickListener { adicionarPontos(1, "A") }

        //  clique para o Time B
        bTresPontosB.setOnClickListener { adicionarPontos(3, "B") }
        bDoisPontosB.setOnClickListener { adicionarPontos(2, "B") }
        bLivreB.setOnClickListener { adicionarPontos(1, "B") }

        //Anular último ponto registrado
        bAnular.setOnClickListener { removerPontos(ultimaPontuacao, ultimoTime) }
        // Reiniciar placar com popup personalizado
        bReiniciar.setOnClickListener {
            mostrarDialogoReiniciar()
        }


        bTrocarTema.setOnClickListener {
            isDarkMode = !isDarkMode
            prefs.edit().putBoolean("darkMode", isDarkMode).apply()

            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            recreate()
        }

        val btnVoltar: Button = findViewById(R.id.Voltar)

        btnVoltar.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }


    private fun adicionarPontos(pontos: Int, time: String) {
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
    }

    private fun removerPontos(pontos: Int, time: String) {
        if (ultimaPontuacao != 0){
            if (time == "A") {
                pontuacaoTimeA -= pontos
                atualizarPlacar("A")
            } else {
                pontuacaoTimeB -= pontos
                atualizarPlacar("B")
            }
        } else{
            mostrarDialogoImpossivelAnular()
        }
        ultimaPontuacao = 0
    }


    private fun mostrarDialogoImpossivelAnular() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_impossivelanular, null)

        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnOK = dialogView.findViewById<Button>(R.id.btnOK)

        btnOK.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun atualizarPlacar(time: String) {
        if (time == "A") {
            pTimeA.text = pontuacaoTimeA.toString()
        } else {
            pTimeB.text = pontuacaoTimeB.toString()
        }
    }


    private fun mostrarDialogoReiniciar() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_reiniciar, null)

        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnCancelar = dialogView.findViewById<Button>(R.id.btnCancelar)
        val btnConfirmar = dialogView.findViewById<Button>(R.id.btnConfirmar)

        btnCancelar.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(this, "Reinício cancelado", Toast.LENGTH_SHORT).show()
        }

        btnConfirmar.setOnClickListener {
            reiniciarPartida()
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun reiniciarPartida() {
        pontuacaoTimeA = 0
        pontuacaoTimeB = 0
        pTimeA.text = "0"
        pTimeB.text = "0"
        Toast.makeText(this, "Placar reiniciado com sucesso!", Toast.LENGTH_SHORT).show()
        ultimaPontuacao = 0
    }
}