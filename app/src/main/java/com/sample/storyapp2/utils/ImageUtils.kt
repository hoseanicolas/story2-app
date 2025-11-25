package com.sample.storyapp2.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object ImageUtils {
    private const val MAX_FILE_SIZE = 1024 * 1024
    private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    private const val PHOTO_EXTENSION = ".jpg"

    fun createTempFile(context: Context): File {
        val timeStamp = java.text.SimpleDateFormat(
            FILENAME_FORMAT,
            java.util.Locale.US
        ).format(System.currentTimeMillis())

        val storageDir = context.externalCacheDir
        return File.createTempFile(timeStamp, PHOTO_EXTENSION, storageDir)
    }

    fun uriToFile(imageUri: Uri, context: Context): File {
        val myFile = createTempFile(context)
        val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
        val outputStream = FileOutputStream(myFile)
        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) {
            outputStream.write(buffer, 0, length)
        }
        outputStream.close()
        inputStream.close()
        return myFile
    }

    fun reduceFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int

        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > MAX_FILE_SIZE && compressQuality > 0)

        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }
}