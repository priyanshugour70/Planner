package com.lssgoo.planner.ui.components.image

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
actual fun S3ImagePicker(
    currentImageUrl: String?,
    onImageUploaded: (String) -> Unit,
    onError: (String) -> Unit,
    folder: String,
    size: Dp,
    shape: RoundedCornerShape,
    placeholder: @Composable () -> Unit,
    showEditButton: Boolean,
    enabled: Boolean,
    modifier: Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (currentImageUrl != null) {
            Text("📸 Image")
        } else {
            placeholder()
        }
    }
}

@Composable
actual fun RectangularImagePicker(
    currentImageUrl: String?,
    onImageUploaded: (String) -> Unit,
    onError: (String) -> Unit,
    folder: String,
    aspectRatio: Float,
    height: Dp,
    cornerRadius: Dp,
    enabled: Boolean,
    modifier: Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
         if (currentImageUrl != null) {
             Text("📸 Image")
        } else {
             Text("Tap to add image")
        }
    }
}
