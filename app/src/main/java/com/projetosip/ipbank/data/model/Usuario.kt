package com.projetosip.ipbank.data.model

data class Usuario(
    var id: String = "",
    var nome: String = "",
    var email: String = "",
    var foto: String = "",
    var saldo: Double = 0.0
)
