package com.outoftheboxrobotics.roadrunnerdsl

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.acmerobotics.roadrunner.path.PathContinuityViolationException
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class AutonomousBuilderTest {
    private fun autonomousBuilder(block: AutonomousBuilder.() -> Unit) = AutonomousBuilder(
        getTrajectoryBuilder = { TrajectoryBuilder(it, false, { _, _, _, _ -> 30.0 }, { _, _, _, _ -> 30.0 }) },
        runTrajectory = {}
    ).apply(block)

    @Test
    fun pathContinuityExceptionTest() {
        autonomousBuilder {
            trajectory {
                splineTo(Vector2d(1.0, 1.0), 0.0)
                splineTo(Vector2d(2.0, 2.0), 0.0)
            }
        }

        assertFailsWith<PathContinuityViolationException> {
            autonomousBuilder {
                trajectory {
                    strafeRight(10.0)
                    forward(5.0)
                }
            }
        }


        autonomousBuilder {
            trajectory { strafeRight(10.0) }
            trajectory { forward(5.0) }
        }
    }

    @Test
    fun endPoseTest() {
        val p1 = autonomousBuilder {
            trajectory { splineToLinearHeading(Pose2d(15.0, -2.0, PI / 2), 0.0) }
            addPose(Pose2d(0.0, 0.0, PI / 2))
            trajectory { strafeLeft(8.0) }
        }.endPose

        val p2 = autonomousBuilder {
            trajectory { lineToSplineHeading(Pose2d(15.0, -10.0, PI)) }
        }.endPose

        // data class .equals() is not working for some reason
        assertEquals("$p1", "$p2")
    }

    @Test
    fun taskTest() {
        var s = ""

        autonomousBuilder {
            task { s += "a" }
            trajectory { strafeRight(6.0) }
            task { s += "b" }
        }.run()

        assertEquals("ab", s)
    }
}
