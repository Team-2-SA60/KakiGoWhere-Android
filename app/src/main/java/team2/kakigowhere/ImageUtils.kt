package team2.kakigowhere

import android.content.Context
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun getImageFile(context: Context, fileName: String): File {
    return File(context.cacheDir, fileName)
}

suspend fun downloadImageToFile(context: Context, url: String, fileName: String): File? {
    return withContext(Dispatchers.IO) {
        try {
            val urlConnection = URL(url).openConnection() as HttpURLConnection
            urlConnection.connect()

            val inputStream = urlConnection.inputStream
            val file = getImageFile(context, fileName)
            file.outputStream().use { output ->
                inputStream.copyTo(output)
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}