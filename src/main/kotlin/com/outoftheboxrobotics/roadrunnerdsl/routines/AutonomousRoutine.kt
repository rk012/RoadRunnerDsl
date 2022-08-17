package com.outoftheboxrobotics.roadrunnerdsl.routines

internal fun interface AutonomousRoutine {
    suspend fun runTask()
}