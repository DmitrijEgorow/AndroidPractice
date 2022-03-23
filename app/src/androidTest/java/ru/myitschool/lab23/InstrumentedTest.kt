package ru.myitschool.lab23

import android.Manifest
import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.FailureHandler
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.base.DefaultFailureHandler
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.util.*


@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class InstrumentedTest {
    //add/remove seed
    private val random = Random()
    private var activityScenario: ActivityScenario<MainActivity>? = null
    private var handler: DescriptionFailureHandler? = null

    private val n = 1 // number of iterations in each test


    @get:Rule
    var permissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_NOTIFICATION_POLICY)


    @Before
    fun setUp() {
        val mInstrumentation = InstrumentationRegistry.getInstrumentation()
        handler = DescriptionFailureHandler(mInstrumentation)
        Espresso.setFailureHandler(handler);
        val appContext = mInstrumentation.targetContext
        val intent = Intent(appContext, MainActivity::class.java)
        activityScenario = ActivityScenario.launch(intent)
        editTextGithubId = appContext.resources
            .getIdentifier("edit_text_github_login", "id", appContext.opPackageName)
        buttonSendId = appContext.resources
            .getIdentifier("button_send", "id", appContext.opPackageName)
        loginId = appContext.resources
            .getIdentifier("requested_login", "id", appContext.opPackageName)
        yearId = appContext.resources
            .getIdentifier("requested_year", "id", appContext.opPackageName)
        twitterId = appContext.resources
            .getIdentifier("requested_twitter", "id", appContext.opPackageName)


        for ((i, e) in colors.withIndex()) {
            rainbowIds[i] = appContext.resources
                .getIdentifier(e, "id", appContext.opPackageName)
        }

    }

    private fun checkInterface(ids: IntArray) {
        var id = 1
        for (e in ids) {
            id *= e
        }
        Assert.assertNotEquals(0, id.toLong())
    }

    @Test
    fun interfaceTest() {
        //Check existence of views
        addTestToStat(1)
        checkInterface(
            intArrayOf(
                editTextGithubId, buttonSendId, loginId,
                yearId, twitterId
            )
        )
        onView(withId(editTextGithubId))
            .check(matches(isDisplayed()))
        onView(withId(buttonSendId))
            .check(matches(isDisplayed()))
        addTestToPass(1)
    }

    @Test
    fun stringRes() {
        //Check string resource
        addTestToStat(1)
        val mInstrumentation = InstrumentationRegistry.getInstrumentation()
        val appContext = mInstrumentation.targetContext
        Assert.assertNotEquals(
            0,
            appContext.resources.getIdentifier(
                "send_request",
                "string",
                appContext.opPackageName
            ).toLong()
        )


        for ((i, e) in colors.withIndex()) {
            appContext.resources
                .getIdentifier(e, "colors", appContext.opPackageName)
        }
        if (twitterId != 0) {
            appContext.getColor(twitterId)
            Assert.assertEquals(
                "$twitterId some message",
                appContext.getColor(twitterId),
                0xFFBB86FC
            )
        }


        addTestToPass(1)
    }

    @Test
    @Throws(InterruptedException::class)
    fun shareTwitter() {
        //Check empty text on startup
        addTestToStat(4)

        Intents.init()
        Intents.intending(CoreMatchers.not(IntentMatchers.isInternal()))
            .respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))

        checkInterface(
            intArrayOf(
                editTextGithubId, buttonSendId, loginId,
                yearId, twitterId
            )
        )


        addTestToPass(4)
        Intents.release()
    }


    @Test
    @Throws(InterruptedException::class)
    fun zCheckRequests() {
        //Check empty text on startup
        addTestToStat(8)
        checkInterface(
            intArrayOf(
                editTextGithubId, buttonSendId, loginId,
                yearId, twitterId
            )
        )

        val mInstrumentation = InstrumentationRegistry.getInstrumentation()
        val uiDevice = UiDevice.getInstance(mInstrumentation)

        for (i in 0..n) {

        }
        addTestToPass(8)
    }

    @Throws(InterruptedException::class)
    private fun rotateDevice(landscapeMode: Boolean) {
        if (landscapeMode) {
            activityScenario!!.onActivity { activity ->
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
        } else {
            activityScenario!!.onActivity { activity ->
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
        Thread.sleep(2_100)
    }

    private fun addTestToStat(incMaxTotal: Int) {
        totalTests++
        maxGrade += incMaxTotal
    }

    private fun addTestToPass(incGrade: Int) {
        passTests++
        grade += incGrade
    }

    companion object {
        private const val APP_NAME = "TrajectoryFinal"
        private const val THREAD_DELAY: Long = 4_700
        private const val BUTTON_SEND_TEXT = "Send Request"
        private const val EMPTY_STRING = ""
        private var grade = 0
        private var totalTests = 0
        private var maxGrade = 0
        private var passTests = 0

        private var editTextGithubId = 0
        private var buttonSendId = 0
        private var loginId = 0
        private var yearId = 0
        private var twitterId = 0
        private var rainbowIds = intArrayOf(0, 0, 0, 0, 0, 0, 0)
        private var colors = arrayOf("red", "orange", "yellow", "green", "azure", "blue", "violet")

        @AfterClass
        @JvmStatic
        fun printResult() {
            val mInstrumentation = InstrumentationRegistry.getInstrumentation()
            val uiDevice = UiDevice.getInstance(mInstrumentation)
            uiDevice.pressHome()

            val results = Bundle()
            results.putInt("passTests", passTests)
            results.putInt("totalTests", totalTests)
            results.putInt("grade", grade)
            results.putInt("maxGrade", maxGrade)
            InstrumentationRegistry.getInstrumentation().addResults(results)
            Log.d("Tests", passTests.toString() + " из " + totalTests + " тестов пройдено.")
            Log.d("Tests", grade.toString() + " из " + maxGrade + " баллов получено.")
        }
    }
}

class DescriptionFailureHandler(instrumentation: Instrumentation) : FailureHandler {
    var extraMessage = ""
    var delegate: DefaultFailureHandler = DefaultFailureHandler(instrumentation.targetContext)

    override fun handle(error: Throwable?, viewMatcher: Matcher<View>?) {
        // Log anything you want here
        if (error != null) {
            val newError = Throwable(extraMessage + " \t\t\n" + error.message, error.cause)

            // Then delegate the error handling to the default handler which will throw an exception
            delegate.handle(newError, viewMatcher)
        }
    }
}