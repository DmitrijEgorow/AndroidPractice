package ru.myitschool.lab23

import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.FailureHandler
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.accessibility.AccessibilityChecks
import androidx.test.espresso.assertion.PositionAssertions.isCompletelyBelow
import androidx.test.espresso.base.DefaultFailureHandler
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResult
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResultUtils.matchesCheckNames
import io.github.kakaocup.kakao.edit.KEditText
import io.github.kakaocup.kakao.spinner.KSpinner
import io.github.kakaocup.kakao.spinner.KSpinnerItem
import io.github.kakaocup.kakao.text.KButton
import io.github.kakaocup.kakao.text.KTextView
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.hamcrest.CoreMatchers.anyOf
import org.hamcrest.Matcher
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
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

    private val upperBoundInBytesSmall = 2

    // todo change to 4
    private val upperBoundInBytesLarge = 5

    private val dividingFactorSmall = 10_000
    private val dividingFactorLarge = 10_000_000

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


        spinnerId = appContext.resources
            .getIdentifier("spinner", "id", appContext.opPackageName)
        buttonId = appContext.resources
            .getIdentifier("calculate", "id", appContext.opPackageName)
        answerId = appContext.resources
            .getIdentifier("solution", "id", appContext.opPackageName)

        editTextIds[0] = appContext.resources
            .getIdentifier("side_a", "id", appContext.opPackageName)
        editTextIds[1] = appContext.resources
            .getIdentifier("side_b", "id", appContext.opPackageName)
        editTextIds[2] = appContext.resources
            .getIdentifier("side_c", "id", appContext.opPackageName)
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


    @Test
    fun lengthCheck() {
        addTestToStat(1)
        addTestToStat(1)
        checkInterface(intArrayOf(spinnerId, buttonId, answerId))
        checkInterface(editTextIds)
        run {

            //step("Small values") {
            lengthCheckStep(upperBoundInBytesSmall, dividingFactorSmall)
            addTestToPass(1)
            //step("High precision") {
            lengthCheckStep(upperBoundInBytesLarge, dividingFactorLarge)
            addTestToPass(1)
        }
    }

    @Test
    fun diagonalCheck() {
        addTestToStat(2)
        addTestToStat(2)
        checkInterface(intArrayOf(spinnerId, buttonId, answerId))
        checkInterface(editTextIds)
        run {

            //step("Small values") {
            diagonalCheckStep(upperBoundInBytesSmall, dividingFactorSmall)
            addTestToPass(2)
            //step("High precision") {
            diagonalCheckStep(upperBoundInBytesLarge, dividingFactorLarge)
            addTestToPass(2)
        }
    }

    @Test
    fun areaCheck() {
        addTestToStat(1)
        addTestToStat(1)
        checkInterface(intArrayOf(spinnerId, buttonId, answerId))
        checkInterface(editTextIds)
        run {

            //step("Small values") {
            areaCheckStep(upperBoundInBytesSmall, dividingFactorSmall)
            addTestToPass(1)
            //step("High precision") {
            areaCheckStep(upperBoundInBytesLarge, dividingFactorLarge)
            addTestToPass(1)
        }
    }

    @Test
    fun volumeCheck() {
        addTestToStat(1)
        addTestToStat(1)
        checkInterface(intArrayOf(spinnerId, buttonId, answerId))
        checkInterface(editTextIds)
        run {

            //step("Small values") {
            volumeCheckStep(upperBoundInBytesSmall, dividingFactorSmall)
            addTestToPass(1)
            //step("High precision") {
            volumeCheckStep(upperBoundInBytesLarge, dividingFactorLarge)
            addTestToPass(1)
        }
    }


    /**
    @param [upperBoundInBytes] -- 2 bytes for intermediate tests
    @param [dividingFactor] -- 10000 for intermediate test
     */
    private fun lengthCheckStep(upperBoundInBytes: Int, dividingFactor: Int) {
        KSpinner(builder = { withId(spinnerId) },
            itemTypeBuilder = { itemType(::KSpinnerItem) }) perform {
            open()
            childAt<KSpinnerItem>(0) {
                click()
            }
        }

        val printedValues = arrayOf(BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE)
        for (i in 0..2) {
            // type text
            val byteArray = ByteArray(upperBoundInBytes)
            securedRandom.nextBytes(byteArray)
            val intGenerated = BigInteger(byteArray).abs()
            val floatValue = BigDecimal(intGenerated)
                .divide(
                    BigDecimal(random.nextInt(dividingFactor) + 1),
                    6, RoundingMode.HALF_UP
                )
            printedValues[i] = floatValue

            KEditText {
                withId(editTextIds[i])
            } perform {
                clearText()
                typeText(floatValue.toString())
            }
        }

        //Thread.sleep(3_000)

        KButton {
            withId(buttonId)
        } perform {
            click()
        }

        KTextView { withId(answerId) }.assert {
            DoubleComparison(
                printedValues[0].add(printedValues[1]).add(printedValues[2])
                    .multiply(BigDecimal(4))
            )
        }
    }

    /**
    @param [upperBoundInBytes] -- 2 bytes for intermediate tests
    @param [dividingFactor] -- 10000 for intermediate test
     */
    private fun diagonalCheckStep(upperBoundInBytes: Int, dividingFactor: Int) {
        KSpinner(builder = { withId(spinnerId) },
            itemTypeBuilder = { itemType(::KSpinnerItem) }) perform {
            open()
            childAt<KSpinnerItem>(1) {
                click()
            }
        }

        val printedValues = arrayOf(BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE)
        for (i in 0..2) {
            // type text
            val byteArray = ByteArray(upperBoundInBytes)
            securedRandom.nextBytes(byteArray)
            val intGenerated = BigInteger(byteArray).abs()
            val floatValue = BigDecimal(intGenerated)
                .divide(
                    BigDecimal(random.nextInt(dividingFactor) + 1),
                    6, RoundingMode.HALF_UP
                )
            printedValues[i] = floatValue

            KEditText {
                withId(editTextIds[i])
            } perform {
                clearText()
                typeText(floatValue.toString())
            }
        }

        //Thread.sleep(3_000)

        KButton {
            withId(buttonId)
        } perform {
            click()
        }

        KTextView { withId(answerId) }.assert {
            DoubleComparison(
                printedValues[0].pow(2)
                    .add(printedValues[1].pow(2))
                    .add(printedValues[2].pow(2)),
                true
            )
        }

        // checks clipboard
        KTextView {
            withId(answerId)
        } perform {
            click()
        }
        KEditText {
            withId(editTextIds[0])
        } perform {
            clearText()
            click()
        }
        UiDevice
            .getInstance(InstrumentationRegistry.getInstrumentation())
            .pressKeyCode(KeyEvent.KEYCODE_V, KeyEvent.META_CTRL_MASK)

        KTextView { withId(editTextIds[0]) }.assert {
            DoubleComparison(
                printedValues[0].pow(2)
                    .add(printedValues[1].pow(2))
                    .add(printedValues[2].pow(2)),
                true
            )
        }
    }

    /**
    @param [upperBoundInBytes] -- 2 bytes for intermediate tests
    @param [dividingFactor] -- 10000 for intermediate test
     */
    private fun areaCheckStep(upperBoundInBytes: Int, dividingFactor: Int) {
        KSpinner(builder = { withId(spinnerId) },
            itemTypeBuilder = { itemType(::KSpinnerItem) }) perform {
            open()
            childAt<KSpinnerItem>(2) {
                click()
            }
        }

        val printedValues = arrayOf(BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE)
        for (i in 0..2) {
            // type text
            val byteArray = ByteArray(upperBoundInBytes)
            securedRandom.nextBytes(byteArray)
            val intGenerated = BigInteger(byteArray).abs()
            val floatValue = BigDecimal(intGenerated)
                .divide(
                    BigDecimal(random.nextInt(dividingFactor) + 1),
                    6, RoundingMode.HALF_UP
                )
            printedValues[i] = floatValue

            KEditText {
                withId(editTextIds[i])
            } perform {
                clearText()
                typeText(floatValue.toString())
            }
        }

        // Thread.sleep(3_000)

        KButton {
            withId(buttonId)
        } perform {
            click()
        }

        KTextView { withId(answerId) }.assert {
            DoubleComparison(
                (printedValues[0].multiply(printedValues[1]))
                    .add(printedValues[1].multiply(printedValues[2]))
                    .add(printedValues[2].multiply(printedValues[0]))
                    .multiply(BigDecimal(2))
            )
        }
    }

    /**
    @param [upperBoundInBytes] -- 2 bytes for intermediate tests
    @param [dividingFactor] -- 10000 for intermediate test
     */
    private fun volumeCheckStep(upperBoundInBytes: Int, dividingFactor: Int) {
        KSpinner(builder = { withId(spinnerId) },
            itemTypeBuilder = { itemType(::KSpinnerItem) }) perform {
            open()
            childAt<KSpinnerItem>(3) {
                click()
            }
        }

        val printedValues = arrayOf(BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE)
        for (i in 0..2) {
            // type text
            val byteArray = ByteArray(upperBoundInBytes)
            securedRandom.nextBytes(byteArray)
            val intGenerated = BigInteger(byteArray).abs()
            val floatValue = BigDecimal(intGenerated)
                .divide(
                    BigDecimal(random.nextInt(dividingFactor) + 1),
                    6, RoundingMode.HALF_UP
                )
            printedValues[i] = floatValue

            KEditText {
                withId(editTextIds[i])
            } perform {
                clearText()
                typeText(floatValue.toString())
            }
        }

        // Thread.sleep(3_000)

        KButton {
            withId(buttonId)
        } perform {
            click()
        }

        KTextView { withId(answerId) }.assert {
            DoubleComparison(
                printedValues[0].multiply(printedValues[1]).multiply(printedValues[2])
            )
        }
    }


    @Test
    fun interfaceTest() {
        addTestToStat(1)

        checkInterface(editTextIds)


        for ((i, e) in editTextIds.withIndex()) {

            if (i + 1 < editTextIds.size) {
                KEditText {
                    withId(editTextIds[i + 1])
                    isDisplayed()
                    isCompletelyBelow(
                        ViewMatchers.withId(editTextIds[i])
                    )
                }
            }
            /*onView(withId(rainbowIds[i]))
                .check(matches(isDisplayed()))
            if (i + 1 < colors.size) {
                onView(withId(rainbowIds[i + 1]))
                    .check(
                        isCompletelyBelow(
                            withId(rainbowIds[i])
                        )
                    )
            }*/
        }

        addTestToPass(1)
    }

    @Test
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
        private const val APP_NAME = "Lab28"
        private const val THREAD_DELAY: Long = 300

        private var grade = 0
        private var totalTests = 0
        private var maxGrade = 0
        private var passTests = 0

        private var spinnerId = 0
        private var buttonId = 0
        private var answerId = 0
        private var editTextIds = intArrayOf(0, 0, 0)


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