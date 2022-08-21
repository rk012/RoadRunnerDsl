package com.outoftheboxrobotics.roadrunnerdsl

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.trajectory.Trajectory
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder
import com.outoftheboxrobotics.roadrunnerdsl.routines.*
import kotlinx.coroutines.*

class AutonomousBuilder(
    private val getTrajectoryBuilder: (Pose2d) -> TrajectoryBuilder,
    private val runTrajectory: (Trajectory) -> Unit,
    private val defaultPose: Pose2d = Pose2d()
) : MotionRoutine {
    private val routines = mutableListOf<AutonomousRoutine>()

    override val endPose
        get() = routines.filterIsInstance<MotionRoutine>().lastOrNull()?.endPose ?: defaultPose

    fun trajectory(block: TrajectoryBuilder.() -> Unit) {
        routines.add(TrajectoryWrapper(getTrajectoryBuilder(endPose).apply(block).build(), runTrajectory))
    }

    fun addPose(pose: Pose2d) {
        routines.add(BlankMotionRoutine(endPose + pose))
    }

    fun task(block: suspend CoroutineScope.() -> Unit) {
        routines.add(block)
    }

    fun asyncTask(block: suspend CoroutineScope.() -> Unit) = AsyncTask(block).also {
        routines.add(it)
    }

    fun awaitTask(task: AsyncTask) {
        routines.add {
            task.job.join()
        }
    }

    @JvmName("waitMillis")
    fun wait(millis: Long) {
        routines.add {
            delay(millis)
        }
    }

    fun asyncScope(block: AutonomousBuilder.() -> Unit) {
        routines.add(AutonomousBuilder(getTrajectoryBuilder, runTrajectory, defaultPose).apply(block))
    }

    override suspend fun CoroutineScope.runTask() {
        coroutineScope {
            routines.forEach {
                if (it is AsyncTask) with(it) { launchTask() }
                else with(it) { runTask() }
            }
        }
    }

    fun run() = runBlocking {
        runTask()
    }
}