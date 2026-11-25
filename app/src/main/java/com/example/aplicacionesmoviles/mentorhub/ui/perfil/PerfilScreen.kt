package com.example.aplicacionesmoviles.mentorhub.ui.perfil

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aplicacionesmoviles.R
import com.example.aplicacionesmoviles.mentorhub.ui.components.CampoTexto
import com.example.aplicacionesmoviles.mentorhub.ui.components.DialogoConfirmar
import com.example.aplicacionesmoviles.mentorhub.ui.components.MensajeError
import com.example.aplicacionesmoviles.mentorhub.ui.components.MostrarMensaje
import com.example.aplicacionesmoviles.mentorhub.ui.components.TarjetaBlanca

// ---------------------------------------------------------------
//  PANTALLA PERFIL: editar datos (UPDATE), eliminar cuenta (DELETE)
//  y cerrar sesión
// ---------------------------------------------------------------
@Composable
fun PerfilScreen(
    onSesionCerrada: () -> Unit,
    viewModel: PerfilViewModel = hiltViewModel()
) {
    val estado by viewModel.uiState.collectAsState()
    var confirmarEliminar by remember { mutableStateOf(false) }

    MostrarMensaje(estado.mensaje, viewModel::limpiarMensaje)

    LaunchedEffect(estado.sesionCerrada) {
        if (estado.sesionCerrada) onSesionCerrada()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // painterResource: carga una imagen de la carpeta res/drawable
        Image(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = stringResource(R.string.mh_foto_perfil),
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )
        Spacer(Modifier.height(8.dp))
        Text(estado.nombre, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(
            stringResource(R.string.mh_matricula_valor, viewModel.matricula),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))

        TarjetaBlanca {
            Text(stringResource(R.string.mh_editar_datos), fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Spacer(Modifier.height(10.dp))
            CampoTexto(stringResource(R.string.mh_nombre_completo), estado.nombre, viewModel::onNombreChange, Icons.Outlined.Person)
            CampoTexto(
                stringResource(R.string.mh_correo), estado.correo, viewModel::onCorreoChange,
                Icons.Outlined.Email, tipoTeclado = KeyboardType.Email
            )
            MensajeError(estado.error)
            Button(
                onClick = viewModel::guardarCambios,
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.mh_guardar_cambios))
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = viewModel::cerrarSesion,
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Outlined.Logout, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.mh_cerrar_sesion))
        }
        TextButton(onClick = { confirmarEliminar = true }) {
            Text(stringResource(R.string.mh_eliminar_cuenta), color = MaterialTheme.colorScheme.error)
        }
    }

    if (confirmarEliminar) {
        DialogoConfirmar(
            titulo = stringResource(R.string.mh_eliminar_cuenta),
            texto = stringResource(R.string.mh_confirmar_eliminar_cuenta),
            onConfirmar = {
                confirmarEliminar = false
                viewModel.eliminarCuenta()
            },
            onCancelar = { confirmarEliminar = false }
        )
    }
}