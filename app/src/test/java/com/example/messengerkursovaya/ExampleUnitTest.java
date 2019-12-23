package com.example.messengerkursovaya;

import com.example.messengerkursovaya.DialogList.DialogData;
import com.example.messengerkursovaya.DialogList.DialogListFragment;
import com.google.firebase.Timestamp;

import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void dialogDataParsing_isCorrect() {
        String dialogId = "messenger@example.com test@example.com";
        String currUserId = "messenger@example.com";
        String lastMessageText = "Message Text";
        Date lastMessageDate = new Date(0);
        Boolean isReadenByAnother = false;

        Map<String, Object> mapTest = new HashMap<>();
        mapTest.put("sender", "messenger@example.com");
        mapTest.put("msgText", lastMessageText);
        mapTest.put("date", new Timestamp(lastMessageDate));
        mapTest.put("isReadenByAnother", isReadenByAnother);

        DialogData rightData = new DialogData(
                dialogId,
                "test@example.com",
                lastMessageText,
                lastMessageDate,
                null,
                true,
                isReadenByAnother,
                true
        );

        DialogListFragment classWithMethod = new DialogListFragment();
        DialogData testData = classWithMethod.getDialogData(dialogId, mapTest, currUserId);

        assertEquals(testData.getId(), rightData.getId());
        assertEquals(testData.getTitle(), rightData.getTitle());
        assertEquals(testData.getLastMessage(), rightData.getLastMessage());
        assertEquals(testData.getLastMessageDate(), rightData.getLastMessageDate());
        assertEquals(testData.getDialogImage(), rightData.getDialogImage());
        assertEquals(testData.isReadenMyself(), rightData.isReadenMyself());
        assertEquals(testData.isReadenByAnother(), rightData.isReadenByAnother());
        assertEquals(testData.isSent(), rightData.isSent());
    }
}