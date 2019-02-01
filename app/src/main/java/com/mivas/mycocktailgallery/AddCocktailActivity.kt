package com.mivas.mycocktailgallery

import android.R.attr.content
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.util.Log
import com.mivas.mycocktailgallery.util.DriveHelper
import kotlinx.android.synthetic.main.activity_add_cocktail.*
import java.io.File
import java.io.IOException
import android.R.attr.data
import com.mivas.mycocktailgallery.util.PathUtil


class AddCocktailActivity : AppCompatActivity() {

    private var tempFile: File? = null

    companion object {
        private const val TAKE_PHOTO_REQUEST = 1
        private const val CROP_PHOTO_REQUEST = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_cocktail)

        photoButton.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(packageManager) != null) {
                val photoFile = createImageFile()
                val photoURI = FileProvider.getUriForFile(
                    this@AddCocktailActivity,
                    "com.mivas.mycocktailgallery.fileprovider",
                    photoFile
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                tempFile = photoFile
                startActivityForResult(intent, TAKE_PHOTO_REQUEST)
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val imageFileName = "photo"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpeg", storageDir)
    }

    private fun cropImage(uri: Uri) {
        val intent = Intent("com.android.camera.action.CROP");

        intent.setDataAndType(uri, "image/*");

        intent.putExtra("crop", "true");
        intent.putExtra("outputX", 180);
        intent.putExtra("outputY", 180);
        intent.putExtra("aspectX", 3);
        intent.putExtra("aspectY", 4);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, CROP_PHOTO_REQUEST);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TAKE_PHOTO_REQUEST && resultCode == Activity.RESULT_OK) {
            val photoURI = FileProvider.getUriForFile(
                this@AddCocktailActivity,
                "com.mivas.mycocktailgallery.fileprovider",
                tempFile!!
            )
            DriveHelper.uploadImage("test.jpeg", tempFile!!.absolutePath).addOnSuccessListener {
                Log.w("asd", "success")
            }.addOnFailureListener { Log.w("asd", "failed $it") }
            //cropImage(photoURI)
        }
    }

    /*private fun getRealPathFromURI(context: Context, contentUri: Uri): String {
        val file = File(contentUri.path)
    }*/
}
