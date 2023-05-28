package com.tw4rs.gamex.model

class EventModel {
    var status = 0
    var point = 0
    var data: String? = null
    var nextData: String? = null
    var versao = 0

    constructor()
    constructor(status: Int, point: Int, data: String?, nextData: String?, versao: Int) {
        this.status = status
        this.point = point
        this.data = data
        this.nextData = nextData
        this.versao = versao
    }
}