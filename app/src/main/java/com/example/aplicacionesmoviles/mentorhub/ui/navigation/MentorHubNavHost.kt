package com.example.aplicacionesmoviles.mentorhub.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.aplicacionesmoviles.R
import com.example.aplicacionesmoviles.mentorhub.ui.asesorias.AsesoriasScreen
import com.example.aplicacionesmoviles.mentorhub.ui.auth.LoginScreen
import com.example.aplicacionesmoviles.mentorhub.ui.auth.RegistroScreen
import com.example.aplicacionesmoviles.mentorhub.ui.components.LogoMentor
import com.example.aplicacionesmoviles.mentorhub.ui.inicio.InicioScreen
import com.example.aplicacionesmoviles.mentorhub.ui.materias.MateriasScreen
import com.example.aplicacionesmoviles.mentorhub.ui.perfil.PerfilScreen

// ---------------------------------------------------------------
//  NAVEGACIÓN
//  NavHost  -> contenedor que muestra la pantalla de la ruta actual
//  composable("ruta") { } -> función de NavGraphBuilder que registra
//                           una pantalla con su ruta
// ---------------------------------------------------------------
object Rutas {
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val PRINCIPAL = "principal"
    const val INICIO = "inicio"
    const val ASESORIAS = "asesorias"
    const val MATERIAS = "materias"
    const val PERFIL = "perfil"
}

/** Navegación general: login -> registro -> principal */
@Composable
fun MentorHubNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Rutas.LOGIN) {
        composable(Rutas.LOGIN) {
            LoginScreen(
                onLoginExitoso = {
                    navController.navigate(Rutas.PRINCIPAL) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }   // no regresa al login con "atrás"
                    }
                },
                onIrARegistro = { navController.navigate(Rutas.REGISTRO) }
            )
        }
        composable(Rutas.REGISTRO) {
            RegistroScreen(
                onRegistroExitoso = { navController.popBackStack() },
                onVolver = { navController.popBackStack() }
            )
        }
        composable(Rutas.PRINCIPAL) {
            PantallaPrincipal(
                onSesionCerrada = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.PRINCIPAL) { inclusive = true }
                    }
                }
            )
        }
    }
}

/** Opciones de la barra inferior */
private data class OpcionMenu(val ruta: String, @StringRes val titulo: Int, val icono: ImageVector)

private val opciones = listOf(
    OpcionMenu(Rutas.INICIO, R.string.mh_menu_inicio, Icons.Outlined.Home),
    OpcionMenu(Rutas.ASESORIAS, R.string.mh_menu_asesorias, Icons.Outlined.EventAvailable),
    OpcionMenu(Rutas.MATERIAS, R.string.mh_menu_materias, Icons.Outlined.Book),
    OpcionMenu(Rutas.PERFIL, R.string.mh_menu_perfil, Icons.Outlined.Person)
)

/** Pantalla principal: barra superior + barra inferior + NavHost interno */
@Composable
fun PantallaPrincipal(onSesionCerrada: () -> Unit) {
    val navController = rememberNavController()
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route

    Scaffold(
        topBar = { BarraSuperior() },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                opciones.forEach { opcion ->
                    NavigationBarItem(
                        selected = rutaActual == opcion.ruta,
                        onClick = {
                            navController.navigate(opcion.ruta) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(opcion.icono, contentDescription = null) },
                        label = { Text(stringResource(opcion.titulo)) }
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Rutas.INICIO,
            modifier = Modifier.padding(padding)
        ) {
            composable(Rutas.INICIO) { InicioScreen() }
            composable(Rutas.ASESORIAS) { AsesoriasScreen() }
            composable(Rutas.MATERIAS) { MateriasScreen() }
            composable(Rutas.PERFIL) { PerfilScreen(onSesionCerrada = onSesionCerrada) }
        }
    }
}

@Composable
private fun BarraSuperior() {
    Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 2.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LogoMentor(tamano = 30.dp)
            Spacer(Modifier.width(8.dp))
            Text(
                stringResource(R.string.mh_nombre_app),
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}