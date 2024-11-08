package com.example.todoapp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import com.example.todoapp.ui.theme.TodoAppTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoAppTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
                ) {
                    MainPage()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true)
@Composable
fun MainPage() {
    val todoName = remember { mutableStateOf("") }
    val myContext = LocalContext.current
    val itemList = readData(myContext)
    val focusManager = LocalFocusManager.current
    val isFocused = remember { mutableStateOf(false) }

    val deleteDialogStatus = remember { mutableStateOf(false) }
    val clickedItemIndex = remember { mutableStateOf(0) }
    val updateDialogStatus = remember { mutableStateOf(false) }
    val clickedItem = remember { mutableStateOf("") }
    val textDialogStatus = remember { mutableStateOf(false) }
    val sortClicked = remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Heading with Sort Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Heading
            Text(
                text = "My To-Do List",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp),
                fontFamily = FontFamily(Font(R.font.montserrat_extrabolditalic))
            )

            IconButton(onClick = {
                itemList.reverse()
                sortClicked.value = !sortClicked.value;
            }) {
                Icon(
                    imageVector = if (sortClicked.value) Icons.Rounded.KeyboardArrowDown else Icons.Rounded.KeyboardArrowUp,
                    contentDescription = "sort the task",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        // Input Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = todoName.value,
                onValueChange = { todoName.value = it },
                maxLines = 4,
                label = {
                    Text(
                        text = "What do you need to do?",
                        color = Color.Gray,
                        fontFamily = FontFamily(Font(R.font.montserrat_bold))
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = TextStyle(fontFamily = FontFamily(Font(R.font.montserrat_regular))),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
                    .onFocusChanged { focusState ->
                        isFocused.value = focusState.isFocused
                        // Reset the input field if it loses focus and is empty
                        if (!focusState.isFocused && todoName.value.isEmpty()) {
                            todoName.value = ""
                        }
                    }

            )

            // Add Button
            IconButton(
                onClick = {
                    if (todoName.value.isNotEmpty()) {
                        itemList.add(todoName.value)
                        writeData(itemList, myContext)
                        todoName.value = ""
                        focusManager.clearFocus()
                    } else {
                        showCustomToast(myContext, "Please write a To-Do")
                    }
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add TODO",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        // Task List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(count = itemList.size) { index ->
                val item = itemList[index]

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            textDialogStatus.value = true
                            clickedItem.value = item
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Task Text
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            fontSize = 16.sp,
                            overflow = TextOverflow.Ellipsis,
                            fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        )

                        // Edit and Delete Buttons
                        Row {
                            IconButton(onClick = {
                                updateDialogStatus.value = true
                                clickedItemIndex.value = index
                                clickedItem.value = item
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = "Edit",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            IconButton(onClick = {
                                deleteDialogStatus.value = true
                                clickedItemIndex.value = index
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }

        // Delete Dialog
        if (deleteDialogStatus.value) {
            AlertDialog(
                onDismissRequest = { deleteDialogStatus.value = false },
                title = {
                    Text(
                        text = "Delete this Task..?",
                        fontFamily = FontFamily(Font(R.font.montserrat_bold))
                    )
                },
                text = {
                    Text(
                        text = clickedItem.value,
                        fontFamily = FontFamily(Font(R.font.montserrat_regular))
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        itemList.removeAt(clickedItemIndex.value)
                        writeData(itemList, myContext)
                        deleteDialogStatus.value = false
                        showCustomToast(myContext, "Task deleted")
                    }) {
                        Text(
                            text = "Yes",
                            color = MaterialTheme.colorScheme.error,
                            fontFamily = FontFamily(Font(R.font.montserrat_bold))
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { deleteDialogStatus.value = false }) {
                        Text(
                            text = "No",
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = FontFamily(Font(R.font.montserrat_bold))
                        )
                    }
                }
            )
        }

        // Update Dialog
        if (updateDialogStatus.value) {
            AlertDialog(
                onDismissRequest = { updateDialogStatus.value = false },
                title = {
                    Text(
                        text = "Update Task",
                        fontFamily = FontFamily(Font(R.font.montserrat_bold))
                    )
                },
                text = {
                    TextField(
                        value = clickedItem.value,
                        onValueChange = { clickedItem.value = it },
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        textStyle = TextStyle(fontFamily = FontFamily(Font(R.font.montserrat_regular)))
                    )

                },
                confirmButton = {
                    TextButton(onClick = {
                        itemList[clickedItemIndex.value] = clickedItem.value
                        writeData(itemList, myContext)
                        updateDialogStatus.value = false
                        showCustomToast(myContext, "Task updated")
                    }) {
                        Text(
                            text = "Change",
                            fontFamily = FontFamily(Font(R.font.montserrat_bold)),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { updateDialogStatus.value = false }) {
                        Text(
                            text = "No",
                            fontFamily = FontFamily(Font(R.font.montserrat_bold)),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }

        // Text Dialog
        if (textDialogStatus.value) {
            AlertDialog(
                onDismissRequest = { textDialogStatus.value = false },
                title = {
                    Text(
                        text = "Gotta do it, buddy",
                        fontFamily = FontFamily(Font(R.font.montserrat_bold))
                    )
                },
                text = {
                    Text(
                        text = clickedItem.value,
                        fontFamily = FontFamily(Font(R.font.montserrat_regular))
                    )
                },
                confirmButton = {
                    TextButton(onClick = { textDialogStatus.value = false }) {
                        Text(
                            text = "Okay",
                            fontFamily = FontFamily(Font(R.font.montserrat_bold)),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    }
}

// Function to show a custom toast message
fun showCustomToast(context: Context, message: String) {
    val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
    val textView = TextView(context).apply {
        text = message
        textSize = 18f
        setTextColor(Color.White.toArgb())
        typeface = ResourcesCompat.getFont(context, R.font.montserrat_regular)
        setPadding(24, 16, 24, 16)
        background = context.getDrawable(R.drawable.toast_background)
    }

    toast.view = textView
    toast.show()
}
