package com.foodsgully.foodsgullyuser.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import com.foodsgully.foodsgullyuser.R
import com.foodsgully.foodsgullyuser.activities.MainActivity
import com.foodsgully.foodsgullyuser.databinding.FragmentSignupInfoBinding
import com.foodsgully.foodsgullyuser.models.responsemodels.User
import com.foodsgully.foodsgullyuser.utils.FoodsGullyConstants
import com.foodsgully.foodsgullyuser.utils.FoodsGullyUtils

import com.synnapps.carouselview.ImageListener
import kotlinx.android.synthetic.main.layout_no_items.view.*


class SignupInfoFragment : Fragment(), ImageListener {

    private var user: User? = null
    private lateinit var bindingSignupInfoFragment: FragmentSignupInfoBinding

    private var carouselImages = arrayOf(R.drawable.signup_info_1,R.drawable.signup_info_2,R.drawable.signup_info_3)
    private var currentPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user = it.getParcelable<User>(FoodsGullyConstants.USER_DATA) as? User
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bindingSignupInfoFragment =
            DataBindingUtil.inflate(inflater, R.layout.fragment_signup_info, container, false)
        bindingSignupInfoFragment.lifecycleOwner = viewLifecycleOwner


        return bindingSignupInfoFragment.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FoodsGullyUtils.initializeAndSetCarouselImages(
            bindingSignupInfoFragment.imgCarousel,
            carouselImages, this
        )


        initializeCLickListeners()
    }

    private fun initializeCLickListeners() {
        bindingSignupInfoFragment.btnSkip.setOnClickListener { goToHomeScreen() }
        bindingSignupInfoFragment.btnNext.setOnClickListener { changeImagesOrGotoHomeScreen() }
        bindingSignupInfoFragment.imgCarousel.setImageListener { position, imageView -> imageView.scaleType = ImageView.ScaleType.CENTER }
    }

    private fun changeImagesOrGotoHomeScreen() {
        if(currentPosition == carouselImages.size - 1) {
            goToHomeScreen()
        }
        currentPosition += 1
        bindingSignupInfoFragment.imgCarousel.setCurrentItem(currentPosition,true)
    }

    private fun goToHomeScreen() {

        val intent = Intent(requireActivity(),MainActivity::class.java)
        intent.putExtra(FoodsGullyConstants.USER_DATA,user)
        startActivity(intent)
        requireActivity().finish()
    }



    override fun setImageForPosition(position: Int, imageView: ImageView?) {
        if(carouselImages.isEmpty()) return
        imageView?.setImageResource(carouselImages[position])
    }
}