package com.foodsgully.foodsgullyuser.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts

import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.foodsgully.foodsgullyuser.R
import com.foodsgully.foodsgullyuser.activities.MainActivity
import com.foodsgully.foodsgullyuser.databinding.ProfileFragmentBinding
import com.foodsgully.foodsgullyuser.firebase.FileDownloadResponseHandler
import com.foodsgully.foodsgullyuser.firebase.FileType
import com.foodsgully.foodsgullyuser.firebase.FileUploadResponseHandler
import com.foodsgully.foodsgullyuser.firebase.FirebaseFileUploadService
import com.foodsgully.foodsgullyuser.models.APIError
import com.foodsgully.foodsgullyuser.models.APILoader
import com.foodsgully.foodsgullyuser.models.Success
import com.foodsgully.foodsgullyuser.models.responsemodels.User
import com.foodsgully.foodsgullyuser.utils.FoodsGullyConstants
import com.foodsgully.foodsgullyuser.utils.FoodsGullyUtils
import com.foodsgully.foodsgullyuser.viewmodels.ProfileViewModel
import com.foodsgully.foodsgullyuser.viewmodels.factories.ProfileViewModelFactory
import com.livefront.bridge.Bridge
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


class ProfileFragment : Fragment(), FileDownloadResponseHandler, FileUploadResponseHandler,
    EasyPermissions.PermissionCallbacks,ImagePickerResultListener {

    private lateinit var observer : ImagePickerLifecycleObserver
    private lateinit var bindingProfileFragment: ProfileFragmentBinding

    private var profileViewModel: ProfileViewModel? = null

    private var mProgressDialog: AlertDialog? = null

    private var mCurrentUser: User? = null
    private var isEditMode: Boolean = false



    private lateinit var fileUploadService: FirebaseFileUploadService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Bridge.restoreInstanceState(this, savedInstanceState)
        arguments?.apply {
            mCurrentUser = getParcelable(FoodsGullyConstants.USER_DATA) as? User
        }

        observer = ImagePickerLifecycleObserver(requireActivity().activityResultRegistry,this)
        lifecycle.addObserver(observer)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingProfileFragment =
            DataBindingUtil.inflate(inflater, R.layout.profile_fragment, container, false)
        bindingProfileFragment.lifecycleOwner = viewLifecycleOwner

        requireActivity().viewModelStore.clear()

        return bindingProfileFragment.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        savedInstanceState?.clear()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        savedInstanceState?.clear()

        profileViewModel = ProfileViewModelFactory().getViewModel(false, requireActivity())

        profileViewModel?.setUserData(mCurrentUser)

        fileUploadService = FirebaseFileUploadService(requireContext())

        bindingProfileFragment.profileVM = profileViewModel
        bindingProfileFragment.isEditMode = isEditMode


        initializeListeners()
        downloadAndSetImage()
    }

    private fun downloadAndSetImage() {

        fileUploadService.downloadFile(
            mCurrentUser?.imageUrl ?: "",
            3,
            this,
            FileType.USER_IMAGE
        )

    }


    override fun onResume() {
        super.onResume()
        if (requireActivity() is MainActivity) {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.showOrHideCartLayout(false)

        }
    }

    private fun initializeListeners() {

        bindingProfileFragment.ivBack.setOnClickListener {
            findNavController().popBackStack(
                R.id.nav_home,
                false
            )
        }

        bindingProfileFragment.btnEditProfile.setOnClickListener {
            bindingProfileFragment.isEditMode = true
        }

        bindingProfileFragment.btnClickPhoto.setOnClickListener { checkPermissionAndOpenPhotoPicker() }

        bindingProfileFragment.btnSubmit.setOnClickListener { updateProfile() }

        bindingProfileFragment.etFullName.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) profileViewModel?.validateFullName(
                requireContext()
            )
        }

        bindingProfileFragment.etPhone.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) profileViewModel?.validatePhoneNumber(
                requireContext()
            )
        }
    }

    private fun updateProfile() {

        FoodsGullyUtils.hideKeyboard(bindingProfileFragment.root, requireContext())
        if (profileViewModel?.getImageName()?.value.isNullOrEmpty()) updateProfileToServer()
        else {
            mProgressDialog = FoodsGullyUtils.showLoaderProgress(
                getString(R.string.uploading_image),
                requireContext()
            )
            fileUploadService.uploadFile(
                profileViewModel?.getImagePath()?.value,
                profileViewModel?.getImageName()?.value ?: "profile_image",
                mCurrentUser?.userId ?: "",
                1,
                this,
                FileType.USER_IMAGE
            )
        }

    }

    private fun updateProfileToServer() {

        profileViewModel?.updateProfileDetails(requireContext())
            ?.observe(viewLifecycleOwner, Observer { response ->
                when (response) {

                    is APILoader -> {
                        mProgressDialog = FoodsGullyUtils.showLoaderProgress(
                            getString(R.string.updatiing_profile),
                            requireContext()
                        )
                    }

                    is APIError -> {
                        FoodsGullyUtils.dismissProgress(mProgressDialog)
                        FoodsGullyUtils.showSnackbar(
                            response.errorMessage,
                            bindingProfileFragment.root
                        )
                    }

                    is Success<*> -> {
                        FoodsGullyUtils.dismissProgress(mProgressDialog)
                        val user = profileViewModel?.getUser()
                        user?.image = mCurrentUser?.image
                        FoodsGullyUtils.storeUpdatedUser(user, requireContext())
                        goToHomeScreen()
                    }
                }
            })

    }

    private fun goToHomeScreen() {
        findNavController().popBackStack(R.id.nav_home, false)
    }

    @AfterPermissionGranted(FoodsGullyConstants.RC_READ_WRITE_STORAGE)
    private fun openImagePicker() {

        observer.selectImage()



    }



    private fun getFileName(resolver: ContentResolver, uri: Uri): String? {
        val returnCursor: Cursor = resolver.query(uri, null, null, null, null)!!
        val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name: String = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }

    private fun checkPermissionAndOpenPhotoPicker() {

        if (EasyPermissions.hasPermissions(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            openImagePicker()
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.need_storage_permission),
                FoodsGullyConstants.RC_READ_WRITE_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )

        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    override fun onFileUploaded(imageUrl: String, fileType: FileType) {
        profileViewModel?.getImageUrl()?.value = imageUrl
        FoodsGullyUtils.dismissProgress(mProgressDialog)
        updateProfileToServer()
    }

    override fun onFileUploadFailed(fileType: FileType) {
        FoodsGullyUtils.dismissProgress(mProgressDialog)
        updateProfileToServer()
    }

    override fun onFileDownloaded(fileUri: Uri, fileType: FileType) {
        FoodsGullyUtils.setUserImageToView(
            bindingProfileFragment.ivProfileImage,
            fileUri,
            requireContext(),
            R.drawable.ic_person_gray,
            0.2f,
            mCurrentUser
        )
    }

    override fun onFileDownloadFailed(fileType: FileType) {
        FoodsGullyUtils.setImageToView(
            bindingProfileFragment.ivProfileImage,
            Uri.EMPTY, requireContext(), R.drawable.ic_person_gray, 0.2f
        )
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        openImagePicker()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Bridge.saveInstanceState(this, outState);
    }

    override fun onDestroy() {
        super.onDestroy()
        Bridge.clear(this)
    }

    override fun onImagePicked(uri: Uri?) {

        if (uri != null) {
            profileViewModel?.getImageName()?.value =
                getFileName(requireActivity().contentResolver, uri)
            profileViewModel?.getImagePath()?.value = uri
            FoodsGullyUtils.setUserImageToView(
                bindingProfileFragment.ivProfileImage,
                uri,
                requireContext(),
                R.drawable.ic_person_gray,
                0.3f,
                mCurrentUser
            )
        }
    }
}


class ImagePickerLifecycleObserver(private val registry : ActivityResultRegistry, private val imagePickerResultListener : ImagePickerResultListener) : DefaultLifecycleObserver  {

    lateinit var getContent : ActivityResultLauncher<String>

    override fun onCreate(owner: LifecycleOwner) {
        val activityResultContract : ActivityResultContract<String, Uri> = ActivityResultContracts.GetContent()

        getContent = registry.register("Key",owner, activityResultContract,
            ActivityResultCallback<Uri> { uri ->
                imagePickerResultListener.onImagePicked(uri)
             })

        fun selectImage() {
            getContent.launch("image/*")
        }

    }

    fun selectImage() {
        getContent.launch("image/*")
    }
}


interface ImagePickerResultListener {
    fun onImagePicked(uri: Uri?)
}