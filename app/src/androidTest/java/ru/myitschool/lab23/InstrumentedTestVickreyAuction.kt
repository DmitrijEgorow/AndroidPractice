package ru.myitschool.lab23

import android.app.Instrumentation
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.KeyEvent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.uiautomator.By
import com.jessecorbett.diskord.bot.bot
import com.jessecorbett.diskord.bot.events
import com.jessecorbett.diskord.util.sendMessage
import io.github.kakaocup.kakao.edit.KEditText
import io.github.kakaocup.kakao.screen.Screen
import io.github.kakaocup.kakao.text.KTextView
import java.util.TreeMap
import kotlin.test.assertEquals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import ru.myitschool.lab23.core.BaseTest
import ru.myitschool.lab23.core.Utils

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@MediumTest
class InstrumentedTestVickreyAuction : BaseTest() {

    private var customerNamePrefix = arrayOf("S", "D", "J")
    private var customerNameGenerated = "S"
    private var bidValue = 100
    private var randomDelay = 100L

    private val numOfCustomers = 3
    private val separator = "#"
    private val errorText = "All fields are required"

    private var winner = Pair("", 10)

    override fun beforeTest() {
        super.beforeTest()

        randomDelay = random.nextInt(THREAD_DELAY.toInt()).toLong()
        bidValue = random.nextInt(147483647)
        customerNameGenerated = customerNamePrefix[random.nextInt(customerNamePrefix.size)] +
            Utils().getRandomString(13, false)

        mClipboardManager =
            appContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        customerNameId = appContext.resources
            .getIdentifier("customer_name", "id", appContext.opPackageName)
        customerBidId = appContext.resources
            .getIdentifier("customer_bid", "id", appContext.opPackageName)
        submitBidId = appContext.resources
            .getIdentifier("submit_bid", "id", appContext.opPackageName)
        winnerNameId = appContext.resources
            .getIdentifier("winner_name", "id", appContext.opPackageName)
        winnerBidId = appContext.resources
            .getIdentifier("winner_bid", "id", appContext.opPackageName)
    }

    @Test(timeout = MAX_TIMEOUT_MS)
    fun aMainTestVickreyAuction() = runTest {
        addTestToStat(5)
        checkInterface(
            intArrayOf(
                customerNameId,
                customerBidId,
                submitBidId,
                winnerNameId,
                winnerBidId,
            ),
        )
        checkVickreyAuction()
        addTestToPass(5)
    }

    @Test(timeout = MAX_TIMEOUT_MS)
    fun bMainTestForEmptyInput() = runTest {
        addTestToStat(1)
        checkInterface(
            intArrayOf(
                customerNameId,
                customerBidId,
                submitBidId,
            ),
        )
        checkVickreyAuction(true)
        addTestToPass(1)
    }

    private fun checkVickreyAuction(emptyInputRequired: Boolean = false) {
        class SearchScreen : Screen<SearchScreen>() {
            val customerName = KEditText { withId(customerNameId) }
            val customerBid = KEditText { withId(customerBidId) }
            val submitBid = KTextView { withId(submitBidId) }
            val winnerName = KTextView { withId(winnerNameId) }
            val winnerBid = KTextView { withId(winnerBidId) }
        }

        val screen = SearchScreen()
        screen {
            if (!emptyInputRequired) {
                Thread.sleep(randomDelay)

                customerName.typeText(customerNameGenerated)
                mClipboardManager.setPrimaryClip(
                    ClipData.newPlainText(
                        "Info",
                        "$bidValue",
                    ),
                )
                customerBid.click()
                uiDevice?.pressKeyCode(KeyEvent.KEYCODE_PASTE)

                submitBid.click()

                CoroutineScope(Dispatchers.Default).launch {
                    sendDiscordMessage()
                }

                Thread.sleep(THREAD_DELAY + 5_000)
                winnerName.hasText(winner.first)
                winnerBid.hasText(winner.second.toString())
            } else {
                if (random.nextBoolean()) {
                    customerName.clearText()
                } else {
                    customerBid.clearText()
                }
                submitBid.click()
                assertEquals(true, uiDevice?.hasObject(By.textContains(errorText)))
            }
        }
    }

    private suspend fun sendDiscordMessage() {
        bot(token) {
            events {
                onReady {
                    // 2023-03-27T18:40:14.064000+00:00 SiBWqSrycddLGf#88735528
                    val messages = arrayListOf<String>()
                    var sendAt = ""
                    var winner = Pair("", 10)
                    channel(channelId)
                        .sendMessage("${customerNameGenerated}$separator$bidValue$separator${Build.MODEL}")
                    channel(channelId).getMessages(numOfCustomers)
                        .forEachIndexed { i, v ->
                            Log.d("Tests", "${v.sentAt} ${v.content} ")
                            if (i == 0) {
                                sendAt = v.sentAt
                            }
                            if (Utils().checkValidDate(sendAt, v.sentAt)) {
                                messages.add(v.content)
                            }
                        }
                    Log.d("Tests", messages.toList().toString())
                    onMessageCreate { message ->
                        if (message.content !in messages.toSet()) {
                            messages.add(message.content)
                        }
                        Log.d("Tests create", message.content)
                        Log.d("Tests", "${messages.size} ${messages.toList()}")
                        if (messages.size == numOfCustomers) {
                            winner = checkAuction(messages)
                            Log.d("Tests winner", "$winner")
                            setMessages(winner)
                        }
                    }
                }
            }
        }
    }

    private fun checkAuction(messages: ArrayList<String>): Pair<String, Int> {
        Log.d("Tests checkAuction", messages.toList().toString())
        val bids = TreeMap<Int, String>(compareByDescending { it })
        messages.forEach { v ->
            bids[v.split(separator)[1].toInt()] = v.split(separator)[0]
        }
        Log.d("Tests bids", bids.toString())
        return Pair(bids.pollFirstEntry()?.value ?: "", bids.firstEntry()?.key ?: 0)
    }

    private fun setMessages(winner: Pair<String, Int>) {
        this.winner = winner
    }

    companion object {
        private var grade = 0
        private var totalTests = 0
        private var maxGrade = 0
        private var passTests = 0

        private const val token = "API TOKEN"
        private const val channelId = "CHANNEL ID"

        private var customerNameId = 0
        private var customerBidId = 0
        private var submitBidId = 0

        private var winnerNameId = 0
        private var winnerBidId = 0
    }
}
