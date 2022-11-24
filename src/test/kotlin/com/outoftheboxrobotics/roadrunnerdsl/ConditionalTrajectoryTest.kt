package com.outoftheboxrobotics.roadrunnerdsl

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.outoftheboxrobotics.roadrunnerdsl.AutonomousBuilderTest.Companion.autonomousBuilder
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class ConditionalTrajectoryTest {
    @Test
    fun conditionalTest() {
        val expected = "qwertyuiopasdfghjklzxcvbnm".toCharArray()

        repeat(expected.size) {n ->
            var s = ""

            autonomousBuilder {
                conditionalTrajectory {
                    repeat(expected.size) {
                        condition({ it == n }) {
                            task { s += expected[n] }
                        }
                    }
                }
            }.run()

            assertEquals(expected[n].toString(), s)
        }

        var conditionRun = false

        autonomousBuilder {
            conditionalTrajectory {
                condition({ false }) { conditionRun = false }
                default { conditionRun = true }
            }
        }

        assertTrue(conditionRun)
    }

    @Test
    fun conditionalPoseTest() {
        val defaultPose = autonomousBuilder {
            conditionalTrajectory {

            }
        }.endPose

        assertEquals(Pose2d().toString(), defaultPose.toString())

        val nullPose1 = autonomousBuilder {
            conditionalTrajectory {
                default {
                    trajectory { strafeRight(2.0) }
                }
            }
        }.endPose

        val nullPose2 = autonomousBuilder {
            conditionalTrajectory {
                condition({ true }) {
                    trajectory { strafeRight(2.0) }
                }
            }
        }.endPose

        assertNull(nullPose1)
        assertNull(nullPose2)

        val zeroPose = autonomousBuilder {
            conditionalTrajectory {
                condition({ true }) {
                    trajectory { strafeRight(4.0) }
                    trajectory { strafeLeft(4.0) }
                }
            }
        }.endPose

        assertEquals(Pose2d().toString(), zeroPose.toString())

        val definitePose = autonomousBuilder {
            conditionalTrajectory {
                condition({ true }) {
                    trajectory { strafeRight(4.0) }
                    trajectory { strafeLeft(3.0) }
                    trajectory { forward(1.0) }
                }

                condition({ false }) {
                    trajectory { strafeRight(1.0) }
                    trajectory { forward(1.0) }
                }

                default {
                    trajectory { strafeLeft(1.0) }
                    trajectory { strafeRight(2.0) }
                    trajectory { forward(1.0) }
                }
            }
        }.endPose

        assertEquals(
            Pose2d(1.0, -1.0, 0.0).toString(),
            definitePose.toString()
        )
    }

    @Test
    fun conditionalExceptionTest() {
        assertThrows<IllegalArgumentException> {
            autonomousBuilder {
                conditionalTrajectory {
                    default { trajectory { strafeRight(1.0) } }
                }

                trajectory { forward(1.0) }
            }
        }

        autonomousBuilder {
            conditionalTrajectory {
                default { trajectory { strafeRight(1.0) } }
            }

            task {  }
        }

        autonomousBuilder {
            conditionalTrajectory {
                default { trajectory { strafeRight(1.0) } }
                condition({ true }) {
                    trajectory { strafeRight(2.0) }
                    trajectory { strafeLeft(1.0) }
                }
            }

            trajectory { forward(1.0) }
        }
    }
}