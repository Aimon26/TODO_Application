package com.example.todoapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todoapplication.ui.theme.TODOApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TODOApplicationTheme {
                TODO() // Main TODO composable
            }
        }
    }
}
data class TodoItem(
    var id: Int,
    var title: String,
    var isCompleted: Boolean = false
)

fun getFakeTodo(): List<TodoItem> {
    return listOf(
        TodoItem(1, "Buy groceries", false),
        TodoItem(2, "Complete homework", false),
        TodoItem(3, "Call friend", true),
        TodoItem(4, "Go to gym", false)
    )
}

@Composable
fun ActionButton(
    text: String,
    backgroundColor: Color,
    icon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(150.dp)
            .height(45.dp)
            .background(backgroundColor, shape = RoundedCornerShape(12.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon?.let {
                it()
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun TODO() {
    val todoItems = remember { mutableStateListOf(*getFakeTodo().toTypedArray()) }
    var newTask by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(color = Color.Black)
            .padding(8.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "TODO Management App",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val keyboardController = LocalSoftwareKeyboardController.current
            val addTask = {
                if (newTask.isNotBlank()) {
                    val newId = if (todoItems.isEmpty()) 1 else todoItems.maxOf { it.id } + 1
                    todoItems.add(TodoItem(newId, newTask.trim(), false))
                    newTask = ""
                    keyboardController?.hide() // Hide keyboard after adding
                }
            }

            OutlinedTextField(
                value = newTask,
                onValueChange = { newTask = it },
                placeholder = { Text("Enter new task") },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        addTask()
                    }
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            ActionButton(
                text = "Add",
                backgroundColor = Color(0xFF2196F3),
                onClick = addTask
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            content = {
                itemsIndexed(todoItems) { index: Int, item: TodoItem ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (item.isCompleted)
                                    Color(0xFF2E7D32)
                                else
                                    MaterialTheme.colorScheme.primary
                            )
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Column(modifier = Modifier.weight(1f)) {

                            Text(
                                text = item.title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textDecoration = if (item.isCompleted)
                                    TextDecoration.LineThrough
                                else
                                    null
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Task Status: ${if (item.isCompleted) "Completed" else "Pending"}",
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            ActionButton(
                                text = if (item.isCompleted) "Undo" else "Complete",
                                backgroundColor = if (item.isCompleted)
                                    Color(0xFFFF9800)
                                else
                                    Color(0xFF4CAF50),
                                icon = {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "Toggle completion",
                                        tint = Color.White
                                    )
                                },
                                onClick = {
                                    todoItems[index] = item.copy(isCompleted = !item.isCompleted)
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            IconButton(
                                onClick = {
                                    todoItems.removeAt(index)
                                }
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete task",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}

@Preview()
@Composable
fun Preview() {
    TODOApplicationTheme {
        TODO()
    }
}