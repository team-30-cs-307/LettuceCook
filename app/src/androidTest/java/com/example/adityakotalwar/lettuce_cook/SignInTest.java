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
public class SignInTest {

    private String StringtobeTyped;
    private String friend;
    private String username;
    private String password;
    //private String actualFriend;

    @Rule
    public ActivityTestRule<SignIn> SignActivityRule = new ActivityTestRule<>(SignIn.class);


    @Before
    public void Strings(){

        username = "armo@armo.com";
        password = "armo123";
    }

    @Test
    public void SharedRecipeTest(){                 //User story 2: Check if the shared recipe is clickable and is actually shared
                                                    //login to a different account
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.Email))
                .perform(replaceText(username), closeSoftKeyboard());
        onView(withId(R.id.Password)).perform(replaceText(password), closeSoftKeyboard());
        onView(withId(R.id.ButtonSignIn)).perform(click());

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //onView(withId(R.id.buttonRecipes)).perform(click());



        onView(withId(R.id.Main)).perform(
                new GeneralSwipeAction(Swipe.FAST,
                        GeneralLocation.CENTER_RIGHT, GeneralLocation.CENTER_LEFT, Press.FINGER));

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.SharedRecipeButton)).perform(click());

        onView(withId(R.id.shared_recipe_list)).perform(click());

        //onData(anything()).inAdapterView(withId(R.id.shared_recipe_list)).atPosition(0).perform(click());



//        private static ViewAction swipeFromTopToBottom() {
//            return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.TOP_CENTER,
//                    GeneralLocation.BOTTOM_CENTER, Press.FINGER);
//        }

        //SignActivityRule.launchActivity(intent);
        //onView(withId(R.id.SharedRecipeButton)).perform(click());

    }
}