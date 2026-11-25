package com.example.aplicacionesmoviles.mentorhub.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aplicacionesmoviles.R
import com.example.aplicacionesmoviles.mentorhub.ui.components.CampoTexto
import com.example.aplicacionesmoviles.mentorhub.ui.components.LogoMentor
import com.example.aplicacionesmoviles.mentorhub.ui.components.MensajeError

// ---------------------------------------------------------------
//  PANTALLA 1 — INICIO DE SESIÓN
//  Observa el estado del ViewModel (collectAsState = PATRÓN OBSERVER)
//  y le manda los eventos (UDF).
// ---------------------------------------------------------------
@Composable
fun LoginScreen(
    onLoginExitoso: () -> Unit,
    onIrARegistro: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val estado by viewModel.uiState.collectAsState()

    // Cuando el login es correcto, navega a la pantalla principal
    LaunchedEffect(estado.exito) {
        if (estado.exito) {
            viewModel.limpiarExito()
            onLoginExitoso()
        }
    }

    TarjetaAuth {
        LogoMentor(tamano = 76.dp, forma = RoundedCornerShape(24.dp))
        Spacer(Modifier.height(16.dp))
        Text(stringResource(R.string.mh_nombre_app), fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text(
            stringResource(R.string.mh_subtitulo),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))

        CampoTexto(
            etiqueta = stringResource(R.string.mh_matricula),
            valor = estado.matricula,
            onCambio = viewModel::onMatriculaChange,
            icono = Icons.Outlined.Badge
        )
        CampoTexto(
            etiqueta = stringResource(R.string.mh_contrasena),
            valor = estado.contrasena,
            onCambio = viewModel::onContrasenaChange,
            icono = Icons.Outlined.Lock,
            esContrasena = true
        )

        MensajeError(estado.error)
        Spacer(Modifier.height(8.dp))

        Button(
            onClick = viewModel::iniciarSesion,
            enabled = !estado.cargando,
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            if (estado.cargando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(stringResource(R.string.mh_iniciar_sesion), fontSize = 16.sp)
            }
        }

        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onIrARegistro) {
            Text(stringResource(R.string.mh_ir_a_registro), color = MaterialTheme.colorScheme.primary)
        }
    }
}

/** Fondo gris + tarjeta blanca centrada (se usa en login y registro) */
@Composable
fun TarjetaAuth(contenido: @Composable ColumnScope.() -> Unit) {
    val colores = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colores.background)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = colores.surface),
            border = BorderStroke(1.dp, colores.outlineVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = contenido
            )
        }
    }
}