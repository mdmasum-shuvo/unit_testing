package com.example.android.architecture.blueprints.todoapp.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import com.example.android.architecture.blueprints.todoapp.data.Task
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import androidx.test.filters.SmallTest
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class TasksDaoTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private lateinit var database: ToDoDatabase

    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            ToDoDatabase::class.java
        ).build()
    }

    @Test
    fun insertTaskAndGetById() = runBlockingTest {
        val task = Task("Title", "Description")
        database.taskDao().insertTask(task)
        val loaded = database.taskDao().getTaskById(task.id)

        assertThat<Task>(loaded as Task, notNullValue())
        assertThat(loaded.id, `is`(task.id))
        assertThat(loaded.title, `is`(task.title))
        assertThat(loaded.description, `is`(task.description))
        assertThat(loaded.isCompleted, `is`(task.isCompleted))
    }

    @Test
    fun updateTaskAndGetById() = runBlockingTest {
        // 1. Insert a task into the DAO.
        val task = Task("title", "description")
        database.taskDao().insertTask(task)

        // 2. Update the task by creating a new task with the same ID but different attributes.
       // val loaded = database.taskDao().getTaskById(task.id)

        val taskUpdate = Task("Title2", "Description2", true, task.id)
        database.taskDao().updateTask(taskUpdate)
        // 3. Check that when you get the task by its ID, it has the updated values.
        val loaded = database.taskDao().getTaskById(task.id)
        assertThat<Task>(loaded as Task, notNullValue())
        assertThat(loaded.id,`is`(taskUpdate.id))
        assertThat(loaded.title,`is`(taskUpdate.title))
        assertThat(loaded.description,`is`(taskUpdate.description))
        assertThat(loaded.isCompleted,`is`(taskUpdate.isCompleted))

    }

    @After
    fun closeDb() = database.close()
}