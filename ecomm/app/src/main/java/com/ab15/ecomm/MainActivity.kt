package com.ab15.ecomm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ab15.ecomm.ui.HomeScreen
import com.ab15.ecomm.ui.theme.EcommTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EcommTheme {
                HomeScreen()
            }
        }
    }
}
