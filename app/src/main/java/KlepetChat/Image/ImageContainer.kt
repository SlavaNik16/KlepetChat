package KlepetChat.Image

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import java.io.ByteArrayOutputStream

class ImageContainer {

    companion object {

        fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
            val bytes = ByteArrayOutputStream()
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path =
                MediaStore.Images.Media.insertImage(
                    inContext.contentResolver,
                    inImage,
                    "Title",
                    null
                )
            return Uri.parse(path)
        }

        fun getRealPathFromURI(activity: Activity, uri: Uri?): String {
            val cursor = activity.contentResolver.query(uri!!, null, null, null, null)
            var largeImagePath = ""
            try {
                cursor!!.moveToFirst()
                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                largeImagePath = cursor.getString(idx)
            } finally {
                cursor?.close()
            }
            return largeImagePath
        }
    }
}