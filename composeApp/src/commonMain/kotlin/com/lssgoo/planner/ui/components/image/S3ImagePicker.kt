package com.lssgoo.planner.ui.components.image

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Image picker state holder
 */
data class ImagePickerState(
    val localUri: Any? = null, // Uri on Android, something else on iOS? Keeping Any for now or expect class?
    // In common code, we can't use android.net.Uri. 
    // We can use String for URI or just wrapper.
    // However, this state class is used inside the implementation mostly.
    // If I expose it in common code, I should use platform agnostic type.
    // String is fine for display (toString).
    val remoteUrl: String? = null,
    val isUploading: Boolean = false,
    val uploadProgress: Float = 0f,
    val error: String? = null
)

@Composable
expect fun S3ImagePicker(
    currentImageUrl: String? = null,
    onImageUploaded: (String) -> Unit,
    onError: (String) -> Unit = {},
    folder: String = "planner/images",
    size: Dp = 120.dp,
    shape: RoundedCornerShape = CircleShape,
    placeholder: @Composable () -> Unit = { DefaultPlaceholder() },
    showEditButton: Boolean = true,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
)

@Composable
expect fun RectangularImagePicker(
    currentImageUrl: String? = null,
    onImageUploaded: (String) -> Unit,
    onError: (String) -> Unit = {},
    folder: String = "planner/images",
    aspectRatio: Float = 16f / 9f,
    height: Dp = 200.dp,
    cornerRadius: Dp = 16.dp,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
)

@Composable
fun DefaultPlaceholder() {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.AddPhotoAlternate,
            contentDescription = "Add photo",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Add Image",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Profile image picker with avatar fallback
 */
@Composable
fun ProfileImagePicker(
    currentImageUrl: String?,
    userName: String,
    onImageUploaded: (String) -> Unit,
    onError: (String) -> Unit = {},
    size: Dp = 100.dp,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    S3ImagePicker(
        currentImageUrl = currentImageUrl,
        onImageUploaded = onImageUploaded,
        onError = onError,
        folder = "planner/images/profile",
        size = size,
        shape = CircleShape,
        showEditButton = true,
        enabled = enabled,
        placeholder = {
            // Avatar fallback with initials
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(x = 0.dp, y = 0.dp), // Fix for box
                contentAlignment = Alignment.Center
            ) {
                 // Background
                 Surface(
                     color = MaterialTheme.colorScheme.primaryContainer,
                     modifier = Modifier.fillMaxSize()
                 ) {}
                 
                Text(
                    text = userName.take(2).uppercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        },
        modifier = modifier
    )
}

/**
 * Journal image picker with date stamp
 */
@Composable
fun JournalImagePicker(
    currentImageUrl: String?,
    entryId: String,
    onImageUploaded: (String) -> Unit,
    onError: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Note: The original Android implementation generated a custom filename "journal_${entryId}_..."
    // but the actual code used RectangularImagePicker which seemingly ignored the custom logic and used generic UUID.
    // If we want custom filename, we need to pass it to RectangularImagePicker if it supported it.
    // For now, we stick to the functional equivalent which uploads to the folder.
    RectangularImagePicker(
        currentImageUrl = currentImageUrl,
        onImageUploaded = onImageUploaded,
        onError = onError,
        folder = "planner/images/journal",
        height = 180.dp,
        modifier = modifier
    )
}
