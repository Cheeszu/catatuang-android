package com.catatuang.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.catatuang.app.ui.navigation.CatatUangApp
import com.catatuang.app.ui.theme.CatatUangTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            CatatUangTheme {
                CatatUangApp()
            }
        }
    }
}
