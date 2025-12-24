package com.lssgoo.planner.ui.components.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * Enum for different dialog types
 */
enum class DialogType {
    INFO,
    SUCCESS,
    WARNING,
    ERROR,
    CONFIRMATION,
    INPUT
}

/**
 * Enhanced reusable dialog component with proper alignment and truncation
 */
@Composable
fun AppDialog(
    onDismiss: () -> Unit,
    title: String,
    message: String? = null,
    type: DialogType = DialogType.INFO,
    icon: ImageVector? = null,
    confirmText: String = "OK",
    cancelText: String = "Cancel",
    showCancel: Boolean = type == DialogType.CONFIRMATION,
    onConfirm: () -> Unit = onDismiss,
    onCancel: () -> Unit = onDismiss,
    isDestructive: Boolean = false,
    content: (@Composable () -> Unit)? = null
) {
    val iconToShow = icon ?: when (type) {
        DialogType.INFO -> Icons.Filled.Info
        DialogType.SUCCESS -> Icons.Filled.CheckCircle
        DialogType.WARNING -> Icons.Filled.Warning
        DialogType.ERROR -> Icons.Filled.Error
        DialogType.CONFIRMATION -> Icons.Filled.HelpOutline
        DialogType.INPUT -> Icons.Filled.Edit
    }
    
    val iconColor = when (type) {
        DialogType.INFO -> MaterialTheme.colorScheme.primary
        DialogType.SUCCESS -> Color(0xFF4CAF50)
        DialogType.WARNING -> Color(0xFFFFA000)
        DialogType.ERROR -> MaterialTheme.colorScheme.error
        DialogType.CONFIRMATION -> MaterialTheme.colorScheme.tertiary
        DialogType.INPUT -> MaterialTheme.colorScheme.secondary
    }
    
    val iconBackground = iconColor.copy(alpha = 0.12f)
    
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        visible = true
    }
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        )
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(200)) + scaleIn(initialScale = 0.8f, animationSpec = tween(250)),
            exit = fadeOut(animationSpec = tween(150)) + scaleOut(targetScale = 0.8f, animationSpec = tween(150))
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Icon with background
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(iconBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = iconToShow,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Title with proper truncation
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // Message with proper truncation
                    if (!message.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 22.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    
                    // Custom content area
                    if (content != null) {
                        Spacer(modifier = Modifier.height(20.dp))
                        content()
                    }
                    
                    Spacer(modifier = Modifier.height(28.dp))
                    
                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (showCancel) Arrangement.spacedBy(12.dp) else Arrangement.Center
                    ) {
                        if (showCancel) {
                            OutlinedButton(
                                onClick = {
                                    visible = false
                                    onCancel()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            ) {
                                Text(
                                    text = cancelText,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        
                        Button(
                            onClick = {
                                visible = false
                                onConfirm()
                            },
                            modifier = Modifier
                                .then(if (showCancel) Modifier.weight(1f) else Modifier.fillMaxWidth(0.6f))
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                text = confirmText,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Loading dialog with progress indicator
 */
@Composable
fun LoadingDialog(
    message: String = "Please wait...",
    onDismiss: () -> Unit = {}
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = false,
            dismissOnBackPress = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    strokeWidth = 4.dp
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Input dialog with text field
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputDialog(
    onDismiss: () -> Unit,
    title: String,
    placeholder: String = "",
    initialValue: String = "",
    confirmText: String = "Save",
    cancelText: String = "Cancel",
    maxLength: Int = 200,
    singleLine: Boolean = true,
    onConfirm: (String) -> Unit
) {
    var inputValue by remember { mutableStateOf(initialValue) }
    
    AppDialog(
        onDismiss = onDismiss,
        title = title,
        type = DialogType.INPUT,
        confirmText = confirmText,
        cancelText = cancelText,
        showCancel = true,
        onConfirm = { onConfirm(inputValue) }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = inputValue,
                onValueChange = { 
                    if (it.length <= maxLength) inputValue = it 
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { 
                    Text(
                        text = placeholder,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    ) 
                },
                singleLine = singleLine,
                maxLines = if (singleLine) 1 else 4,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
            
            // Character counter
            if (maxLength < 1000) {
                Text(
                    text = "${inputValue.length}/$maxLength",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (inputValue.length >= maxLength) 
                        MaterialTheme.colorScheme.error 
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 4.dp)
                )
            }
        }
    }
}

/**
 * Selection dialog with multiple options  
 */
@Composable
fun <T> SelectionDialog(
    onDismiss: () -> Unit,
    title: String,
    options: List<T>,
    selectedOption: T? = null,
    optionLabel: (T) -> String,
    optionIcon: ((T) -> ImageVector)? = null,
    onSelect: (T) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Options list
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    options.forEach { option ->
                        val isSelected = option == selectedOption
                        
                        Surface(
                            onClick = {
                                onSelect(option)
                                onDismiss()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) 
                                MaterialTheme.colorScheme.primaryContainer 
                            else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (optionIcon != null) {
                                    Icon(
                                        imageVector = optionIcon(option),
                                        contentDescription = null,
                                        tint = if (isSelected) 
                                            MaterialTheme.colorScheme.primary 
                                        else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                }
                                
                                Text(
                                    text = optionLabel(option),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isSelected) 
                                        MaterialTheme.colorScheme.primary 
                                    else MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Cancel button
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = "Cancel",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

/**
 * Bottom sheet style dialog for actions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionBottomSheet(
    onDismiss: () -> Unit,
    title: String? = null,
    actions: List<ActionItem>,
    showDragHandle: Boolean = true
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = if (showDragHandle) {
            { BottomSheetDefaults.DragHandle() }
        } else null,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            if (!title.isNullOrBlank()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            actions.forEach { action ->
                Surface(
                    onClick = {
                        action.onClick()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.Transparent
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = action.icon,
                            contentDescription = null,
                            tint = if (action.isDestructive) 
                                MaterialTheme.colorScheme.error 
                            else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(24.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Text(
                            text = action.label,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (action.isDestructive) 
                                MaterialTheme.colorScheme.error 
                            else MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

/**
 * Data class for action items in bottom sheet
 */
data class ActionItem(
    val label: String,
    val icon: ImageVector,
    val isDestructive: Boolean = false,
    val onClick: () -> Unit
)

/**
 * Quick confirmation dialog - minimal design
 */
@Composable
fun QuickConfirmDialog(
    onDismiss: () -> Unit,
    title: String,
    message: String,
    confirmText: String = "Confirm",
    isDestructive: Boolean = false,
    onConfirm: () -> Unit
) {
    AppDialog(
        onDismiss = onDismiss,
        title = title,
        message = message,
        type = if (isDestructive) DialogType.WARNING else DialogType.CONFIRMATION,
        confirmText = confirmText,
        showCancel = true,
        isDestructive = isDestructive,
        onConfirm = onConfirm,
        onCancel = onDismiss
    )
}
