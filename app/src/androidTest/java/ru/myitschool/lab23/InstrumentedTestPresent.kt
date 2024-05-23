package ru.myitschool.lab23

import androidx.test.espresso.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import io.github.kakaocup.kakao.image.KImageView
import io.github.kakaocup.kakao.screen.Screen
import io.github.kakaocup.kakao.text.KButton
import java.util.*
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import ru.myitschool.lab23.core.BaseTest


@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@MediumTest
class InstrumentedTestPresent : BaseTest() {

    @Before
    override fun beforeTest() {
        super.beforeTest()

        imageId =
            appContext.resources.getIdentifier("box_image", "id", appContext.opPackageName)
        buttonId =
            appContext.resources.getIdentifier("test_button", "id", appContext.opPackageName)

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

    private fun checkStep(needsRotate: Boolean) {

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

    companion object {
        private const val APP_NAME = "Context Menu"
        private const val CHECK_INTERFACE_MESSAGE = "Some UI elements seems to be missing"
        private const val THREAD_DELAY: Long = 1_500
        private const val MAX_TIMEOUT: Long = 10_000 // or 20sec

        private var grade = 0
        private var totalTests = 1
        private var maxGrade = 0
        private var passTests = 0

        private var buttonId = 0
        private var imageId = 0

    }
}
