package com.foodsgully.foodsgullyuser.firebase

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException


enum class FileType {
    CATEGORY_IMAGE,USER_IMAGE,PRODUCT_IMAGE
}

interface FileUploadResponseHandler {
    fun onFileUploaded(imageUrl : String,fileType : FileType)
    fun onFileUploadFailed(fileType: FileType)

}

interface FileDownloadResponseHandler {
    fun onFileDownloaded(fileUri : Uri, fileType: FileType)
    fun onFileDownloadFailed(fileType: FileType)
}


class FirebaseFileUploadService(private val context : Context) {


    private var firebaseStorage: FirebaseStorage =
        FirebaseStorage.getInstance("gs://foodsgully.appspot.com")


    fun uploadFile(
        fileUri : Uri?,
        fileName: String,
        userId: String,
        retryCount: Int,
        fileUploadResponseHandler: FileUploadResponseHandler,
        fileType: FileType
    ) {
        val storageReference = firebaseStorage.reference

        var imageUri = "gs://foodsgully.appspot.com/user_docs/$userId/${fileName}"
        val fileReference = storageReference.child("user_docs/$userId/$fileName")


        var imageBitmap: Bitmap? = null
        try {
            imageBitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, fileUri)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val outputByteArrayStream = ByteArrayOutputStream()

        imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 15, outputByteArrayStream)
        val fileInBytes: ByteArray = outputByteArrayStream.toByteArray()
        val uploadTask = fileReference.putBytes(fileInBytes)

        uploadTask.addOnFailureListener {
            if (retryCount > 0) uploadFile(
                fileUri,
                fileName,
                userId,
                retryCount - 1,
                fileUploadResponseHandler,
                fileType
            ) else fileUploadResponseHandler.onFileUploadFailed(fileType)
        }.addOnSuccessListener {
            fileUploadResponseHandler.onFileUploaded(imageUri,fileType)
        }

    }


    fun downloadFile(
        fileName: String,
        userId: String,
        retryCount: Int,
        fileDownloadResponseHandler: FileDownloadResponseHandler,
        fileType: FileType
    ) {
        val storageReference = firebaseStorage.reference
        val fileReference = storageReference.child("user_docs/$userId/$fileName")
        fileReference.downloadUrl.addOnCanceledListener {
            if (retryCount > 0) downloadFile(
                fileName,
                userId,
                retryCount - 1,
                fileDownloadResponseHandler,
                fileType
            ) else fileDownloadResponseHandler.onFileDownloadFailed(fileType = fileType)
        }.addOnSuccessListener { uri ->
            fileDownloadResponseHandler.onFileDownloaded(
                fileUri = uri,
                fileType = fileType
            )
        }
    }

    fun downloadFile( fileUrl : String,retryCount: Int,fileDownloadResponseHandler: FileDownloadResponseHandler,fileType: FileType) {
        val firebaseStorage = FirebaseStorage.getInstance()

        try {
            val gsReference = firebaseStorage.getReferenceFromUrl(fileUrl)

            gsReference.downloadUrl.addOnCanceledListener {
                if (retryCount > 0) downloadFile(
                    fileUrl,
                    retryCount - 1,
                    fileDownloadResponseHandler,
                    fileType
                ) else fileDownloadResponseHandler.onFileDownloadFailed(fileType = fileType)
            }.addOnSuccessListener { uri ->
                fileDownloadResponseHandler.onFileDownloaded(
                    fileUri = uri,
                    fileType = fileType
                )
            }
        } catch (exception : Exception) {
            fileDownloadResponseHandler.onFileDownloadFailed(fileType)
        }
    }
}