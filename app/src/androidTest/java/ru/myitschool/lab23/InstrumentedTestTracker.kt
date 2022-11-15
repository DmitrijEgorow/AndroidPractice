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
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import io.github.kakaocup.kakao.edit.KEditText
import io.github.kakaocup.kakao.recycler.KRecyclerItem
import io.github.kakaocup.kakao.recycler.KRecyclerView
import io.github.kakaocup.kakao.screen.Screen
import io.github.kakaocup.kakao.spinner.KSpinner
import io.github.kakaocup.kakao.spinner.KSpinnerItem
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
import java.util.*
import java.util.concurrent.TimeUnit
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

    private val limit = 2
    private var index = 0
    private var mean = 0.0
    private var variance = 1.0

    private val meanDelta = 1e-1
    private val varianceDelta = 2.1
    private val skewnessDelta = 1.7
    private val kurtosisDelta = 49.7 // 3.1

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

        val intent = Intent(appContext, MainActivity::class.java)
        //intent.putExtra("long number", inputNumbers[index])
        Log.d("Tests", "index = $index")

        activityScenario = ActivityScenario.launch(intent)

        efAmountCardId = appContext.resources
            .getIdentifier("ef_amount_card", "id", appContext.opPackageName)
        efCurrentBalanceTextId = appContext.resources
            .getIdentifier("ef_current_balance_text", "id", appContext.opPackageName)
        efExpensesRvId = appContext.resources
            .getIdentifier("ef_expenses_rv", "id", appContext.opPackageName)
        expenseTypeTextId = appContext.resources
            .getIdentifier("expense_type_text", "id", appContext.opPackageName)
        expenseAmountTextId = appContext.resources
            .getIdentifier("expense_amount_text", "id", appContext.opPackageName)
        typeSpinnerId = appContext.resources
            .getIdentifier("type_spinner", "id", appContext.opPackageName)
        expenseAmountEditTextId = appContext.resources
            .getIdentifier("expense_amount_edit_text", "id", appContext.opPackageName)
        addButtonId = appContext.resources
            .getIdentifier("add_button", "id", appContext.opPackageName)
        addFabId = appContext.resources
            .getIdentifier("add_fab", "id", appContext.opPackageName)

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
                efAmountCardId, efCurrentBalanceTextId, efExpensesRvId, addFabId
            )
        )

        /*run {
            mainTestCheckStep()
            addTestToPass(1)
        }*/
        mainTestCheckStep()
        addTestToPass(1)
    }

    private fun mainTestCheckStep() {
        class Item(parent: Matcher<View>) : KRecyclerItem<Double>(parent) {
            val type = KEditText(parent) { withId(expenseTypeTextId) }
            //todo
            val date = KEditText(parent) { withId(expenseTypeTextId) }
            val amount = KEditText(parent) { withId(expenseAmountTextId) }
        }
        class SearchScreen : Screen<SearchScreen>() {
            val efAmountCard = KTextView { withId(efAmountCardId) }
            val efCurrentBalanceText = KTextView { withId(efCurrentBalanceTextId) }
            val addFab = KButton { withId(addFabId) }

            val typeSpinner = KSpinner(
                builder = { withId(typeSpinnerId) },
                itemTypeBuilder = { itemType(::KSpinnerItem) }
            )
            val expenseAmountEditText = KEditText { withId(expenseAmountEditTextId) }
            val addButton = KButton { withId(addButtonId) }

            val recyclerView = KRecyclerView(
                builder = { withId(efExpensesRvId) },
                itemTypeBuilder = { itemType (::Item) }
            )
        }



        val screen = SearchScreen()
        screen {
            addFab.click()

/*            spinner {
                isVisible()
                hasSize(10)

                open()

                emptyFirstChild {
                    isVisible()
                    hasText("Title 0")
                }

                childAt<KSpinnerItem>(1) {
                    isVisible()
                    hasText("Title 1")
                }

                emptyLastChild {
                    isVisible()
                    hasText("Title 9")
                }

                emptyChildWith {
                    isInstanceOf(String::class.java)
                    equals("Title 5")
                }

                emptyChildAt(4) {
                    isDisplayed()
                    hasText("Title 4")
                    click()
                }

                hasText("Title 4")
            }
        }*/

            typeSpinner {
                isVisible()
                hasSize(2)

                open()

                emptyFirstChild {
                    isVisible()
                    hasText("Income")
                }

                emptyLastChild {
                    isVisible()
                    hasText("Expenses")
                    click()
                }

                hasText("Expenses")
            }
            expenseAmountEditText.typeText("100")
            closeSoftKeyboard()

            addButton.click()

            Log.d("Tests", "${recyclerView.getSize()}")
            assertEquals("List has inappropriate number of elements",
                1//limit
                , recyclerView.getSize())
            for (i in 0 until 1) {
                Log.d("Tests", "${i}")
                recyclerView {
                    childAt<Item>(i) {
                        amount {
                            isVisible()
                            hasText("100.0")
                        }
                    }
                }
                /*recyclerView.assert {
                    DoubleComparison(mean, variance, this@InstrumentedTestLogNorm)
                }*/
            }
            // checking saving state after rotation
            rotateDevice(true)
            rotateDevice(false)

            //todo check

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
        totalTests++
        maxGrade += incMaxTotal
    }

    private fun addTestToPass(incGrade: Int) {
        passTests++
        grade += incGrade
    }

    companion object {
        private const val APP_NAME = "MVVM"
        private const val THREAD_DELAY: Long = 10
        private const val MAX_TIMEOUT: Long = 300_000 // 50sec

        private var grade = 0
        private var totalTests = 0
        private var maxGrade = 0
        private var passTests = 0

        private var efAmountCardId = 0
        private var efCurrentBalanceTextId = 0
        private var efExpensesRvId = 0
        private var expenseTypeTextId = 0
        private var expenseAmountTextId = 0
        private var typeSpinnerId = 0
        private var expenseAmountEditTextId = 0
        private var addButtonId = 0
        private var addFabId = 0

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
                    0,
                    min( //todo change length
                        100000, error.message?.length ?: 0
                    )
                ) + "...", error.cause
            )

            // Then delegate the error handling to the default handler which will throw an exception
            delegate.handle(newError, viewMatcher)
        }
    }
}