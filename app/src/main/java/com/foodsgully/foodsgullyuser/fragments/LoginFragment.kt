package com.foodsgully.foodsgullyuser.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import carbon.dialog.ProgressDialog
import com.foodsgully.foodsgullyuser.R
import com.foodsgully.foodsgullyuser.activities.MainActivity
import com.foodsgully.foodsgullyuser.database.SharedPreferenceManager
import com.foodsgully.foodsgullyuser.databinding.LoginFragmentBinding
import com.foodsgully.foodsgullyuser.models.APIError
import com.foodsgully.foodsgullyuser.models.APILoader
import com.foodsgully.foodsgullyuser.models.Success
import com.foodsgully.foodsgullyuser.models.postdatamodels.SetTokenData
import com.foodsgully.foodsgullyuser.models.responsemodels.User
import com.foodsgully.foodsgullyuser.utils.FoodsGullyConstants
import com.foodsgully.foodsgullyuser.utils.FoodsGullyUtils
import com.foodsgully.foodsgullyuser.viewmodels.LoginViewModel
import com.foodsgully.foodsgullyuser.viewmodels.repositories.SetFCMTokenRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Error


class LoginFragment : Fragment() {

    private lateinit var bindingLoginFragment: LoginFragmentBinding
    private var loginViewModel: LoginViewModel? = null
    private var mProgressDialog: AlertDialog? = null
    private val backgroundScope = CoroutineScope(Dispatchers.IO)

    companion object {
        fun newInstance() = LoginFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingLoginFragment = DataBindingUtil.inflate(
            inflater,
            R.layout.login_fragment, container, false
        )
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
        bindingLoginFragment.etEmail.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) loginViewModel?.validateEmail(
                requireContext()
            )
        }

        bindingLoginFragment.etPassword.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) loginViewModel?.validatePassword(
                requireContext()
            )
        }
    }

    private fun initializeClickListeners() {
        bindingLoginFragment.btnLogin.setOnClickListener { handleUserLogin() }
        bindingLoginFragment.tvSignup.setOnClickListener { launchSignUpScreen() }
        bindingLoginFragment.tvForgotPassword.setOnClickListener { launchForgotPasswordScreen() }

    }

    private fun launchForgotPasswordScreen() {
        findNavController().navigate(R.id.nav_forgot_password)
    }

    private fun launchSignUpScreen() {
        findNavController().navigate(R.id.nav_signup)
    }

    private fun handleUserLogin() {

        loginViewModel?.loginUser(requireContext())
            ?.observe(viewLifecycleOwner, Observer { response ->
                when (response) {

                    is APILoader -> {
                        mProgressDialog = FoodsGullyUtils.showLoaderProgress(
                            getString(R.string.login_in),
                            requireContext()
                        )
                    }

                    is APIError -> {
                        FoodsGullyUtils.dismissProgress(mProgressDialog)
                        FoodsGullyUtils.showSnackbar(
                            response.errorMessage,
                            bindingLoginFragment.root
                        )
                    }

                    is Success<*> -> {

                        val user = response.data as? User
                        sendFirebaseToken(
                            context = requireContext().applicationContext,
                            user = user,
                            firebaseToken = FoodsGullyUtils.getFirebaseToken(requireContext())
                        )

                    }
                }
            })
    }

    private fun goToHomeScreen(user: User?) {
        val intent = Intent(requireActivity(), MainActivity::class.java)
        intent.putExtra(FoodsGullyConstants.USER_DATA, user)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun sendFirebaseToken(context: Context, user: User?, firebaseToken: String?) {

        SetFCMTokenRepository<String>(
            SetTokenData(user?.userId, firebaseToken),
            context
        ).getResponse().observe(viewLifecycleOwner,
            Observer { response ->
                when (response) {
                    is Success<*> -> {
                        SharedPreferenceManager(
                            requireContext(),
                            FoodsGullyConstants.FCM_SP
                        ).storeBooleanPreference(
                            FoodsGullyConstants.KEY_FIREBASE_TOKEN_REGISTERED,
                            true
                        )

                        FoodsGullyUtils.dismissProgress(mProgressDialog)
                        goToHomeScreen(user)
                    }
                    is APIError -> {
                        SharedPreferenceManager(
                            requireContext(),
                            FoodsGullyConstants.FCM_SP
                        ).storeBooleanPreference(
                            FoodsGullyConstants.KEY_FIREBASE_TOKEN_REGISTERED,
                            false
                        )
                        FoodsGullyUtils.dismissProgress(mProgressDialog)
                        goToHomeScreen(user)
                    }


                }

            })


    }


}