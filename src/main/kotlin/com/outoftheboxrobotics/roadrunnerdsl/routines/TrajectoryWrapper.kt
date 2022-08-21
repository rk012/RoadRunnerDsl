package com.outoftheboxrobotics.roadrunnerdsl.routines

import com.acmerobotics.roadrunner.trajectory.Trajectory
import kotlinx.coroutines.CoroutineScope

internal class TrajectoryWrapper(private val trajectory: Trajectory, val trajectoryRunner: (Trajectory) -> Unit) : MotionRoutine {
    override val endPose = trajectory.end()

    override suspend fun CoroutineScope.runTask() {
        trajectoryRunner(trajectory)
    }
}