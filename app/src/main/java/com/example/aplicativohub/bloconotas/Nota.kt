package com.example.aplicativohub.bloconotas

data class Nota(
    val titulo: String,
    val conteudo: String,
    val timestamp: Long = System.currentTimeMillis()
)