package com.example.uimodule

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.uimodule.ui.theme.UIModuleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UIModuleTheme {
                ChatScreen()
            }
        }
    }
}

@Composable
fun ChatScreen(initialPrepared: Boolean = false) {
    var isPrepared by remember { mutableStateOf(initialPrepared) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (!isPrepared) {
            ChatManager.prepareIfNeeded(context)
            isPrepared = true
        }
    }


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isPrepared) {
            Button(onClick = {
                val activity = context as? Activity
                if (activity != null) {
                    ChatManager.startChat(activity)
                } else {
                    println("No activity to start chat from.")
                }
            }) {
                Text(
                    text = "Start Chat"
                )

            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Text(
                    text = "Preparing chat..."
                )
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatPreview() {
    UIModuleTheme {
        ChatScreen()
    }
}