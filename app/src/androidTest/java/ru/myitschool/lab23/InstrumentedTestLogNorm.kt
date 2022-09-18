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
import androidx.test.espresso.*
import androidx.test.espresso.base.DefaultFailureHandler
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasPackage
import androidx.test.espresso.intent.matcher.IntentMatchers.isInternal
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import io.github.kakaocup.kakao.edit.KEditText
import io.github.kakaocup.kakao.recycler.KRecyclerItem
import io.github.kakaocup.kakao.recycler.KRecyclerView
import io.github.kakaocup.kakao.screen.Screen
import io.github.kakaocup.kakao.text.KButton
import io.github.kakaocup.kakao.text.KTextView
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.apache.commons.math3.stat.StatUtils
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.hamcrest.CoreMatchers.anyOf
import org.hamcrest.Matcher
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.security.SecureRandom
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.min
import kotlin.math.sqrt


@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@MediumTest
class InstrumentedTestLogNorm {
    //add/remove seed
    private val random = Random()
    private var outFlag = false
    private var lastNumber = 0.0

    private var limit = 500 // 300 for 1m 36s
    private var index = 0
    private var k = 1
    private var lambda = 1.0

    private val meanDelta = 1e-1
    private val varianceDelta = 0.9
    private val skewnessDelta = 0.9
    private val kurtosisDelta = 0.9 // 3.1

    private var generatedNums = ArrayList<Double>(0)

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

        limit = SecureRandom().nextInt(limit) + 1
        k = random.nextInt(10_000)
        lambda = (SecureRandom().nextDouble() + random.nextDouble() + 1e-3) *
               (random.nextInt(1000) + 1e-1)


        val intent = Intent(appContext, MainActivity::class.java)
        //intent.putExtra("long number", inputNumbers[index])
        Log.d("Tests", "index = $limit $k $lambda")

        activityScenario = ActivityScenario.launch(intent)

        sizeId = appContext.resources
            .getIdentifier("size_param", "id", appContext.opPackageName)
        meanId = appContext.resources
            .getIdentifier("shape_param", "id", appContext.opPackageName)
        varianceId = appContext.resources
            .getIdentifier("rate_param", "id", appContext.opPackageName)
        getRandomNumId = appContext.resources
            .getIdentifier("get_random_nums", "id", appContext.opPackageName)
        recyclerViewId = appContext.resources
            .getIdentifier("generated_list", "id", appContext.opPackageName)
        resultNumId = appContext.resources
            .getIdentifier("random_number_result", "id", appContext.opPackageName)

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
    fun mainTest() {
        addTestToStat(1)
        checkInterface(
            intArrayOf(
                sizeId,
                meanId,
                varianceId,
                getRandomNumId)
        )

        Intents.init()
        /*run {
            mainTestCheckStep()
            addTestToPass(1)
        }*/
        mainTestCheckStep()
        addTestToPass(1)
        Intents.release()
    }

    private fun mainTestCheckStep() {
        class Item(parent: Matcher<View>) : KRecyclerItem<Double>(parent) {
            val name = KTextView(parent) { withId(resultNumId) }
        }
        class SearchScreen : Screen<SearchScreen>() {
            val sizeView = KEditText { withId(sizeId) }
            val meanView = KEditText { withId(meanId) }
            val varianceView = KEditText { withId(varianceId) }
            val getRandomNum = KButton { withId(getRandomNumId) }
            val recyclerView = KRecyclerView(
                builder = { withId(recyclerViewId) },
                itemTypeBuilder = { itemType (::Item) }
            )
        }



        val screen = SearchScreen()
        screen {
            meanView.clearText()
            varianceView.clearText()
            sizeView.typeText("$limit")
            meanView.typeText("$k")
            varianceView.typeText("$lambda")
            closeSoftKeyboard()
            getRandomNum.click()

            Intents.intended(
                anyOf(
                    isInternal(),
                    hasPackage("ru.myitschool.lab23"),
                )
            )

            Log.d("Tests", "${recyclerView.getSize()}")
            assertEquals("List has inappropriate number of elements",
                limit, recyclerView.getSize())
            for (i in 0 until limit) {
                Log.d("Tests", "${i}")
                recyclerView {
                    childAt<Item>(i) {
                        name.assert {
                            isVisible()
                            DoubleComparison(k, lambda, this@InstrumentedTestLogNorm)
                        }
                    }
                }
                /*recyclerView.assert {
                    DoubleComparison(mean, variance, this@InstrumentedTestLogNorm)
                }*/
            }
            // checking saving state after rotation
            rotateDevice(true)
            // resultNum.hasText("$lastNumber")
            rotateDevice(false)
            // resultNum.hasText("$lastNumber")

            // Erlang dist
            checkLogNorm(generatedNums,
                k / lambda,
                k / lambda / lambda,
                2.0 * 1.0 / sqrt(k + 0.0),
                6.0 / k
            )
        }
    }

