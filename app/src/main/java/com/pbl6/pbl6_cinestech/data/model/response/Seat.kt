package com.pbl6.pbl6_cinestech.data.model.response

data class Seat(
    val id: String,
    val name: String,
    val type: SeatType,
    var isOccupied: Boolean = false,
    var isSelected: Boolean = false
)