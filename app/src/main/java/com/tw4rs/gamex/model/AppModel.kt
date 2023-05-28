/*
 * Copyright (c) de Tw4rs 2021.
 */
package com.tw4rs.gamex.model

class AppModel {
    var Coins = 0
    var Name: String? = null
    var pix_chave: String? = null
    var pointsAll = 0
    var uid: String? = null
    var punishment = 0
    var Email: String? = null
    var status_block : Boolean? = null
    var my_ref_id: String? = null
    var level:String? = null
    var progress = 0

    constructor()
    constructor(
        Coins: Int,
        Name: String?,
        pix_chave: String?,
        pointsAll: Int,
        uid: String?,
        punishment: Int,
        Email: String?,
        status_block: Boolean?,
        my_ref_id: String?,
        level: String?,
        progress: Int
    ) {
        this.Coins = Coins
        this.Name = Name
        this.pix_chave = pix_chave
        this.pointsAll = pointsAll
        this.uid = uid
        this.punishment = punishment
        this.Email = Email
        this.status_block = status_block
        this.my_ref_id = my_ref_id
        this.level = level
        this.progress = progress
    }


}