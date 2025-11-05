package com.pbl6.pbl6_cinestech.data.model.response

class SeatBookingResponse(
    var roomId: String,
    var roomName: String,
    var seatLayout: SeatLayout,
    var totalSeats: Int,
    var availableSeats: Int,
    var occupiedSeats: Int,
    var typeSeatList: List<SeatType>
) {
}