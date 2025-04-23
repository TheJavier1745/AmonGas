package com.amongas.amongas

import org.threeten.bp.LocalDateTime
import org.threeten.bp.temporal.ChronoUnit

object HistorialManager {
    val registrosEjemplo = listOf(
        RegistroGas(LocalDateTime.now().minus(1, ChronoUnit.MINUTES), 843),
        RegistroGas(LocalDateTime.now().minus(10, ChronoUnit.MINUTES), 432),
        RegistroGas(LocalDateTime.now().minusHours(1), 180),
        RegistroGas(LocalDateTime.now().minusHours(2), 525),
        RegistroGas(LocalDateTime.now().minusHours(3), 298)
    )
}
