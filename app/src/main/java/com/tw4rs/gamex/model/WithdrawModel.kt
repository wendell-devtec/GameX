/*
 * Copyright (c) de Tw4rs 2021.
 */
package com.tw4rs.gamex.model

class WithdrawModel {
    var title: String? = null
    var valor: String? = null
    var point: String? = null

    constructor()
    constructor(title: String?, valor: String?, point: String?) {
        this.title = title
        this.valor = valor
        this.point = point
    }
}