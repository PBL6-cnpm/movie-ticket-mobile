package com.pbl6.pbl6_cinestech.ui.login

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.pbl6.pbl6_cinestech.R
import com.pbl6.pbl6_cinestech.data.repository.RepositoryProvider
import com.pbl6.pbl6_cinestech.databinding.FragmentLoginBinding
import com.pbl6.pbl6_cinestech.ui.home.HomeViewModel
import com.pbl6.pbl6_cinestech.utils.AppConstants
import com.pbl6.pbl6_cinestech.utils.SecurePrefs
import hoang.dqm.codebase.base.activity.BaseFragment
import hoang.dqm.codebase.base.activity.navigate
import hoang.dqm.codebase.utils.singleClick


class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>() {
    override val viewModelFactory: ViewModelProvider.Factory
        get() = LoginViewModel.LoginViewModelFactory(
            RepositoryProvider.authRepository
        )

    override fun initView() {
        setUpObserver()
    }

    fun setUpObserver(){
        viewModel.loginResultLiveData.observe(viewLifecycleOwner){ value ->
            Log.d("check login", "setUpObserver: $value")
            if (value?.success == true){
                if(value.data == null) return@observe
                Log.d("check login", "setUpObserver: ${value.data.accessToken}")
                if (value.data.account.status == AppConstants.PENDING){
                    Toast.makeText(requireContext(), value.data.message, Toast.LENGTH_SHORT).show()
                }else if (value.data.account.status == AppConstants.ACTIVE){
                    // navigate to main
                    SecurePrefs.saveTokens(requireContext(), value.data.accessToken, value.data.refreshToken)
                    navigate(R.id.homeFragment)

                }
            }else{
                value?.message?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun initListener() {
        binding.btnLogin.singleClick {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            Log.d("check login", "setUpObserver: login")
            viewModel.login(email, password)
        }
        binding.tvForgotPassword.singleClick {
            navigate(R.id.forgotPasswordFragment)
        }
    }

    override fun initData() {
    }
}