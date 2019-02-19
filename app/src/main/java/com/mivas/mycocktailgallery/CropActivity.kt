package com.mivas.mycocktailgallery

import android.app.Activity
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import com.mivas.mycocktailgallery.util.Constants
import com.mivas.mycocktailgallery.util.ProgressHelper
import com.naver.android.helloyako.imagecrop.view.ImageCropView
import kotlinx.android.synthetic.main.activity_crop.*
import java.io.File
import java.io.FileOutputStream

class CropActivity : AppCompatActivity() {

    private lateinit var progressHelper: ProgressHelper

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_crop_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.action_save -> {
                progressHelper.show(R.string.message_cropping_photo)
                Thread {
                    val out = FileOutputStream(File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), AddEditCocktailActivity.CROPPED_FILE), false)
                    cropView.croppedImage.compress(Bitmap.CompressFormat.PNG, 100, out)
                    runOnUiThread {
                        progressHelper.hide()
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                }.start()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop)

        progressHelper = ProgressHelper(this)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val imagePath = intent.getStringExtra(Constants.EXTRA_IMAGE_PATH)
        with(cropView) {
            setGridInnerMode(ImageCropView.GRID_ON)
            setGridOuterMode(ImageCropView.GRID_ON)
            setImageFilePath(imagePath)
        }
    }
}
