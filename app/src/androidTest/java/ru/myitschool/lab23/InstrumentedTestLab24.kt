package ru.myitschool.lab23

import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.FailureHandler
import androidx.test.espresso.accessibility.AccessibilityChecks
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.base.DefaultFailureHandler
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResult.AccessibilityCheckResultType
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResultBaseUtils.matchesCheckNames
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matcher
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.security.SecureRandom
import java.util.*
import kotlin.math.min


@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@MediumTest
class InstrumentedTestLab24 {
    //add/remove seed
    private val random = Random()
    private val securedRandom = SecureRandom()
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
        val interval = random.nextInt(25 - 15) + 15
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
    fun zClipboardTest() {
        //Check existence of views
        addTestToStat(1)
        // Thread.sleep(5_000)


        // type text
        val byteArray = ByteArray(2)
        securedRandom.nextBytes(byteArray)
        val valueTyped = BigInteger(byteArray).abs()
        onView(withTagValue(`is`("et_metre"))).perform(clearText())
        onView(withTagValue(`is`("et_metre"))).perform(typeText(valueTyped.toString()))

        // copy via TextView
        onView(withText("Метр")).perform(click())
        // onView(withTagValue(`is`("km")))
        onView(withTagValue(`is`(editTextTags[lowerB]))).perform(clearText())
        onView(withTagValue(`is`(editTextTags[lowerB]))).perform(click())
        UiDevice
            .getInstance(InstrumentationRegistry.getInstrumentation())
            .pressKeyCode(KeyEvent.KEYCODE_V, KeyEvent.META_CTRL_MASK)
        onView(
            allOf(
                withText(valueTyped.toString()),
                withTagValue(`is`(editTextTags[lowerB]))
            )
        ).check(matches(isDisplayed()))


        // Thread.sleep(10_000)


        addTestToPass(1)
    }


    @Test
    fun bLanguageTest() {
        //Check existence of views
        addTestToStat(1)
        /*onView(withId(mainTextId))
            .check(matches(isDisplayed()))*/
        val mainTextLangId = appContext.resources.getIdentifier(
            "text_view_captions",
            "array",
            appContext.opPackageName
        )

        if (mainTextLangId != 0) {
            val arrayGot = appContext.resources.getStringArray(
                mainTextLangId
            )

            val flag = arrayGot contentDeepEquals textViewContentsEn

            assertEquals(
                "Do you have correct string resources?",
                flag,
                true
            )

            addTestToPass(1)
        } else {
            assertEquals(
                "Do you have correct string resources?",
                "", "?"
            )
        }

    }

    @Test
    fun checkSmallValues() {
        //Check string resource
        addTestToStat(1)
        /*checkInterface(
            intArrayOf(mainTextId)
        )*/

        val valueTyped = random.nextDouble() * 100_000
        onView(withTagValue(`is`("et_metre"))).perform(clearText())
        onView(withTagValue(`is`("et_metre"))).perform(typeText(valueTyped.toString()))

        /*onView(
            allOf(
                withText((valueTyped * converterArray[lowerB]).toString()),
                withTagValue(`is`(editTextTags[lowerB]))
            )
        ).check(matches(isDisplayed()))
        onView(
            allOf(
                withText((valueTyped * converterArray[upperB]).toString()),
                withTagValue(`is`(editTextTags[upperB]))
            )
        ).check(matches(isDisplayed()))*/

        for (i in lowerB..upperB) {
            onView(withTagValue(`is`(editTextTags[i])))
                .check { view, noViewFoundException ->
                    if (noViewFoundException != null) throw noViewFoundException
                    assertTrue(view is EditText)
                    val number = (view as EditText).text.toString().toDouble()
                    assertEquals(
                        "Wrong number in ${editTextTags[i]}",
                        valueTyped, //* converterArray[i],
                        number / converterArray[i],
                        1e-5
                    )
                }
        }


        // Thread.sleep(10_000)


        addTestToPass(1)
    }

