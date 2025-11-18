package com.liangyu.wealthtrackplus.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@Composable
fun UserProfileRoute(
    navController: NavHostController,
    initialNickname: String,
    viewModel: UserProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(initialNickname) {
        viewModel.updateDisplayNameFromNav(initialNickname)
    }

    UserProfileScreen(
        uiState = uiState,
        onBackClick = { navController.popBackStack() },
        onChangeAvatar = viewModel::cycleAvatarBackground,
        onUpdateDisplayName = viewModel::updateDisplayName
    )
}

@Composable
fun UserProfileScreen(
    uiState: UserProfileUiState,
    onBackClick: () -> Unit,
    onChangeAvatar: () -> Unit,
    onUpdateDisplayName: (String) -> Unit,
    modifier: Modifier = Modifier,
    onFieldClick: (ProfileField) -> Unit = {}
) {
    val topAppBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.primary,
        titleContentColor = MaterialTheme.colorScheme.onPrimary,
        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "使用者資訊", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = topAppBarColors
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            AvatarChanger(
                background = uiState.avatarBackground,
                onChangeAvatar = onChangeAvatar
            )
            EditableDisplayName(
                name = uiState.displayName,
                onUpdateDisplayName = onUpdateDisplayName
            )
            ProfileDetailsCard(fields = uiState.fields, onFieldClick = onFieldClick)
        }
    }
}

@Composable
private fun AvatarChanger(
    background: Color,
    onChangeAvatar: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Column(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(background)
                .clickable(onClick = onChangeAvatar),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.CameraAlt,
                contentDescription = "變更照片",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "輕觸變更照片",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EditableDisplayName(
    name: String,
    onUpdateDisplayName: (String) -> Unit
) {
    var isEditing by rememberSaveable { mutableStateOf(false) }
    var pendingName by rememberSaveable { mutableStateOf(name) }

    LaunchedEffect(name) {
        if (!isEditing) {
            pendingName = name
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (isEditing) {
            OutlinedTextField(
                value = pendingName,
                onValueChange = { pendingName = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                singleLine = true
            )
            IconButton(
                onClick = {
                    if (pendingName.isNotBlank()) {
                        onUpdateDisplayName(pendingName)
                        isEditing = false
                    }
                }
            ) {
                Icon(imageVector = Icons.Rounded.Check, contentDescription = "儲存名稱")
            }
        } else {
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(end = 8.dp)
            )
            IconButton(onClick = { isEditing = true }) {
                Icon(imageVector = Icons.Rounded.Edit, contentDescription = "編輯暱稱")
            }
        }
    }
}

@Composable
private fun ProfileDetailsCard(
    fields: List<ProfileField>,
    onFieldClick: (ProfileField) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
            fields.forEachIndexed { index, field ->
                ProfileDetailRow(field = field, onFieldClick = onFieldClick)
                if (index != fields.lastIndex) {
                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}

@Composable
private fun ProfileDetailRow(
    field: ProfileField,
    onFieldClick: (ProfileField) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onFieldClick(field) }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = field.title, style = MaterialTheme.typography.bodyMedium)
            field.supportingText?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = field.value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 8.dp)
            )
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UserProfileScreenPreview() {
    val viewModel = UserProfileViewModel()
    UserProfileScreen(
        uiState = viewModel.uiState.value,
        onBackClick = {},
        onChangeAvatar = {},
        onUpdateDisplayName = {},
    )
}
