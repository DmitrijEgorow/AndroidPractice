package ru.myitschool.lab23

import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.FailureHandler
import androidx.test.espresso.accessibility.AccessibilityChecks
import androidx.test.espresso.base.DefaultFailureHandler
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResult
import org.hamcrest.Matcher
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.security.SecureRandom
import java.util.*
import kotlin.math.min


@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@MediumTest
class InstrumentedTestTrickyHexahedron {
    //add/remove seed
    private val random = Random()
    private val securedRandom = SecureRandom()
    private var activityScenario: ActivityScenario<MainActivity>? = null
    private var handler: DescriptionFailureHandler? = null

    private lateinit var appContext: Context
    private lateinit var mInstrumentation: Instrumentation

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

    }

    private fun checkInterface(ids: IntArray) {
        var id = 1
        for (e in ids) {
            id *= e
        }
        Assert.assertNotEquals(0, id.toLong())
    }

    @Test
    fun zClipboardTest() {
        //Check existence of views
        addTestToStat(1)
        // Thread.sleep(5_000)



        UiDevice
            .getInstance(InstrumentationRegistry.getInstrumentation())
            .pressKeyCode(KeyEvent.KEYCODE_V, KeyEvent.META_CTRL_MASK)


        addTestToPass(1)
    }

    @Test
    fun checkSmallValues() {
        //Check string resource
        addTestToStat(1)
        checkInterface(
            intArrayOf(mainTextId)
        )



        addTestToPass(1)
    }

    @Test
    fun checkLargeValues() {
        addTestToStat(2)
        Thread.sleep(3_000)



        addTestToPass(2)
    }


    @Test
    fun checkCrossConversion() {
        //Check string resource
        addTestToStat(3)



        addTestToPass(3)
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
        private const val APP_NAME = "Lab28"
        private const val THREAD_DELAY: Long = 300

        private var grade = 0
        private var totalTests = 0
        private var maxGrade = 0
        private var passTests = 0

        private var mainTextId = 0
        private var outerLayoutId = 0


        @BeforeClass
        @JvmStatic
        fun enableAccessibilityChecks() {
            AccessibilityChecks.enable()
                .setRunChecksFromRootView(true)
                .setThrowExceptionFor(AccessibilityCheckResult.AccessibilityCheckResultType.WARNING)
                .setThrowExceptionFor(AccessibilityCheckResult.AccessibilityCheckResultType.ERROR)
                .setThrowExceptionFor(AccessibilityCheckResult.AccessibilityCheckResultType.INFO)
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