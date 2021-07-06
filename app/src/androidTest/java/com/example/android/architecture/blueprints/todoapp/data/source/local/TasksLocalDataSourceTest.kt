package com.example.android.architecture.blueprints.todoapp.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.succeeded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class TasksLocalDataSourceTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var localDataSource: TasksLocalDataSource
    private lateinit var database: ToDoDatabase

    @Before
    fun setup() {
        // Using an in-memory database for testing, because it doesn't survive killing the process.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ToDoDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        localDataSource =
            TasksLocalDataSource(
                database.taskDao(),
                Dispatchers.Main
            )
    }

    @Test
    fun saveTask_retrievesTask() = runBlocking {
        val task = Task("Title", "Description", false)
        localDataSource.saveTask(task)

        val result = localDataSource.getTask(task.id)

        assertThat(result.succeeded, `is`(true))
        result as Result.Success
        assertThat(result.data.title, `is`("Title"))
        assertThat(result.data.description, `is`("Description"))
        assertThat(result.data.isCompleted, `is`(false))

    }

    @Test
    fun completeTask_retrievedTaskIsComplete() = runBlocking {
        // 1. Save a new active task in the local data source.
        val task = Task("Title", "Description", true)
        localDataSource.saveTask(task)
        // 2. Mark it as complete.
        val result = localDataSource.getTask(task.id)
        assertThat(result.succeeded, `is`(true))
        result as Result.Success
        // 3. Check that the task can be retrieved from the local data source and is complete.
        assertThat(result.data.title, `is`(task.title))
        assertThat(result.data.isCompleted, `is`(true))
    }

    @After
    fun cleanUp() {
        database.close()
    }
}