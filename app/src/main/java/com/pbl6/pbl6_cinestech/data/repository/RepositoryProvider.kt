package com.pbl6.pbl6_cinestech.data.repository

import com.pbl6.pbl6_cinestech.data.api.NetworkProvider

object RepositoryProvider {
    val movieRepository: MovieRepository by lazy {
        MovieRepository(NetworkProvider.movieApiService)
    }
    val reviewRepository: ReviewRepository by lazy {
        ReviewRepository(NetworkProvider.reviewApiService)
    }
    val authRepository: AuthRepository by lazy {
        AuthRepository(NetworkProvider.authApiService)
    }
    val branchRepository: BranchRepository by lazy {
        BranchRepository(NetworkProvider.branchApiService)
    }

    val showTimeRepository: ShowTimeRepository by lazy {
        ShowTimeRepository(NetworkProvider.showTimeApiService)
    }

    val seatRepository: SeatRepository by lazy {
        SeatRepository(NetworkProvider.seatApiService)
    }

    val refreshmentsRepository: RefreshmentsRepository by lazy {
        RefreshmentsRepository(NetworkProvider.refreshmentsApiService)
    }

    val bookingRepository: BookingRepository by lazy {
        BookingRepository(NetworkProvider.bookingApiService)
    }

    val paymentRepository: PaymentRepository by lazy {
        PaymentRepository(NetworkProvider.paymentApiService)
    }

    val profileRepository: ProfileRepository by lazy {
        ProfileRepository(NetworkProvider.profileApiService)
    }


}