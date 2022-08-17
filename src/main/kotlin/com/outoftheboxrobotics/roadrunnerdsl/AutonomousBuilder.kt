package com.outoftheboxrobotics.roadrunnerdsl

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.trajectory.Trajectory
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder
import kotlinx.coroutines.runBlocking

class AutonomousBuilder(
    private val getTrajectoryBuilder: (Pose2d) -> TrajectoryBuilder,
    private val runTrajectory: (Trajectory) -> Unit,
    private val defaultPose: Pose2d = Pose2d()
) : AutonomousRoutine {
    private val routines = mutableListOf<AutonomousRoutine>()

    val endPose
        get() = routines.filterIsInstance<TrajectoryWrapper>().let {
            if (it.isEmpty()) defaultPose else it.last().trajectory.end()
        }

    fun trajectory(block: TrajectoryBuilder.() -> Unit) {
        routines.add(TrajectoryWrapper(getTrajectoryBuilder(endPose).apply(block).build(), runTrajectory))
    }
    fun task(block: () -> Unit) {
        routines.add(Task(block))
    }

    override suspend fun runTask() {
        routines.forEach { it.runTask() }
    }

    fun run() = runBlocking {
        runTask()
    }
}