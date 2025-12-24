package com.lssgoo.planner.ui.components.image

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lssgoo.planner.data.remote.S3ImageManager
import kotlinx.coroutines.launch

// ImagePickerState is defined in commonMain

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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val s3Manager = remember { S3ImageManager(context) }
    
    var state by remember { 
        mutableStateOf(ImagePickerState(remoteUrl = currentImageUrl)) 
    }
    
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            state = state.copy(localUri = selectedUri, isUploading = true, error = null)
            
            scope.launch {
                val (url, error) = s3Manager.uploadImage(selectedUri, folder)
                
                if (url != null) {
                    state = state.copy(
                        remoteUrl = url,
                        isUploading = false,
                        uploadProgress = 1f
                    )
                    onImageUploaded(url)
                } else {
                    state = state.copy(
                        isUploading = false,
                        error = error ?: "Upload failed"
                    )
                    onError(error ?: "Upload failed")
                }
            }
        }
    }
    
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Image container
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape)
                .clickable(enabled = enabled && !state.isUploading) {
                    imagePicker.launch("image/*")
                },
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = shape
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Display image if available
                val localUri = state.localUri
                val imageToShow = if (localUri is Uri) localUri.toString() else state.remoteUrl
                
                if (imageToShow != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(imageToShow)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Selected image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    
                    // Dark overlay when uploading
                    AnimatedVisibility(
                        visible = state.isUploading,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(size / 3),
                                color = Color.White,
                                strokeWidth = 3.dp
                            )
                        }
                    }
                } else {
                    placeholder()
                }
            }
        }
        
        // Edit button
        if (showEditButton && !state.isUploading) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 4.dp, y = 4.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .size(36.dp)
                        .clickable(enabled = enabled) {
                            imagePicker.launch("image/*")
                        },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    shadowElevation = 4.dp
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (state.remoteUrl != null || state.localUri != null) 
                                Icons.Filled.Edit else Icons.Filled.Add,
                            contentDescription = "Edit image",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
        
        // Error indicator
        if (state.error != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-4).dp)
            ) {
                Surface(
                    modifier = Modifier.size(24.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.error
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = "Upload error",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val s3Manager = remember { S3ImageManager(context) }
    
    var state by remember { 
        mutableStateOf(ImagePickerState(remoteUrl = currentImageUrl)) 
    }
    
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            state = state.copy(localUri = selectedUri, isUploading = true, error = null)
            
            scope.launch {
                val (url, error) = s3Manager.uploadImage(selectedUri, folder)
                
                if (url != null) {
                    state = state.copy(
                        remoteUrl = url,
                        isUploading = false,
                        uploadProgress = 1f
                    )
                    onImageUploaded(url)
                } else {
                    state = state.copy(
                        isUploading = false,
                        error = error ?: "Upload failed"
                    )
                    onError(error ?: "Upload failed")
                }
            }
        }
    }
    
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .clickable(enabled = enabled && !state.isUploading) {
                imagePicker.launch("image/*")
            },
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(cornerRadius)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val localUri = state.localUri
            val imageToShow = if (localUri is Uri) localUri.toString() else state.remoteUrl
            
            if (imageToShow != null) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageToShow)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Selected image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.3f)
                                )
                            )
                        )
                )
                
                // Edit overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
                
                // Loading overlay
                AnimatedVisibility(
                    visible = state.isUploading,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Uploading to cloud...",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            } else {
                // Empty state
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CloudUpload,
                        contentDescription = "Upload image",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Tap to add image",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Automatically synced to cloud",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
