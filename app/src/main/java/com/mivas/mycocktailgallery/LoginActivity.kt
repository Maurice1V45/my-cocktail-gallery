package com.mivas.mycocktailgallery

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import kotlinx.android.synthetic.main.activity_login.*
import android.widget.Toast
import com.mivas.mycocktailgallery.model.DriveFile
import com.mivas.mycocktailgallery.util.Constants


class LoginActivity : AppCompatActivity() {

    private lateinit var driveFiles: List<DriveFile>

    companion object {
        private const val REQUEST_CODE_SIGN_IN = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        requestSignIn()

        stuffButton.setOnClickListener {
            DriveHelper.queryFiles().addOnSuccessListener { task ->
                Log.w("asd", task.files.toString())
            }.addOnFailureListener {
                Log.w("asd", it)
            }
            /*driveHelper?.createCfg()?.addOnSuccessListener { task ->
                Log.w("asd", task)
            }*/
            /*driveHelper?.saveCfg("1mDLN5dur9Gg1bRJyFk28Ety4I5-JHac9", "abc123")?.addOnSuccessListener { task ->
                //Log.w("asd", task)
            }*/
            /*driveHelper?.readCfg("1mDLN5dur9Gg1bRJyFk28Ety4I5-JHac9")?.addOnSuccessListener { task ->
                Log.w("asd", task)
            }*/
            /*DriveHelper.deleteFile("1C4mHnd1gibXP5Q6vP3qLfUvx6y86FQUD").addOnSuccessListener { Log.w("asd", "asd1") }
            DriveHelper.deleteFile("1Fqo3z1-9X2FrZcfRBkIZV-oQlB2Xsf8D").addOnSuccessListener { Log.w("asd", "asd2") }
            DriveHelper.deleteFile("1mDLN5dur9Gg1bRJyFk28Ety4I5-JHac9").addOnSuccessListener { Log.w("asd", "asd3") }*/

        }
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
            val credential = GoogleAccountCredential.usingOAuth2(this, Collections.singleton(DriveScopes.DRIVE_FILE))
            credential.selectedAccount = it.account
            val googleDriveService = Drive.Builder(AndroidHttp.newCompatibleTransport(), GsonFactory(), credential)
                .setApplicationName("My Cocktail Gallery")
                .build()
            DriveHelper.drive = googleDriveService
            queryDriveFiles()
        }.addOnFailureListener {
            Toast.makeText(this, "Unable to sign in to Google", Toast.LENGTH_SHORT).show()
        }
    }

    private fun queryDriveFiles() {
        DriveHelper.queryFiles().addOnSuccessListener { task ->
            driveFiles = task.files.map { DriveFile(it.id, it.name, it.mimeType) }
            if (folderExists()) {
                DriveHelper.folderId = getFolderId()
                readCfg()
            } else {
                DriveHelper.createBaseFolder().addOnSuccessListener {
                    queryDriveFiles()
                }.addOnFailureListener {
                    Toast.makeText(this, "Unable to create base folder", Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Unable to get Drive files", Toast.LENGTH_SHORT).show()
        }
    }

    private fun readCfg() {
        if (configFileExists()) {
            DriveHelper.configId = getConfigFileId()
            DriveHelper.readCfg(getConfigFileId()).addOnSuccessListener {
                startActivity(Intent(this@LoginActivity, MainActivity::class.java).apply {
                    putExtra(Constants.EXTRA_COCKTAILS, it)
                })
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Unable to read cfg file", Toast.LENGTH_SHORT).show()
            }
        } else {
            createCfg()
        }
    }

    private fun createCfg() {
        DriveHelper.createCfg(getFolderId()).addOnSuccessListener {
            DriveHelper.saveCfg(it, "{\"cocktails\":[]}").addOnSuccessListener {
                queryDriveFiles()
            }.addOnFailureListener {
                Toast.makeText(this, "Unable to update cfg file", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Unable to create cfg file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun folderExists() = driveFiles.any { it.name == DriveHelper.FOLDER }
    private fun configFileExists() = driveFiles.any { it.name == DriveHelper.CONFIG_FILE }
    private fun getConfigFileId(): String {
        val configFile = driveFiles.find { it.name == DriveHelper.CONFIG_FILE }
        return configFile?.id ?: ""
    }
    private fun getFolderId(): String {
        val folder = driveFiles.find { it.name == DriveHelper.FOLDER }
        return folder?.id ?: ""
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SIGN_IN && data != null) {
            handleSignInResult(data)
        }
    }
}
