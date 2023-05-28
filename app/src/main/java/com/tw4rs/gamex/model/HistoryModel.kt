package com.tw4rs.gamex.model

class HistoryModel {
    var id: String? = null
    var credencial: String? = null
    var status: String? = null
    var tipo: String? = null
    var valor: String? = null
    var date: String? = null
    var pontos = 0

    constructor(
        id: String?,
        credencial: String?,
        status: String?,
        tipo: String?,
        valor: String?,
        date: String?,
        pontos: Int
    ) {
        this.id = id
        this.credencial = credencial
        this.status = status
        this.tipo = tipo
        this.valor = valor
        this.date = date
        this.pontos = pontos
    }

    constructor()
}