package com.tw4rs.gamex.util

class DeviceInformation {
    var deviceId: String? = null
    var email: String? = null

    constructor()
    constructor(deviceId: String?, email: String?) {
        this.deviceId = deviceId
        this.email = email
    }
}