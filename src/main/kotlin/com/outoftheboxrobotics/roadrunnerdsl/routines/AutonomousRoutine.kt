package com.outoftheboxrobotics.roadrunnerdsl.routines

import kotlinx.coroutines.CoroutineScope

internal fun interface AutonomousRoutine {
    suspend fun CoroutineScope.runTask()
}