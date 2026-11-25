package com.example.aplicacionesmoviles.mentorhub.ui.inicio

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aplicacionesmoviles.R
import com.example.aplicacionesmoviles.mentorhub.data.local.InscripcionDetalle
import com.example.aplicacionesmoviles.mentorhub.data.local.InscripcionEntity
import com.example.aplicacionesmoviles.mentorhub.ui.components.DialogoConfirmar
import com.example.aplicacionesmoviles.mentorhub.ui.components.FilaDato
import com.example.aplicacionesmoviles.mentorhub.ui.components.ListaVacia
import com.example.aplicacionesmoviles.mentorhub.ui.components.MostrarMensaje
import com.example.aplicacionesmoviles.mentorhub.ui.components.TituloSeccion

// ---------------------------------------------------------------
//  PANTALLA 2 — INICIO DEL ESTUDIANTE: sus asesorías inscritas
// ---------------------------------------------------------------
@Composable
fun InicioScreen(viewModel: InicioViewModel = hiltViewModel()) {
    val usuario by viewModel.usuario.collectAsState()
    val inscripciones by viewModel.inscripciones.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    var porCancelar by remember { mutableStateOf<InscripcionDetalle?>(null) }

    MostrarMensaje(mensaje, viewModel::limpiarMensaje)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                stringResource(R.string.mh_hola, usuario?.nombre ?: ""),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // ----- Próxima asesoría (la más reciente) -----
        val proxima = inscripciones.firstOrNull { it.estado == InscripcionEntity.ESTADO_INSCRITO }
        item { TituloSeccion(Icons.Outlined.EventAvailable, stringResource(R.string.mh_proxima_asesoria)) }
        item {
            if (proxima == null) ListaVacia(stringResource(R.string.mh_sin_proxima))
            else TarjetaProxima(proxima)
        }

        // ----- Todas mis asesorías -----
        item {
            Spacer(Modifier.height(4.dp))
            TituloSeccion(Icons.Outlined.BookmarkAdded, stringResource(R.string.mh_mis_asesorias))
        }
        if (inscripciones.isEmpty()) {
            item { ListaVacia(stringResource(R.string.mh_sin_inscripciones)) }
        }
        items(inscripciones, key = { it.idInscripcion }) { inscripcion ->
            TarjetaInscripcion(
                inscripcion = inscripcion,
                onAsistencia = { viewModel.marcarAsistencia(inscripcion.idInscripcion) },
                onCancelar = { porCancelar = inscripcion }
            )
        }
    }

    porCancelar?.let { inscripcion ->
        DialogoConfirmar(
            titulo = stringResource(R.string.mh_cancelar_inscripcion),
            texto = stringResource(R.string.mh_confirmar_cancelar, inscripcion.nombreMateria),
            onConfirmar = {
                viewModel.cancelar(inscripcion.idInscripcion)
                porCancelar = null
            },
            onCancelar = { porCancelar = null }
        )
    }
}

/** Tarjeta con línea azul a la izquierda */
@Composable
private fun TarjetaProxima(inscripcion: InscripcionDetalle) {
    val colores = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colores.surface),
        border = BorderStroke(1.dp, colores.outlineVariant)
    ) {
        Row(Modifier.height(IntrinsicSize.Min)) {
            Box(
                Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(colores.primary)
            )
            Column(Modifier.padding(14.dp)) {
                FilaDato(Icons.Outlined.Book, stringResource(R.string.mh_materia), inscripcion.nombreMateria)
                FilaDato(Icons.Outlined.Schedule, stringResource(R.string.mh_horario), "${inscripcion.dia} · ${inscripcion.hora} h")
                FilaDato(Icons.Outlined.LocationOn, stringResource(R.string.mh_aula), inscripcion.aula)
            }
        }
    }
}

@Composable
private fun TarjetaInscripcion(
    inscripcion: InscripcionDetalle,
    onAsistencia: () -> Unit,
    onCancelar: () -> Unit
) {
    val colores = MaterialTheme.colorScheme
    val asistio = inscripcion.estado == InscripcionEntity.ESTADO_ASISTIO
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colores.surface),
        border = BorderStroke(1.dp, colores.outlineVariant)
    ) {
        Column(Modifier.padding(14.dp)) {
            FilaDato(Icons.Outlined.Book, stringResource(R.string.mh_materia), inscripcion.nombreMateria)
            FilaDato(Icons.Outlined.Person, stringResource(R.string.mh_docente), inscripcion.docente)
            FilaDato(Icons.Outlined.Schedule, stringResource(R.string.mh_horario), "${inscripcion.dia} · ${inscripcion.hora} h")
            FilaDato(Icons.Outlined.LocationOn, stringResource(R.string.mh_aula), inscripcion.aula)
            FilaDato(Icons.Outlined.Info, stringResource(R.string.mh_estado), inscripcion.estado)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                if (!asistio) {
                    OutlinedButton(onClick = onAsistencia, shape = RoundedCornerShape(20.dp)) {
                        Icon(Icons.Outlined.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.mh_marcar_asistencia), fontSize = 12.sp)
                    }
                }
                TextButton(onClick = onCancelar) {
                    Text(stringResource(R.string.mh_cancelar_inscripcion), color = colores.error, fontSize = 12.sp)
                }
            }
        }
    }
}