    @Test
    fun checkLargeValues() {
        addTestToStat(2)
        // Thread.sleep(3_000)

        //todo with tags
        val byteArray = ByteArray(5)
        securedRandom.nextBytes(byteArray)
        val valueTyped = BigInteger(byteArray).abs()
        onView(withTagValue(`is`("et_metre"))).perform(clearText())
        onView(withTagValue(`is`("et_metre"))).perform(typeText(valueTyped.toString()))

        for (i in lowerB..upperB) {
            // Thread.sleep(1_000)
            onView(withTagValue(`is`(editTextTags[i])))
                .check { view, noViewFoundException ->
                    if (noViewFoundException != null) throw noViewFoundException
                    assertTrue(view is EditText)
                    val number = (view as EditText).text.toString().toDouble()

                    val flag = BigDecimal(number)
                        .divide(
                            BigDecimal(converterArray[i]),
                            7, RoundingMode.HALF_UP
                        )
                        .minus(BigDecimal(valueTyped))
                        .abs()
                        .compareTo(BigDecimal(1e-4))

                    Log.d(
                        "Tests d", BigDecimal(number)
                            .divide(
                                BigDecimal(converterArray[i]),
                                7, RoundingMode.HALF_UP
                            )
                            .minus(BigDecimal(valueTyped))
                            .abs().toString()
                    )

                    assertEquals(
                        "Wrong number in ${editTextTags[i]}: got ${number} instead of ${
                            BigDecimal(valueTyped)
                                .multiply(BigDecimal(converterArray[i]))
                        }",
                        -1,
                        flag
                    )

                    /*assertEquals(
                        "Wrong number in ${editTextTags[i]}",
                        valueTyped * converterArray[i],
                        number,
                        1e-5
                    )*/
                }

        }

        for (i in 0..(lowerB - 1)) {
            // Thread.sleep(1_000)
            onView(withTagValue(`is`(editTextTags[i])))
                .check(doesNotExist());

            onView(withText(textViewContents[i])).check(doesNotExist());
        }

        for (i in (upperB + 1)..24) {
            // Thread.sleep(1_000)
            onView(withTagValue(`is`(editTextTags[i])))
                .check(doesNotExist());
            onView(withText(textViewContents[i])).check(doesNotExist());
        }


        addTestToPass(2)
    }


    @Test
    fun checkCrossConversion() {
        //Check string resource
        addTestToStat(3)

        //todo add printing values to other edittexts

        val byteArray = ByteArray(2)
        securedRandom.nextBytes(byteArray)
        val valueTyped = BigInteger(byteArray).abs()
        val index = random.nextInt(upperB - lowerB + 1) + lowerB
        onView(withTagValue(`is`(editTextTags[index]))).perform(clearText())
        onView(withTagValue(`is`(editTextTags[index]))).perform(
            typeText(
                BigDecimal(converterArray[index])
                    .multiply(BigDecimal(valueTyped))
                    .toString()
            )
        )

        for (i in lowerB..upperB) {
            // Thread.sleep(1_000)
            onView(withTagValue(`is`(editTextTags[i])))
                .check { view, noViewFoundException ->
                    if (noViewFoundException != null) throw noViewFoundException
                    assertTrue(view is EditText)
                    val number = (view as EditText).text.toString().toDouble()

                    val flag = BigDecimal(number)
                        .divide(
                            BigDecimal(converterArray[i]),
                            7, RoundingMode.HALF_UP
                        )
                        .minus(BigDecimal(valueTyped))
                        .abs()
                        .compareTo(BigDecimal(1e-4))

                    Log.d(
                        "Tests d", BigDecimal(number)
                            .divide(
                                BigDecimal(converterArray[i]),
                                7, RoundingMode.HALF_UP
                            )
                            .minus(BigDecimal(valueTyped))
                            .abs().toString()
                    )

                    assertEquals(
                        "Wrong number in ${editTextTags[i]}: got ${number} instead of ${
                            BigDecimal(valueTyped)
                                .multiply(BigDecimal(converterArray[i]))
                        }",
                        -1,
                        flag
                    )
                }

        }
        // Thread.sleep(10_000)

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
        private var textViewContentsEn =
            arrayOf(
                "Inches",
                "Yards",
                "Feet",
                "Miles",
                "Yottametres",
                "Zettametres",
                "Exametres",
                "Petametres",
                "Terametres",
                "Gigametres",
                "Megametres",
                "Kilometres",
                "Hectometres",
                "Decametres",
                "Metres",
                "Decimetres",
                "Centimetres",
                "Millimetres",
                "Micrometres",
                "Nanometres",
                "Picometres",
                "Femtometres",
                "Attometres",
                "Zeptometres",
                "Yoctometres"
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
        private val editTextTags = arrayOf(
            "et_inch",
            "et_yard",
            "et_foot",
            "et_mile",
            "et_yottametre",
            "et_zettametre",
            "et_exametre",
            "et_petametre",
            "et_terametre",
            "et_gigametre",
            "et_megametre",
            "et_kilometre",
            "et_hectometre",
            "et_decametre",
            "et_metre",
            "et_decimetre",
            "et_centimetre",
            "et_millimetre",
            "et_micrometre",
            "et_nanometre",
            "et_picometre",
            "et_femtometre",
            "et_attometre",
            "et_zeptometre",
            "et_yoctometre"
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