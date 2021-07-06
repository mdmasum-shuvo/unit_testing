package com.example.android.architecture.blueprints.todoapp

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import com.example.android.architecture.blueprints.todoapp.tasks.TasksActivity
import com.example.android.architecture.blueprints.todoapp.util.DataBindingIdlingResource
import com.example.android.architecture.blueprints.todoapp.util.monitorActivity
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class TaskActivityTest {
    private lateinit var repository: TasksRepository
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun init() {
        repository = ServiceLocator.provideTaskRepository(getApplicationContext())
        runBlocking {
            repository.deleteAllTasks()
        }
    }

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)

    }

    @After
    fun unRegisterIdlingResource() {

        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun editTask() = runBlocking {
        val task = Task("Title", "Description")

        repository.saveTask(task)
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        // Click on the task on the list and verify that all the data is correct.
        onView(withText("Title")).perform(click())
        onView(withId(R.id.task_detail_title_text)).check(matches(withText("Title")))
        onView(withId(R.id.task_detail_description_text)).check(matches(withText("Description")))
        onView(withId(R.id.task_detail_complete_checkbox)).check(matches(not(isChecked())))

        // Click on the edit button, edit, and save.
        onView(withId(R.id.edit_task_fab)).perform(click())
        onView(withId(R.id.add_task_title_edit_text)).perform(replaceText("NEW TITLE"))
        onView(withId(R.id.add_task_description_edit_text)).perform(replaceText("NEW DESCRIPTION"))
        onView(withId(R.id.save_task_fab)).perform(click())

        // Verify task is displayed on screen in the task list.
        onView(withText("NEW TITLE")).check(matches(isDisplayed()))
        // Verify previous task is not displayed.
        onView(withText("TITLE1")).check(doesNotExist())
        activityScenario.close()
    }
    @Test
    fun createOneTask_deleteTask() {

        // start up Tasks screen
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Add active task
        onView(withId(R.id.add_task_fab)).perform(click())
        onView(withId(R.id.add_task_title_edit_text))
            .perform(typeText("TITLE1"), closeSoftKeyboard())
        onView(withId(R.id.add_task_description_edit_text)).perform(typeText("DESCRIPTION"))
        onView(withId(R.id.save_task_fab)).perform(click())

        // Open it in details view
        onView(withText("TITLE1")).perform(click())
        // Click delete task in menu
        onView(withId(R.id.menu_delete)).perform(click())

        // Verify it was deleted
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_all)).perform(click())
        onView(withText("TITLE1")).check(doesNotExist())
        // Make sure the activity is closed before resetting the db:
        activityScenario.close()
    }

    @After
    fun reset() {
        ServiceLocator.resetRepository()
    }

}