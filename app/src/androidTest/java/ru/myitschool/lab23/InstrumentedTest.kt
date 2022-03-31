package ru.myitschool.lab23

import android.Manifest
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.os.LocaleList
import android.util.Log
import android.view.View
import androidx.annotation.CallSuper
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.FailureHandler
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.base.DefaultFailureHandler
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
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

    private lateinit var appContext: Context

    private lateinit var mInstrumentation: Instrumentation

    // https://github.com/rainbowcake/rainbowcake


    private val n = 1 // number of iterations in each test


    @Before
    fun setUp() {
        mInstrumentation = InstrumentationRegistry.getInstrumentation()
        handler = DescriptionFailureHandler(mInstrumentation)
        Espresso.setFailureHandler(handler)




        val nonLocalizedContext = mInstrumentation.targetContext
        val configuration = nonLocalizedContext.resources.configuration
        configuration.setLocale(Locale.UK)
        //configuration.setLayoutDirection(Locale.UK)
        appContext = nonLocalizedContext.createConfigurationContext(configuration)


        val intent = Intent(appContext, MainActivity::class.java)
        activityScenario = ActivityScenario.launch(intent)

        Log.d("lang",  appContext.resources.getString(
            appContext.resources.getIdentifier(
                "main_text",
                "string",
                appContext.opPackageName
            )
        ))

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

        rotateDevice(true)

        //todo add checks

        rotateDevice(false)

        addTestToPass(1)
    }

    @Test
    fun checkResPortrait() {
        //Check string resource
        addTestToStat(1)


        Thread.sleep(7_000)

        /*onView(withId(

            appContext.resources
                .getIdentifier("text_view", "id", appContext.opPackageName)
        ))
            .check(matches(withText("Основной текст")))*/

        Assert.assertNotEquals(
            "Mismatch exception: 'app_name' resource does not exist",
            0,
            appContext.resources.getIdentifier(
                "app_name",
                "string",
                appContext.opPackageName
            ).toLong()
        )

        Assert.assertNotEquals(
            "Mismatch exception: 'vertical' resource does not exist",
            0,
            appContext.resources.getIdentifier(
                "vertical",
                "string",
                appContext.opPackageName
            ).toLong()
        )

        Assert.assertNotEquals(
            "Mismatch exception: 'horizontal' resource does not exist",
            0,
            appContext.resources.getIdentifier(
                "horizontal",
                "string",
                appContext.opPackageName
            ).toLong()
        )


        val colorResIds = arrayOf(0, 0, 0, 0, 0, 0, 0)
        for ((i, e) in colors.withIndex()) {
            colorResIds[i] = appContext.resources
                .getIdentifier(e, "colors", appContext.opPackageName)
        }
        if (colorResIds.reduce(Int::times) != 0) {
            for ((i, e) in colors.withIndex()) {
                Assert.assertEquals(
                    "Mismatch exception: $e as a color does not match the one in the task",
                    appContext.getColor(colorResIds[i]),
                    colorsCorrectValues[i]
                )
            }
        }


        addTestToPass(1)
    }


    @Test
    fun checkResLandscape() {
        //Check string resource
        addTestToStat(1)


        rotateDevice(true)
        Thread.sleep(7_000)

        Assert.assertNotEquals(
            "Mismatch exception: 'app_name' resource does not exist",
            0,
            appContext.resources.getIdentifier(
                "app_name",
                "string",
                appContext.opPackageName
            ).toLong()
        )

        Assert.assertNotEquals(
            "Mismatch exception: 'vertical' resource does not exist",
            0,
            appContext.resources.getIdentifier(
                "vertical",
                "string",
                appContext.opPackageName
            ).toLong()
        )

        Assert.assertNotEquals(
            "Mismatch exception: 'horizontal' resource does not exist",
            0,
            appContext.resources.getIdentifier(
                "horizontal",
                "string",
                appContext.opPackageName
            ).toLong()
        )


        val colorResIds = arrayOf(0, 0, 0, 0, 0, 0, 0)
        for ((i, e) in colors.withIndex()) {
            colorResIds[i] = appContext.resources
                .getIdentifier(e, "colors", appContext.opPackageName)
        }
        if (colorResIds.reduce(Int::times) != 0) {
            for ((i, e) in colors.withIndex()) {
                Assert.assertEquals(
                    "Mismatch exception: $e as a color does not match the one in the task",
                    appContext.getColor(colorResIds[i]),
                    colorsCorrectValues[i]
                )
            }
        }

        rotateDevice(false)
        addTestToPass(1)
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
        private const val APP_NAME = "Lab23"
        private const val THREAD_DELAY: Long = 4_700
        private const val BUTTON_SEND_TEXT = "Send Request"
        private const val EMPTY_STRING = ""
        private const val TEXT = "Richard (red) Of (orange) York (yellow) Gave (green) Battle (blue) In (indigo) Vain (violet)"

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
        private var colors =
            arrayOf("red", "orange", "yellow", "green", "azure", "blue", "violet")
        private var colorsCorrectValues =
            arrayOf(0xFF0000, 0xF6A630, 0xFFEB3B, 0x00ff00, 0x2196F3, 0x0000ff, 0x673AB7)

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