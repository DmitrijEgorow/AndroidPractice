package ru.myitschool.lab23

import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.FailureHandler
import androidx.test.espresso.accessibility.AccessibilityChecks
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.base.DefaultFailureHandler
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResult.AccessibilityCheckResultType
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResultBaseUtils.matchesCheckNames
import org.hamcrest.CoreMatchers.`is`
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
        val lowerBound = random.nextInt(15)
        val interval = random.nextInt(21 - 9) + 9
        val upperBound = min(24, lowerBound + interval)

        intent.putExtra("param", "TestString")
        intent.putExtra("lower", lowerBound)
        intent.putExtra("upper", upperBound)
        lowerB = lowerBound
        upperB = upperBound
        Log.d("Tests", "${lowerBound} ${upperBound}")
        activityScenario = ActivityScenario.launch(intent)

        mainTextId = appContext.resources
            .getIdentifier("main_text", "id", appContext.opPackageName)
        outerLayoutId = appContext.resources
            .getIdentifier("outer_layout", "id", appContext.opPackageName)

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
        Thread.sleep(5_000)


        //todo with tags
        //onView(withTa)
        onView(withText("Метр")).perform(click())
        // onView(withTagValue(`is`("km")))
        onView(withTagValue(`is`("et_metre"))).perform(click())
        UiDevice
            .getInstance(InstrumentationRegistry.getInstrumentation())
            .pressKeyCode(KeyEvent.KEYCODE_V, KeyEvent.META_CTRL_MASK)
        onView(withText("textt")).check(matches(isDisplayed()))


        Thread.sleep(10_000)


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


        addTestToPass(1)
    }

    @Test
    fun checkResPortait() {
        addTestToStat(2)
        checkInterface(
            intArrayOf(mainTextId)
        )



        addTestToPass(2)
    }


    @Test
    fun checkResLandscape() {
        //Check string resource
        addTestToStat(3)


        checkInterface(
            intArrayOf(mainTextId)
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
        private const val MAIN_TEXT_ENG = "Richard Of York Gave Battle In Vain"
        private const val EMPTY_STRING = ""

        private var grade = 0
        private var totalTests = 0
        private var maxGrade = 0
        private var passTests = 0

        private var mainTextId = 0
        private var outerLayoutId = 0

        private var lowerB = 0
        private var upperB = 25
        private var textViewContents =
            arrayOf(
                "Дюйм",
                "Ярд",
                "Фут",
                "Миля",
                "Иоттаметр",
                "Зеттаметр",
                "Эксаметр",
                "Петаметр",
                "Тераметр",
                "Гигаметр",
                "Мегаметр",
                "Километр",
                "Гектометр",
                "Декаметр",
                "Метр",
                "Дециметр",
                "Сантиметр",
                "Миллиметр",
                "Микрометр",
                "Нанометр",
                "Пикометр",
                "Фемтометр",
                "Аттометр",
                "Зептометр",
                "Иоктометр"
            )
        private val converterArray = doubleArrayOf(
            39.37007874015748031,
            1.093613298337707787,
            3.280839895013123360,
            0.000621371192237330,
            1e-10,
            1e-9,
            1e-8,
            1e-7,
            1e-6,
            1e-5,
            1e-4,
            1e-3,
            1e-2,
            1e-1,
            1.0,
            10.0,
            100.0,
            1000.0,
            10000.0,
            100000.0,
            1000000.0,
            10000000.0,
            100000000.0,
            1000000000.0,
            10000000000.0
        )

        @BeforeClass
        @JvmStatic
        fun enableAccessibilityChecks() {
            AccessibilityChecks.enable()
                .setRunChecksFromRootView(true)
                .setThrowExceptionFor(AccessibilityCheckResultType.WARNING)
                .setThrowExceptionFor(AccessibilityCheckResultType.ERROR)
                .setThrowExceptionFor(AccessibilityCheckResultType.INFO)
                .setSuppressingResultMatcher(
                    matchesCheckNames(
                        `is`
                            ("TouchTargetSizeCheck")
                    )
                )
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
                    min(1000, error.message?.length ?: 0)
                ) + "...", error.cause
            )

            // Then delegate the error handling to the default handler which will throw an exception
            delegate.handle(newError, viewMatcher)
        }
    }
}