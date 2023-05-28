/*
 * Copyright (c) de Tw4rs 2021.
 */
package com.tw4rs.gamex.model

class VideoModel {
    var videoId: String? = null
    var id: String? = null
    var title: String? = null
    var timer: String? = null
    var point: String? = null
    var ads: String? = null

    constructor(
        videoId: String?,
        id: String?,
        title: String?,
        timer: String?,
        point: String?,
        ads: String?
    ) {
        this.videoId = videoId
        this.id = id
        this.title = title
        this.timer = timer
        this.point = point
        this.ads = ads
    }

    constructor()
}