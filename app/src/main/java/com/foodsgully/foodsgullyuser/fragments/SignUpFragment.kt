package com.foodsgully.foodsgullyuser.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import carbon.dialog.ProgressDialog
import com.foodsgully.foodsgullyuser.R
import com.foodsgully.foodsgullyuser.database.SharedPreferenceManager
import com.foodsgully.foodsgullyuser.databinding.SignUpFragmentBinding
import com.foodsgully.foodsgullyuser.models.APIError
import com.foodsgully.foodsgullyuser.models.APILoader
import com.foodsgully.foodsgullyuser.models.Success
import com.foodsgully.foodsgullyuser.models.postdatamodels.SetTokenData
import com.foodsgully.foodsgullyuser.models.responsemodels.HomeAddress
import com.foodsgully.foodsgullyuser.models.responsemodels.User
import com.foodsgully.foodsgullyuser.utils.FoodsGullyConstants
import com.foodsgully.foodsgullyuser.utils.FoodsGullyUtils
import com.foodsgully.foodsgullyuser.viewmodels.SignUpViewModel
import com.foodsgully.foodsgullyuser.viewmodels.factories.SignUpViewModelFactory
import com.foodsgully.foodsgullyuser.viewmodels.repositories.SetFCMTokenRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.model.AddressComponent
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import kotlin.math.sign

class SignUpFragment : Fragment(),EasyPermissions.PermissionCallbacks {

    private lateinit var bindingSignUpFragment : SignUpFragmentBinding
    private var signUpViewModel : SignUpViewModel? = null
    private var mProgressDialog : AlertDialog? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesClient : PlacesClient
    private val backgroundScope = CoroutineScope(Dispatchers.IO)

    companion object {
        fun newInstance() = SignUpFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingSignUpFragment = DataBindingUtil.inflate(inflater,
            R.layout.sign_up_fragment, container, false)

         placesClient = FoodsGullyUtils.getPlacesClient(requireActivity())


        bindingSignUpFragment.lifecycleOwner = viewLifecycleOwner
        return bindingSignUpFragment.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        signUpViewModel =  SignUpViewModelFactory().getViewModel(false,requireActivity())

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        initializeViewModelForBinding()

        initializeListeners()
    }

    private fun initializeListeners() {

        initializeFocusChangeListeners()
        initializeClickListeners()


    }

