package ru.myitschool.lab23

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.VideoView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.FailureHandler
import androidx.test.espresso.accessibility.AccessibilityChecks
import androidx.test.espresso.base.DefaultFailureHandler
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.anyIntent
import androidx.test.espresso.screenshot.captureToBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResult
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResultUtils.matchesCheckNames
import io.github.kakaocup.kakao.common.views.KView
import io.github.kakaocup.kakao.screen.Screen
import io.github.kakaocup.kakao.text.KButton
import junit.framework.Assert.assertEquals
import org.hamcrest.CoreMatchers.anyOf
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.Matcher
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.util.*
import kotlin.math.abs
import kotlin.math.min
import org.hamcrest.CoreMatchers.`is` as iz


// https://github.com/microsoft/surface-duo-dual-screen-experience-example/blob/main/app/src/androidTest/java/com/microsoft/device/samples/dualscreenexperience/HiltJUnitRunner.kt

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
class InstrumentedTestTrickyHexahedron {
    private var activityScenario: ActivityScenario<MainActivity>? = null
    private var handler: DescriptionFailureHandler? = null

    private lateinit var appContext: Context
    private lateinit var mInstrumentation: Instrumentation


    @get:Rule
    var permissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)


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
        videoViewId = appContext.resources
            .getIdentifier("video_view", "id", appContext.opPackageName)

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
        addTestToStat(2)

        checkInterface(intArrayOf(buttonId, videoViewId))
        Intents.init()
        run {
            lengthCheckStep()
            addTestToPass(2)
        }
        Intents.release()
    }


    private fun lengthCheckStep() {
        class SearchScreen : Screen<SearchScreen>() {
            val startButton = KButton { withId(buttonId) }
            val videoView = KView { withId(videoViewId) }
        }

        val screen = SearchScreen()
        screen {
            Intents.intending(
                anyIntent()
            ).respondWithFunction { intent ->
                val ur = intent.getParcelableExtra<Uri>(MediaStore.EXTRA_OUTPUT)

                if (ur != null) {
                    Log.d("Tests", ur.toString() + "\t" + ur?.path)
                    val inp = appContext.assets.open("net.mp4")
                    Log.d("Tests", inp.toString())
                    appContext.contentResolver.openOutputStream(ur)?.let { inp.copyTo(it) }
                }
                Instrumentation.ActivityResult(Activity.RESULT_OK, intent)
            }

            startButton.click()

            videoView.click()
            Thread.sleep(BUFFER_QUEUE_TOLERANCE) // 300
            Thread.sleep(3_000)
            val beforeBitmap = onView(
                instanceOf(VideoView::class.java)
            ).captureToBitmap()
            Thread.sleep(3_000)
            // 6 sec
            val afterBitmap = onView(
                instanceOf(VideoView::class.java)
            ).captureToBitmap()
            Thread.sleep(3_000)
            // 9 sec
            val afterBitmap1 = onView(
                instanceOf(VideoView::class.java)
            ).captureToBitmap()
            Thread.sleep(4_000)
            // 13 sec
            val afterBitmapNot = onView(
                instanceOf(VideoView::class.java)
            ).captureToBitmap()
            assertEquals(true, afterBitmap.same(afterBitmap1))
            assertEquals(false, afterBitmap.same(afterBitmapNot))

            /*Intents.intended(
                anyOf(
                    hasPackage("com.sec.android.app.camera"),
                    hasExtra("act", "android.media.action.VIDEO_CAPTURE"),
                )
            )*/
        }
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
        private const val APP_NAME = "Lab55Camera"
        private const val THREAD_DELAY: Long = 300
        private const val BUFFER_QUEUE_TOLERANCE: Long = 300
        private const val MAX_TIMEOUT: Long = 25_000

        private var grade = 0
        private var totalTests = 0
        private var maxGrade = 0
        private var passTests = 0

        private var buttonId = 0
        private var videoViewId = 0


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

/**
 * @param eps shows whether a particular pixel differs in the bitmaps
 * @param tol checks whether different pixels are prevalent among all
 */
private fun Bitmap.same(bitmap: Bitmap, eps: Double = 0.01, tol: Double = 0.89): Boolean {
    // Different types of image
    if (this.config !== bitmap.config) return false
    // Different sizes
    if (this.width != bitmap.width) return false

    if (this.height != bitmap.height) return false

    val w: Int = this.width
    val h: Int = this.height

    val argbA = IntArray(w * h)
    val argbB = IntArray(w * h)

    this.getPixels(argbA, 0, w, 0, 0, w, h)
    bitmap.getPixels(argbB, 0, w, 0, 0, w, h)
    var counter = 0
    if (bitmap.config === Bitmap.Config.ARGB_8888) {
        val length = w * h
        for (i in 0 until length) {
            if (abs(abs(argbA[i] - argbB[i] + 1e-8) / argbB[i]) > eps) {
                counter++
            }
        }
        Log.d(
            "Tests",
            "$counter $length"
        )
        if (abs(length - counter + 1e-8) / length < tol) {
            return false
        }
        return true
    }
    return argbA.contentEquals(argbB)
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