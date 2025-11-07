package com.pbl6.pbl6_cinestech.data.model.response

class SeatLayout(
    var rows: List<String>,
    var cols: Int,
    var occupiedSeats: List<Seat>,
    var seats: List<Seat>
) {
}