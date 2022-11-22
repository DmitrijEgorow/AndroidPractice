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
import io.github.kakaocup.kakao.edit.KEditText
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
    //add/remove seed
    private val random = Random()
    private var outFlag = false
    private var lastNumber = 0.0

    private val limit = 13
    private var index = 0
    private var count = 0
    private var countNegative = 0

    private val inputValue = 1000
    private val inputDoubleText = "1000.0"
    private val inputText = "1000"

    private val income = "Income"
    private val expenses = "Expenses"

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
        //intent.putExtra("long number", inputNumbers[index])
        Log.d("Tests", "index = $index")

        activityScenario = ActivityScenario.launch(intent)

        efCurrentBalanceTextId = appContext.resources.getIdentifier(
            "ef_current_balance_text",
            "id",
            appContext.opPackageName
        )
        efExpensesRvId =
            appContext.resources.getIdentifier("ef_expenses_rv", "id", appContext.opPackageName)
        expenseTypeTextId =
            appContext.resources.getIdentifier("expense_type_text", "id", appContext.opPackageName)
        expenseDateTextId =
            appContext.resources.getIdentifier("expense_date_text", "id", appContext.opPackageName)
        expenseAmountTextId = appContext.resources.getIdentifier(
            "expense_amount_text",
            "id",
            appContext.opPackageName
        )
        typeSpinnerId =
            appContext.resources.getIdentifier("type_spinner", "id", appContext.opPackageName)
        expenseAmountEditTextId = appContext.resources.getIdentifier(
            "expense_amount_edit_text",
            "id",
            appContext.opPackageName
        )
        addButtonId =
            appContext.resources.getIdentifier("add_button", "id", appContext.opPackageName)
        addFabId = appContext.resources.getIdentifier("add_fab", "id", appContext.opPackageName)

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
        //
        addTestToStat(limit + 3)
        checkInterface(
            intArrayOf(
                efCurrentBalanceTextId, efExpensesRvId, addFabId
            ), CHECK_INTERFACE_MESSAGE
        )

        /*run {
            mainTestCheckStep()
            addTestToPass(1)
        }*/
        mainTestCheckStep()
        //addTestToPass(1)
    }

    private fun mainTestCheckStep() {
        class Item(parent: Matcher<View>) : KRecyclerItem<Double>(parent) {
            val type = KEditText(parent) { withId(expenseTypeTextId) }

            //todo
            val date = KEditText(parent) { withId(expenseDateTextId) }
            val amount = KEditText(parent) { withId(expenseAmountTextId) }
        }

        class SearchScreen : Screen<SearchScreen>() {
            val efCurrentBalanceText = KTextView { withId(efCurrentBalanceTextId) }
            val addFab = KButton { withId(addFabId) }

            val typeSpinner = KSpinner(builder = { withId(typeSpinnerId) },
                itemTypeBuilder = { itemType(::KSpinnerItem) })
            val expenseAmountEditText = KEditText { withId(expenseAmountEditTextId) }
            val addButton = KButton { withId(addButtonId) }

            val recyclerView = KRecyclerView(builder = { withId(efExpensesRvId) },
                itemTypeBuilder = { itemType(::Item) })
        }


        val screen = SearchScreen()
        screen {

            fun inputIterations(isExpensesType: Boolean = true) {
                for (i in 1 until limit + 1) {
                    addFab.click()

                    typeSpinner {
                        isVisible()
                        hasSize(2)
                        open()
                        emptyFirstChild {
                            isVisible()
                            hasText(income)
                        }
                        emptyLastChild {
                            isVisible()
                            hasText(expenses)
                            click()
                        }
                        hasText(expenses)
                    }
                    expenseAmountEditText.typeText(inputText)
                    count++
                    closeSoftKeyboard()

                    addButton.click()

                    addTestToPass(1) // depends on limit

                    Log.d("Tests", "${recyclerView.getSize()}")
                    assertEquals(
                        "List has inappropriate number of elements", count, recyclerView.getSize()
                    )
                    Log.d("Tests", "$i")
                    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.UK)
                    val date: String = sdf.format(System.currentTimeMillis())
                    recyclerView {
                        childAt<Item>(count - 1) {
                            amount {
                                isVisible()
                                hasText(inputDoubleText)
                            }
                            date {
                                isVisible()
                                hasText(date)
                            }
                            type {
                                isVisible()
                                hasText(expenses)
                            }
                        }
                    }
                    efCurrentBalanceText.hasText("-${inputValue * count}.0")

                    if ((i % 2 == 0) and (count > 0)) {
                        handler?.extraMessage =
                            "Attempt to delete an element with index ${count - 1}, len = ${recyclerView.getSize()}"
                        recyclerView {
                            childAt<Item>(count - 1) {
                                amount {
                                    longClick()
                                }
                            }
                        }
                        uiDevice?.findObject(By.textStartsWith("Delete"))?.click()
                        count--
                        Thread.sleep(THREAD_DELAY)
                        efCurrentBalanceText.hasText("-${inputValue * count}.0")
                        handler?.extraMessage = ""
                    }

                    if ((i % 3 == 0) and (count > 0)) {
                        handler?.extraMessage =
                            "Attempt to duplicate an element with index ${count - 1}, len = ${recyclerView.getSize()}"
                        recyclerView {
                            childAt<Item>(count - 1) {
                                amount {
                                    longClick()
                                }
                            }
                        }
                        uiDevice?.findObject(By.textStartsWith("Duplicate"))?.click()
                        count++
                        Thread.sleep(THREAD_DELAY)
                        efCurrentBalanceText.hasText("-${inputValue * count}.0")
                        handler?.extraMessage = ""
                    }

                    if ((i % 5 == 0) and (count > 0)) {
                        handler?.extraMessage =
                            "Attempt to delete an element with index 0, len = ${recyclerView.getSize()}"
                        recyclerView {
                            childAt<Item>(0) {
                                amount {
                                    longClick()
                                }
                            }
                        }
                        uiDevice?.findObject(By.textStartsWith("Delete"))?.click()
                        count--
                        Thread.sleep(THREAD_DELAY)
                        efCurrentBalanceText.hasText("-${inputValue * count}.0")
                        handler?.extraMessage = ""
                    }
                }
            }

            inputIterations()

            countNegative = count
            for (i in 0 until 2) {
                addFab.click()

                typeSpinner {
                    isVisible()
                    hasSize(2)
                    open()
                    emptyLastChild {
                        isVisible()
                        hasText(expenses)
                    }
                    emptyFirstChild {
                        isVisible()
                        hasText(income)
                        click()
                    }
                    hasText(income)
                }
                expenseAmountEditText.typeText("${(i + 1) * 2 * inputValue}")
                count++
                closeSoftKeyboard()

                addButton.click()

                Log.d("Tests", "${recyclerView.getSize()}")
                assertEquals(
                    "List has inappropriate number of elements", count, recyclerView.getSize()
                )
                Log.d("Tests", "$i")
                val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.UK)
                val date: String = sdf.format(System.currentTimeMillis())
                recyclerView {
                    scrollTo(count - 1)
                    lastChild<Item> {
                        amount {
                            isVisible()
                            hasText("${(i + 1) * 2 * inputValue}.0")
                        }
                        date {
                            isVisible()
                            hasText(date)
                        }
                        type {
                            isVisible()
                            hasText(income)
                        }
                    }
                }
                if (i == 0) {
                    efCurrentBalanceText.hasText("${-inputValue * countNegative + 2 * inputValue}.0")
                } else {
                    efCurrentBalanceText.hasText("${-inputValue * countNegative + 2 * inputValue + 2 * 2 * inputValue}.0")
                }
                addTestToPass(1)
            }

            // checks saving state after rotation
            rotateDevice(true)
            handler?.extraMessage = "Rotating device"
            Thread.sleep(THREAD_DELAY)
            assertEquals(
                "RecyclerView has inappropriate number of elements", count, recyclerView.getSize()
            )
            addTestToPass(1)
            rotateDevice(false)
            handler?.extraMessage = ""

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
        private const val MAX_TIMEOUT: Long = 3 * 60_000 // 50sec

        private var grade = 0
        private var totalTests = 1
        private var maxGrade = 0
        private var passTests = 0

        private var efCurrentBalanceTextId = 0
        private var efExpensesRvId = 0
        private var expenseTypeTextId = 0
        private var expenseDateTextId = 0
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
                    0, min( //todo change length
                        100000, error.message?.length ?: 0
                    )
                ) + "...", error.cause
            )

            // Then delegate the error handling to the default handler which will throw an exception
            delegate.handle(newError, viewMatcher)
        }
    }
}