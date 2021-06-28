package com.example.android.architecture.blueprints.todoapp.statistics

import com.example.android.architecture.blueprints.todoapp.data.Task
import junit.framework.Assert.assertEquals
import junit.framework.TestCase
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class StatisticsUtilsTest {

    @Test
    fun getActiveAndCompletedStats_noCompleted_returnsHundredZero() {
        val task = listOf<Task>(
            Task("title", "description", isCompleted = false)
        )
        val result = getActiveAndCompletedStats(task)
        assertThat(result.completedTasksPercent, `is`(0f))
        assertEquals(100f, result.activeTasksPercent)
    }

    @Test
    fun getActiveAndCompletedStats_forty_sixty() {
        val task = listOf<Task>(
            Task("title", "description", isCompleted = true),
            Task("title", "description", isCompleted = true),
            Task("title", "description", isCompleted = false),
            Task("title", "description", isCompleted = false),
            Task("title", "description", isCompleted = false)
        )
        val result = getActiveAndCompletedStats(task)
        assertEquals(40f, result.completedTasksPercent)
        assertEquals(60f, result.activeTasksPercent)
    }

    @Test
    fun getActiveAndCompletedStats_empty_returnsZero() {
        val task = ArrayList<Task>()
        val result = getActiveAndCompletedStats(task)
        assertEquals(0f, result.completedTasksPercent)
        assertEquals(0f, result.activeTasksPercent)
    }

    @Test
    fun getActiveAndCompletedStats_null_returnsZero() {
        val task = null
        val result = getActiveAndCompletedStats(task)
        assertEquals(0f, result.completedTasksPercent)
        assertEquals(0f, result.activeTasksPercent)
    }
}