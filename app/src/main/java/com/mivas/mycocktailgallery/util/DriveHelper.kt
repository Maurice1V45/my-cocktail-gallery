package com.mivas.mycocktailgallery.util

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.api.client.http.ByteArrayContent
import com.google.api.client.http.FileContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import com.google.common.io.CharStreams
import java.io.InputStreamReader
import java.util.concurrent.Callable
import java.util.concurrent.Executors

object DriveHelper {
    lateinit var drive: Drive
    const val CONFIG_FILE = "cocktails.cfg"

    private val executor = Executors.newSingleThreadExecutor()

    fun queryFiles(): Task<FileList> = Tasks.call(executor, Callable<FileList> {
        drive.files().list().setSpaces("drive").execute()
    })

    fun createCfg(): Task<String> = Tasks.call(executor, Callable<String> {
        val metadata = File().setParents(listOf("root"))
            .setMimeType("text/plain")
            .setName("cocktails.cfg")
        val googleFile = drive.files().create(metadata).execute()
        googleFile.id
    })

    fun saveCfg(fileId: String, content: String): Task<Unit> = Tasks.call(executor, Callable<Unit> {
        val metadata = File().setName("cocktails.cfg")
        val contentStream = ByteArrayContent.fromString("text/plain", content)
        drive.files().update(fileId, metadata, contentStream).execute()
    })

    fun readCfg(fileId: String): Task<String> = Tasks.call(executor, Callable<String> {
        val inputStream = drive.files().get(fileId).executeMediaAsInputStream()
        CharStreams.toString(InputStreamReader(inputStream, Charsets.UTF_8))
    })

    fun deleteFile(fileId: String): Task<Unit> = Tasks.call(executor, Callable<Unit> {
        drive.files().delete(fileId)
    })

    fun uploadImage(name: String, path: String): Task<Unit> = Tasks.call(executor, Callable<Unit> {
        val metadata = File().setName(name)
        val mediaContent = FileContent("image/jpeg", java.io.File(path))
        drive.files().create(metadata, mediaContent).execute()
    })

    fun createBaseFolder(): Task<Unit> = Tasks.call(executor, Callable<Unit> {
        val metadata = File()
            .setName("My Cocktail Gallery")
            .setMimeType("application/vnd.google-apps.folder")
        drive.files().create(metadata).setFields("id").execute()
    })


}