    private fun initializeClickListeners() {
        bindingSignUpFragment.layoutEmailPassword.btnEmailNext.setOnClickListener { validateEmailDetailsAndProceed() }
        bindingSignUpFragment.layoutContactDetails.btnContactNext.setOnClickListener { validateContactDetailsAndProceed() }
        bindingSignUpFragment.layoutHomeAddress.tvCurrentLocation.setOnClickListener { checkForPermissionAndFetchLocation() }
        bindingSignUpFragment.layoutHomeAddress.etAddress.setOnClickListener { findAndFetchAddress() }
        bindingSignUpFragment.layoutHomeAddress.btnSignUp.setOnClickListener { signUpUser() }
        bindingSignUpFragment.layoutHomeAddress.root.setOnClickListener {
            FoodsGullyUtils.hideKeyboard(bindingSignUpFragment.root, requireContext())
            bindingSignUpFragment.layoutHomeAddress.root.requestFocus()
        }

        bindingSignUpFragment.layoutHomeAddress.etPin.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus &&  bindingSignUpFragment.layoutHomeAddress.etPin.text.length == 6) {
                FoodsGullyUtils.hideKeyboard(bindingSignUpFragment.root, requireContext())
                setLocationCoordinatesFromZip( bindingSignUpFragment.layoutHomeAddress.etPin.text.toString())
            }
        }
    }

    private fun setLocationCoordinatesFromZip(zip: String) {

        mProgressDialog = FoodsGullyUtils.showLoaderProgress(
            getString(R.string.fetching_current_address),
            requireContext()
        )
        val address = Geocoder(requireContext()).getFromLocationName(zip, 1)

        if (address.isNotEmpty()) {
            setLatLong(address[0].latitude, address[0].longitude)
            if (!FoodsGullyUtils.checkIfDeliverableLocation(
                    address[0].latitude,
                    address[0].longitude
                )
            ) {
                FoodsGullyUtils.showSnackbar(
                    getString(R.string.not_deliverable_location),
                    bindingSignUpFragment.root
                )

                bindingSignUpFragment.layoutHomeAddress.btnSignUp.isEnabled = false
            }
            else  bindingSignUpFragment.layoutHomeAddress.btnSignUp.isEnabled = true

            setAddress(address[0])
            updateAddressFields()

        }

        FoodsGullyUtils.dismissProgress(mProgressDialog)
    }

    private fun setAddress(address: Address) {

        val addresses = ArrayList<String>()
        if (!address.premises.isNullOrEmpty()) {
            addresses.add(address.premises)
        }

        if (!address.subLocality.isNullOrEmpty()) {
            addresses.add(address.subLocality)
        }

        if (!address.locality.isNullOrEmpty()) {
            addresses.add(address.locality)
        }
        signUpViewModel?.getHomeAddress()?.streetAddress = addresses.joinToString(",")


        signUpViewModel?.getHomeAddress()?.city = address.locality
        signUpViewModel?.getHomeAddress()?.state = address.adminArea
        signUpViewModel?.getHomeAddress()?.zip = address.postalCode
    }

    private fun signUpUser() {
        FoodsGullyUtils.hideKeyboard(bindingSignUpFragment.root,requireContext())
        signUpViewModel?.signUpUser(requireContext())?.observe(viewLifecycleOwner, Observer { response ->
            when(response) {

                is APILoader -> {
                    mProgressDialog =  FoodsGullyUtils.showLoaderProgress(getString(R.string.signing_up),requireContext())
                }

                is APIError ->  {
                    FoodsGullyUtils.dismissProgress(mProgressDialog)
                    FoodsGullyUtils.showSnackbar(response.errorMessage,bindingSignUpFragment.root)
                }

                is Success<*> -> {
                    FoodsGullyUtils.dismissProgress(mProgressDialog)
                    val user = response.data as? User
                    sendFirebaseToken(user?.userId,FoodsGullyUtils.getFirebaseToken(requireContext()))
                    goToSignUpInfoScreen(response.data as? User);
                }
            }
        })
    }

    private fun goToSignUpInfoScreen(user: User?) {
        findNavController().navigate(R.id.nav_signup_info, bundleOf(FoodsGullyConstants.USER_DATA to user))
    }

    private fun findAndFetchAddress() {

        val fields = listOf(Place.Field.ADDRESS_COMPONENTS, Place.Field.LAT_LNG)
        val intent  : Intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN,fields).build(requireActivity())

        startActivityForResult(intent,FoodsGullyConstants.AUTOCOMPLETE_REQUEST_CODE)
    }


    private fun checkForPermissionAndFetchLocation() {

        if(EasyPermissions.hasPermissions(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION)) {
            fetchLocationAndSetAddress()
        }
        else {
            EasyPermissions.requestPermissions(requireActivity(),getString(R.string.location_permission_rationale),FoodsGullyConstants.RC_FINE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    @AfterPermissionGranted(FoodsGullyConstants.RC_FINE_LOCATION)
    private fun fetchLocationAndSetAddress() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ) { return }

        mProgressDialog = FoodsGullyUtils.showLoaderProgress(getString(R.string.current_location),requireContext())
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->

            if (signUpViewModel?.getHomeAddress() == null) {
                signUpViewModel?.setHomeAddress(address = HomeAddress())
            }
            signUpViewModel?.getHomeAddress()?.latitude = location.latitude
            signUpViewModel?.getHomeAddress()?.longitude = location.longitude

            if(location != null) {
                if (!FoodsGullyUtils.checkIfDeliverableLocation(
                        location.latitude,
                        location.longitude
                    )
                ) {

                    FoodsGullyUtils.showSnackbar(
                        getString(R.string.not_deliverable_location),
                        bindingSignUpFragment.root
                    )

                    bindingSignUpFragment.layoutHomeAddress.btnSignUp.isEnabled = false


                } else  bindingSignUpFragment.layoutHomeAddress.btnSignUp.isEnabled = true

                fetchAddress(location.latitude, location.longitude)
            } else {
                FoodsGullyUtils.dismissProgress(mProgressDialog)
            }

        }.addOnFailureListener { exception ->
           exception.printStackTrace()
            FoodsGullyUtils.dismissProgress(mProgressDialog)
        }

    }

    private fun fetchAddress(latitude: Double, longitude: Double) {
        val address = Geocoder(requireContext()).getFromLocation(latitude,longitude,1)
        if (address.isNotEmpty()) {
            signUpViewModel?.getHomeAddress()?.streetAddress =
                address[0].premises + ", " + address[0].subLocality

            signUpViewModel?.getHomeAddress()?.city = address[0].locality
            signUpViewModel?.getHomeAddress()?.state = address[0].adminArea
            signUpViewModel?.getHomeAddress()?.zip = address[0].postalCode
            updateAddressFields()

        }

        FoodsGullyUtils.dismissProgress(mProgressDialog)


    }

    private fun updateAddressFields() {
        bindingSignUpFragment.layoutHomeAddress.etAddress.setText(
            signUpViewModel?.getHomeAddress()?.streetAddress
                ?: "" + ", " +  signUpViewModel?.getHomeAddress()?.city
        )
        bindingSignUpFragment.layoutHomeAddress.etPin.setText( signUpViewModel?.getHomeAddress()?.zip ?: "")

    }


    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        fetchLocationAndSetAddress()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (resultCode) {
            Activity.RESULT_OK -> {
                data?.let {
                    val place = Autocomplete.getPlaceFromIntent(data)
                    val locationCoordinates = place.latLng
                    setLatLong(locationCoordinates?.latitude ?: -91.0, locationCoordinates?.longitude ?: -181.0)

                    if (!FoodsGullyUtils.checkIfDeliverableLocation(
                            locationCoordinates?.latitude ?: -91.0,
                            locationCoordinates?.longitude ?: -181.0
                        )
                    ) {

                        FoodsGullyUtils.showSnackbar(
                            getString(R.string.not_deliverable_location),
                            bindingSignUpFragment.root
                        )

                        bindingSignUpFragment.layoutHomeAddress.btnSignUp.isEnabled = false


                    } else  bindingSignUpFragment.layoutHomeAddress.btnSignUp.isEnabled = true

                    val addressComponents = place.addressComponents?.asList()
                    val streetAddresses = ArrayList<String>()
                    if(addressComponents?.isNotEmpty() == true) {
                        for(addressComponent in addressComponents) {
                            setAddressFromAddressComponent(addressComponent,streetAddresses)
                        }
                    }

                    signUpViewModel?.getHomeAddress()?.streetAddress = streetAddresses.joinToString(",")

                    updateAddressFields()

                }
            }
            AutocompleteActivity.RESULT_ERROR -> {

                data?.let {
                    val status = Autocomplete.getStatusFromIntent(data)
                    Log.i("TAG", status.statusMessage)
                }
            }
            Activity.RESULT_CANCELED -> {

            }
        }
        return

    }

    private fun setAddressFromAddressComponent(addressComponent: AddressComponent?,streetAddresses : ArrayList<String>) {

        if(addressComponent?.types?.isNotEmpty() == true) {
            when(addressComponent.types[0].toLowerCase()) {
                "floor" -> signUpViewModel?.getHomeAddress()?.floorNumber = addressComponent.name
                "street_number" -> streetAddresses.add(addressComponent.name)
                "route" -> streetAddresses.add(addressComponent.name)
                "sublocality_level_1" -> streetAddresses.add((addressComponent.name))
                "sublocality_level_2" -> streetAddresses.add(addressComponent.name)
                "locality" -> streetAddresses.add(addressComponent.name)
                "administrative_area_level_2" -> signUpViewModel?.getHomeAddress()?.city =  addressComponent.shortName ?: ""
                "administrative_area_level_1" -> signUpViewModel?.getHomeAddress()?.state = addressComponent.shortName ?: ""
                "postal_code" -> signUpViewModel?.getHomeAddress()?.zip = addressComponent.name

            }


        }

    }


    private fun setLatLong(latitude: Double, longitude: Double) {
        if (signUpViewModel?.getHomeAddress() == null) {
            signUpViewModel?.setHomeAddress(HomeAddress())
        }
        signUpViewModel?.getHomeAddress()?.latitude = latitude
        signUpViewModel?.getHomeAddress()?.longitude = longitude


    }



    private fun validateContactDetailsAndProceed() {
        FoodsGullyUtils.hideKeyboard(bindingSignUpFragment.root,requireContext())
        if(signUpViewModel?.validateContactDetails(requireContext()) != true) return
        bindingSignUpFragment.layoutContactDetails.parentContactLayout.visibility = View.GONE
        bindingSignUpFragment.layoutHomeAddress.parentAddressLayout.visibility = View.VISIBLE

    }

    private fun validateEmailDetailsAndProceed() {
        FoodsGullyUtils.hideKeyboard(bindingSignUpFragment.root,requireContext())
        if(signUpViewModel?.validateEmailPassword(requireContext()) != true) return
        bindingSignUpFragment.layoutEmailPassword.parentEmailLayout.visibility = View.GONE
        bindingSignUpFragment.layoutContactDetails.parentContactLayout.visibility = View.VISIBLE
    }




    private fun initializeFocusChangeListeners() {
        bindingSignUpFragment.layoutEmailPassword.etEmail.setOnFocusChangeListener { v, hasFocus -> if(!hasFocus) signUpViewModel?.validateEmail(requireContext()) }

        bindingSignUpFragment.layoutEmailPassword.etPassword.setOnFocusChangeListener { v, hasFocus -> if(!hasFocus) signUpViewModel?.validatePassword(requireContext()) }

        bindingSignUpFragment.layoutContactDetails.etFullName.setOnFocusChangeListener { v, hasFocus -> if(!hasFocus) signUpViewModel?.validateFullName(requireContext()) }

        bindingSignUpFragment.layoutContactDetails.etPhoneNumber.setOnFocusChangeListener { v, hasFocus -> if(!hasFocus) signUpViewModel?.validatePhoneNumber(requireContext()) }
    }

    private fun initializeViewModelForBinding() {
        bindingSignUpFragment.layoutEmailPassword.signUpVM = signUpViewModel
        bindingSignUpFragment.layoutContactDetails.signUpVM = signUpViewModel
        bindingSignUpFragment.layoutHomeAddress.signUpVM = signUpViewModel

    }

    private fun sendFirebaseToken(userId: String?, firebaseToken: String?) {

        backgroundScope.launch {
            SetFCMTokenRepository<String>(
                SetTokenData(userId, firebaseToken),
                requireContext()
            ).getResponse().observe(viewLifecycleOwner,
                Observer { response ->
                    when (response) {
                        is Success<*> -> SharedPreferenceManager(
                            requireContext(),
                            FoodsGullyConstants.FCM_SP
                        ).storeBooleanPreference(
                            FoodsGullyConstants.KEY_FIREBASE_TOKEN_REGISTERED,
                            true
                        )
                        is APIError -> SharedPreferenceManager(
                            requireContext(),
                            FoodsGullyConstants.FCM_SP
                        ).storeBooleanPreference(
                            FoodsGullyConstants.KEY_FIREBASE_TOKEN_REGISTERED,
                            false
                        )

                    }

                })

        }

    }

}