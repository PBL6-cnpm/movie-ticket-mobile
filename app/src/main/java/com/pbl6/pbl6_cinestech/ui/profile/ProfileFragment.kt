package com.pbl6.pbl6_cinestech.ui.profile

import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.pbl6.pbl6_cinestech.R
import com.pbl6.pbl6_cinestech.data.model.request.ChangePasswordRequest
import com.pbl6.pbl6_cinestech.data.model.request.UpdateProfileRequest
import com.pbl6.pbl6_cinestech.data.repository.RepositoryProvider
import com.pbl6.pbl6_cinestech.databinding.FragmentProfileBinding
import com.pbl6.pbl6_cinestech.ui.main.MainViewModel
import hoang.dqm.codebase.base.activity.BaseFragment
import hoang.dqm.codebase.base.activity.onBackPressed
import hoang.dqm.codebase.base.activity.popBackStack
import hoang.dqm.codebase.utils.loadImageSketch
import hoang.dqm.codebase.utils.singleClick


class ProfileFragment : BaseFragment<FragmentProfileBinding, ProfileViewModel>() {
    override val viewModelFactory: ViewModelProvider.Factory
        get() = ProfileViewModel.ProfileViewModelFactory(
            RepositoryProvider.profileRepository
        )
    private val mainViewModel by activityViewModels <MainViewModel>()

    override fun initView() {
        adjustInsetsForBottomNavigation(binding.btnBack)
        setUpObserver()
    }

    fun setUpObserver(){
        mainViewModel.account.observe(viewLifecycleOwner){ value ->
            if (value == null) return@observe
            binding.edtEmail.setText(value.email)
            binding.edtName.setText(value.fullName)
            binding.edtPhone.setText(value.phoneNumber ?:"")
            value.avatarUrl?.let {
                binding.avatar.loadImageSketch(it)
            }
        }

        viewModel.accountResultLiveData.observe(viewLifecycleOwner){ value ->
            if (value?.success == true){
                if (value.data == null) return@observe
                mainViewModel.setAccount(value.data)
                Toast.makeText(requireContext(), "Update profile success", Toast.LENGTH_SHORT).show()
            }else if (value?.success == false) {
                Toast.makeText(requireContext(), getString(R.string.text_something_went_wrong_please_try_again), Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.updatePasswordResultLiveData.observe(viewLifecycleOwner){value ->
            if (value?.success == true){
                Toast.makeText(requireContext(), "Update Password success", Toast.LENGTH_SHORT).show()
                reloadEditPassword()
            }else if (value?.success == false) {
                Toast.makeText(requireContext(), getString(R.string.text_something_went_wrong_please_try_again), Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun reloadEditPassword(){
        binding.edtCurrentPassword.setText("")
        binding.edtPassword.setText("")
        binding.edtConfirmPassword.setText("")
    }

    override fun initListener() {
        binding.btnSave.singleClick {
            val account = mainViewModel.account.value
            if (account != null) {
                if (account.email != binding.edtEmail.text.toString() || account.fullName != binding.edtName.text.toString() || account.phoneNumber != binding.edtPhone.text.toString()) {
                    viewModel.updateProfile(
                        UpdateProfileRequest(
                            email = binding.edtEmail.text.toString(),
                            fullName = binding.edtName.text.toString(),
                            phoneNumber = binding.edtPhone.text.toString(),
                            avatarUrl = account.avatarUrl
                        )
                    )
                }
            }
        }

        binding.btnUpdatePassword.singleClick {
            if (binding.edtCurrentPassword.text != binding.edtPassword.text || binding.edtCurrentPassword.text.isEmpty() ||binding.edtCurrentPassword.text.isEmpty()) {
                Toast.makeText(requireContext(), "Password Invalid", Toast.LENGTH_SHORT).show()
                return@singleClick
            }
            viewModel.updatePassword(ChangePasswordRequest(binding.edtCurrentPassword.text.toString(), binding.edtPassword.text.toString(), binding.edtConfirmPassword.text.toString()))
        }

        binding.btnBack.singleClick {
            popBackStack()
        }

        onBackPressed {
            popBackStack()
        }
    }

    override fun initData() {
    }
}