package com.example.passwordmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.passwordmanager.ui.theme.PasswordManagerTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PasswordManagerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: PasswordViewModel = viewModel()
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedEntry by remember { mutableStateOf<PasswordEntry?>(null) }
    var showDetailsDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val passwordList by viewModel.passwords.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(passwordList) { entry ->
                Card(
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = entry.accountType,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "******",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Details",
                            tint = Color.LightGray,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clickable {
                                    selectedEntry = entry
                                    showDetailsDialog = true
                                }
                        )
                    }
                }
            }
        }

        // Floating Action Button at bottom right
        androidx.compose.material3.FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = Color(0xFF2196F3),
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add Password"
            )
        }

        if (showAddDialog) {
            AddPasswordDialog(
                onAdd = { newEntry ->
                    viewModel.addPassword(newEntry)
                },
                onDismiss = { showAddDialog = false }
            )
        }

        if (showDetailsDialog && selectedEntry != null) {
            PasswordDetailsBottomSheet(
                entry = selectedEntry!!,
                onEdit = { updatedEntry ->
                    viewModel.updatePassword(updatedEntry.copy(id = selectedEntry!!.id))
                    selectedEntry = updatedEntry.copy(id = selectedEntry!!.id)
                },
                onDelete = {
                    viewModel.deletePassword(selectedEntry!!)
                    showDetailsDialog = false
                    selectedEntry = null
                },
                onDismiss = {
                    showDetailsDialog = false
                    selectedEntry = null
                },
                sheetState = sheetState
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    PasswordManagerTheme {
        MainScreen()
    }
}

@Composable
fun AddPasswordDialog(
    onAdd: (PasswordEntry) -> Unit,
    onDismiss: () -> Unit
) {
    var accountType by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = accountType,
                    onValueChange = { accountType = it },
                    label = { Text("Account Name") },
                    singleLine = true
                )
                Spacer(Modifier.height(12.dp))
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username/Email") },
                    singleLine = true
                )
                Spacer(Modifier.height(12.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = {
                        onAdd(PasswordEntry(accountType = accountType, username = username, password = password))
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add New Account")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordDetailsBottomSheet(
    entry: PasswordEntry,
    onEdit: (PasswordEntry) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState
) {
    var isEditing by remember { mutableStateOf(false) }
    var accountType by remember { mutableStateOf(entry.accountType) }
    var username by remember { mutableStateOf(entry.username) }
    var password by remember { mutableStateOf(entry.password) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Account Details",
                color = Color(0xFF2196F3),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(16.dp))
            if (isEditing) {
                TextField(
                    value = accountType,
                    onValueChange = { accountType = it },
                    label = { Text("Account Type") },
                    singleLine = true
                )
                Spacer(Modifier.height(12.dp))
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username/Email") },
                    singleLine = true
                )
                Spacer(Modifier.height(12.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
            } else {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                    Text("Account Type", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    Text(accountType, style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(8.dp))
                    Text("Username/ Email", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    Text(username, style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(8.dp))
                    Text("Password", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("******", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        if (isEditing) {
                            onEdit(PasswordEntry(id = entry.id, accountType = accountType, username = username, password = password))
                            isEditing = false
                        } else {
                            isEditing = true
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isEditing) Color(0xFF2196F3) else Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Text(if (isEditing) "Save" else "Edit")
                }
                Spacer(Modifier.width(16.dp))
                Button(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF44336),
                        contentColor = Color.White
                    )
                ) {
                    Text("Delete")
                }
            }
        }
    }
}