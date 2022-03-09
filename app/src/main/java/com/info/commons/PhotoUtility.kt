package com.info.commons

import com.info.interfaces.PhotoOptionSelectListener
import com.info.tradewyse.BuildConfig
import com.info.tradewyse.R
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.facebook.drawee.view.SimpleDraweeView
import kotlinx.android.synthetic.main.photo_option.view.*
import java.io.File
import java.io.IOException

class PhotoUtility {
    private var TAG = "PhotoUtility"
    private var selectedFileUri: Uri? = null
    private var selectedFilePath: String? = null

    private val REQUEST_IMAGE_CAPTURE = 110;
    public var REQUEST_IMAGE_SELECT = 111;
    public var REQUEST_PERMISSION = 112;
    private var activity: AppCompatActivity;
    private var simpleDraweeView: SimpleDraweeView?
    private var outPutFile: File
    private lateinit var photoOptionSelectListener: PhotoOptionSelectListener

    private constructor(builder: Builder) {
        activity = builder.getActivity()
        simpleDraweeView = builder.getSimpleDraweeView()
        outPutFile = builder.getOutPutFile()
    }

    fun setPhotoOptionSelectListener(photoOptionSelectListener: PhotoOptionSelectListener) {
        this.photoOptionSelectListener = photoOptionSelectListener
    }

    fun requestPermissionsCameraAndStorage() {

        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                photoOptionSelectListener.requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA), REQUEST_PERMISSION)
            } else {
                // selectImage()
                showBottomDialog()
            }

        } else {
            // selectImage()
            showBottomDialog()
        }
    }

    private fun selectImage() {
        val options = arrayOf<CharSequence>(activity.getString(R.string.take_photo), activity.getString(R.string.choose_from_gallery), activity.getString(R.string.cancel))
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(activity.getString(R.string.select_option))
        builder.setItems(options) { dialog, item ->

            when (item) {
                0 -> {
                    cameraIntent()
                    dialog.dismiss()
                }
                1 -> {
                    galleryIntent()
                    dialog.dismiss()
                }
                2 -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    private fun showBottomDialog() {
        try{
            if(activity!=null&&!activity.isFinishing){
                var mBottomSheetDialog = BottomSheetDialog(activity);
                var sheetView = activity.layoutInflater.inflate(R.layout.photo_option, null);
                mBottomSheetDialog.setContentView(sheetView)
                sheetView.optionCamera.setOnClickListener {
                    cameraIntent()
                    mBottomSheetDialog.hide()
                }

                sheetView.optionGallery.setOnClickListener {
                    galleryIntent()
                    mBottomSheetDialog.hide()
                }

                sheetView.optionCancel.setOnClickListener {
                    mBottomSheetDialog.hide()
                }

                mBottomSheetDialog.show()
            }

        }catch (e:Exception){ }

    }


    /**
     * Launch gallery intent.
     */
    private fun galleryIntent() {
        if (Build.VERSION.SDK_INT < 19) {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            photoOptionSelectListener.startActivityForResult(Intent.createChooser(intent, "Select Document"), REQUEST_IMAGE_SELECT)
        } else {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            photoOptionSelectListener.startActivityForResult(intent, REQUEST_IMAGE_SELECT)

        }
    }

    fun getSelectedFileUri(): Uri? {
        return selectedFileUri
    }

    fun getSelectedFilePath(): String? {
        return selectedFilePath;
    }

    /**
     * Launch camera activity to get photo to upload server.
     */
    private fun cameraIntent() {
        val packageManager = activity!!.packageManager
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(activity, "This device does not have a camera.", Toast.LENGTH_SHORT).show()
            return
        }
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (takePictureIntent.resolveActivity(activity!!.packageManager) != null) {

            try {

                if (outPutFile != null) {
                    selectedFileUri = FileProvider.getUriForFile(activity!!, BuildConfig.APPLICATION_ID + ".provider", outPutFile)
                    selectedFilePath = outPutFile.absolutePath;
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedFileUri)
                    takePictureIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    takePictureIntent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION

                    //Grant URI Permission
                    val resolvedIntentActivities = activity!!.packageManager.queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY)
                    for (resolvedIntentInfo in resolvedIntentActivities) {
                        val packageName = resolvedIntentInfo.activityInfo.packageName
                        activity!!.grantUriPermission(packageName, selectedFileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    photoOptionSelectListener.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)

                } else {
                    Log.e(TAG, "variable outPutFile can not be null")
                    Toast.makeText(activity, "Unable to capture photo", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        } else {
            Toast.makeText(activity, "Unable to take photo,Camera Application not available", Toast.LENGTH_SHORT).show()
        }
    }

    fun setResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_IMAGE_CAPTURE -> {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    simpleDraweeView?.let {
                        it.setImageURI(selectedFileUri, activity)
                        it.visibility = View.VISIBLE
                    }
                }
            }

            REQUEST_IMAGE_SELECT -> {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    selectedFileUri = data!!.data
                    selectedFilePath = FIleHelper.getPath(activity, selectedFileUri)
                    val mimType = FIleHelper.getMimeType(activity, selectedFileUri)
                    if (FIleHelper.isInValidFile(mimType)) {
                        Toast.makeText(activity, "Invalid File type", Toast.LENGTH_LONG).show()
                        return
                    }
                    simpleDraweeView?.let {
                        it.setImageURI(selectedFileUri, activity)
                        it.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    fun setPermissionResult(requestCode: Int, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    showBottomDialog()
                } else {
                    Toast.makeText(activity, "All the permissions are required", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }


    class Builder {

        private var activity: AppCompatActivity
        private var simpleDraweeView: SimpleDraweeView? = null;
        private lateinit var outPutFile: File

        constructor(activity: AppCompatActivity) {
            this.activity = activity
        }

        fun build(): PhotoUtility {
            return PhotoUtility(this)
        }

        fun getActivity(): AppCompatActivity {
            return activity
        }

        fun getSimpleDraweeView(): SimpleDraweeView? {
            return simpleDraweeView
        }

        fun setImageView(simpleDraweeView: SimpleDraweeView): Builder {
            this.simpleDraweeView = simpleDraweeView
            return this
        }

        fun setOutPutFile(outPutFile: File): Builder {
            this.outPutFile = outPutFile
            return this
        }

        fun getOutPutFile(): File {
            return outPutFile
        }
    }
}