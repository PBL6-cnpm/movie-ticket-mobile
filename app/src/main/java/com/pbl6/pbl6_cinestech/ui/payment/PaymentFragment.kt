package com.pbl6.pbl6_cinestech.ui.payment

import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.pbl6.pbl6_cinestech.R
import com.pbl6.pbl6_cinestech.databinding.FragmentPaymentBinding
import com.stripe.android.ApiResultCallback
import com.stripe.android.PaymentConfiguration
import com.stripe.android.PaymentIntentResult
import com.stripe.android.Stripe
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.model.PaymentMethodCreateParams
import com.stripe.android.model.StripeIntent
import hoang.dqm.codebase.base.activity.BaseFragment
import hoang.dqm.codebase.base.activity.onBackPressed
import hoang.dqm.codebase.base.activity.popBackStack
import hoang.dqm.codebase.utils.singleClick


class PaymentFragment : BaseFragment<FragmentPaymentBinding, PaymentViewModel>() {
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        stripe.onPaymentResult(
            requestCode,
            data!!,
            object : ApiResultCallback<PaymentIntentResult> {
                override fun onSuccess(result: PaymentIntentResult) {
                    val paymentIntent = result.intent
                    when (paymentIntent.status) {
                        StripeIntent.Status.Succeeded -> Toast.makeText(
                            requireContext(),
                            "Payment success!",
                            Toast.LENGTH_SHORT
                        ).show()

                        StripeIntent.Status.RequiresPaymentMethod -> Toast.makeText(
                            requireContext(),
                            "Payment failed!",
                            Toast.LENGTH_SHORT
                        ).show()

                        else -> Toast.makeText(
                            requireContext(),
                            "Payment status: ${paymentIntent.status}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onError(e: Exception) {
                    Log.d("check payment", "onError: ${e.localizedMessage}")
                    Toast.makeText(
                        requireContext(),
                        "Error: ${e.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private val clientSecret by lazy {
        arguments?.getString("clientSecret") ?: ""
    }
    private lateinit var stripe: Stripe
    override fun initView() {
        updateUI()
        preparePayment()
    }

    private fun updateUI() {
        adjustInsetsForBottomNavigation(binding.btnBack)
    }

    fun preparePayment() {
        stripe = Stripe(
            requireActivity(),
            PaymentConfiguration.getInstance(requireContext()).publishableKey
        )
        binding.btnPay.singleClick {
            val params = binding.cardInputWidget.paymentMethodCreateParams
            if (params != null) {
                confirmPayment(params)
            } else {
                Toast.makeText(requireContext(), "Invalid cards", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun confirmPayment(paymentMethodCreateParams: PaymentMethodCreateParams) {
        Log.d("check payment", "confirmPayment: $clientSecret")
        val confirmParams = ConfirmPaymentIntentParams.createWithPaymentMethodCreateParams(
            paymentMethodCreateParams,
            clientSecret
        )

        stripe.confirmPayment(this, confirmParams)
    }

    override fun initListener() {
        binding.btnBack.singleClick {
            popBackStack(R.id.homeFragment)
        }
        onBackPressed {
            popBackStack(R.id.homeFragment)
        }

    }

    override fun initData() {
    }
}