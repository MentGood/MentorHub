package com.example.aplicacionesmoviles.mentorhub.ui.asesorias

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aplicacionesmoviles.R
import com.example.aplicacionesmoviles.mentorhub.data.local.AsesoriaConMateria
import com.example.aplicacionesmoviles.mentorhub.data.local.MateriaEntity
import com.example.aplicacionesmoviles.mentorhub.ui.components.CampoTexto
import com.example.aplicacionesmoviles.mentorhub.ui.components.DialogoConfirmar
import com.example.aplicacionesmoviles.mentorhub.ui.components.FilaDato
import com.example.aplicacionesmoviles.mentorhub.ui.components.ListaVacia
import com.example.aplicacionesmoviles.mentorhub.ui.components.MensajeError
import com.example.aplicacionesmoviles.mentorhub.ui.components.MostrarMensaje
import com.example.aplicacionesmoviles.mentorhub.ui.components.TarjetaBlanca
import com.example.aplicacionesmoviles.mentorhub.util.estaLlena
import com.example.aplicacionesmoviles.mentorhub.util.lugaresDisponibles

// ---------------------------------------------------------------
//  PANTALLA CENTRO DE ASESORÍAS
//  Reservar (INSERT inscripción) + CRUD de asesorías
// ---------------------------------------------------------------
@Composable
fun AsesoriasScreen(viewModel: AsesoriasViewModel = hiltViewModel()) {
    val asesorias by viewModel.asesorias.collectAsState()
    val materias by viewModel.materias.collectAsState()
    val formulario by viewModel.formulario.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    var porEliminar by remember { mutableStateOf<AsesoriaConMateria?>(null) }

    MostrarMensaje(mensaje, viewModel::limpiarMensaje)

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (asesorias.isEmpty()) {
                item { ListaVacia(stringResource(R.string.mh_sin_asesorias)) }
            }
            items(asesorias, key = { it.asesoria.idAsesoria }) { item ->
                TarjetaAsesoria(
                    item = item,
                    onReservar = { viewModel.reservar(item) },
                    onEditar = { viewModel.abrirEditar(item) },
                    onEliminar = { porEliminar = item }
                )
            }
        }

        ExtendedFloatingActionButton(
            onClick = viewModel::abrirNueva,
            icon = { Icon(Icons.Outlined.Add, contentDescription = null) },
            text = { Text(stringResource(R.string.mh_nueva_asesoria)) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }

    formulario?.let { form ->
        DialogoAsesoria(
            form = form,
            materias = materias,
            onCambio = viewModel::onFormChange,
            onGuardar = viewModel::guardar,
            onCancelar = viewModel::cerrarFormulario
        )
    }

    porEliminar?.let { item ->
        DialogoConfirmar(
            titulo = stringResource(R.string.mh_eliminar_asesoria),
            texto = stringResource(R.string.mh_confirmar_eliminar_asesoria, item.nombreMateria),
            onConfirmar = {
                viewModel.eliminar(item)
                porEliminar = null
            },
            onCancelar = { porEliminar = null }
        )
    }
}

@Composable
private fun TarjetaAsesoria(
    item: AsesoriaConMateria,
    onReservar: () -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    val a = item.asesoria
    TarjetaBlanca {
        FilaDato(Icons.Outlined.Book, stringResource(R.string.mh_materia), item.nombreMateria)
        FilaDato(Icons.Outlined.Person, stringResource(R.string.mh_docente), a.docente)
        FilaDato(Icons.Outlined.Schedule, stringResource(R.string.mh_horario), "${a.dia} · ${a.hora} h")
        FilaDato(Icons.Outlined.LocationOn, stringResource(R.string.mh_aula), a.aula)
        FilaDato(
            Icons.Outlined.Groups,
            stringResource(R.string.mh_cupos),
            stringResource(R.string.mh_cupos_valor, item.lugaresDisponibles(), a.cupo)
        )
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = onReservar,
                enabled = !item.estaLlena(),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Outlined.EventAvailable, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(
                    stringResource(if (item.estaLlena()) R.string.mh_lleno else R.string.mh_reservar),
                    fontSize = 13.sp
                )
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onEditar) {
                Icon(Icons.Outlined.Edit, contentDescription = stringResource(R.string.mh_editar), tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onEliminar) {
                Icon(Icons.Outlined.Delete, contentDescription = stringResource(R.string.mh_eliminar), tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun DialogoAsesoria(
    form: FormAsesoria,
    materias: List<MateriaEntity>,
    onCambio: (FormAsesoria) -> Unit,
    onGuardar: () -> Unit,
    onCancelar: () -> Unit
) {
    var menuAbierto by remember { mutableStateOf(false) }
    val materiaElegida = materias.firstOrNull { it.idMateria == form.idMateria }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = {
            Text(stringResource(if (form.idAsesoria == 0) R.string.mh_nueva_asesoria else R.string.mh_editar_asesoria))
        },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                // Lista desplegable de materias
                Text(stringResource(R.string.mh_materia), fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                Box {
                    OutlinedButton(
                        onClick = { menuAbierto = true },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            materiaElegida?.nombre ?: stringResource(R.string.mh_elige_materia),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = null)
                    }
                    DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
                        materias.forEach { materia ->
                            DropdownMenuItem(
                                text = { Text(materia.nombre) },
                                onClick = {
                                    onCambio(form.copy(idMateria = materia.idMateria))
                                    menuAbierto = false
                                }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
                CampoTexto(stringResource(R.string.mh_docente), form.docente, { onCambio(form.copy(docente = it)) }, Icons.Outlined.Person)
                CampoTexto(stringResource(R.string.mh_dia), form.dia, { onCambio(form.copy(dia = it)) }, Icons.Outlined.CalendarMonth)
                CampoTexto(stringResource(R.string.mh_hora), form.hora, { onCambio(form.copy(hora = it)) }, Icons.Outlined.Schedule)
                CampoTexto(stringResource(R.string.mh_aula), form.aula, { onCambio(form.copy(aula = it)) }, Icons.Outlined.LocationOn)
                CampoTexto(
                    stringResource(R.string.mh_cupo_maximo), form.cupo, { onCambio(form.copy(cupo = it)) },
                    Icons.Outlined.Groups, tipoTeclado = KeyboardType.Number
                )
                MensajeError(form.error)
            }
        },
        confirmButton = { TextButton(onClick = onGuardar) { Text(stringResource(R.string.mh_guardar)) } },
        dismissButton = { TextButton(onClick = onCancelar) { Text(stringResource(R.string.mh_cancelar)) } }
    )
}