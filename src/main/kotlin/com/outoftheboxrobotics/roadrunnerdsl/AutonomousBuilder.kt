package com.outoftheboxrobotics.roadrunnerdsl

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.trajectory.Trajectory
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder
import com.outoftheboxrobotics.roadrunnerdsl.routines.AutonomousRoutine
import com.outoftheboxrobotics.roadrunnerdsl.routines.BlankMotionRoutine
import com.outoftheboxrobotics.roadrunnerdsl.routines.MotionRoutine
import com.outoftheboxrobotics.roadrunnerdsl.routines.TrajectoryWrapper
import kotlinx.coroutines.runBlocking

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

    fun task(block: () -> Unit) {
        routines.add {
            block()
        }
    }



    override suspend fun runTask() {
        routines.forEach { it.runTask() }
    }

    fun run() = runBlocking {
        runTask()
    }
}