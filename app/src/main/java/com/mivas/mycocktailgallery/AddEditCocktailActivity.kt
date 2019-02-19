package com.mivas.mycocktailgallery

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.mivas.mycocktailgallery.model.Cocktail
import com.mivas.mycocktailgallery.model.CocktailsJson
import com.mivas.mycocktailgallery.util.Constants
import com.mivas.mycocktailgallery.util.ConverterUtils
import com.mivas.mycocktailgallery.util.DriveHelper
import com.mivas.mycocktailgallery.util.ProgressHelper
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_edit_cocktail.*
import java.io.File


class AddEditCocktailActivity : AppCompatActivity() {

    private lateinit var cocktailsJsonString: String
    private lateinit var cocktailsJson: CocktailsJson
    private lateinit var selectedCocktailId: String
    private lateinit var progressHelper: ProgressHelper
    private var tempFile: File? = null
    private var photoChanged = false

    companion object {
        const val CROPPED_FILE = "cropped.png"
        private const val TAKE_PHOTO_REQUEST = 1
        private const val CROP_PHOTO_REQUEST = 2
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_edit_cocktail_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.action_save -> {
                if (titleField.text.toString().isEmpty()) {
                    Toast.makeText(this, R.string.toast_no_title, Toast.LENGTH_SHORT).show()
                } else {
                    progressHelper.show(R.string.message_saving_cocktail)
                    if (selectedCocktailId.isNotEmpty()) {
                        if (photoChanged) {
                            DriveHelper.deleteImage(selectedCocktailId).addOnSuccessListener {
                                uploadImage()
                            }.addOnFailureListener {
                                progressHelper.hide()
                                Toast.makeText(this, "Failed to set delete image", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            updateCfg(selectedCocktailId)
                        }
                    } else {
                        uploadImage()
                    }
                }
            }
            R.id.action_delete -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Delete cocktail")
                        .setMessage("Are you sure you want to delete this cocktail?")
                        .setPositiveButton("DELETE") { _, _ ->
                            DriveHelper.deleteImage(selectedCocktailId).addOnSuccessListener {
                                progressHelper.show(R.string.message_deleting_cocktail)
                                cocktailsJson.cocktails.remove(getCocktailById(selectedCocktailId))
                                saveCfg()
                            }.addOnFailureListener {
                                progressHelper.hide()
                                Toast.makeText(this, "Failed to set delete image", Toast.LENGTH_SHORT).show()
                            }
                        }.setNegativeButton("CANCEL") { _, _ ->
                            // do nothing
                        }.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_cocktail)

        progressHelper = ProgressHelper(this)
        cocktailsJsonString = intent.getStringExtra(Constants.EXTRA_COCKTAILS)
        cocktailsJson = ConverterUtils.toObject(cocktailsJsonString)
        selectedCocktailId = intent.getStringExtra(Constants.EXTRA_SELECTED_COCKTAIL) ?: ""

        initViews()
        initListeners()

    }

    private fun initViews() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = if (selectedCocktailId.isEmpty()) getString(R.string.add_edit_cocktail_activity_title_add) else getString(R.string.add_edit_cocktail_activity_title_edit)
        val adapter = ArrayAdapter.createFromResource(this, R.array.category_array, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

        if (selectedCocktailId.isNotEmpty()) {
            getCocktailById(selectedCocktailId)?.let {
                titleField.setText(it.title)
                ingredientsField.setText(it.ingredients)
                categorySpinner.setSelection(adapter.getPosition(it.category))
                photoView.visibility = View.VISIBLE
                photoButton.visibility = View.INVISIBLE
                photoIcon.visibility = View.INVISIBLE
                Picasso.get()
                        .load("https://drive.google.com/thumbnail?id=${it.id}")
                        .resize(1000, 1000)
                        .centerCrop()
                        .into(photoView)
            }
        } else {
            photoView.visibility = View.INVISIBLE
            photoButton.visibility = View.VISIBLE
            photoIcon.visibility = View.VISIBLE
        }
    }

    private fun initListeners() {
        photoButton.setOnClickListener { takePhoto() }
        photoView.setOnClickListener { takePhoto() }
    }

    private fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            val photoFile = createImageFile()
            val photoURI = FileProvider.getUriForFile(
                    this@AddEditCocktailActivity,
                    "com.mivas.mycocktailgallery.fileprovider",
                    photoFile)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            tempFile = photoFile
            startActivityForResult(intent, TAKE_PHOTO_REQUEST)
        }
    }

    private fun uploadImage() {
        val cropped = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), CROPPED_FILE)
        DriveHelper.createImage(titleField.text.toString(), cropped.absolutePath, DriveHelper.folderId)
                .addOnSuccessListener { fileId ->
                    DriveHelper.makePublic(fileId).addOnSuccessListener {
                        updateCfg(fileId)
                    }.addOnFailureListener {
                        progressHelper.hide()
                        Toast.makeText(this, R.string.toast_file_permission_error, Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    progressHelper.hide()
                    Toast.makeText(this, R.string.toast_upload_image_error, Toast.LENGTH_SHORT).show()
                }
    }

    private fun saveCfg() {
        val json = ConverterUtils.toJson(cocktailsJson)
        DriveHelper.updateCfg(DriveHelper.configId, json)
                .addOnSuccessListener {
                    setResult(Activity.RESULT_OK, Intent().putExtra(Constants.EXTRA_COCKTAILS, json))
                    finish()
                }.addOnFailureListener {
                    progressHelper.hide()
                    Toast.makeText(this, R.string.toast_update_config_file_error, Toast.LENGTH_SHORT).show()
                }
    }

    private fun updateCfg(fileId: String) {
        if (selectedCocktailId.isNotEmpty()) {
            val cocktail = cocktailsJson.cocktails.find { it.id == selectedCocktailId }
            cocktail?.run {
                id = fileId
                title = titleField.text.toString()
                ingredients = ingredientsField.text.toString()
                category = categorySpinner.selectedItem.toString()
            }
        } else {
            val cocktail = Cocktail(
                    fileId,
                    titleField.text.toString(),
                    ingredientsField.text.toString(),
                    categorySpinner.selectedItem.toString()
            )
            cocktailsJson.cocktails.add(cocktail)
        }
        saveCfg()
    }

    private fun createImageFile(): File {
        val imageFileName = "photo"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpeg", storageDir)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TAKE_PHOTO_REQUEST && resultCode == Activity.RESULT_OK) {
            startActivityForResult(Intent(this@AddEditCocktailActivity, CropActivity::class.java).apply {
                putExtra(Constants.EXTRA_IMAGE_PATH, tempFile!!.absolutePath)
            }, CROP_PHOTO_REQUEST)
        } else if (requestCode == CROP_PHOTO_REQUEST && resultCode == Activity.RESULT_OK) {
            photoChanged = true
            val cropped = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), CROPPED_FILE)
            photoView.visibility = View.VISIBLE
            photoButton.visibility = View.INVISIBLE
            photoIcon.visibility = View.INVISIBLE
            Picasso.get()
                    .load(cropped)
                    .resize(1000, 1000)
                    .centerCrop()
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(photoView)
        }
    }

    private fun getCocktailById(id: String) = cocktailsJson.cocktails.find { it.id == id }

    override fun onDestroy() {
        getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.listFiles()?.forEach { it.delete() }
        super.onDestroy()
    }

}
