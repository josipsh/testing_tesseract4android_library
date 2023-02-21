package com.josipsh

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun Context.getTessDataPath(): String {
    // We need to return folder that contains the "tessdata" folder,
    // which is in this sample directly the app's files dir
    return this.filesDir.absolutePath
}

fun Context.getLanguage(): String {
    return "eng"
}

fun Context.getImageFile(): File {
    return File(filesDir, "sample.jpg")
}

fun Context.getImageBitmap(): Bitmap? {
    return BitmapFactory.decodeFile(this.getImageFile().absolutePath)
}

fun Context.extractAssets() {
    val assetManager = this.assets
    val imageFile = this.getImageFile()
    if (!imageFile.exists()) {
        copyFile(assetManager, "sample.jpg", imageFile)
    }
    val tessDir: File =
        File(getTessDataPath(), "tessdata")
    if (!tessDir.exists()) {
        tessDir.mkdir()
    }
    val engFile = File(tessDir, "eng.traineddata")
    if (!engFile.exists()) {
        copyFile(assetManager, "eng.traineddata", engFile)
    }
}

private fun copyFile(
    am: AssetManager, assetName: String,
    outFile: File
) {
    try {
        am.open(assetName).use { `in` ->
            FileOutputStream(outFile).use { out ->
                val buffer = ByteArray(1024)
                var read: Int
                while (`in`.read(buffer).also { read = it } != -1) {
                    out.write(buffer, 0, read)
                }
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}