package ru.myitschool.lab23;

import android.text.SpannedString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class Utils {

    static TypeSafeMatcher<View> hasTypefaceSpan(
            int start, int end, /*@Typeface.Style*/ int style) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                if (!(item instanceof TextView)) {
                    return false;
                }
                TextView textView = (TextView) item;
                Log.d("Tests", textView.getFontFeatureSettings());
                if (!(textView.getText() instanceof SpannedString)) {
                    return false;
                }
                if (start >= textView.length() || end >= textView.length()) {
                    return false;
                }
                StyleSpan[] spans =
                        ((SpannedString) textView.getText()).getSpans(start, end, StyleSpan.class);
                for (StyleSpan span : spans) {
                    if (span.getStyle() == style) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(
                        "hasTypefaceSpan(" + style + ") in [" + start + ", " + end + "]");
            }
        };
    }
}
