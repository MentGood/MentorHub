package com.example.aplicacionesmoviles.mentorhub.ui.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aplicacionesmoviles.R
import com.example.aplicacionesmoviles.mentorhub.ui.components.CampoTexto
import com.example.aplicacionesmoviles.mentorhub.ui.components.LogoMentor
import com.example.aplicacionesmoviles.mentorhub.ui.components.MensajeError

// ---------------------------------------------------------------
//  PANTALLA DE REGISTRO (INSERT en la tabla usuarios)
// ---------------------------------------------------------------
@Composable
fun RegistroScreen(
    onRegistroExitoso: () -> Unit,
    onVolver: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val estado by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(estado.exito) {
        if (estado.exito) {
            Toast.makeText(context, context.getString(R.string.mh_registro_exitoso), Toast.LENGTH_LONG).show()
            viewModel.limpiarExito()
            onRegistroExitoso()
        }
    }

    TarjetaAuth {
        LogoMentor(tamano = 60.dp, forma = RoundedCornerShape(20.dp))
        Spacer(Modifier.height(12.dp))
        Text(stringResource(R.string.mh_crear_cuenta), fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(
            stringResource(R.string.mh_registro_subtitulo),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(20.dp))

        CampoTexto(stringResource(R.string.mh_nombre_completo), estado.nombre, viewModel::onNombreChange, Icons.Outlined.Person)
        CampoTexto(stringResource(R.string.mh_matricula), estado.matricula, viewModel::onMatriculaChange, Icons.Outlined.Badge)
        CampoTexto(
            stringResource(R.string.mh_correo), estado.correo, viewModel::onCorreoChange,
            Icons.Outlined.Email, tipoTeclado = KeyboardType.Email
        )
        CampoTexto(
            stringResource(R.string.mh_contrasena), estado.contrasena, viewModel::onContrasenaChange,
            Icons.Outlined.Lock, esContrasena = true
        )
        CampoTexto(
            stringResource(R.string.mh_confirmar_contrasena), estado.confirmar, viewModel::onConfirmarChange,
            Icons.Outlined.Lock, esContrasena = true
        )

        MensajeError(estado.error)
        Spacer(Modifier.height(8.dp))

        Button(
            onClick = viewModel::registrar,
            enabled = !estado.cargando,
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(stringResource(R.string.mh_crear_cuenta), fontSize = 16.sp)
        }

        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onVolver) {
            Text(stringResource(R.string.mh_ir_a_login), color = MaterialTheme.colorScheme.primary)
        }
    }
}