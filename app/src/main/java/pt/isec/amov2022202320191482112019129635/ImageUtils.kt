package pt.isec.amov2022202320191482112019129635

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max
import kotlin.math.min

fun getTempFilename(context: Context): String =
    File.createTempFile(
        "image", ".img",
        context.externalCacheDir
    ).absolutePath

fun createFileFromUri(
    context: Context,
    uri: Uri,
    filename: String = getTempFilename(context)
): String {
    FileOutputStream(filename).use { outputStream ->
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            inputStream.copyTo(outputStream)
        }
    }
    return filename
}

fun setPic(view: View, path: String) {
    val targetW = view.width
    val targetH = view.height
    if (targetH < 1 || targetW < 1)
        return
    val bmpOptions = BitmapFactory.Options()
    bmpOptions.inJustDecodeBounds = true
    BitmapFactory.decodeFile(path, bmpOptions)
    val photoW = bmpOptions.outWidth
    val photoH = bmpOptions.outHeight
    val scale = max(1, min(photoW / targetW, photoH / targetH))
    bmpOptions.inSampleSize = scale
    bmpOptions.inJustDecodeBounds = false
    val bitmap = BitmapFactory.decodeFile(path, bmpOptions)
    when {
        view is ImageView -> (view as ImageView).setImageBitmap(bitmap)
//else -> view.background = bitmap.toDrawable(view.resources)
        else -> view.background = BitmapDrawable(view.resources,bitmap)
    }
}