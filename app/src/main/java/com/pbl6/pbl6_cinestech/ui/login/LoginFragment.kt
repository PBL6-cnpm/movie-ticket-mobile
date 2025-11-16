package com.pbl6.pbl6_cinestech.ui.login

import android.text.InputType
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.pbl6.pbl6_cinestech.R
import com.pbl6.pbl6_cinestech.data.repository.RepositoryProvider
import com.pbl6.pbl6_cinestech.databinding.FragmentLoginBinding
import com.pbl6.pbl6_cinestech.ui.home.HomeViewModel
import com.pbl6.pbl6_cinestech.ui.main.MainViewModel
import com.pbl6.pbl6_cinestech.utils.AppConstants
import com.pbl6.pbl6_cinestech.utils.SecurePrefs
import hoang.dqm.codebase.base.activity.BaseFragment
import hoang.dqm.codebase.base.activity.navigate
import hoang.dqm.codebase.base.activity.onBackPressed
import hoang.dqm.codebase.base.activity.popBackStack
import hoang.dqm.codebase.utils.singleClick
import kotlin.getValue


class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>() {
    override val viewModelFactory: ViewModelProvider.Factory
        get() = LoginViewModel.LoginViewModelFactory(
            RepositoryProvider.authRepository
        )
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun initView() {
        setUpObserver()
    }

    fun setUpObserver(){
        viewModel.loginResultLiveData.observe(viewLifecycleOwner){ value ->
            Log.d("check login", "setUpObserver: $value")
            if (value?.success == true){
                if(value.data == null) return@observe
                if (value.data.account.status == AppConstants.PENDING){
                    Toast.makeText(requireContext(), value.data.message, Toast.LENGTH_SHORT).show()
                }else if (value.data.account.status == AppConstants.ACTIVE){
                    // navigate to main
                    SecurePrefs.saveTokens(requireContext(), value.data.accessToken, value.data.refreshToken)
                    mainViewModel.setLogin(true)
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
            if (email.isEmpty()){
                Toast.makeText(requireContext(), "Email is empty", Toast.LENGTH_SHORT).show()
                return@singleClick
            } else if (password.isEmpty()){
                Toast.makeText(requireContext(), "Password is empty", Toast.LENGTH_SHORT).show()
                return@singleClick
            }
            viewModel.login(email, password)
        }
        binding.tvForgotPassword.singleClick {
            navigate(R.id.forgotPasswordFragment)
        }
        binding.tvRegister.singleClick {
            navigate(R.id.registerFragment)
        }

        binding.imgToggleVisiblePassword.singleClick {
            togglePasswordVisibility()
        }
        onBackPressed {
            popBackStack(R.id.homeFragment)
        }
    }

    private var isPasswordVisible = false

    private fun togglePasswordVisibility() {
        val inputType = if (isPasswordVisible) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        }
        binding.edtPassword.inputType = inputType

        val iconRes = if (isPasswordVisible) R.drawable.icon_invisible else R.drawable.icon_visible
        binding.imgToggleVisiblePassword.setImageResource(iconRes)

        binding.edtPassword.setSelection(binding.edtPassword.text.length)
        isPasswordVisible = !isPasswordVisible
    }




    override fun initData() {
    }
}