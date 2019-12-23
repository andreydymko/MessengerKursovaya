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
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.messengerkursovaya", appContext.getPackageName());
    }

    @Test
    public void messageDataParsing() {
        String id = "user@test.com";
        Date date = new Date(0);
        boolean isReadenByAnother = true;
        String msgText = "Test text";

        FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().getTargetContext());
        DocumentReference sender = FirebaseFirestore.getInstance().collection("users").document("sender");

        Map<String, Object> mapTest = new HashMap<>();
        mapTest.put("id", id);
        mapTest.put("date", new Timestamp(date));
        mapTest.put("isReadenByAnother", isReadenByAnother);
        mapTest.put("msgText", msgText);
        mapTest.put("sender", sender);

        MessageData newData = new MessageData(id, date, isReadenByAnother, msgText, sender, (byte) 1);

        MessagingListFragment mListFrag = new MessagingListFragment();
        MessageData testData = mListFrag.getMessageData(id, mapTest, sender.toString());

        assertEquals(testData.getId(), newData.getId());
        assertEquals(testData.getDate(), newData.getDate());
        assertEquals(testData.isReadenByAnother(), newData.isReadenByAnother());
        assertEquals(testData.getMsgText(), newData.getMsgText());
        assertEquals(testData.getSender(), newData.getSender());
    }
}
