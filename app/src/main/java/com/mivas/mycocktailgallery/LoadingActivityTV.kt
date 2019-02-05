package com.mivas.mycocktailgallery

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.mivas.mycocktailgallery.util.DriveHelper
import java.util.*
import android.widget.Toast
import com.mivas.mycocktailgallery.model.DriveFile
import com.mivas.mycocktailgallery.util.Constants
import kotlinx.android.synthetic.main.activity_loading.*


class LoadingActivityTV : Activity() {

    private lateinit var driveFiles: List<DriveFile>

    companion object {
        private const val REQUEST_CODE_SIGN_IN = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        initViews()
        requestSignIn()
    }

    private fun initViews() {
        val animation = RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f).apply {
            repeatCount = Animation.INFINITE
            duration = 1000
            interpolator = LinearInterpolator()
        }
        progressImage.startAnimation(animation)
    }

    private fun requestSignIn() {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(Scope(DriveScopes.DRIVE_FILE))
                .build()
        val client = GoogleSignIn.getClient(this, signInOptions)
        startActivityForResult(client.signInIntent, REQUEST_CODE_SIGN_IN)
    }

    private fun handleSignInResult(result: Intent) {
        GoogleSignIn.getSignedInAccountFromIntent(result).addOnSuccessListener {
            val credential = GoogleAccountCredential.usingOAuth2(this, Collections.singleton(DriveScopes.DRIVE_FILE)).apply {
                selectedAccount = it.account
            }
            DriveHelper.drive = Drive.Builder(AndroidHttp.newCompatibleTransport(), GsonFactory(), credential)
                    .setApplicationName(getString(R.string.app_name))
                    .build()
            queryDriveFiles()
        }.addOnFailureListener {
            Toast.makeText(this, getString(R.string.toast_google_sign_in_error), Toast.LENGTH_SHORT).show()
        }
    }

    private fun queryDriveFiles() {
        DriveHelper.getFiles().addOnSuccessListener { task ->
            driveFiles = task.files.map { DriveFile(it.id, it.name, it.mimeType) }
            if (baseFolderExists()) {
                DriveHelper.folderId = getFolderId()
                readCfg()
            } else {
                DriveHelper.createBaseFolder().addOnSuccessListener {
                    queryDriveFiles()
                }.addOnFailureListener {
                    Toast.makeText(this, getString(R.string.toast_create_base_folder_error), Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this, getString(R.string.toast_get_drive_files_error), Toast.LENGTH_SHORT).show()
        }
    }

    private fun readCfg() {
        if (configFileExists()) {
            DriveHelper.configId = getConfigFileId()
            DriveHelper.readCfg(getConfigFileId()).addOnSuccessListener {
                startActivity(Intent(this, MainActivityTV::class.java).apply {
                    putExtra(Constants.EXTRA_COCKTAILS, it)
                }).also { finish() }
            }.addOnFailureListener {
                Toast.makeText(this, getString(R.string.toast_read_config_file_error), Toast.LENGTH_SHORT).show()
            }
        } else {
            createCfg()
        }
    }

    private fun createCfg() {
        DriveHelper.createCfg(getFolderId()).addOnSuccessListener {
            DriveHelper.updateCfg(it, "{\"cocktails\":[]}").addOnSuccessListener {
                queryDriveFiles()
            }.addOnFailureListener {
                Toast.makeText(this, getString(R.string.toast_update_config_file_error), Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, getString(R.string.toast_create_config_file_error), Toast.LENGTH_SHORT).show()
        }
    }

    private fun baseFolderExists() = driveFiles.any { it.name == DriveHelper.BASE_FOLDER }
    private fun configFileExists() = driveFiles.any { it.name == DriveHelper.CONFIG_FILE }
    private fun getConfigFileId() = driveFiles.find { it.name == DriveHelper.CONFIG_FILE }?.id ?: ""
    private fun getFolderId() = driveFiles.find { it.name == DriveHelper.BASE_FOLDER }?.id ?: ""

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SIGN_IN && data != null) {
            handleSignInResult(data)
        }
    }
}
