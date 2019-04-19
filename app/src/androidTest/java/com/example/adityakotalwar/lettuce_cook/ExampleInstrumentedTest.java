package com.example.adityakotalwar.lettuce_cook;




import android.content.Context;
import android.content.Intent;
import android.view.Gravity;

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
public class ExampleInstrumentedTest {

    private String StringtobeTyped;
    private String friend;
    private String username;
    private String password;
    private String ingredientTomatch;
    //private String actualFriend;




    @Rule
    public ActivityTestRule<Recipes> activityRule
            = new ActivityTestRule<>(Recipes.class);


    //My user and pass is:
    //hiya and hihi123


    @Before
    public void initValidString() {
        // Specify a valid string.
        //StringtobeTyped = "imma not type anything";
         friend = "bobo";
         username = "armo@armo.com";
         password = "armo123";
         ingredientTomatch="banana";



    }


    @Test
    public void missingIngredients() {                  //checks if there are actually missing ingredients after selecting a recipe
        // Type text and then press the button.



        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        //onData(withText("Cheesy Eggplant Apple Quesadillas")).perform(click());

        onView(withId(R.id.buttonChooseIngredients)).perform(click());

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 3; i++) {
            onData(anything()).inAdapterView(withId(R.id.listViewStock)).atPosition(i).perform(click());

        }
        onView(withId(R.id.get_recipe)).perform(click());


        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onData(anything()).inAdapterView(withId(R.id.listViewSuggestedRecipes)).atPosition(0).perform(click());

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //onData(withId(R.id.listViewSuggestedRecipes)).atPosition(0).perform(click());
        onView(withId(R.id.buttonMissingIngr)).perform(click());
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onData(anything()).inAdapterView(withId(R.id.listViewStock)).atPosition(0).check(matches(not(withText(""))));

        //onData(withId(R.id.listViewStock)).atPosition(0).check(matches(not(withText(""))));



    }



    @Test
    public void SharingRecipes(){        //User story 2   //checks if the friend to share the recipe is there and whether it is clickable


        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onData(anything()).inAdapterView(withId(R.id.my_list_view2)).atPosition(0).perform(click());


        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.button_share)).perform(click());


        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onData(anything()).inAdapterView(withId(R.id.listViewStock)).atPosition(0).check(matches(withText(friend)));
        //onData(anything()).inAdapterView(withId(R.id.listViewStock)).atPosition(0).perform(click());

        //Friend was purduelol, expected

        //onView(withId(R.id.get_recipe)).perform(click());





        //start by logging out

        //onView(withId(R.id.buttonStock)).perform(click());
        //onView(withId(R.id.logout)).perform(click());

        //onView(withId(R.id.activity_drawer)).perform(DrawerActions.open());




    }

    @Test
    public void MissingIngredientsPageTest(){

        onView(withId(R.id.buttonChooseIngredients)).perform(click());
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onData(anything()).inAdapterView(withId(R.id.listViewStock)).atPosition(0).perform(click());
        onView(withId(R.id.get_recipe)).perform(click());
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onData(anything()).inAdapterView(withId(R.id.listViewSuggestedRecipes)).atPosition(0).perform(click());
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.buttonMissingIngr)).perform(click());

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        onData(anything()).inAdapterView(withId(R.id.listViewStock)).atPosition(0).perform(click());
        onView(withId(R.id.buttonAskFriend)).perform(click());
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.buttonAddGrocery)).perform(click());
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.buttonSubstitute)).perform(click());
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onData(anything()).inAdapterView(withId(R.id.listViewStock)).atPosition(0).check(matches(
                not(withText(ingredientTomatch))));




    }


}


