package com.outoftheboxrobotics.roadrunnerdsl

internal class Task(val task: () -> Unit) : AutonomousRoutine {
    override suspend fun runTask() {
        task()
    }
}