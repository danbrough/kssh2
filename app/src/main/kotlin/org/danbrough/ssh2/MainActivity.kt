package org.danbrough.ssh2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.tooling.preview.Preview
import io.github.danbrough.kssh2.messageTest
import kotlinx.coroutines.launch
import org.danbrough.klog.logger
import org.danbrough.ssh2.ui.theme.MyApplicationTheme

val log = logger("SSH2")

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    log.trace { "init() trace" }
    log.debug { "init() debug" }
    log.info { "init() info" }
    log.warn { "init() warn" }
    log.error { "init() error!!" }
    setContent {
      MyApplicationTheme {
        val scope = rememberCoroutineScope()
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
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(
    text = "Hello $name!",
    modifier = modifier
  )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  MyApplicationTheme {
    Greeting("Android")
  }
}