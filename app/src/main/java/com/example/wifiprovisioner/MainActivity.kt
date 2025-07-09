package com.example.wifiprovisioner

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.ScanResult as WifiScanResult
import android.net.wifi.WifiManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    private lateinit var wifiManager: WifiManager
    private var wifiScanResults by mutableStateOf(listOf<WifiScanResult>())
    private var selectedSSID by mutableStateOf("")
    private var wifiPassword by mutableStateOf("")
    private var scanInProgress by mutableStateOf(false)
    private var statusMessage by mutableStateOf("")

    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT
    )

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perms ->
            if (perms.all { it.value }) {
                startWifiScan()
            } else {
                statusMessage = "Permissions required for Wi-Fi scan and BLE."
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        setContent {
            MaterialTheme {
                WifiProvisionerUI()
            }
        }
        // Request permissions on launch
        requestPermissionsIfNeeded()
    }

    private fun requestPermissionsIfNeeded() {
        val missing = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (missing.isNotEmpty()) {
            requestPermissionsLauncher.launch(missing.toTypedArray())
        } else {
            startWifiScan()
        }
    }

    private fun startWifiScan() {
        scanInProgress = true
        statusMessage = "Scanning for Wi-Fi..."
        val success = wifiManager.startScan()
        if (!success) {
            statusMessage = "Wi-Fi scan failed."
            scanInProgress = false
        } else {
            // Wait for scan results
            // In a real app, register a BroadcastReceiver for WifiManager.SCAN_RESULTS_AVAILABLE_ACTION
            // For simplicity, poll after a short delay
            window.decorView.postDelayed({
                wifiScanResults = wifiManager.scanResults
                scanInProgress = false
                statusMessage = if (wifiScanResults.isEmpty()) "No networks found." else "Select a network."
            }, 2000)
        }
    }

    // Dummy BLE send function (replace with real BLE logic for your device)
    private fun sendCredentialsOverBLE(ssid: String, password: String, onResult: (Boolean) -> Unit) {
        // TODO: Implement BLE connection and write logic
        // For demo, simulate success after delay
        statusMessage = "Sending credentials over BLE..."
        window.decorView.postDelayed({
            onResult(true) // Simulate success
        }, 2000)
    }

    @Composable
    fun WifiProvisionerUI() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White) // Arka planı düz beyaz yap
                .padding(16.dp)
        ) {
            Text(
                "Wi-Fi Provisioning",
                style = MaterialTheme.typography.h6,
                color = Color.Black // Metin rengi siyah
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (scanInProgress) {
                CircularProgressIndicator()
            } else {
                Button(onClick = { startWifiScan() }) {
                    Text("Scan Wi-Fi", color = Color.Black)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(modifier = Modifier.height(150.dp)) {
                items(wifiScanResults.size) { idx ->
                    val ssid = wifiScanResults[idx].SSID
                    if (ssid.isNotBlank()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                        ) {
                            RadioButton(
                                selected = (selectedSSID == ssid),
                                onClick = { selectedSSID = ssid },
                                colors = RadioButtonDefaults.colors(selectedColor = Color.Black)
                            )
                            Text(ssid, modifier = Modifier.padding(start = 8.dp), color = Color.Black)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = wifiPassword,
                onValueChange = { wifiPassword = it },
                label = { Text("Wi-Fi Password", color = Color.Black) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                textStyle = LocalTextStyle.current.copy(color = Color.Black)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    if (selectedSSID.isBlank() || wifiPassword.isBlank()) {
                        statusMessage = "Select SSID and enter password."
                    } else {
                        sendCredentialsOverBLE(selectedSSID, wifiPassword) { success ->
                            statusMessage = if (success) "Provisioning successful!" else "Provisioning failed."
                        }
                    }
                },
                enabled = selectedSSID.isNotBlank() && wifiPassword.isNotBlank()
            ) {
                Text("Connect", color = Color.Black)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(statusMessage, color = Color.Black)
        }
    }
}