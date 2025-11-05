package com.pbl6.pbl6_cinestech.data.model.response

class MovieResponse(
    var id: String,
    var name: String,
    var description: String,
    var duration: Int,
    var ageLimit: Int,
    var director: String,
    var trailer: String,
    var poster: String,
    var releaseDate: String,
    var screeningStart: String?,
    var screeningEnd: String?,
    var genres: List<GenreResponse>,
    var actors: List<Actor>,
    var rated: Int?,
    var createdAt: String,
    var updatedAt: String,
    reviews: List<ReviewResponse>
) {
}