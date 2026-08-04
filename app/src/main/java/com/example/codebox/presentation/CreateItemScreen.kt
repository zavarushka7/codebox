package com.example.codebox.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.codebox.domain.Item

@Composable
fun CreateItemScreen(
    onSaveItem: (Item) -> Unit,
    viewModel: CreateItemViewModel = hiltViewModel()
) {

    Scaffold { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {

            TextField(
                value = viewModel.name.value,
                onValueChange = { viewModel.name.value = it },
                label = { Text("Название") },
                modifier = Modifier.padding(bottom = 8.dp)
            )

            TextField(
                value = viewModel.ratingText.value,
                onValueChange = { viewModel.ratingText.value = it },
                label = { Text("Рейтинг (1-5)") },
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Button(onClick = {
                val newItem = viewModel.createItem()
                onSaveItem(newItem)
            }) {
                Text("Сохранить")
            }
        }
    }
}

