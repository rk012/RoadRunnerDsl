package com.outoftheboxrobotics.roadrunnerdsl

import com.acmerobotics.roadrunner.trajectory.Trajectory

internal class TrajectoryWrapper(val trajectory: Trajectory, val trajectoryRunner: (Trajectory) -> Unit) : AutonomousRoutine {
    override suspend fun runTask() {
        trajectoryRunner(trajectory)
    }
}