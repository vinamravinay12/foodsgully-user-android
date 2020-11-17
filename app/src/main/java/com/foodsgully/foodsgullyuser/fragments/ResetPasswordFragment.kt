package com.foodsgully.foodsgullyuser.fragments

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.foodsgully.foodsgullyuser.R
import com.foodsgully.foodsgullyuser.databinding.ResetPasswordFragmentBinding
import com.foodsgully.foodsgullyuser.models.APIError
import com.foodsgully.foodsgullyuser.models.APILoader
import com.foodsgully.foodsgullyuser.models.Success
import com.foodsgully.foodsgullyuser.models.responsemodels.User
import com.foodsgully.foodsgullyuser.utils.FoodsGullyUtils
import com.foodsgully.foodsgullyuser.viewmodels.ResetPasswordViewModel
import com.foodsgully.foodsgullyuser.viewmodels.factories.ResetPasswordVMFactory

class ResetPasswordFragment : Fragment() {

    private lateinit var bindingResetPasswordFragment : ResetPasswordFragmentBinding
    private var resetPasswordViewModel : ResetPasswordViewModel? = null
    private var mProgressDialog : AlertDialog? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingResetPasswordFragment = DataBindingUtil.inflate(inflater,R.layout.reset_password_fragment, container, false)
        bindingResetPasswordFragment.lifecycleOwner = viewLifecycleOwner
        return bindingResetPasswordFragment.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        resetPasswordViewModel = ResetPasswordVMFactory().getViewModel(false,requireActivity())
        bindingResetPasswordFragment.resetVM = resetPasswordViewModel

        initializeListeners()
    }

    private fun initializeListeners() {
        initializeFocusChangeListeners()
        initializeClickListeners()
    }

    private fun initializeFocusChangeListeners() {
        bindingResetPasswordFragment.etEmail.setOnFocusChangeListener { v, hasFocus -> if(!hasFocus) {
            resetPasswordViewModel?.validateEmail(requireContext())
        } }

        bindingResetPasswordFragment.inputPassword.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus){
                bindingResetPasswordFragment.etPassword.hint = if(bindingResetPasswordFragment.inputPassword.text.isNullOrEmpty()) getString(R.string.enter_password) else ""
                resetPasswordViewModel?.validatePassword(requireContext())
            } else {
                bindingResetPasswordFragment.etPassword.hint = ""
            }
        }
    }

    private fun initializeClickListeners() {
        bindingResetPasswordFragment.btnSubmit.setOnClickListener { resetPassword() }


    }

    private fun resetPassword() {
        resetPasswordViewModel?.resetPassword(requireContext())?.observe(viewLifecycleOwner, Observer { response ->
            when(response) {

                is APILoader -> {
                    mProgressDialog =  FoodsGullyUtils.showLoaderProgress(getString(R.string.resetting_password),requireContext())
                }

                is APIError ->  {
                    FoodsGullyUtils.dismissProgress(mProgressDialog)
                    FoodsGullyUtils.showSnackbar(response.errorMessage,bindingResetPasswordFragment.root)
                }

                is Success<*> -> {
                    FoodsGullyUtils.dismissProgress(mProgressDialog)
                    FoodsGullyUtils.showSnackbar(getString(R.string.password_update_success),bindingResetPasswordFragment.root)
                    goToLoginScreen()
                }
            }
        })
    }

    private fun goToLoginScreen() {
        findNavController().popBackStack(R.id.nav_login,false)
    }

}