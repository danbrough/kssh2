package org.danbrough.ssh2.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.danbrough.ssh2.TestButtons


@Composable
fun PlaylistScreen(modifier: Modifier = Modifier) {
  Box(
    modifier = modifier.fillMaxSize().padding(24.dp),
    contentAlignment = Alignment.Center
  ) {
    Text("Playlist Screen")
    TestButtons()
  }
}
