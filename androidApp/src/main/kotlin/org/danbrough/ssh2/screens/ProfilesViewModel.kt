package org.danbrough.ssh2.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import org.danbrough.ssh2.androidLog

data class ProfileUiState(
  val name: String = "",
  val userName: String = "",
  val hostname: String = "",
  val port: String = "22",
  val password: String = ""
)

class ProfilesViewModel : ViewModel() {
  var uiState by mutableStateOf(ProfileUiState())
    private set

  fun updateName(name: String) {
    uiState = uiState.copy(name = name)
  }

  fun updateUsername(userName: String) {
    uiState = uiState.copy(userName = userName)
  }


  fun updateHostname(hostname: String) {
    uiState = uiState.copy(hostname = hostname)
  }

  fun updatePort(port: String) {
    uiState = uiState.copy(port = port)
  }

  fun updatePassword(password: String) {
    uiState = uiState.copy(password = password)
  }

  fun saveProfile() {
    // Process and store the profile state safely here
    androidLog.info {  "Saving profile: ${uiState.name} @ ${uiState.hostname}:${uiState.port}" }
  }
}
