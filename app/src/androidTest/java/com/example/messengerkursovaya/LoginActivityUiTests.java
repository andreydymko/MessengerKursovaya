package com.example.messengerkursovaya;

import android.content.Context;

import com.example.messengerkursovaya.MessagingActivity.MessageData;
import com.example.messengerkursovaya.MessagingActivity.MessagingListFragment;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;

import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.test.platform.app.InstrumentationRegistry;

import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class LoginActivityUiTests {

    private String getResourceString(int id) {
        Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        return targetContext.getResources().getString(id);
    }

    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule =
            new ActivityTestRule<>(LoginActivity.class);
    @Test
    public void checkContainerIsDisplayed() {
        onView(ViewMatchers.withId(R.id.userEmailReg)).check(matches(isDisplayed()));
    }

    @Test
    public void checkLoginTextFieldsConstraints() {
        onView(ViewMatchers.withId(R.id.userEmailReg))
                .perform(ViewActions.typeText("Definitely not a email"));

        closeSoftKeyboard();

        onView(ViewMatchers.withId(R.id.button_logIn))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.userEmailReg))
                .check(matches(hasErrorText(getResourceString(R.string.error_email_not_valid))));
    }
}
