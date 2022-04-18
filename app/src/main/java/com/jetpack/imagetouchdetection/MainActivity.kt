package com.jetpack.imagetouchdetection

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.isUnspecified
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.jetpack.imagetouchdetection.ui.theme.ImageTouchDetectionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ImageTouchDetectionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = "Image Touch Detection",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            )
                        }
                    ) {
                        ImageTouchDetection()
                    }
                }
            }
        }
    }
}

@Composable
fun ImageTouchDetection() {
    val imageBitmap: ImageBitmap = ImageBitmap.imageResource(
        LocalContext.current.resources,
        R.drawable.sample
    )
    val bitmapWidth = imageBitmap.width
    val bitmapHeight = imageBitmap.height

    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var imageSize by remember { mutableStateOf(Size.Zero) }

    var text by remember { mutableStateOf("") }
    var colorAtTouchPosition by remember { mutableStateOf(Color.Unspecified) }

    val imageModifier = Modifier
        .background(Color.LightGray)
        .fillMaxWidth()
        .aspectRatio(4f / 3)
        .pointerInput(Unit) {
            detectTapGestures { offset: Offset ->
                offsetX = offset.x
                offsetY = offset.y

                val scaledX = (bitmapWidth / imageSize.width) * offsetX
                val scaledY = (bitmapHeight / imageSize.height) * offsetY

                try {
                    val pixel: Int = imageBitmap
                        .asAndroidBitmap()
                        .getPixel(scaledX.toInt(), scaledY.toInt())
                    val red = android.graphics.Color.red(pixel)
                    val green = android.graphics.Color.green(pixel)
                    val blue = android.graphics.Color.blue(pixel)

                    text = "Image Touch offsetX: $offsetX, offsetY: $offsetY\n" +
                            "Image width: ${imageSize.width}, height: ${imageSize.height}\n" +
                            "Bitmap width: ${bitmapWidth}, height: $bitmapHeight\n" +
                            "scaledX: ${scaledX}, scaledY: $scaledY\n" +
                            "red: $red, green: $green, blue: $blue\n"

                    colorAtTouchPosition = Color(red, green, blue)
                } catch (e: Exception) {
                    Log.d("Exception", "ImageTouchDetection: ${e.message}")
                }
            }
        }
        .onSizeChanged {
            imageSize = it.toSize()
        }

    Column {
        Image(
            bitmap = imageBitmap,
            contentDescription = "Bitmap Image",
            modifier = imageModifier
                .border(2.dp, Color.Red),
            contentScale = ContentScale.Crop
        )

        Text(text = text)

        Box(
            modifier = Modifier
                .then(
                    if (colorAtTouchPosition.isUnspecified) {
                        Modifier
                    } else {
                        Modifier.background(colorAtTouchPosition)
                    }
                )
                .size(100.dp)
        )
    }
}


























