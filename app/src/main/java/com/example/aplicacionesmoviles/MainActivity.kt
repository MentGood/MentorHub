package com.example.aplicacionesmoviles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.aplicacionesmoviles.mentorhub.MentorHubTheme
import com.example.aplicacionesmoviles.mentorhub.ui.navigation.MentorHubNavHost
import dagger.hilt.android.AndroidEntryPoint

// ---------------------------------------------------------------
//  PUNTO DE ENTRADA de la app (se declara en AndroidManifest.xml)
//  @AndroidEntryPoint -> permite que Hilt inyecte dependencias aquí
//  y en los ViewModels de las pantallas (hiltViewModel()).
// ---------------------------------------------------------------
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MentorHubTheme {
                MentorHubNavHost()
            }
        }
    }
}