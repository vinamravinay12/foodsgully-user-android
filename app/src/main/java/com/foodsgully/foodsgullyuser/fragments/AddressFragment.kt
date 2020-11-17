package com.foodsgully.foodsgullyuser.fragments

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.foodsgully.foodsgullyuser.BuildConfig
import com.foodsgully.foodsgullyuser.R
import com.foodsgully.foodsgullyuser.activities.MainActivity
import com.foodsgully.foodsgullyuser.databinding.AddressFragmentBinding
import com.foodsgully.foodsgullyuser.models.APIError
import com.foodsgully.foodsgullyuser.models.APILoader
import com.foodsgully.foodsgullyuser.models.Success
import com.foodsgully.foodsgullyuser.models.responsemodels.HomeAddress
import com.foodsgully.foodsgullyuser.models.responsemodels.User
import com.foodsgully.foodsgullyuser.utils.FoodsGullyConstants
import com.foodsgully.foodsgullyuser.utils.FoodsGullyUtils
import com.foodsgully.foodsgullyuser.viewmodels.AddressViewModel
import com.foodsgully.foodsgullyuser.viewmodels.factories.UpdateAddressViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AddressComponent
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.lang.Exception

class AddressFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private val TAG = AddressFragment::class.qualifiedName
    private lateinit var bindingAddressFragment: AddressFragmentBinding
    private var viewModel: AddressViewModel? = null
    private var isEditMode: Boolean = false;
    private var mCurrentUser: User? = null
    private var mProgressDialog: AlertDialog? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient
    private var isDeliverableLocation: Boolean = false


    private var isLocationFetchedFromZip = false

    companion object {
        fun newInstance() = AddressFragment()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            mCurrentUser = getParcelable(FoodsGullyConstants.USER_DATA) as? User
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingAddressFragment =
            DataBindingUtil.inflate(inflater, R.layout.address_fragment, container, false)
        bindingAddressFragment.lifecycleOwner = viewLifecycleOwner

        placesClient = FoodsGullyUtils.getPlacesClient(requireActivity())
        return bindingAddressFragment.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = UpdateAddressViewModelFactory(mCurrentUser?.homeAddress).getViewModel(
            true,
            requireActivity()
        )

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        bindingAddressFragment.isEditMode = isEditMode
        bindingAddressFragment.homeAddress = viewModel?.getHomeAddress()

        initializeListeners()

    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is MainActivity) {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.showOrHideCartLayout(false)

        }
    }

    private fun initializeListeners() {

        bindingAddressFragment.parentAddressLayout.setOnClickListener {
            FoodsGullyUtils.hideKeyboard(bindingAddressFragment.root, requireContext())
            bindingAddressFragment.parentAddressLayout.requestFocus()
        }
        bindingAddressFragment.ivBack.setOnClickListener { navigateToHomeScreen() }

        bindingAddressFragment.etPin.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && bindingAddressFragment.etPin.text.length == 6) {
                FoodsGullyUtils.hideKeyboard(bindingAddressFragment.root, requireContext())
                setLocationCoordinatesFromZip(bindingAddressFragment.etPin.text.toString())
            }
        }
        bindingAddressFragment.btnEditAddress.setOnClickListener {
            bindingAddressFragment.isEditMode = true
        }
        bindingAddressFragment.tvCurrentLocation.setOnClickListener { checkForPermissionAndFetchLocation() }
        bindingAddressFragment.btnSubmit.setOnClickListener { updateAddress() }
        bindingAddressFragment.etAddress.setOnClickListener { searchForAddress() }
    }

    private fun searchForAddress() {
        val fields =
            listOf(Place.Field.ADDRESS_COMPONENTS, Place.Field.LAT_LNG, Place.Field.ADDRESS)
        val intent: Intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(requireActivity())

        startActivityForResult(intent, FoodsGullyConstants.AUTOCOMPLETE_REQUEST_CODE)

    }

    private fun setLocationCoordinatesFromZip(zip: String) {

        mProgressDialog = FoodsGullyUtils.showLoaderProgress(
            getString(R.string.fetching_current_address),
            requireContext()
        )
        try {
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
                        bindingAddressFragment.root
                    )

                    bindingAddressFragment.btnSubmit.isEnabled = false
                } else bindingAddressFragment.btnSubmit.isEnabled = true

                setAddress(address[0])
                updateAddressFields()
                isLocationFetchedFromZip = true
                FoodsGullyUtils.dismissProgress(mProgressDialog)
            }

        }catch (exception : Exception) {
            Log.e(TAG,"unable to fetch location from zip " + exception.localizedMessage)

        }

    }


    private fun updateAddress() {
        viewModel?.updateAddress(requireContext())
            ?.observe(viewLifecycleOwner, Observer { response ->
                when (response) {

                    is APILoader -> {
                        mProgressDialog = FoodsGullyUtils.showLoaderProgress(
                            getString(R.string.updating_address),
                            requireContext()
                        )
                    }

                    is APIError -> {
                        FoodsGullyUtils.dismissProgress(mProgressDialog)
                        FoodsGullyUtils.showSnackbar(
                            response.errorMessage,
                            bindingAddressFragment.root
                        )
                    }

                    is Success<*> -> {
                        FoodsGullyUtils.dismissProgress(mProgressDialog)
                        FoodsGullyUtils.showSnackbar(
                            getString(R.string.address_updated),
                            bindingAddressFragment.root
                        )
                        FoodsGullyUtils.updateHomeAddress(
                            viewModel?.getHomeAddress(),
                            requireContext()
                        )
                        navigateToHomeScreen()

                    }
                }
            })
    }

    private fun checkForPermissionAndFetchLocation() {

        if (EasyPermissions.hasPermissions(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            fetchLocationAndSetAddress()
        } else {
            EasyPermissions.requestPermissions(
                requireActivity(),
                getString(R.string.location_permission_rationale),
                FoodsGullyConstants.RC_FINE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    @AfterPermissionGranted(FoodsGullyConstants.RC_FINE_LOCATION)
    private fun fetchLocationAndSetAddress() {
        FoodsGullyUtils.hideKeyboard(bindingAddressFragment.root,requireContext())
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        mProgressDialog = FoodsGullyUtils.showLoaderProgress(
            getString(R.string.fetching_current_address),
            requireContext()
        )
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                setLatLong(location.latitude, location.longitude)

                fetchAddress(location.latitude, location.longitude)
            } else {
                FoodsGullyUtils.dismissProgress(mProgressDialog)
            }

        }
    }

    private fun setLatLong(latitude: Double, longitude: Double) {
        if (viewModel?.getHomeAddress() == null) {
            viewModel?.setHomeAddress(HomeAddress())
        }
        viewModel?.getHomeAddress()?.latitude = latitude
        viewModel?.getHomeAddress()?.longitude = longitude

    }

    private fun fetchAddress(latitude: Double, longitude: Double) {
        val address = Geocoder(requireContext()).getFromLocation(latitude, longitude, 1)

        if (address.isNotEmpty()) {
            if (!FoodsGullyUtils.checkIfDeliverableLocation(
                    latitude,
                    longitude
                )
            ) {
                FoodsGullyUtils.showSnackbar(
                    getString(R.string.not_deliverable_location),
                    bindingAddressFragment.root
                )

                bindingAddressFragment.btnSubmit.isEnabled = false
            } else bindingAddressFragment.btnSubmit.isEnabled = true

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
        viewModel?.getHomeAddress()?.streetAddress = addresses.joinToString(",")


        viewModel?.getHomeAddress()?.city = address.locality
        viewModel?.getHomeAddress()?.state = address.adminArea
        viewModel?.getHomeAddress()?.zip = address.postalCode
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
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        FoodsGullyUtils.hideKeyboard(bindingAddressFragment.root,requireContext())
        when (resultCode) {
            Activity.RESULT_OK -> {
                data?.let {
                    val place = Autocomplete.getPlaceFromIntent(data)
                    val locationCoordinates = place.latLng
                    setLatLong(
                        locationCoordinates?.latitude ?: -91.0,
                        locationCoordinates?.longitude ?: -181.0
                    )
                    if (!FoodsGullyUtils.checkIfDeliverableLocation(
                            locationCoordinates?.latitude ?: -91.0,
                            locationCoordinates?.longitude ?: -181.0
                        )
                    ) {

                        FoodsGullyUtils.showSnackbar(
                            getString(R.string.not_deliverable_location),
                            bindingAddressFragment.root
                        )

                        bindingAddressFragment.btnSubmit.isEnabled = false


                    } else bindingAddressFragment.btnSubmit.isEnabled = true
                    val addressComponents = place.addressComponents?.asList()
                    val streetAddresses = ArrayList<String>()
                    if (addressComponents?.isNotEmpty() == true) {
                        for (addressComponent in addressComponents) {
                            setAddressFromAddressComponent(addressComponent, streetAddresses)

                        }

                        viewModel?.getHomeAddress()?.streetAddress =
                            streetAddresses.joinToString(",")
                        updateAddressFields()
                    }

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

    private fun setAddressFromAddressComponent(
        addressComponent: AddressComponent?,
        streetAddresses: ArrayList<String>
    ) {

        if (addressComponent?.types?.isNotEmpty() == true) {
            when (addressComponent.types[0].toLowerCase()) {
                "floor" -> viewModel?.getHomeAddress()?.floorNumber = addressComponent.name
                "street_number" -> streetAddresses.add(addressComponent.name)
                "route" -> streetAddresses.add(addressComponent.name)
                "neighborhood" -> streetAddresses.add(addressComponent.name)
                "sublocality_level_1" -> streetAddresses.add((addressComponent.name))
                "sublocality_level_2" -> streetAddresses.add(addressComponent.name)
                "locality" -> streetAddresses.add(addressComponent.name)
                "administrative_area_level_2" -> viewModel?.getHomeAddress()?.city =
                    addressComponent.shortName ?: ""
                "administrative_area_level_1" -> viewModel?.getHomeAddress()?.state =
                    addressComponent.shortName ?: ""
                "postal_code" -> viewModel?.getHomeAddress()?.zip = addressComponent.name

            }


        }

    }


    private fun navigateToHomeScreen() {
        findNavController().popBackStack(R.id.nav_home, false)
    }

    private fun updateAddressFields() {

        bindingAddressFragment.etAddress.setText(
            viewModel?.getHomeAddress()?.streetAddress
                ?: "" + ", " + viewModel?.getHomeAddress()?.city
        )
        bindingAddressFragment.tvCityName.text = viewModel?.getHomeAddress()?.getCityName()
        bindingAddressFragment.etPin.setText(viewModel?.getHomeAddress()?.zip ?: "")

    }

}