package com.example.aplicativohub.hub

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.aplicativohub.R
import com.example.aplicativohub.bloconotas.BlocoNotasActivity
import com.example.aplicativohub.calculadora.CalculadoraActivity
import com.example.aplicativohub.placar.PlacarActivity
import com.example.aplicativohub.utils.AppLogger

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppLogger.i("Hub iniciado")

        val cardPlacar = findViewById<LinearLayout>(R.id.cardPlacar)
        val cardCalc = findViewById<LinearLayout>(R.id.cardCalc)
        val cardBloco = findViewById<LinearLayout>(R.id.cardBlocoNotas)

        cardPlacar.setOnClickListener {
            AppLogger.d("Usuário clicou em Placar")
            try {
                startActivity(Intent(this, PlacarActivity::class.java))
                AppLogger.i("Navegando para PlacarActivity")
            } catch (e: Exception) {
                AppLogger.e("Erro ao abrir PlacarActivity", e)
            }
        }

        cardCalc.setOnClickListener {
            AppLogger.d("Usuário clicou em Calculadora")
            try {
                startActivity(Intent(this, CalculadoraActivity::class.java))
                AppLogger.i("Navegando para CalculadoraActivity")
            } catch (e: Exception) {
                AppLogger.e("Erro ao abrir CalculadoraActivity", e)
            }
        }

        cardBloco.setOnClickListener {
            AppLogger.d("Usuário clicou em Bloco de Notas")
            try {
                startActivity(Intent(this, BlocoNotasActivity::class.java))
                AppLogger.i("Navegando para BlocoNotasActivity")
            } catch (e: Exception) {
                AppLogger.e("Erro ao abrir BlocoNotasActivity", e)
            }
        }
    }
}
