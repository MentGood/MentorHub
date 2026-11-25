package com.example.aplicacionesmoviles.mentorhub.ui.materias

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.aplicacionesmoviles.mentorhub.data.local.MateriaEntity
import com.example.aplicacionesmoviles.mentorhub.ui.components.CampoTexto
import com.example.aplicacionesmoviles.mentorhub.ui.components.DialogoConfirmar
import com.example.aplicacionesmoviles.mentorhub.ui.components.ListaVacia
import com.example.aplicacionesmoviles.mentorhub.ui.components.MensajeError
import com.example.aplicacionesmoviles.mentorhub.ui.components.MostrarMensaje
import com.example.aplicacionesmoviles.mentorhub.ui.components.TarjetaBlanca

// ---------------------------------------------------------------
//  PANTALLA MATERIAS: CRUD completo de la tabla materias
// ---------------------------------------------------------------
@Composable
fun MateriasScreen(viewModel: MateriasViewModel = hiltViewModel()) {
    val materias by viewModel.materias.collectAsState()
    val formulario by viewModel.formulario.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    var porEliminar by remember { mutableStateOf<MateriaEntity?>(null) }

    MostrarMensaje(mensaje, viewModel::limpiarMensaje)

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (materias.isEmpty()) {
                item { ListaVacia(stringResource(R.string.mh_sin_materias)) }
            }
            items(materias, key = { it.idMateria }) { materia ->
                TarjetaBlanca {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Book, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(materia.nombre, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            Text(
                                stringResource(R.string.mh_clave_valor, materia.clave),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                        }
                        IconButton(onClick = { viewModel.abrirEditar(materia) }) {
                            Icon(Icons.Outlined.Edit, contentDescription = stringResource(R.string.mh_editar), tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = { porEliminar = materia }) {
                            Icon(Icons.Outlined.Delete, contentDescription = stringResource(R.string.mh_eliminar), tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = viewModel::abrirNueva,
            icon = { Icon(Icons.Outlined.Add, contentDescription = null) },
            text = { Text(stringResource(R.string.mh_nueva_materia)) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }

    formulario?.let { form ->
        AlertDialog(
            onDismissRequest = viewModel::cerrarFormulario,
            title = { Text(stringResource(if (form.idMateria == 0) R.string.mh_nueva_materia else R.string.mh_editar_materia)) },
            text = {
                Column {
                    CampoTexto(stringResource(R.string.mh_nombre_materia), form.nombre, { viewModel.onFormChange(form.copy(nombre = it)) }, Icons.Outlined.Book)
                    CampoTexto(stringResource(R.string.mh_clave), form.clave, { viewModel.onFormChange(form.copy(clave = it)) }, Icons.Outlined.Tag)
                    MensajeError(form.error)
                }
            },
            confirmButton = { TextButton(onClick = viewModel::guardar) { Text(stringResource(R.string.mh_guardar)) } },
            dismissButton = { TextButton(onClick = viewModel::cerrarFormulario) { Text(stringResource(R.string.mh_cancelar)) } }
        )
    }

    porEliminar?.let { materia ->
        DialogoConfirmar(
            titulo = stringResource(R.string.mh_eliminar_materia),
            texto = stringResource(R.string.mh_confirmar_eliminar_materia, materia.nombre),
            onConfirmar = {
                viewModel.eliminar(materia)
                porEliminar = null
            },
            onCancelar = { porEliminar = null }
        )
    }
}