package com.outoftheboxrobotics.roadrunnerdsl

import com.outoftheboxrobotics.roadrunnerdsl.AutonomousBuilderTest.Companion.autonomousBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.test.Test
import kotlin.test.assertEquals

internal class AsyncTest {
    @Test
    fun asyncTest() {
        var s = ""

        autonomousBuilder {
            task {
                launch {
                    delay(20)
                    s += "1"
                }
            }

            asyncTask {
                delay(20)
                s += "3"
            }

            asyncScope {
                asyncTask {
                    delay(50)
                    s += "4"
                }

                task { s += "2" }
            }

            task { s += "5" }
        }.run()

        assertEquals("12345", s)
    }

    @Test
    fun delayAwaitTest() {
        var s = ""

        autonomousBuilder {
            val a = asyncTask {
                delay(100)
                s += "2"
            }
            wait(50)
            asyncTask {
                delay(20)
                s += "1"
            }
            awaitTask(a)
            task { s += "3" }
        }.run()

        assertEquals("123", s)
    }
}