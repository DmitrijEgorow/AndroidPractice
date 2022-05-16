package ru.myitschool.lab23

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.FailureHandler
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.accessibility.AccessibilityChecks
import androidx.test.espresso.base.DefaultFailureHandler
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.anyIntent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResult
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResultUtils.matchesCheckNames
import io.github.kakaocup.kakao.screen.Screen
import io.github.kakaocup.kakao.text.KButton
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.hamcrest.CoreMatchers.anyOf
import org.hamcrest.Matcher
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.math.BigDecimal
import java.security.SecureRandom
import java.util.*
import kotlin.math.min
import org.hamcrest.CoreMatchers.`is` as iz

// https://github.com/microsoft/surface-duo-dual-screen-experience-example/blob/main/app/src/androidTest/java/com/microsoft/device/samples/dualscreenexperience/HiltJUnitRunner.kt

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
class InstrumentedTestTrickyHexahedron {
    //add/remove seed
    private val random = Random()
    private val securedRandom = SecureRandom()
    private var activityScenario: ActivityScenario<MainActivity>? = null
    private var handler: DescriptionFailureHandler? = null

    private lateinit var appContext: Context
    private lateinit var mInstrumentation: Instrumentation

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


        buttonId = appContext.resources
            .getIdentifier("capture_video", "id", appContext.opPackageName)

    }

    private fun checkInterface(ids: IntArray, message: String = "?") {
        var id = 1
        for (e in ids) {
            id *= e
        }
        if (message != "?") {
            Assert.assertNotEquals(message, 0, id.toLong())
        } else {
            Assert.assertNotEquals(0, id.toLong())
        }

    }


    @Test(timeout = MAX_TIMEOUT)
    fun lengthCheck() {
        addTestToStat(1)
        addTestToStat(1)

        checkInterface(intArrayOf(buttonId))
        Intents.init()
        run {

            //step("Small values") {
            lengthCheckStep()
            addTestToPass(1)
            //step("High precision") {
            //lengthCheckStep()
            addTestToPass(1)
        }

        Intents.release()
    }


    private fun lengthCheckStep() {
        class SearchScreen : Screen<SearchScreen>() {
            val startButton = KButton { withId(buttonId) }
        }

        val screen = SearchScreen()
        screen {
            /*Intents.intending(
                anyIntent()
            ).respondWithFunction { intent ->
                Instrumentation.ActivityResult(Activity.RESULT_CANCELED, intent)
            }*/

            Intents.intending(
                anyIntent()
            ).respondWithFunction { intent ->
                val ur = intent.getParcelableExtra<Uri>(MediaStore.EXTRA_OUTPUT)
                Log.d("Tests", ur.toString())
                Instrumentation.ActivityResult(Activity.RESULT_OK, intent)
            }

            startButton.click()


            /* Intents.intending(CoreMatchers.not(IntentMatchers.isInternal()))
                 .respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))*/



            /*Intents.intended(
                anyOf(
                    hasPackage("com.sec.android.app.camera"),
                    hasExtra("act", "android.media.action.VIDEO_CAPTURE"),
                )
            )*/

            Thread.sleep(10_000)
        }


    }

    @Test(timeout = MAX_TIMEOUT)
    fun aStringsTest() {
        //Check existence of views
        addTestToStat(1)

        var stringRes = intArrayOf(0, 0, 0)

        stringRes[0] = appContext.resources.getIdentifier(
            "side_a_text",
            "string",
            appContext.opPackageName
        )
        stringRes[1] = appContext.resources.getIdentifier(
            "side_b_text",
            "string",
            appContext.opPackageName
        )
        stringRes[2] = appContext.resources.getIdentifier(
            "side_c_text",
            "string",
            appContext.opPackageName
        )

        checkInterface(stringRes, "Do you have required string resources?")

        addTestToPass(1)
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
        private const val APP_NAME = "Lab55"
        private const val THREAD_DELAY: Long = 300
        private const val MAX_TIMEOUT: Long = 50_000

        private var grade = 0
        private var totalTests = 0
        private var maxGrade = 0
        private var passTests = 0

        private var buttonId = 0


        @BeforeClass
        @JvmStatic
        fun enableAccessibilityChecks() {
            AccessibilityChecks.enable()
                .setRunChecksFromRootView(true)
                .setThrowExceptionFor(AccessibilityCheckResult.AccessibilityCheckResultType.WARNING)
                .setThrowExceptionFor(AccessibilityCheckResult.AccessibilityCheckResultType.ERROR)
                .setThrowExceptionFor(AccessibilityCheckResult.AccessibilityCheckResultType.INFO)
                .setSuppressingResultMatcher(
                    matchesCheckNames(
                        anyOf(
                            iz("TouchTargetSizeCheck"),
                            iz("DuplicateSpeakableTextCheck")
                        )

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

class DoubleComparison(
    private val trueValue: BigDecimal, private
    val squared: Boolean = false
) :
    ViewAssertion {
    override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
        if (noViewFoundException != null) throw noViewFoundException
        assertTrue(view is TextView)
        val gotValue = (view as TextView).text.toString().toDouble()

        val flag = if (squared) {
            trueValue
                .minus(BigDecimal(gotValue * gotValue))
                .abs()
                .compareTo(BigDecimal(1e-4))
        } else {
            trueValue
                .minus(BigDecimal(gotValue))
                .abs()
                .compareTo(BigDecimal(1e-4))
        }

        assertEquals(
            "Wrong number: got $gotValue instead of $trueValue",
            -1,
            flag
        )
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
                    min(1100, error.message?.length ?: 0)
                ) + "...", error.cause
            )

            // Then delegate the error handling to the default handler which will throw an exception
            delegate.handle(newError, viewMatcher)
        }
    }
}