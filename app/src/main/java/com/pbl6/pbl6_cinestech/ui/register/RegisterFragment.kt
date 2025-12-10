package com.pbl6.pbl6_cinestech.ui.register

import androidx.core.widget.addTextChangedListener
import com.pbl6.pbl6_cinestech.R
import com.pbl6.pbl6_cinestech.databinding.FragmentRegisterBinding
import hoang.dqm.codebase.base.activity.BaseFragment
import hoang.dqm.codebase.base.activity.navigate
import hoang.dqm.codebase.utils.singleClick


class RegisterFragment : BaseFragment<FragmentRegisterBinding, RegisterViewModel>() {
    override fun initView() {
        setUpPasswordConfirmation()

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

    override fun initListener() {
        binding.btnRegister.singleClick {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtFullname.text.toString()
            val fullName = binding.edtFullname.text.toString()
            viewModel.register(email, password, fullName)
        }
        binding.btnLoginWGoogle.singleClick {

        }
        binding.tvLogin.singleClick {
            navigate(R.id.loginFragment)
        }
    }

    override fun initData() {
    }
}