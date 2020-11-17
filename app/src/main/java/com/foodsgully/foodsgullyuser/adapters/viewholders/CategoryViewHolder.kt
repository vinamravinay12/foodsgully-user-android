package com.foodsgully.foodsgullyuser.adapters.viewholders

import android.graphics.Bitmap
import android.net.Uri
import com.foodsgully.foodsgullyuser.models.responsemodels.Category

import androidx.databinding.library.baseAdapters.BR
import com.foodsgully.foodsgullyuser.R
import com.foodsgully.foodsgullyuser.databinding.CardCategoryBinding
import com.foodsgully.foodsgullyuser.firebase.FileDownloadResponseHandler
import com.foodsgully.foodsgullyuser.firebase.FileType
import com.foodsgully.foodsgullyuser.firebase.FirebaseFileUploadService
import com.foodsgully.foodsgullyuser.utils.FoodsGullyUtils
import com.foodsgully.foodsgullyuser.utils.listeners.ImageBitmapDownloadedListener


class CategoryViewHolder(private val viewBinding : CardCategoryBinding, private val variables : HashMap<Int,Any?>) : BaseViewHolder<Category>(viewBinding), FileDownloadResponseHandler{

    private var category : Category? = null
    override fun bind(item: Category, position: Int) {
            this.category = item
            variables[BR.category] = item
            setVariables(variables)

            FirebaseFileUploadService(viewBinding.root.context).downloadFile(item.categoryUrl ?: "",3,this,FileType.CATEGORY_IMAGE)
            viewBinding.cardCategory.setOnClickListener { viewBinding.itemClickListener?.onItemClick(item) }


    }

    override fun onFileDownloaded(fileUri: Uri, fileType: FileType) {
        FoodsGullyUtils.setCategoryImage(viewBinding.ivCategory,fileUri,viewBinding.cardCategory.context,
            R.drawable.category_sample_image,category)
    }

    override fun onFileDownloadFailed(fileType: FileType) {
        FoodsGullyUtils.setImageToView(viewBinding.ivCategory, Uri.EMPTY,viewBinding.root.context,
            R.drawable.category_sample_image)
    }

}