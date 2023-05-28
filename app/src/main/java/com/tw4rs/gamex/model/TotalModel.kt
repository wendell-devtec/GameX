/*
 * Copyright (c) de Tw4rs 2021.
 */
package com.tw4rs.gamex.model

class TotalModel {
    var total = 0.0
    var wait = 0

    constructor()
    constructor(total: Double, wait: Int) {
        this.total = total
        this.wait = wait
    }
}