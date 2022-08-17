package com.outoftheboxrobotics.roadrunnerdsl

internal interface AutonomousRoutine {
    suspend fun runTask()
}