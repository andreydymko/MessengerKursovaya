package com.example.messengerkursovaya;

import android.content.Intent;
import android.content.UriMatcher;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.messengerkursovaya.DialogList.DialogData;
import com.example.messengerkursovaya.MessagingActivity.MessagingActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ChatCreateActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mButtonCreateChat;
    private TextView getEmail, firstMsg;
    private FirebaseFirestore db;
    private static final String TAG = "Chat Create Activity";
    private String mCurrUserEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_create);

        mButtonCreateChat = findViewById(R.id.button_create_chat);
        getEmail = findViewById(R.id.getEmail);
        firstMsg = findViewById(R.id.edit_text_new_message);
        mButtonCreateChat.setOnClickListener(this);
        db = FirebaseFirestore.getInstance();
        mCurrUserEmail = UtilsClass.getCurrUserEmail();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_create_chat:
                String msgText = firstMsg.getText().toString().trim();
                String anotherUserId = getEmail.getText().toString().trim();
                if (msgText.isEmpty() || anotherUserId.isEmpty()) break;
                CheckForName(mCurrUserEmail, anotherUserId, msgText);
                break;
            default:
                break;
        }
    }


    private void CheckForName(final String currUserId, final String checkName, final String msgText) {
        try {
            db.collection("users")
                    .document(checkName)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot ds = task.getResult();
                                if(ds.exists()){
                                    createChat(currUserId, checkName, msgText);
                                } else {
                                    Toast.makeText(ChatCreateActivity.this, R.string.no_such_user, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(ChatCreateActivity.this, R.string.network_error, Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChatCreateActivity.this, R.string.network_error, Toast.LENGTH_LONG).show();
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            Toast.makeText(this, R.string.network_error, Toast.LENGTH_LONG).show();
        }
    }

    private void createChat(final String currUserId, final String anotherUserId, final String msgText) {
        final String dialogNameVar1 = currUserId + " " + anotherUserId;
        final String dialogNameVar2 = anotherUserId + " " + currUserId;
        db.collection("users")
                .document(currUserId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (!documentSnapshot.exists()) {
                            return;
                        }

                        Map<String, Object> data = documentSnapshot.getData();
                        try {
                            ArrayList<DocumentReference> list = (ArrayList<DocumentReference>) data.get("dialogs");
                            if (list == null || list.isEmpty()) {
                                // user don't have dialogs at all
                                // create new dialog
                                createDialog(dialogNameVar1, currUserId, anotherUserId, msgText);
                            }

                            String alreadyExistedDialogId = null;
                            for (DocumentReference docRef : list) {
                                if (docRef.getId().equals(dialogNameVar1)) {
                                    alreadyExistedDialogId = dialogNameVar1;
                                }
                                if (docRef.getId().equals(dialogNameVar2)) {
                                    alreadyExistedDialogId = dialogNameVar2;
                                }
                            }

                            if (alreadyExistedDialogId == null || alreadyExistedDialogId.isEmpty()) {
                                createDialog(dialogNameVar1, currUserId, anotherUserId, msgText);
                            } else {
                                sendMessage(alreadyExistedDialogId, currUserId, msgText);
                                openMessagingActivity(alreadyExistedDialogId, anotherUserId);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                });
//        db.collection("dialogs")
//                .document(docNameVar1)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if(task.isSuccessful()){
//                            DocumentSnapshot documentSnapshot = task.getResult();
//                            if(documentSnapshot != null && documentSnapshot.exists()) {
//                                openMessagingActivity();
//                            } else {
//                                addInfo();
//                                openMessagingActivity();
//                            }
//
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(ChatCreateActivity.this, R.string.network_error, Toast.LENGTH_LONG).show();
//                Log.d(TAG, e.toString());
//            }
//        });
    }

    private void createDialog(final String dialogId, final String currUserId, final String anotherUserId, final String msgText) {
        addMsgToDialog(dialogId, currUserId, msgText, 0, 0);
        addDialogToUsersList(dialogId, Arrays.asList(currUserId, anotherUserId));
        openMessagingActivity(dialogId, anotherUserId);
    }

    private void addDialogToUsersList(final String dialogId, final List<String> usersIds) {
        for (String userId : usersIds) {
            db.collection("users")
                    .document(userId)
                    .update("dialogs", FieldValue.arrayUnion(db.collection("dialogs").document(dialogId)));
        }
    }

    private void sendMessage(final String dialogId, final String userId, final String msgText) {
        db.collection("dialogs")
                .document(dialogId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        long oldIndex = -1;
                        long oldUnreadenCount = -1;
                        try {
                            oldIndex = Long.valueOf(documentSnapshot.getString("lastMsgIndex"));
                            oldUnreadenCount = documentSnapshot.getLong("numOfUnreaden");
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }

                        if (oldIndex <= -1 || oldUnreadenCount <= -1) {
                            return;
                        }

                        addMsgToDialog(dialogId, userId, msgText, oldIndex, oldUnreadenCount);
                    }
                });
    }

    private void addMsgToDialog(String dialogId, String userId, String msgText, long oldIndex, long oldUnreadenCount) {
        long newIndex = oldIndex + 1;
        Map<String, Object> data = new HashMap<>();
        data.put("date", new Date());
        data.put("msgText", msgText);
        data.put("sender", db.collection("users").document(userId));

        //todo successful and failure listeners
        db.collection("dialogs")
                .document(dialogId)
                .collection("messages")
                .document(String.format(Locale.US,"%019d", newIndex))
                .set(data);

        data.put("isReadenByAnother", false);
        data.put("lastMsgIndex", String.format(Locale.US,"%019d", newIndex));
        data.put("numOfUnreaden", oldUnreadenCount + 1);
        db.collection("dialogs")
                .document(dialogId)
                .set(data);
    }

    private void openMessagingActivity(String dialogId, String anotherUserEmail){
        Intent msgActivityIntent = new Intent(this, MessagingActivity.class);
        msgActivityIntent.putExtra("dialogId", dialogId);
        msgActivityIntent.putExtra("dialogTitle", anotherUserEmail);
        startActivity(msgActivityIntent);
    }
}


