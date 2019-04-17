package com.example.adityakotalwar.lettuce_cook;




import android.content.Context;

import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import org.junit.Before;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    private String StringtobeTyped;


    @Rule
    public ActivityTestRule<MainActivity> activityRule
            = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void initValidString() {
        // Specify a valid string.
        StringtobeTyped = "imma not type anything";
    }

    @Test
    public void changeText_sameActivity() {
        // Type text and then press the button.
        onView(withId(R.id.edit_text_add_item))
                .perform(typeText(StringtobeTyped), closeSoftKeyboard());
        //onView(withId(R.id.button_add_item)).perform(click());

        // Check that the text was changed.
        onView(withId(R.id.edit_text_add_item))
                .check(matches(withText("")));

    }
}
