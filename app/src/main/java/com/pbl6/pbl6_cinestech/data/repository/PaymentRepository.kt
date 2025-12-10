package com.pbl6.pbl6_cinestech.data.repository

import com.pbl6.pbl6_cinestech.data.api.PaymentApiService
import com.pbl6.pbl6_cinestech.data.model.request.PaymentRequest
import com.pbl6.pbl6_cinestech.data.model.response.PaymentResponse
import com.pbl6.pbl6_cinestech.data.model.response.Response

class PaymentRepository(
    private val paymentApiService: PaymentApiService

) {
    suspend fun createPaymentIntent(paymentRequest: PaymentRequest): Response<PaymentResponse> {
        return paymentApiService.createPaymentIntent(paymentRequest)
    }
}