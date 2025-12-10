package com.pbl6.pbl6_cinestech.ui.forgot

import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.pbl6.pbl6_cinestech.R
import com.pbl6.pbl6_cinestech.data.repository.RepositoryProvider
import com.pbl6.pbl6_cinestech.databinding.FragmentForgotPasswordBinding
import com.pbl6.pbl6_cinestech.ui.login.LoginViewModel
import hoang.dqm.codebase.base.activity.BaseFragment
import hoang.dqm.codebase.base.activity.navigate
import hoang.dqm.codebase.base.activity.onBackPressed
import hoang.dqm.codebase.base.activity.popBackStack
import hoang.dqm.codebase.utils.singleClick

class ForgotPasswordFragment : BaseFragment<FragmentForgotPasswordBinding, ForgotPasswordViewModel>() {
    override val viewModelFactory: ViewModelProvider.Factory
        get() = ForgotPasswordViewModel.ForgotPasswordViewModelFactory(
            RepositoryProvider.authRepository
        )

    override fun initView() {
        setUpObserver()
    }

    fun setUpObserver(){
        viewModel.forgotPasswordResultLiveData.observe(viewLifecycleOwner){ value ->
            if (value?.success == true){
                Toast.makeText(requireContext(),
                    getString(R.string.text_open_your_email_to_retrieve_your_new_password), Toast.LENGTH_SHORT).show()
                popBackStack(R.id.loginFragment)
            }else{
                value?.message?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    override fun initListener() {
        binding.edtEmail.addTextChangedListener{
            val email = binding.edtEmail.text.toString()
            binding.btnResetPassword.isEnabled = email.isNotEmpty()
            binding.btnResetPassword.setImageResource(
                if (email.isNotEmpty()) R.drawable.shape_bg_enter else R.drawable.shape_bg_disable
            )
        }
        onBackPressed {
            popBackStack()
        }
        binding.btnBack.singleClick {
            popBackStack()
        }
    }

    override fun initData() {
        binding.btnResetPassword.singleClick {
            viewModel.forgotPassword(binding.edtEmail.text.toString())
        }
    }
}