package com.edu.tarc.iotproject.AquariumModule

data class AquariumData(
    var temperature: Int?,
    var humidity: Int?,
    var img: String?,
    var id: String?
) {
    constructor() : this(null, null, null, null)
}