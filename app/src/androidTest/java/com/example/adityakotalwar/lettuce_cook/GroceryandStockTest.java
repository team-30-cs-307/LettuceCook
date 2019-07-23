package com.example.adityakotalwar.lettuce_cook;




import android.content.Context;
import android.content.Intent;
import android.view.Gravity;

import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.GeneralLocation;
import androidx.test.espresso.action.GeneralSwipeAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Swipe;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.rule.ActivityTestRule;

import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static androidx.test.espresso.Espresso.onData;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.EasyMock2Matchers.equalTo;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */




@RunWith(AndroidJUnit4.class)
public class GroceryandStockTest {

    private String StringtobeTyped;
    private String friend;
    private String ingredient;
    private String desc;
    private String ing2;

    //private String actualFriend;

    @Rule
    public ActivityTestRule<Grocery> GroceryActivityRule = new ActivityTestRule<>(Grocery.class);


    @Before
    public void Strings() {

        ingredient = "apple";
        desc = "green";
        ing2 = "banana";
    }

    @Test
    public void GroceryTestDeletion(){                  //user story 4 for deletion

        onView(withId(R.id.edit_text_add_item))
                .perform(replaceText(ingredient), closeSoftKeyboard());
        onView(withId(R.id.edit_text_add_description)).perform(replaceText(desc), closeSoftKeyboard());
        onView(withId(R.id.button_add_item)).perform(click());

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.edit_text_add_item))
                .perform(replaceText(ing2), closeSoftKeyboard());
        onView(withId(R.id.button_add_item)).perform(click());

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        onData(anything()).inAdapterView(withId(R.id.GroceryListView)).atPosition(0).perform(longClick());

        onView(withText("DELETE")).inRoot(isDialog()) // <---
                .check(matches(isDisplayed()))
                .perform(click());


        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onData(anything()).inAdapterView(withId(R.id.GroceryListView)).atPosition(0).check(matches(withText(ing2)));

    }

    @Test
    public void GroceryMoveTest(){              //User story 4 checks if the grocery is transferred to stock

        onData(anything()).inAdapterView(withId(R.id.GroceryListView)).atPosition(0).perform(click());
        onView(withId(R.id.updateToStock)).perform(click());

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.grocery)).perform(
                new GeneralSwipeAction(Swipe.FAST,
                        GeneralLocation.CENTER_RIGHT, GeneralLocation.CENTER_LEFT, Press.FINGER));

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onData(anything()).inAdapterView(withId(R.id.my_list_view2)).atPosition(0).check(matches(withText(ing2)));

    }
}
