package ru.myitschool.lab23;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static androidx.test.runner.lifecycle.Stage.STOPPED;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;

import ru.myitschool.lab23.core.BaseTest;

@RunWith(AndroidJUnit4.class)
public class IntermediateTest extends BaseTest {
    private static int grade = 0;
    private static int totalTests = 0;
    private static int maxGrade = 0;
    private static int passTests = 0;
    private final String notificationTitle = "Update requested";
    private final String notificationText = "RESUMED";

    @Test
    public void testNotificationBundle() {
        //countingIdlingResource.increment();
        final Activity[] activity = new Activity[1];
        getInstrumentation().runOnMainSync(() -> {
            Activity currentActivity = null;
            Collection resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(STOPPED);
            if (resumedActivities.iterator().hasNext()) {
                currentActivity = (Activity) resumedActivities.iterator().next();
                Log.d("Tests", "STOPPED " + currentActivity.getLocalClassName());
                activity[0] = currentActivity;
            }
        });

        UiDevice uiDevice = UiDevice.getInstance(mInstrumentation);
        uiDevice.openNotification();

        uiDevice.wait(Until.hasObject(By.textStartsWith(notificationText)), 3_000);
        UiObject2 title = uiDevice.findObject(By.textStartsWith(notificationTitle));
        UiObject2 text = uiDevice.findObject(By.textStartsWith(notificationText));
        int score = 0;

        if (title != null && text != null) {
            if (title.getText().equals(notificationTitle) && text.getText().equals(notificationText)) {
                score = 1;
                passTests = 1;
                grade = 10;
            } else {
                throw new AssertionError("incorrect notification text");
            }
        } else {
            throw new AssertionError("incorrect notification text");
        }

        clearAllNotifications();
        uiDevice.pressHome();
        uiDevice.pressHome();

    }

    @BeforeClass
    public static void writeDefaultGrades() {
        totalTests = 1;
        maxGrade = 10;

        Bundle results = new Bundle();
        results.putInt("passTests", passTests);
        results.putInt("totalTests", totalTests);
        results.putInt("grade", grade);
        results.putInt("maxGrade", maxGrade);
        InstrumentationRegistry.getInstrumentation().addResults(results);
    }
}
