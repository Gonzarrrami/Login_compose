package com.gonzalo.login_compose.ui

import android.app.Activity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.gonzalo.login_compose.R
import com.gonzalo.login_compose.ui.theme.LoginDesignTheme

// ---------- DATOS DE PRUEBA (hardcodeados) ----------
// En una app real estos datos vendrían de un servidor o de una base de datos.
// Para el taller los dejamos escritos aquí, a la vista de todos.
private const val CORREO_CORRECTO = "sulbaranjc@gmail.com"
private const val CLAVE_CORRECTA = "1234"
private const val NOMBRE_USUARIO = "Juan Carlos"

/*
 * PANTALLA 1: LOGIN
 *
 * Diseño basado en "Login Page" de Adarsh (comunidad de Figma, licencia CC BY 4.0):
 * https://www.figma.com/community/file/1244126388179404671/login-page
 *
 * Idea clave de Compose: la pantalla es una FUNCIÓN que dibuja la interfaz
 * a partir de un ESTADO. Cuando el estado cambia (por ejemplo, al escribir),
 * Compose vuelve a ejecutar la función y la pantalla se actualiza sola.
 *
 * Esta pantalla NO sabe cómo se abre la siguiente. Solo recibe una función
 * ("onLoginCorrecto") y la llama cuando el usuario acierta, entregándole el
 * nombre. Quien la use (MainActivity) decide qué hacer con él.
 */
@Composable
fun LoginScreen(
    onLoginCorrecto: (nombre: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    // El diseño de Figma es siempre oscuro, aunque el móvil esté en modo claro.
    LoginDesignTheme {
        IconosDeBarrasClaros()

        // ---------- ESTADO ----------
        // "rememberSaveable" guarda el valor mientras la pantalla está viva (y aunque
        // se gire el móvil); "mutableStateOf" avisa a Compose cuando cambia para que redibuje.
        var email by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("") }
        var passwordVisible by rememberSaveable { mutableStateOf(false) }
        var hayError by rememberSaveable { mutableStateOf(false) }   // ¿mostramos el aviso de error?

        // ¿Se puede pulsar "Iniciar sesión"? Solo si los dos campos tienen algo escrito.
        val camposRellenos = email.isNotBlank() && password.isNotEmpty()

        // ---------- DISEÑO ----------
        // El degradado llega hasta los bordes, también detrás de las barras del sistema.
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.background,
                        ),
                    ),
                ),
        ) {
            CirculosDecorativos()

            // Column = apila los elementos en vertical.
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()                  // no se tapa con las barras ni con el teclado
                    .verticalScroll(rememberScrollState()) // con el móvil girado, se puede desplazar
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(48.dp))             // Spacer = espacio vacío

                // --- Cabecera: logo + título ---
                LogoCircular(icono = R.drawable.ic_lock, tamanoIcono = 32.dp)
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "LOGIN",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                Spacer(Modifier.height(32.dp))

                // --- Campo de correo ---
                // Como en el diseño, la etiqueta va FUERA del campo y el borde es una "píldora".
                EtiquetaDeCampo("Correo electrónico")
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it           // guardamos lo escrito en el estado
                        hayError = false     // y quitamos el error mientras el usuario corrige
                    },
                    singleLine = true,
                    isError = hayError,
                    shape = CircleShape,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Correo electrónico" },
                )

                Spacer(Modifier.height(16.dp))

                // --- Campo de contraseña ---
                EtiquetaDeCampo("Contraseña")
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        hayError = false
                    },
                    singleLine = true,
                    isError = hayError,
                    shape = CircleShape,
                    // "supportingText" es el texto pequeño bajo el campo: solo lo mostramos si hay error.
                    supportingText = { if (hayError) Text("Correo o contraseña incorrectos") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    // Si la contraseña es visible mostramos el texto tal cual; si no, puntos.
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    // Icono del ojo a la derecha: al pulsarlo cambia el estado.
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                painter = painterResource(
                                    if (passwordVisible) R.drawable.ic_visibility_off else R.drawable.ic_visibility
                                ),
                                contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Contraseña" },
                )

                // --- "¿Olvidaste tu contraseña?" alineado a la izquierda ---
                TextButton(
                    onClick = { /* pendiente: recuperar contraseña */ },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onBackground),
                    modifier = Modifier.align(Alignment.Start),
                ) {
                    Text("¿Olvidaste tu contraseña?")
                }

                Spacer(Modifier.height(24.dp))

                // --- Dos botones con contorno, separados a los extremos ---
                // Row = apila en horizontal. SpaceBetween los empuja a cada lado.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    val coloresBoton = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onBackground,
                    )

                    OutlinedButton(
                        onClick = { /* pendiente: pantalla de registro */ },
                        colors = coloresBoton,
                    ) {
                        Text("Registrarse")
                    }

                    // --- Botón principal: AQUÍ ESTÁ LA LÓGICA DEL LOGIN ---
                    OutlinedButton(
                        onClick = {
                            // Comparamos lo escrito con los datos de prueba.
                            // trim() quita espacios sobrantes al principio y al final del correo.
                            if (email.trim() == CORREO_CORRECTO && password == CLAVE_CORRECTA) {
                                onLoginCorrecto(NOMBRE_USUARIO)   // ¡acertó! Avisamos y enviamos el nombre
                            } else {
                                hayError = true                   // falló: mostramos el error
                            }
                        },
                        enabled = camposRellenos,                 // desactivado si falta algún campo
                        colors = coloresBoton,
                    ) {
                        Text("Iniciar sesión")
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

/** Etiqueta pequeña encima de un campo, con la sangría que tiene en el diseño. */
@Composable
private fun EtiquetaDeCampo(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, bottom = 8.dp),
    )
}

