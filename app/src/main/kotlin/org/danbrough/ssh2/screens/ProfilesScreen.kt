package org.danbrough.ssh2.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun ProfilesScreen(viewModel: ProfilesViewModel, modifier: Modifier = Modifier) {
  val uiState = viewModel.uiState
  var passwordVisible by rememberSaveable { mutableStateOf(false) }

  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.TopCenter
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp)
        .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Text("Create Profile")

      TextField(
        value = uiState.name,
        onValueChange = { viewModel.updateName(it) },
        label = { Text("Name") },
        singleLine = true,
        placeholder = { Text("e.g. My Server") },
        keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Text,
          imeAction = ImeAction.Next
        ),
        modifier = Modifier.fillMaxWidth()
      )

      TextField(
        value = uiState.hostname,
        onValueChange = { viewModel.updateHostname(it) },
        label = { Text("Hostname / IP Address") },
        singleLine = true,
        placeholder = { Text("e.g. 192.168.1.100") },
        keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Uri,
          imeAction = ImeAction.Next
        ),
        modifier = Modifier.fillMaxWidth()
      )

      TextField(
        value = uiState.port,
        onValueChange = { viewModel.updatePort(it) },
        label = { Text("Port") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Number,
          imeAction = ImeAction.Next
        ),
        modifier = Modifier.fillMaxWidth()
      )

      TextField(
        value = uiState.password,
        onValueChange = { viewModel.updatePassword(it) },
        label = { Text("Password") },
        singleLine = true,
        placeholder = { Text("Enter password") },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Password,
          imeAction = ImeAction.Done
        ),
        trailingIcon = {
          val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
          val description = if (passwordVisible) "Hide password" else "Show password"

          IconButton(onClick = { passwordVisible = !passwordVisible }) {
            Icon(imageVector = image, contentDescription = description)
          }
        },
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(8.dp))

      Button(
        onClick = { viewModel.saveProfile() },
        modifier = Modifier.fillMaxWidth()
      ) {
        Text("Save Profile")
      }
    }
  }
}