package org.danbrough.ssh2.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.danbrough.ssh2.OrderViewModel

@Composable
fun SongsScreen(viewModel: OrderViewModel, modifier: Modifier = Modifier) {
  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var lastAmount by viewModel.lastQuantity


    Column(modifier = Modifier.padding(16.dp)) {
      Text("Songs Screen")
      Text("Quantity: ${uiState.quantity}")
      Text("Price: ${uiState.price}")

      Button(onClick = {
        viewModel.setQuantity(lastAmount)
        lastAmount *= 2
      }) {
        Text("Set Quantity to '$lastAmount'")
      }
    }

  }
}