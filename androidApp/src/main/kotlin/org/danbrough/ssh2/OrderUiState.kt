package org.danbrough.ssh2

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class OrderUiState(val quantity: Int = 0, val price: String = "$0.00")

class OrderViewModel : ViewModel() {

  init {
    androidLog.info { "OrderViewModel::init" }
  }

  val uiState: StateFlow<OrderUiState>
    field = MutableStateFlow(OrderUiState())


  val lastQuantity = mutableIntStateOf(2)

  fun setQuantity(n: Int) {
    uiState.update { it.copy(quantity = n, price = "$${n * 2}.00") }
  }
}