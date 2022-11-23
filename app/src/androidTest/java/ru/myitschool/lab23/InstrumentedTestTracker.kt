package ru.myitschool.lab23

import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.*
import androidx.test.espresso.base.DefaultFailureHandler
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import io.github.kakaocup.kakao.edit.KEditText
import io.github.kakaocup.kakao.image.KImageView
import io.github.kakaocup.kakao.recycler.KRecyclerItem
import io.github.kakaocup.kakao.recycler.KRecyclerView
import io.github.kakaocup.kakao.screen.Screen
import io.github.kakaocup.kakao.spinner.KSpinner
import io.github.kakaocup.kakao.spinner.KSpinnerItem
import io.github.kakaocup.kakao.text.KButton
import io.github.kakaocup.kakao.text.KTextView
import junit.framework.TestCase.assertEquals
import org.hamcrest.Matcher
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.min


@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@MediumTest
class InstrumentedTestTracker {

    private var activityScenario: ActivityScenario<MainActivity>? = null
    private var handler: DescriptionFailureHandler? = null
    private var uiDevice: UiDevice? = null

    private lateinit var appContext: Context
    private lateinit var mInstrumentation: Instrumentation

    @Before
    fun setUp() {
        mInstrumentation = InstrumentationRegistry.getInstrumentation()
        handler = DescriptionFailureHandler(mInstrumentation)
        uiDevice = UiDevice.getInstance(mInstrumentation)
        Espresso.setFailureHandler(handler)

        val nonLocalizedContext = mInstrumentation.targetContext
        val configuration = nonLocalizedContext.resources.configuration
        configuration.setLocale(Locale.UK)
        //configuration.setLayoutDirection(Locale.UK)
        appContext = nonLocalizedContext.createConfigurationContext(configuration)

        val intent = Intent(appContext, MainActivity::class.java)

        activityScenario = ActivityScenario.launch(intent)

        imageId =
            appContext.resources.getIdentifier("box_image", "id", appContext.opPackageName)
        buttonId =
            appContext.resources.getIdentifier("test_button", "id", appContext.opPackageName)


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
    fun aPortraitOrientationTest() {
        addTestToStat(1)
        checkInterface(
            intArrayOf(
                imageId, buttonId
            ), CHECK_INTERFACE_MESSAGE
        )
        checkStep(false)
        addTestToPass(1)
    }

    @Test(timeout = MAX_TIMEOUT)
    fun bLandscapeOrientationTest() {
        //
        addTestToStat(1)
        checkInterface(
            intArrayOf(
                imageId, buttonId
            ), CHECK_INTERFACE_MESSAGE
        )
        checkStep(true)
        addTestToPass(1)
    }


    @Test(timeout = MAX_TIMEOUT)
    fun cStringTest() {
        addTestToStat(1)
        Assert.assertNotEquals(
            "Mismatch exception: string resource does not exist",
            0,
            appContext.resources.getIdentifier(
                "main_text_button",
                "string",
                appContext.opPackageName
            ).toLong()
        )
        addTestToPass(1)
    }

    private fun checkStep(needsRotate : Boolean) {

        class SearchScreen : Screen<SearchScreen>() {
            val button = KButton { withId(buttonId) }
            val image = KImageView { withId(imageId) }
        }


        val screen = SearchScreen()
        screen {
            val orange =
                appContext.resources
                    .getIdentifier("orange", "drawable", appContext.opPackageName)
            val blue =
                appContext.resources
                    .getIdentifier("blue", "drawable", appContext.opPackageName)

            image.matches { isDisplayed() }
            button.matches { isDisplayed() }

            image.isCompletelyAbove { withId(buttonId) }
            button.isCompletelyBelow { withId(imageId) }
            image.hasDrawable(orange, null)

            // checks ui after rotation
            if (needsRotate) {
                rotateDevice(true)
                Thread.sleep(THREAD_DELAY)

                image.matches { isDisplayed() }
                button.matches { isDisplayed() }
                image.isCompletelyLeftOf { withId(buttonId) }
                button.isCompletelyRightOf { withId(imageId) }

                image.hasDrawable(blue, null)

                rotateDevice(false)
            }


        }
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
    }

    private fun addTestToStat(incMaxTotal: Int) {
        // totalTests++
        maxGrade += incMaxTotal
    }

    private fun addTestToPass(incGrade: Int) {
        // passTests++
        grade += incGrade
        if (grade == maxGrade) {
            passTests = 1
        }
    }

    companion object {
        private const val APP_NAME = "Context Menu"
        private const val CHECK_INTERFACE_MESSAGE = "Some UI elements seems to be missing"
        private const val THREAD_DELAY: Long = 1_500
        private const val MAX_TIMEOUT: Long = 10_000 // 20sec

        private var grade = 0
        private var totalTests = 1
        private var maxGrade = 0
        private var passTests = 0

        private var buttonId = 0
        private var imageId = 0

        @BeforeClass
        @JvmStatic
        fun enableAccessibilityChecks() {
            IdlingPolicies.setMasterPolicyTimeout(5, TimeUnit.SECONDS);
            IdlingPolicies.setIdlingResourceTimeout(5, TimeUnit.SECONDS);
            val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
            uiDevice.pressHome()
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
                    0, min( //todo change length
                        130, error.message?.length ?: 0
                    )
                ) + "...", error.cause
            )

            // Then delegate the error handling to the default handler which will throw an exception
            delegate.handle(newError, viewMatcher)
        }
    }
}