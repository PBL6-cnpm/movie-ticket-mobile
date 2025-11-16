package com.pbl6.pbl6_cinestech.ui.register

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresExtension
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.pbl6.pbl6_cinestech.R
import com.pbl6.pbl6_cinestech.data.repository.RepositoryProvider
import com.pbl6.pbl6_cinestech.databinding.FragmentRegisterBinding
import com.pbl6.pbl6_cinestech.ui.main.MainViewModel
import hoang.dqm.codebase.base.activity.BaseFragment
import hoang.dqm.codebase.base.activity.navigate
import hoang.dqm.codebase.utils.singleClick


class RegisterFragment : BaseFragment<FragmentRegisterBinding, RegisterViewModel>() {
    private val mainViewModel by activityViewModels<MainViewModel>()

    override val viewModelFactory: ViewModelProvider.Factory?
        get() = RegisterViewModel.RegisterViewModelFactory(
            RepositoryProvider.authRepository
        )
    override fun initView() {
        setUpPasswordConfirmation()
        setUpObserver()
    }

    fun setUpPasswordConfirmation() {
        binding.edtConfirmPassword.addTextChangedListener {
            val password = binding.edtPassword.text.toString()
            val confirmPassword = binding.edtConfirmPassword.text.toString()
            binding.btnRegister.isEnabled = password == confirmPassword
            binding.btnRegister.setImageResource(
                if (password == confirmPassword) R.drawable.shape_bg_enter else R.drawable.shape_bg_disable
            )
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun initListener() {
        binding.btnRegister.singleClick {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            val fullName = binding.edtFullname.text.toString()
            viewModel.register(email, password, fullName)
        }
        binding.btnLoginWGoogle.singleClick {

        }
        binding.tvLogin.singleClick {
            navigate(R.id.loginFragment)
        }
        binding.imgToggleVisiblePassword.singleClick {
            togglePasswordVisibility()
        }

        binding.imgToggleConfirmVisiblePassword.singleClick {
            toggleConfirmPasswordVisibility()
        }
    }

    private var isConfirmPasswordVisible = false

    private fun toggleConfirmPasswordVisibility() {
        val inputType = if (isConfirmPasswordVisible) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        }
        binding.edtConfirmPassword.inputType = inputType

        val iconRes = if (isConfirmPasswordVisible) R.drawable.icon_invisible else R.drawable.icon_visible
        binding.imgToggleConfirmVisiblePassword.setImageResource(iconRes)

        binding.edtConfirmPassword.setSelection(binding.edtConfirmPassword.text.length)
        isConfirmPasswordVisible = !isConfirmPasswordVisible
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

    fun setUpObserver() {
        viewModel.registerResultLiveData.observe(viewLifecycleOwner) { value ->
            Log.d("check register", "setUpObserver: $value")
            if (value?.success == true) {
                if (value.data == null) return@observe
                Toast.makeText(requireContext(), "Register success", Toast.LENGTH_SHORT).show()
                navigate(R.id.loginFragment, isPop = true)
            } else if (value?.success == false) {
                if (value.message.length > 16) {
                    val snackbar =
                        Snackbar.make(binding.root, value.message, 1500)
                            .setAction("OK") {
                            }
                    val snackbarView = snackbar.view
                    val textView = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                    textView.maxLines = 5
                    snackbar.setAction("OK") {
                    }
                    snackbar.show()
                } else
                    Toast.makeText(requireContext(), value.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}