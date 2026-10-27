package com.linkitsoft.notyet.Activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.linkitsoft.notyet.data.AppDatabase
import com.linkitsoft.notyet.data.DocumentItem
import com.linkitsoft.notyet.notifications.ReminderScheduler
import com.linkitsoft.notyet.ui.screens.AddEditDocumentScreen
import com.linkitsoft.notyet.ui.screens.HomeScreen
import com.linkitsoft.notyet.ui.screens.SettingsScreen
import com.linkitsoft.notyet.ui.theme.NotYetTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        val db = AppDatabase.getDatabase(this)
        val dao = db.documentDao()

        setContent {
            NotYetTheme {
                val navController = rememberNavController()
                val coroutineScope = rememberCoroutineScope()
                val documents by dao.getAllDocumentsSortedByExpiry().collectAsStateWithLifecycle(initialValue = emptyList())

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(
                            documents = documents,
                            onAddClick = { navController.navigate("add_edit") },
                            onEditClick = { itemId -> navController.navigate("add_edit?itemId=$itemId") },
                            onDeleteClick = { itemId ->
                                coroutineScope.launch(Dispatchers.IO) {
                                    dao.deleteDocumentById(itemId)
                                    ReminderScheduler.cancelRemindersForItem(applicationContext, itemId)
                                }
                            },
                            onRenewConfirm = { item, newExpiryEpochDay ->
                                coroutineScope.launch(Dispatchers.IO) {
                                    val updatedItem = item.copy(expiryEpochDay = newExpiryEpochDay)
                                    dao.updateDocument(updatedItem)
                                    ReminderScheduler.scheduleRemindersForItem(applicationContext, updatedItem)
                                }
                            },
                            onSettingsClick = { navController.navigate("settings") }
                        )
                    }

                    composable("settings") {
                        SettingsScreen(
                            onBackClick = { navController.popBackStack() },
                            onRequestPermissionClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            }
                        )
                    }

                    composable(
                        route = "add_edit?itemId={itemId}",
                        arguments = listOf(
                            navArgument("itemId") {
                                type = NavType.LongType
                                defaultValue = -1L
                            }
                        )
                    ) { backStackEntry ->
                        val itemId = backStackEntry.arguments?.getLong("itemId") ?: -1L
                        val existingItem = documents.find { it.id == itemId }

                        AddEditDocumentScreen(
                            existingItem = existingItem,
                            onBackClick = { navController.popBackStack() },
                            onSaveClick = { title, category, expiryEpochDay, reminderDays, notes ->
                                coroutineScope.launch(Dispatchers.IO) {
                                    val item = DocumentItem(
                                        id = if (itemId > 0) itemId else 0,
                                        title = title,
                                        category = category,
                                        expiryEpochDay = expiryEpochDay,
                                        reminderDays = reminderDays,
                                        notes = notes
                                    )
                                    val savedId = if (itemId > 0) {
                                        dao.updateDocument(item)
                                        itemId
                                    } else {
                                        dao.insertDocument(item)
                                    }
                                    val finalItem = item.copy(id = savedId)
                                    ReminderScheduler.scheduleRemindersForItem(applicationContext, finalItem)
                                }
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
