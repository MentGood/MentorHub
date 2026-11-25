package com.example.aplicacionesmoviles.mentorhub.ui.components

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aplicacionesmoviles.R

// ---------------------------------------------------------------
//  COMPONENTES REUTILIZABLES (se usan en varias pantallas)
// ---------------------------------------------------------------

/** Logo azul con birrete */
@Composable
fun LogoMentor(tamano: Dp, forma: Shape = CircleShape) {
    Box(
        modifier = Modifier
            .size(tamano)
            .clip(forma)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Outlined.School,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(tamano * 0.55f)
        )
    }
}

/** Tarjeta blanca con borde suave */
@Composable
fun TarjetaBlanca(contenido: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(Modifier.padding(14.dp), content = contenido)
    }
}

/** Título de sección con ícono */
@Composable
fun TituloSeccion(icono: ImageVector, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(texto, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}

/** Fila "Etiqueta: valor" con ícono */
@Composable
fun FilaDato(icono: ImageVector, etiqueta: String, valor: String) {
    Row(modifier = Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(17.dp))
        Spacer(Modifier.width(8.dp))
        Text("$etiqueta: ", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        Text(valor, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}

/** Campo de texto con etiqueta, ícono y opción de contraseña */
@Composable
fun CampoTexto(
    etiqueta: String,
    valor: String,
    onCambio: (String) -> Unit,
    icono: ImageVector,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    esContrasena: Boolean = false
) {
    var verContrasena by remember { mutableStateOf(false) }
    val colores = MaterialTheme.colorScheme

    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        leadingIcon = { Icon(icono, contentDescription = null, tint = colores.onSurfaceVariant) },
        trailingIcon = if (esContrasena) {
            {
                IconButton(onClick = { verContrasena = !verContrasena }) {
                    Icon(
                        if (verContrasena) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = stringResource(R.string.mh_mostrar_contrasena),
                        tint = colores.onSurfaceVariant
                    )
                }
            }
        } else null,
        visualTransformation = if (esContrasena && !verContrasena) PasswordVisualTransformation()
        else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (esContrasena) KeyboardType.Password else tipoTeclado),
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(10.dp))
}

/** Texto rojo para mostrar un error de validación */
@Composable
fun MensajeError(@StringRes mensaje: Int?) {
    if (mensaje != null) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
            Icon(Icons.Outlined.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text(stringResource(mensaje), color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
        }
    }
}

/** Muestra un Toast cuando llega un mensaje y avisa que ya se mostró */
@Composable
fun MostrarMensaje(@StringRes mensaje: Int?, onMostrado: () -> Unit) {
    val context = LocalContext.current
    LaunchedEffect(mensaje) {
        if (mensaje != null) {
            Toast.makeText(context, context.getString(mensaje), Toast.LENGTH_SHORT).show()
            onMostrado()
        }
    }
}

/** Diálogo para confirmar antes de eliminar */
@Composable
fun DialogoConfirmar(titulo: String, texto: String, onConfirmar: () -> Unit, onCancelar: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCancelar,
        icon = { Icon(Icons.Outlined.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
        title = { Text(titulo) },
        text = { Text(texto) },
        confirmButton = {
            TextButton(onClick = onConfirmar) {
                Text(stringResource(R.string.mh_eliminar), color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text(stringResource(R.string.mh_cancelar)) } }
    )
}

/** Mensaje cuando una lista está vacía */
@Composable
fun ListaVacia(texto: String) {
    Text(
        texto,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 14.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
    )
}