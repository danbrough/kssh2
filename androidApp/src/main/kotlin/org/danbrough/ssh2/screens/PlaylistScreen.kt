package org.danbrough.ssh2.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.danbrough.kssh2.messageTest
import kotlinx.coroutines.launch
import org.danbrough.ssh2.Greeting
import org.danbrough.ssh2.PasswordPrompt
import org.danbrough.ssh2.androidLog

private val log = androidLog

@Composable
fun PlaylistScreen(modifier: Modifier = Modifier) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .padding(24.dp),
    contentAlignment = Alignment.Center
  ) {
    Text("Playlist Screen")
    TestButtons()
  }
}

@Composable
fun TestButtons() {
  val scope = rememberCoroutineScope()
  val context = LocalContext.current
  Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

      Surface(modifier = Modifier.background(Color.Blue)) {
        Column {
          Greeting(
            name = "Android",
            modifier = Modifier.padding(innerPadding)
          )

          Button({ scope.launch { messageTest() } }) {
            Text("Test")
          }

          Button({ scope.launch { keyTest(context) } }) {
            Text("Key Test")
          }

          Button({ scope.launch { dbTest(context) } }) {
            Text("DB Test")
          }
          PasswordPrompt()
        }
      }
    }
  }
}


suspend fun keyTest(context: Context) {
  log.info { "keyTest() info message context: $context" }
}

suspend fun dbTest(context: Context) {
  log.info { "dbTest() info message context: $context" }
}