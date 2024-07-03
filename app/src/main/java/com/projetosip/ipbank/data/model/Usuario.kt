package com.projetosip.ipbank.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Usuario(
    var id: String = "",
    var nome: String = "",
    var email: String = "",
    var foto: String = "",
    var saldo: Double = 0.0
) : Parcelable
