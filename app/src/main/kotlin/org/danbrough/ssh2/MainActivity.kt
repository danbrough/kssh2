package org.danbrough.ssh2

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import io.github.danbrough.kssh2.messageTest
import kotlinx.coroutines.launch
import org.danbrough.klog.logger
import org.danbrough.ssh2.ui.theme.MyApplicationTheme

val androidLog = logger("SSH2")
private val log = androidLog

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
        NavigationBarExample()
      }
    }
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

          PasswordPrompt()
        }
      }
    }
  }
}

@Composable
fun PasswordPrompt() {
  var password by rememberSaveable { mutableStateOf("") }
  var passwordVisible by rememberSaveable { mutableStateOf(false) }

  TextField(
    value = password,
    onValueChange = { password = it },
    label = { Text("Password") },
    singleLine = true,
    placeholder = { Text("Password") },
    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
    trailingIcon = {
      val image = if (passwordVisible)
        Icons.Filled.Visibility
      else Icons.Filled.VisibilityOff

      // Please provide localized description for accessibility services
      val description = if (passwordVisible) "Hide password" else "Show password"

      IconButton(onClick = { passwordVisible = !passwordVisible }) {
        Icon(imageVector = image, description)
      }
    }
  )

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