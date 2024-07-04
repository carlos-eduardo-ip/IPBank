package com.projetosip.ipbank.ui.activity.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun obterDataAtual(): String{
    val formatoData = SimpleDateFormat("dd/MM",Locale.getDefault())
    val dataAtual = Date()
    return formatoData.format(dataAtual)
}