    fun addGeneratedNumber(e : Double){
        generatedNums.add(e)
    }

    fun setFlag(flag : Boolean){
        outFlag = flag
    }

    fun getFlag(): Boolean{
        return outFlag
    }

    fun setLastNumber(e : Double){
        lastNumber = e
    }

    /**
     * checks mean and std^2 for the whole selection
     * mean and variance
     */
    fun checkLogNorm(a: ArrayList<Double>, m: Double, v: Double, sk: Double, kur: Double){
        val d = a.toDoubleArray()
        Log.d("Tests", "got = $a")
        val gm = StatUtils.mean(d)
        val gv = StatUtils.variance(d)
        val gskewness = DescriptiveStatistics(d).skewness
        val gkurtosis = DescriptiveStatistics(d).kurtosis
        Log.d("Tests",
            "${abs(gm - m)} ${abs(gv - v)} " +
                    "${abs(gskewness - sk)} ${abs(gkurtosis - kur)}"
        )
        assertEquals("Mean is different", m, gm, meanDelta)
        assertEquals("Variance is different", v, gv, varianceDelta)
        assertEquals("Skewness is different", sk, gskewness, skewnessDelta)
        assertEquals("Kurtosis is different", kur, gkurtosis, kurtosisDelta)
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
        totalTests++
        maxGrade += incMaxTotal
    }

    private fun addTestToPass(incGrade: Int) {
        passTests++
        grade += incGrade
    }

    companion object {
        private const val APP_NAME = "Intents + RecyclerView"
        private const val THREAD_DELAY: Long = 10
        private const val MAX_TIMEOUT: Long = 31_000 // 50sec

        private var grade = 0
        private var totalTests = 0
        private var maxGrade = 0
        private var passTests = 0

        private var sizeId = 0
        private var meanId = 0
        private var varianceId = 0
        private var getRandomNumId = 0
        private var recyclerViewId = 0
        private var resultNumId = 0

        @BeforeClass
        @JvmStatic
        fun enableAccessibilityChecks() {
            IdlingPolicies.setMasterPolicyTimeout(5, TimeUnit.SECONDS);
            IdlingPolicies.setIdlingResourceTimeout(5, TimeUnit.SECONDS);
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
    private val mean: Int,
    private val std: Double,
    private val testInstance: InstrumentedTestLogNorm
) :
    ViewAssertion {
    override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
        if (noViewFoundException != null) throw noViewFoundException
        assertTrue(view is TextView)
        val gotValue = (view as TextView).text.toString()

        /*if (testInstance.getFlag() || view.accessibilityClassName == "android.widget.TextView"){
            testInstance.setFlag(true)
        } else {
            assertEquals("View has an incorrect accessibilityClassName", "TextView", "EditText")
        }*/

        val num = gotValue.toDouble()
        testInstance.setLastNumber(num)
        testInstance.addGeneratedNumber(num)
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
                    min(
                        100, error.message?.length ?: 0
                    )
                ) + "...", error.cause
            )

            // Then delegate the error handling to the default handler which will throw an exception
            delegate.handle(newError, viewMatcher)
        }
    }
}
