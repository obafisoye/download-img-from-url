package com.example.downloadimagefromurl

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.downloadimagefromurl.ui.theme.DownloadImageFromUrlTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DownloadImageFromUrlTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    val imageUrl = "https://apersonalstyle.com/upload/alexander_mcqueen_dst_20090721.jpg"
                    val description = "Alexander Mcqueen"

                    ImageScreen(modifier = Modifier.padding(innerPadding), imageUrl, description)
                }
            }
        }
    }
}

@Composable
fun ImageScreen(modifier: Modifier, imageUrl: String, description: String) {
    Box(
        modifier = modifier.fillMaxSize(),
    ){
        NetworkImage(
            url = imageUrl,
            contentDescription = description,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .padding(16.dp)
        )
    }
}

@Composable
fun NetworkImage(url: String, modifier: Modifier = Modifier, contentDescription: String) {
    // manages the async download by creating a bitmap state
    val bitmap: Bitmap? by produceState<Bitmap?>(initialValue = null) {
        // runs the downloadImage function in the background
        value = withContext(Dispatchers.IO) {
            downloadImage(url)
        }
    }

    // if the bitmap is not null, display the image
    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    }
}

fun downloadImage(url: String): Bitmap? {
    // bitmap is an object that holds image data as an array of pixels
    var bitmap: Bitmap? = null

    try {
        // open a connection to the URL and casts it as an HttpURLConnection
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.connect()
        // retrieve input stream (class that reads data through a source) and decode into bitmap
        val inputStream: InputStream = connection.inputStream
        bitmap = BitmapFactory.decodeStream(inputStream)
    }
    catch (e: Exception) {
        Log.e("downloadImage", "Error downloading image: ${e.message}")
    }
    return bitmap
}
