package com.outoftheboxrobotics.roadrunnerdsl.routines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AsyncTask internal constructor(
    private val task: suspend CoroutineScope.() -> Unit
) : AutonomousRoutine {
    internal lateinit var job: Job

    // Empty method - use launchTask()
    override suspend fun CoroutineScope.runTask() {}

    internal fun CoroutineScope.launchTask() { job = launch { task() } }
 }