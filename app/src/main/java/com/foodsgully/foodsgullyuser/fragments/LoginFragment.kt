package com.foodsgully.foodsgullyuser.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.foodsgully.foodsgullyuser.viewmodels.LoginViewModel
import com.niro.foodsgullyuser.R
import com.niro.foodsgullyuser.databinding.LoginFragmentBinding

class LoginFragment : Fragment() {

    private lateinit var bindingLoginFragment : LoginFragmentBinding
    private var loginViewModel : LoginViewModel? = null

    companion object {
        fun newInstance() = LoginFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingLoginFragment = DataBindingUtil.inflate(inflater,R.layout.login_fragment,container,false)
        bindingLoginFragment.lifecycleOwner = viewLifecycleOwner
        return bindingLoginFragment.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loginViewModel = ViewModelProvider(requireActivity()).get(LoginViewModel::class.java)

        bindingLoginFragment.loginVM = loginViewModel

        initializeListeners()
    }

    private fun initializeListeners() {
        initializeFocusChangeListeners()
        initializeClickListeners()
    }

    private fun initializeFocusChangeListeners() {
        bindingLoginFragment.etEmail.setOnFocusChangeListener { v, hasFocus -> if(!hasFocus) loginViewModel?.validateEmail(requireContext()) }

        bindingLoginFragment.etPassword.setOnFocusChangeListener { v, hasFocus -> if(!hasFocus) loginViewModel?.validatePassword(requireContext()) }
    }

    private fun initializeClickListeners() {
        bindingLoginFragment.btnLogin.setOnClickListener { handleUserLogin() }
        bindingLoginFragment.tvSignup.setOnClickListener{ launchSignupScreen() }
        bindingLoginFragment.tvForgotPassword.setOnClickListener { launchForgotPasswordScreen() }

    }

    private fun launchForgotPasswordScreen() {

    }

    private fun launchSignupScreen() {

    }

    private fun handleUserLogin() {

    }

}