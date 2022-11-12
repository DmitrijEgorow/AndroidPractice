package ru.myitschool.lab23;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

@RunWith(AndroidJUnit4.class)
public class EditPropertyTest {

    private ActivityScenario<MainActivity> activityScenario;

    private Context appContext;
    private Instrumentation mInstrumentation;


    @Before
    public void setUp() {
        mInstrumentation = InstrumentationRegistry.getInstrumentation();

        Context nonLocalizedContext = mInstrumentation.getTargetContext();
        android.content.res.Configuration configuration = nonLocalizedContext.getResources().getConfiguration();
        configuration.setLocale(Locale.UK);
        appContext = nonLocalizedContext.createConfigurationContext(configuration);

        Intent intent = new Intent(appContext, MainActivity.class);
        activityScenario = ActivityScenario.launch(intent);
    }

    @Test
    public void testPriors() {
        onView(withId(R.id.property_text)).perform(typeText("RESUMED"));
        closeSoftKeyboard();
        onView(withId(R.id.submit_button)).perform(click());

    }
}