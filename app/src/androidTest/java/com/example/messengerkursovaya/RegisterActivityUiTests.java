package com.example.messengerkursovaya;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.example.messengerkursovaya.MessagingActivity.MessageData;
import com.example.messengerkursovaya.MessagingActivity.MessagingListFragment;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;

import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
public class RegisterActivityUiTests {

    private String getResourceString(int id) {
        Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        return targetContext.getResources().getString(id);
    }

    @Rule
    public ActivityTestRule<RegisterActivity> RegisterActivityTestRule =
            new ActivityTestRule<>(RegisterActivity.class);

    @Test
    public void checkRegisterTextFieldsConstraints() {
        onView(ViewMatchers.withId(R.id.userEmailReg))
                .perform(ViewActions.typeText("example@mail.com"));
        closeSoftKeyboard();
        onView(ViewMatchers.withId(R.id.button_createAccount))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.userPasswordReg1))
                .check(matches(hasErrorText(getResourceString(R.string.error_empty_field))));
        onView(ViewMatchers.withId(R.id.userPasswordReg2))
                .check(matches(hasErrorText(getResourceString(R.string.error_empty_field))));
    }

    @Test
    public void checkRegisterSamePasswords() {
        onView(ViewMatchers.withId(R.id.userEmailReg))
                .perform(ViewActions.typeText("example@mail.com"));
        onView(ViewMatchers.withId(R.id.userPasswordReg1))
                .perform(ViewActions.typeText("pass123"));
        onView(ViewMatchers.withId(R.id.userPasswordReg2))
                .perform(ViewActions.typeText("321pass"));

        closeSoftKeyboard();

        onView(ViewMatchers.withId(R.id.button_createAccount))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.userPasswordReg1))
                .check(matches(hasErrorText(getResourceString(R.string.error_passwords_arent_same))));
        onView(ViewMatchers.withId(R.id.userPasswordReg2))
                .check(matches(hasErrorText(getResourceString(R.string.error_passwords_arent_same))));
    }

    @Test
    public void checkPasswordComplexity() {
        List<String> passesToTest = Arrays.asList(new String[]{"abc", "123", "jue23", "a33kj"});
        for (String pass : passesToTest) {
            onView(ViewMatchers.withId(R.id.userPasswordReg1))
                    .perform(ViewActions.typeText(pass));

            closeSoftKeyboard();

            onView(ViewMatchers.withId(R.id.button_createAccount))
                    .perform(ViewActions.click());

            onView(ViewMatchers.withId(R.id.userPasswordReg1))
                    .check(matches(hasErrorText(getResourceString(R.string.error_password_short))));

            onView(ViewMatchers.withId(R.id.userPasswordReg1))
                    .perform(ViewActions.clearText());
        }
    }

    @Test
    public void checkPasswordComplexity2() {
        onView(ViewMatchers.withId(R.id.userPasswordReg1))
                .perform(ViewActions.typeText("12abc"));

        closeSoftKeyboard();

        onView(ViewMatchers.withId(R.id.button_createAccount))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.userPasswordReg1))
                .check(matches(hasErrorText(getResourceString(R.string.error_password_short))));
    }
}

