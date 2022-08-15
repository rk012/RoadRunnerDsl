package com.outoftheboxrobotics.roadrunnerdsl

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.trajectory.Trajectory
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder

class AutonomousBuilder(
    private val getTrajectoryBuilder: (Pose2d) -> TrajectoryBuilder,
    private val runTrajectory: (Trajectory) -> Unit,
    private val defaultPose: Pose2d = Pose2d()
) {
    private val trajectories = mutableListOf<Trajectory>()

    val endPose
        get() = if (trajectories.isEmpty()) defaultPose else trajectories.last().end()

    fun trajectory(block: TrajectoryBuilder.() -> Unit) {
        trajectories.add(getTrajectoryBuilder(endPose).apply(block).build())
    }

    fun run() {
        trajectories.forEach(runTrajectory)
    }
}