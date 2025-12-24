package com.lssgoo.planner.features.journal.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lssgoo.planner.data.model.Goal
import com.lssgoo.planner.features.journal.components.JournalPromptCard
import com.lssgoo.planner.features.journal.components.MoodCalendar
import com.lssgoo.planner.features.journal.components.MoodDistributionChart
import com.lssgoo.planner.features.journal.components.MoodTimeline
import com.lssgoo.planner.features.journal.models.JournalEntry
import com.lssgoo.planner.features.journal.models.JournalMood
import com.lssgoo.planner.ui.components.EmptyState
import com.lssgoo.planner.ui.components.GradientFAB
import com.lssgoo.planner.ui.viewmodel.PlannerViewModel
import com.lssgoo.planner.util.KmpDateFormatter
import com.lssgoo.planner.util.KmpTimeUtils
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    viewModel: PlannerViewModel,
    onEntryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val entries by viewModel.journalEntries.collectAsState()
    val prompts by viewModel.journalPrompts.collectAsState() // Observe strictly if needed, but we use getDailyPrompt
    val allGoals by viewModel.goals.collectAsState()
    
    var showAddEntryDialog by remember { mutableStateOf(false) }
    var activePromptQuestion by remember { mutableStateOf<String?>(null) }
    
    val today = Clock.System.now().toEpochMilliseconds()
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val dailyPrompt = viewModel.getDailyPrompt()
    
    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background,
                shadowElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Book,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Journal",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        },
        floatingActionButton = {
            GradientFAB(
                onClick = { 
                    activePromptQuestion = null
                    showAddEntryDialog = true 
                },
                icon = Icons.Filled.Add
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Dashboard Section
            if (entries.isNotEmpty()) {
                item {
                    Column {
                        MoodDistributionChart(entries)
                        MoodCalendar(entries, now.year, now.monthNumber)
                        MoodTimeline(entries)
                    }
                }
            }
            
            // 2. Daily Prompt
            item {
                JournalPromptCard(
                    prompt = dailyPrompt,
                    onAnswerClick = {
                        activePromptQuestion = dailyPrompt?.text
                        showAddEntryDialog = true
                    }
                )
            }
            
            // 3. Entries List
            if (entries.isEmpty()) {
                item {
                    EmptyState(
                        title = "No journal entries yet",
                        description = "Start specific journaling with guided prompts.",
                        icon = Icons.Filled.Book,
                        actionText = "Write First Entry",
                        onActionClick = { showAddEntryDialog = true }
                    )
                }
            } else {
                item {
                     Text(
                        "Your Journey",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                items(entries.sortedByDescending { it.date }) { entry ->
                   JournalEntryCard(
                       entry = entry,
                       onClick = { onEntryClick(entry.id) },
                       modifier = Modifier.padding(horizontal = 16.dp)
                   )
                }
            }
        }
    }
    
    if (showAddEntryDialog) {
        AddJournalEntryDialog(
            initialPrompt = activePromptQuestion,
            availableGoals = allGoals,
            onDismiss = { showAddEntryDialog = false },
            onAdd = { entry ->
                viewModel.addJournalEntry(entry)
                showAddEntryDialog = false
            }
        )
    }
}

@Composable
fun JournalEntryCard(
    entry: JournalEntry,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                     Text(
                        text = KmpDateFormatter.formatDate(entry.date),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = entry.title.ifBlank { "Daily Entry" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = entry.mood.emoji,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Content
            if (entry.content.isNotEmpty()) {
                Text(
                    text = entry.content.take(150) + if (entry.content.length > 150) "..." else "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            // Structured Data Preview (Gratitude / Wins)
            if (entry.gratitude.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Grateful for ${entry.gratitude.size} things", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                }
            }
             if (entry.achievements.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.tertiary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Achieved ${entry.achievements.size} goals", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary)
                }
            }
            
            // Photo Placeholder logic
            if (entry.photos.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                // Visual placeholder for photo
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Photo, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Photo Memory", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddJournalEntryDialog(
    initialPrompt: String? = null,
    availableGoals: List<Goal>,
    onDismiss: () -> Unit,
    onAdd: (JournalEntry) -> Unit
) {
    var title by remember { mutableStateOf(initialPrompt ?: "") }
    var content by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf(JournalMood.NEUTRAL) }
    
    // Structured inputs
    var gratitude1 by remember { mutableStateOf("") }
    var gratitude2 by remember { mutableStateOf("") }
    var win1 by remember { mutableStateOf("") }
    var challenge1 by remember { mutableStateOf("") }
    var tagsInput by remember { mutableStateOf("") }
    
    // Linking
    var selectedGoalId by remember { mutableStateOf<String?>(null) }
    var hasPhoto by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column {
                // Gradient Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.tertiary,
                                    MaterialTheme.colorScheme.primary
                                )
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                Icons.Default.AutoStories,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Journal Entry",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "Record your thoughts and growth",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // 1. Mood Selection
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            "How are you feeling?",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            JournalMood.entries.forEach { mood ->
                                val isSelected = selectedMood == mood
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Surface(
                                        onClick = { selectedMood = mood },
                                        modifier = Modifier.size(50.dp),
                                        shape = CircleShape,
                                        color = if (isSelected) Color(mood.color).copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                        border = if (isSelected) BorderStroke(2.dp, Color(mood.color)) else null
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(mood.emoji, fontSize = 28.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        mood.name.lowercase().replaceFirstChar { it.uppercase() },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) Color(mood.color) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // 2. Title & Main Reflection
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Topic / Prompt") },
                            placeholder = { Text("What's on your mind?") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Title, null, tint = MaterialTheme.colorScheme.primary) }
                        )

                        OutlinedTextField(
                            value = content,
                            onValueChange = { content = it },
                            label = { Text("Full Reflection") },
                            placeholder = { Text("Dive deep into your thoughts...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 150.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )
                        )
                    }

                    // 3. Structured Highlights
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Flare, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Structured Reflection",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                        OutlinedTextField(
                            value = gratitude1,
                            onValueChange = { gratitude1 = it },
                            label = { Text("I am grateful for...") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            leadingIcon = { Icon(Icons.Default.Favorite, null, tint = Color(0xFFE91E63)) }
                        )

                        OutlinedTextField(
                            value = win1,
                            onValueChange = { win1 = it },
                            label = { Text("Key Achievement / Win") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            leadingIcon = { Icon(Icons.Default.EmojiEvents, null, tint = Color(0xFFFFC107)) }
                        )
                        
                        OutlinedTextField(
                            value = challenge1,
                            onValueChange = { challenge1 = it },
                            label = { Text("Challenge overcome") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            leadingIcon = { Icon(Icons.Default.Terrain, null, tint = Color(0xFF795548)) }
                        )
                    }

                    // 4. Tags & Goals
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = tagsInput,
                            onValueChange = { tagsInput = it },
                            label = { Text("Tags") },
                            placeholder = { Text("work, growth, peace...") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            leadingIcon = { Icon(Icons.Default.Label, null, tint = MaterialTheme.colorScheme.primary) }
                        )

                        if (availableGoals.isNotEmpty()) {
                            Text(
                                "Link to a Life Goal",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                availableGoals.forEach { goal ->
                                    val isSelected = selectedGoalId == goal.id
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedGoalId = if (isSelected) null else goal.id },
                                        label = { Text(goal.title) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                        )
                                    )
                                }
                            }
                        }
                    }
                    
                    // 5. Attachments
                    Surface(
                        onClick = { hasPhoto = !hasPhoto },
                        shape = RoundedCornerShape(16.dp),
                        color = if (hasPhoto) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                if (hasPhoto) Icons.Default.CheckCircle else Icons.Default.AddAPhoto,
                                contentDescription = null,
                                tint = if (hasPhoto) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                if (hasPhoto) "Photo Memory Attached" else "Capture a Memory (Photo)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = if (hasPhoto) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Action Buttons
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Row(
                    modifier = Modifier.padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            val gratitudes = listOfNotNull(
                                gratitude1.ifBlank { null },
                                gratitude2.ifBlank { null }
                            )
                            val wins = listOfNotNull(win1.ifBlank { null })
                            val challenges = listOfNotNull(challenge1.ifBlank { null })
                            val tags = tagsInput.split(",").map { it.trim() }.filter { it.isNotBlank() }
                            val photos = if (hasPhoto) listOf("mock_photo_uri") else emptyList()
                            val linkedGoals = if (selectedGoalId != null) listOf(selectedGoalId!!) else emptyList()
                            
                            onAdd(
                                JournalEntry(
                                    date = Clock.System.now().toEpochMilliseconds(),
                                    title = title,
                                    content = content,
                                    mood = selectedMood,
                                    gratitude = gratitudes,
                                    achievements = wins,
                                    challenges = challenges,
                                    tags = tags,
                                    photos = photos,
                                    linkedGoalIds = linkedGoals
                                )
                            )
                        },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Save, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Entry")
                    }
                }
            }
        }
    }
}

