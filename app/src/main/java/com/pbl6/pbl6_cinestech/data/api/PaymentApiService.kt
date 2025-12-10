package com.pbl6.pbl6_cinestech.data.api

import com.pbl6.pbl6_cinestech.data.model.request.PaymentRequest
import com.pbl6.pbl6_cinestech.data.model.response.PaymentResponse
import com.pbl6.pbl6_cinestech.data.model.response.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PaymentApiService {
    @POST("/api/v1/bookings/create-payment-intent")
    suspend fun createPaymentIntent(@Body paymentRequest: PaymentRequest): Response<PaymentResponse>

}