package com.example.aplicativohub.hub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.aplicativohub.R
import com.example.aplicativohub.placar.PlacarActivity
import com.example.aplicativohub.calculadora.CalculadoraActivity



class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cardPlacar = findViewById<LinearLayout>(R.id.cardPlacar)
        val cardCalc = findViewById<LinearLayout>(R.id.cardCalc)
      //  val cardNovoApp = findViewById<LinearLayout>(R.id.cardNovoApp)

        cardPlacar.setOnClickListener {
            startActivity(Intent(this, PlacarActivity::class.java))
        }
        cardCalc.setOnClickListener {
            startActivity(Intent(this, CalculadoraActivity::class.java))
        }


    }
}

