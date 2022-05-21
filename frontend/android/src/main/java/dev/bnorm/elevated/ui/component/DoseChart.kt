//package dev.bnorm.elevated.ui.component
//
//import android.util.Log
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material.Button
//import androidx.compose.material.OutlinedTextField
//import androidx.compose.material.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import ElevatedClient
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import java.io.IOException
//
//@Composable
//fun DoseChartButton(
//    hydroService: ElevatedClient,
//    modifier: Modifier = Modifier,
//) {
//    var chart by remember { mutableStateOf("Grow") }
//    var week by remember { mutableStateOf(1L) }
//    val scope = rememberCoroutineScope { Dispatchers.IO }
//
//    Row(modifier = modifier) {
//        Column(
//            modifier = Modifier
//                .align(Alignment.CenterVertically)
//        ) {
//            OutlinedTextField(
//                value = chart,
//                onValueChange = { chart = it },
//                label = { Text("Chart") },
//                keyboardOptions = KeyboardOptions(autoCorrect = false)
//            )
//            LongInputField(
//                value = week,
//                onValueChange = { week = it },
//                label = { Text("Week") },
//            )
//        }
//        Column(
//            modifier = Modifier
//                .weight(1.0f)
//                .align(Alignment.CenterVertically)
//        ) {
//            Button(
//                modifier = Modifier
//                    .align(Alignment.CenterHorizontally),
//                onClick = {
//                    scope.launch {
//                        try {
//                            Log.i("IO", "Dose $chart $week")
//                            hydroService.doseChart(chart, week)
//                        } catch (e: IOException) {
//                            Log.w("IO", "Error dosing $chart $week", e)
//                        }
//                    }
//                },
//            ) {
//                Text("Dose")
//            }
//        }
//    }
//}
