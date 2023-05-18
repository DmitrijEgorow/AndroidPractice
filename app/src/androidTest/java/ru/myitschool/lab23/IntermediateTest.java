package ru.myitschool.lab23;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static androidx.test.runner.lifecycle.Stage.STOPPED;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingPolicies;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class IntermediateTest {

    private final String clearAllNotificationsText = "Очистить";

    private static int grade = 0;
    private static int totalTests = 0;
    private static int maxGrade = 0;
    private static int passTests = 0;

    private ActivityScenario<MainActivity> activityScenario;

    private Context appContext;
    private Instrumentation mInstrumentation;


    @Before
    public void setUp() {
        mInstrumentation = getInstrumentation();

        UiDevice uiDevice = UiDevice.getInstance(mInstrumentation);
        uiDevice.pressHome();


        Context nonLocalizedContext = mInstrumentation.getTargetContext();
        android.content.res.Configuration configuration = nonLocalizedContext.getResources().getConfiguration();
        configuration.setLocale(Locale.UK);
        appContext = nonLocalizedContext.createConfigurationContext(configuration);

        Intent intent = new Intent(appContext, MainActivity.class);

        activityScenario = ActivityScenario.launch(intent);
    }

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
            //topActivity[0]=activity[0];
            //countingIdlingResource.decrement();
        });

        UiDevice uiDevice = UiDevice.getInstance(mInstrumentation);
        uiDevice.openNotification();

        uiDevice.wait(Until.hasObject(By.textStartsWith("RESUMED")), 3_000);
        UiObject2 title = uiDevice.findObject(By.textStartsWith("Update requested"));
        UiObject2 text = uiDevice.findObject(By.textStartsWith("RESUMED"));
        int score = 0;

        if (title != null && text != null) {
            if (title.getText().equals("Update requested") && text.getText().equals("RESUMED")) {
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

    private void clearAllNotifications() {
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        uiDevice.openNotification();

        uiDevice.wait(Until.hasObject(By.
                textStartsWith(appContext.getString(R.string.app_name))), 2_000);
        uiDevice.findObject(By.textStartsWith(clearAllNotificationsText))
                .click();
    }



    @BeforeClass
    public static void enableAccessibilityChecks() {
        totalTests = 1;
        maxGrade = 10;

        Bundle results = new Bundle();
        results.putInt("passTests", passTests);
        results.putInt("totalTests", totalTests);
        results.putInt("grade", grade);
        results.putInt("maxGrade", maxGrade);
        InstrumentationRegistry.getInstrumentation().addResults(results);

        IdlingPolicies.setMasterPolicyTimeout(5, TimeUnit.SECONDS);
        IdlingPolicies.setIdlingResourceTimeout(5, TimeUnit.SECONDS);
    }

    @AfterClass
    public static void printResult() {

        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        uiDevice.pressHome();

        Bundle results = new Bundle();
        results.putInt("passTests", passTests);
        results.putInt("totalTests", totalTests);
        results.putInt("grade", grade);
        results.putInt("maxGrade", maxGrade);
        InstrumentationRegistry.getInstrumentation().addResults(results);
        Log.d("Tests", passTests + " из " + totalTests + " тестов пройдено.");
        Log.d("Tests", grade + " из " + maxGrade + " баллов получено.");
    }
}
