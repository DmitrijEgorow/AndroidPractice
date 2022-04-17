package ru.myitschool.lab23

import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.FailureHandler
import androidx.test.espresso.accessibility.AccessibilityChecks
import androidx.test.espresso.assertion.PositionAssertions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.base.DefaultFailureHandler
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResult.AccessibilityCheckResultType
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.util.*
import kotlin.math.min


@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
class InstrumentedTestLab23 {
    //add/remove seed
    private val random = Random()
    private var activityScenario: ActivityScenario<MainActivity>? = null
    private var handler: DescriptionFailureHandler? = null

    private lateinit var appContext: Context

    private lateinit var mInstrumentation: Instrumentation

    // https://github.com/rainbowcake/rainbowcake

    // https://stackoverflow.com/questions/40508113/android-espresso-testing-close-and-app-and-then-re-open-it


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

        mainTextId = appContext.resources
            .getIdentifier("main_text", "id", appContext.opPackageName)
        outerLayoutId = appContext.resources
            .getIdentifier("outer_layout", "id", appContext.opPackageName)


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
            intArrayOf(mainTextId)
        )

        onView(
            allOf(
                withId(mainTextId),
                withText(MAIN_TEXT_RUS)
            )
        )
            .check(matches(isDisplayed()))

        rotateDevice(true)

        onView(
            allOf(
                withId(mainTextId),
                withText(MAIN_TEXT_RUS)
            )
        )
            .check(matches(isDisplayed()))

        rotateDevice(false)

        addTestToPass(1)
    }


    @Test
    fun languageTest() {
        //Check existence of views
        addTestToStat(1)
        /*onView(withId(mainTextId))
            .check(matches(isDisplayed()))*/
        val mainTextLangId = appContext.resources.getIdentifier(
            "main_text",
            "string",
            appContext.opPackageName
        )

        if (mainTextLangId != 0) {
            Assert.assertEquals(
                "Do you have correct qualifiers? ",
                appContext.resources.getString(
                    mainTextLangId
                ),
                MAIN_TEXT_ENG
            )

            addTestToPass(1)
        } else {
            Assert.assertEquals(
                "Do you have correct qualifiers? ",
                "", "?"
            )
        }

    }

    @Test
    fun checkResValues() {
        //Check string resource
        addTestToStat(1)

        Thread.sleep(THREAD_DELAY)


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
    fun checkResPortait() {
        addTestToStat(2)

        checkInterface(
            rainbowIds
        )
        checkInterface(
            intArrayOf(mainTextId)
        )

        for ((i, e) in colors.withIndex()) {
            onView(withId(rainbowIds[i]))
                .check(matches(isDisplayed()))
            if (i + 1 < colors.size) {
                onView(withId(rainbowIds[i + 1]))
                    .check(
                        isCompletelyBelow(
                            withId(rainbowIds[i])
                        )
                    )
            }
        }

        onView(withId(mainTextId))
            .check(
                isCompletelyAbove(
                    withId(rainbowIds[0])
                )
            )


        addTestToPass(2)
    }


    @Test
    fun checkResLandscape() {
        //Check string resource
        addTestToStat(3)

        checkInterface(
            rainbowIds
        )
        checkInterface(
            intArrayOf(mainTextId)
        )

        rotateDevice(true)
        Thread.sleep(THREAD_DELAY)

        for ((i, e) in colors.withIndex()) {
            onView(withId(rainbowIds[i]))
                .check(matches(isDisplayed()))
            if (i + 1 < colors.size) {
                onView(withId(rainbowIds[i + 1]))
                    .check(
                        isCompletelyRightOf(
                            withId(rainbowIds[i])
                        )
                    )
            }
        }

        onView(withId(mainTextId))
            .check(
                isCompletelyAbove(
                    withId(rainbowIds[0])
                )
            )

        handler?.extraMessage = "Does outer_layout contain all coloured views?"
        onView(withId(outerLayoutId))
            .check(
                matches(hasChildCount(15))
            )
        handler?.extraMessage = ""

        rotateDevice(false)
        addTestToPass(3)
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
        private const val THREAD_DELAY: Long = 300
        private const val MAIN_TEXT_RUS = "Каждый Охотник Желает Знать Где Сидит Фазан"
        private const val MAIN_TEXT_ENG = "Richard Of York Gave Battle In Vain"
        private const val EMPTY_STRING = ""

        private var grade = 0
        private var totalTests = 0
        private var maxGrade = 0
        private var passTests = 0

        private var mainTextId = 0
        private var outerLayoutId = 0
        private var buttonSendId = 0
        private var loginId = 0
        private var yearId = 0
        private var twitterId = 0
        private var rainbowIds = intArrayOf(0, 0, 0, 0, 0, 0, 0)
        private var colors =
            arrayOf("red", "orange", "yellow", "green", "azure", "blue", "violet")
        private var colorsCorrectValues =
            arrayOf(0xFF0000, 0xF6A630, 0xFFEB3B, 0x00ff00, 0x2196F3, 0x0000ff, 0x673AB7)

        @BeforeClass
        @JvmStatic
        fun enableAccessibilityChecks() {
            AccessibilityChecks.enable()
                .setRunChecksFromRootView(true)
                .setThrowExceptionFor(AccessibilityCheckResultType.WARNING)
                .setThrowExceptionFor(AccessibilityCheckResultType.ERROR)
                .setThrowExceptionFor(AccessibilityCheckResultType.INFO)
        }

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
            val newError = Throwable(
                extraMessage + "     " + error.message?.substring(
                    0,
                    min(530, error.message?.length ?: 0)
                ) + "...", error.cause
            )

            // Then delegate the error handling to the default handler which will throw an exception
            delegate.handle(newError, viewMatcher)
        }
    }
}


class FontSizeMatcher(private val expectedSize: Float) : TypeSafeMatcher<View?>(
    View::class.java
) {

    override fun describeTo(description: Description) {
        description.appendText("with fontSize: ")
        description.appendValue(expectedSize)
    }

    override fun matchesSafely(target: View?): Boolean {
        if (target !is TextView) {
            return false
        }
        val targetEditText: TextView = target
        return kotlin.math.abs(targetEditText.textSize - expectedSize) < 1e-5
    }
}