package com.example.aplicativohub.calculadora

import com.example.aplicativohub.calculadora.HistoryActivity
import com.example.aplicativohub.calculadora.SettingsActivity


import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.aplicativohub.R
import com.example.aplicativohub.hub.MainActivity
import com.google.android.material.button.MaterialButton

class CalculadoraActivity : AppCompatActivity() {
    private lateinit var txtResultado: EditText
    private lateinit var sharedPreferences: SharedPreferences

    private val calculationHistory = mutableListOf<String>()
    private var fullExpression: String = ""
    private var operand: Double? = null
    private var pendingOp: String? = null
    private var resetInput: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculadora)
        applySavedTheme();

        // EditText de display
        txtResultado = findViewById(R.id.txtResultado)
        txtResultado.isFocusableInTouchMode = true
        txtResultado.requestFocus()
        txtResultado.isCursorVisible = true


        // Botões de dígitos
        val digits = listOf(
            "0" to R.id.btn0,
            "1" to R.id.btn1,
            "2" to R.id.btn2,
            "3" to R.id.btn3,
            "4" to R.id.btn4,
            "5" to R.id.btn5,
            "6" to R.id.btn6,
            "7" to R.id.btn7,
            "8" to R.id.btn8,
            "9" to R.id.btn9,
            "." to R.id.btnPonto
        )
        digits.forEach { (digit, id) ->
            findViewById<Button>(id).setOnClickListener { appendDigit(digit) }
        }

        // Botões de operações
        val ops = listOf(
            "+" to R.id.btnSomar,
            "-" to R.id.btnSubtrair,
            "×" to R.id.btnMultiplicar,
            "÷" to R.id.btnDividir
        )
        ops.forEach { (op, id) ->
            findViewById<Button>(id).setOnClickListener { onOperator(op) }
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        // EditText de display
        txtResultado = findViewById(R.id.txtResultado)



        // Botão de configurações
        findViewById<MaterialButton>(R.id.btnConfiguracoes).setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        // Botão igual
        findViewById<Button>(R.id.btnIgual).setOnClickListener { onEquals() }

        // Botão limpar tudo (AC)
        findViewById<Button>(R.id.btnBackspace).setOnClickListener { clearAll() }

        // Botão backspace (⌫)
        findViewById<Button>(R.id.btnClear).setOnClickListener { backspace() }

        // Botões de parênteses
        findViewById<MaterialButton>(R.id.btnParentesesE).setOnClickListener { appendParenthesis("(") }
        findViewById<MaterialButton>(R.id.btnParentesesD).setOnClickListener { appendParenthesis(")") }

        // Botões de navegação do cursor
        findViewById<MaterialButton>(R.id.btnAvancar).setOnClickListener { moveCursor(1) }
        findViewById<MaterialButton>(R.id.btnVoltar).setOnClickListener { moveCursor(-1) }

        // Botão de quadrado (x²)
        findViewById<MaterialButton>(R.id.btnQuadrado).setOnClickListener { calculateSquare() }

        // Botão de raiz quadrada (√)
        findViewById<MaterialButton>(R.id.btnRaiz).setOnClickListener { calculateSquareRoot() }

        findViewById<MaterialButton>(R.id.btnHist).setOnClickListener {
            showHistory()
        }

        val btnVoltar: Button = findViewById(R.id.Voltar)

        btnVoltar.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun appendDigit(d: String) {
        val cursorPosition = txtResultado.selectionStart
        val currentText = txtResultado.text.toString()

        // Verifica se pode adicionar ponto decimal
        if (d == ".") {
            val currentNumber = getSelectedOrCurrentNumber(cursorPosition, currentText)
            if (currentNumber.contains(".")) return
        }

        // Insere o dígito na posição do cursor
        val newText = currentText.substring(0, cursorPosition) + d + currentText.substring(cursorPosition)
        txtResultado.setText(newText)
        txtResultado.setSelection(cursorPosition + 1)
    }

    private fun appendParenthesis(parenthesis: String) {
        val cursorPosition = txtResultado.selectionStart
        val currentText = txtResultado.text.toString()

        // Insere o parêntese na posição do cursor
        val newText = currentText.substring(0, cursorPosition) + parenthesis + currentText.substring(cursorPosition)
        txtResultado.setText(newText)
        txtResultado.setSelection(cursorPosition + 1)
    }

    private fun onOperator(op: String) {
        val cursorPosition = txtResultado.selectionStart
        val currentText = txtResultado.text.toString()

        if (currentText.isNotEmpty()) {
            // Adiciona o operador na posição do cursor
            val newText = currentText.substring(0, cursorPosition) + " $op " + currentText.substring(cursorPosition)
            txtResultado.setText(newText)
            txtResultado.setSelection(cursorPosition + 3) // Move cursor após o operador com espaços
        }
    }

    private fun applySavedTheme() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val savedTheme = SettingsActivity.getCurrentTheme(prefs)
        AppCompatDelegate.setDefaultNightMode(savedTheme)

        delegate.applyDayNight()
    }

    override fun onResume() {
        super.onResume()
        // Recarrega as configurações quando a activity retorna
        applySavedTheme()
    }

    private fun onEquals() {
        val expression = txtResultado.text.toString()
        if (expression.isNotEmpty()) {
            try {
                val result = calcularExpressao(expression)
                val decimalPlaces = SettingsActivity.getDecimalPlaces(sharedPreferences)

                val formattedResult = "%.${decimalPlaces}f".format(result)
                val resultText = "$expression = $formattedResult"
                txtResultado.setText(resultText)
                txtResultado.setSelection(resultText.length)

                addToHistory(expression, formattedResult)

            } catch (e: Exception) {
                Toast.makeText(this, "Erro na expressão: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun addToHistory(expression: String, result: String) {
        // Formato: expressão|resultado
        val historyItem = "$expression|$result"
        calculationHistory.add(0, historyItem) // Adiciona no início da lista

        // Limita o histórico aos últimos 50 itens
        if (calculationHistory.size > 50) {
            calculationHistory.removeAt(calculationHistory.size - 1)
        }
    }

    private fun showHistory() {
        try {
            if (calculationHistory.isNotEmpty()) {
                val intent = Intent(this, HistoryActivity::class.java)
                intent.putStringArrayListExtra("history", ArrayList(calculationHistory))
                startActivity(intent)
            } else {
                Toast.makeText(this, "Nenhum cálculo no histórico", Toast.LENGTH_SHORT).show()
            }
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Tela de histórico não disponível", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        } catch (e: SecurityException) {
            Toast.makeText(this, "Permissão negada para abrir histórico", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao abrir histórico: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun calcularExpressao(expr: String): Double {
        val expressao = expr
            .replace("×", "*")
            .replace("÷", "/")

        return try {
            avaliar(expressao)
        } catch (e: Exception) {
            Double.NaN
        }
    }

    private fun avaliar(expressao: String): Double {
        val tokens = expressao.replace(" ", "").toCharArray()
        val valores = java.util.Stack<Double>()
        val operadores = java.util.Stack<Char>()

        var i = 0
        while (i < tokens.size) {
            val c = tokens[i]
            when {
                c.isDigit() || c == '.' -> {
                    val sb = StringBuilder()
                    while (i < tokens.size && (tokens[i].isDigit() || tokens[i] == '.')) {
                        sb.append(tokens[i])
                        i++
                    }
                    valores.push(sb.toString().toDouble())
                    i--
                }
                c == '(' -> operadores.push(c)
                c == ')' -> {
                    while (operadores.peek() != '(') {
                        valores.push(aplicarOperador(operadores.pop(), valores.pop(), valores.pop()))
                    }
                    operadores.pop()
                }
                c == '+' || c == '-' || c == '*' || c == '/' -> {
                    while (!operadores.empty() && temPrecedencia(c, operadores.peek())) {
                        valores.push(aplicarOperador(operadores.pop(), valores.pop(), valores.pop()))
                    }
                    operadores.push(c)
                }
            }
            i++
        }

        while (!operadores.empty()) {
            valores.push(aplicarOperador(operadores.pop(), valores.pop(), valores.pop()))
        }

        return valores.pop()
    }

    private fun temPrecedencia(op1: Char, op2: Char): Boolean {
        if (op2 == '(' || op2 == ')') return false
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) return false
        return true
    }

    private fun aplicarOperador(op: Char, b: Double, a: Double): Double {
        return when (op) {
            '+' -> a + b
            '-' -> a - b
            '*' -> a * b
            '/' -> if (b == 0.0) Double.NaN else a / b
            else -> 0.0
        }
    }


    private fun evaluateSimpleExpression(expression: String): Double {
        var expr = expression

        // Processa multiplicação e divisão
        val mulDivRegex = Regex("""([\d\.]+)\s*([×÷])\s*([\d\.]+)""")
        var match = mulDivRegex.find(expr)
        while (match != null) {
            val (left, op, right) = match.destructured
            val leftNum = left.toDouble()
            val rightNum = right.toDouble()
            val result = when (op) {
                "×" -> leftNum * rightNum
                "÷" -> {
                    if (rightNum == 0.0) throw ArithmeticException("Divisão por zero")
                    leftNum / rightNum
                }
                else -> throw IllegalArgumentException("Operador inválido: $op")
            }
            expr = expr.replaceRange(match.range, result.toString())
            match = mulDivRegex.find(expr)
        }

        // Processa adição e subtração
        val addSubRegex = Regex("""([\d\.]+)\s*([+\-])\s*([\d\.]+)""")
        match = addSubRegex.find(expr)
        while (match != null) {
            val (left, op, right) = match.destructured
            val leftNum = left.toDouble()
            val rightNum = right.toDouble()
            val result = when (op) {
                "+" -> leftNum + rightNum
                "-" -> leftNum - rightNum
                else -> throw IllegalArgumentException("Operador inválido: $op")
            }
            expr = expr.replaceRange(match.range, result.toString())
            match = addSubRegex.find(expr)
        }

        return expr.toDouble()
    }

    private fun calculateSquare() {
        val cursorPosition = txtResultado.selectionStart
        val currentText = txtResultado.text.toString()

        if (currentText.isNotEmpty()) {
            val selectedText = getSelectedOrCurrentNumber(cursorPosition, currentText)
            val value = selectedText.toDoubleOrNull()

            if (value != null) {
                val square = value * value
                val newText = currentText.replaceRange(
                    getNumberStartIndex(cursorPosition, currentText),
                    getNumberEndIndex(cursorPosition, currentText),
                    square.toString()
                )

                txtResultado.setText(newText)
                txtResultado.setSelection(getNumberStartIndex(cursorPosition, newText) + square.toString().length)
            }
        }
    }

    private fun calculateSquareRoot() {
        val cursorPosition = txtResultado.selectionStart
        val currentText = txtResultado.text.toString()

        if (currentText.isNotEmpty()) {
            val selectedText = getSelectedOrCurrentNumber(cursorPosition, currentText)
            val value = selectedText.toDoubleOrNull()

            if (value != null && value >= 0) {
                val sqrt = Math.sqrt(value)
                val newText = currentText.replaceRange(
                    getNumberStartIndex(cursorPosition, currentText),
                    getNumberEndIndex(cursorPosition, currentText),
                    sqrt.toString()
                )

                txtResultado.setText(newText)
                txtResultado.setSelection(getNumberStartIndex(cursorPosition, newText) + sqrt.toString().length)
            } else if (value != null && value < 0) {
                Toast.makeText(this, "Não é possível calcular raiz de número negativo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun clearAll() {
        txtResultado.setText("")
        txtResultado.setSelection(0)
        fullExpression = ""
        operand = null
        pendingOp = null
        resetInput = false
    }

    private fun backspace() {
        val cursorPosition = txtResultado.selectionStart
        if (cursorPosition > 0) {
            val currentText = txtResultado.text.toString()
            val newText = currentText.substring(0, cursorPosition - 1) + currentText.substring(cursorPosition)
            txtResultado.setText(newText)
            txtResultado.setSelection(cursorPosition - 1)
        }
    }

    private fun moveCursor(direction: Int) {
        val currentPosition = txtResultado.selectionStart
        val newPosition = currentPosition + direction

        if (newPosition >= 0 && newPosition <= txtResultado.text.length) {
            txtResultado.setSelection(newPosition)
        }
    }

    // Funções auxiliares para manipulação de números
    private fun getSelectedOrCurrentNumber(cursorPosition: Int, text: String): String {
        val start = getNumberStartIndex(cursorPosition, text)
        val end = getNumberEndIndex(cursorPosition, text)
        return text.substring(start, end)
    }

    private fun getNumberStartIndex(cursorPosition: Int, text: String): Int {
        var start = cursorPosition
        while (start > 0 && isPartOfNumber(text[start - 1])) {
            start--
        }
        return start
    }

    private fun getNumberEndIndex(cursorPosition: Int, text: String): Int {
        var end = cursorPosition
        while (end < text.length && isPartOfNumber(text[end])) {
            end++
        }
        return end
    }

    private fun isPartOfNumber(char: Char): Boolean {
        return char.isDigit() || char == '.' || char == '-' || char == 'E' || char == 'e'
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("currentText", txtResultado.text.toString())
        outState.putStringArrayList("calculationHistory", ArrayList(calculationHistory))
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val currentText = savedInstanceState.getString("currentText", "")
        txtResultado.setText(currentText)
        txtResultado.setSelection(currentText.length)

        val savedHistory = savedInstanceState.getStringArrayList("calculationHistory")
        savedHistory?.let {
            calculationHistory.clear()
            calculationHistory.addAll(it)
        }
    }
}