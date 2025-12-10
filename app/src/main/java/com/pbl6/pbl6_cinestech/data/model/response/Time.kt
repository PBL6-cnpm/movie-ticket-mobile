package com.pbl6.pbl6_cinestech.data.model.response

class Time(
    var id: String,
    var time: String,
    var totalSeats: Int,
    var availableSeats: Int,
    var occupiedSeats: Int,
    var roomId: String,
    var roomName: String
) {
}