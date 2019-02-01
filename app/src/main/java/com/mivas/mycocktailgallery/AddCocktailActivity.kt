package com.mivas.mycocktailgallery

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.mivas.mycocktailgallery.model.Cocktail
import com.mivas.mycocktailgallery.model.Cocktails
import com.mivas.mycocktailgallery.util.Constants
import com.mivas.mycocktailgallery.util.DriveHelper
import kotlinx.android.synthetic.main.activity_add_cocktail.*
import java.io.File
import java.io.IOException


class AddCocktailActivity : AppCompatActivity() {

    private lateinit var cocktailsJson: String
    private var tempFile: File? = null

    companion object {
        private const val TAKE_PHOTO_REQUEST = 1
        private const val CROP_PHOTO_REQUEST = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_cocktail)

        cocktailsJson = intent.getStringExtra(Constants.EXTRA_COCKTAILS)
        photoButton.setOnClickListener {
            if (nameField.text.toString().isEmpty()) {
                Toast.makeText(this, "Please type in a name", Toast.LENGTH_SHORT).show()
            } else {
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
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val imageFileName = "photo"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpeg", storageDir)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TAKE_PHOTO_REQUEST && resultCode == Activity.RESULT_OK) {
            DriveHelper.uploadImage(nameField.text.toString(), tempFile!!.absolutePath, DriveHelper.folderId).addOnSuccessListener { fileId ->
                    DriveHelper.makePublic(fileId).addOnSuccessListener {
                        val cocktail = Cocktail(fileId, nameField.text.toString(), "")
                        val cocktails = Gson().fromJson(cocktailsJson, Cocktails::class.java)
                        cocktails.cocktails.add(cocktail)
                        DriveHelper.saveCfg(DriveHelper.configId, Gson().toJson(cocktails)).addOnSuccessListener {
                            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(this, "Failed to update config file", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(this, "Failed to set file permission", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                Toast.makeText(this, "Failed to upload the image", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
