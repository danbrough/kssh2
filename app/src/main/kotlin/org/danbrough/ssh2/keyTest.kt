package org.danbrough.ssh2

import android.content.Context
import androidx.compose.ui.platform.LocalContext

private val log = androidLog
suspend fun keyTest(context: Context){

  log.info { "keyTest() info message context: $context" }


}