/** Los dos círculos difuminados de la parte superior del diseño de Figma. */
@Composable
private fun CirculosDecorativos() {
    // Los colores se leen del tema ANTES de entrar en el Canvas.
    val blanco = MaterialTheme.colorScheme.onBackground
    val azul = MaterialTheme.colorScheme.secondary

    Canvas(modifier = Modifier.fillMaxSize()) {
        // Medidas pensadas para un ancho de 360: "u" las adapta a cualquier pantalla.
        val u = size.width / 360f

        dibujarCirculo(Offset(24 * u, 66 * u), 100 * u, blanco)
        dibujarCirculo(Offset(288 * u, 72 * u), 56 * u, azul)
    }
}

/** Un círculo que se desvanece de arriba hacia abajo hasta hacerse transparente. */
private fun DrawScope.dibujarCirculo(
    centro: Offset,
    radio: Float,
    color: Color,
) {
    drawCircle(
        brush = Brush.verticalGradient(
            colors = listOf(color, color.copy(alpha = 0f)),
            startY = centro.y - radio,
            endY = centro.y + radio,
        ),
        radius = radio,
        center = centro,
    )
}

/** Con el fondo oscuro del Login, los iconos de las barras del sistema deben ser claros. */
@Composable
private fun IconosDeBarrasClaros() {
    val view = LocalView.current
    if (view.isInEditMode) return   // en la @Preview no hay barras del sistema

    DisposableEffect(Unit) {
        val controller = WindowCompat.getInsetsController((view.context as Activity).window, view)
        val estabaClaroArriba = controller.isAppearanceLightStatusBars
        val estabaClaroAbajo = controller.isAppearanceLightNavigationBars
        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = false
        // Al salir del Login, las barras vuelven a como estaban.
        onDispose {
            controller.isAppearanceLightStatusBars = estabaClaroArriba
            controller.isAppearanceLightNavigationBars = estabaClaroAbajo
        }
    }
}

// Vista previa en Android Studio, con el tamaño exacto del Samsung Galaxy S24 (360 x 780 dp).
@Preview(name = "Galaxy S24", showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun LoginScreenPreview() {
    LoginScreen(onLoginCorrecto = {})
}
