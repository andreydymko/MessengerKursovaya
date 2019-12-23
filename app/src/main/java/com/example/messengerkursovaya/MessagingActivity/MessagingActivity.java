package com.example.messengerkursovaya.MessagingActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.messengerkursovaya.DialogList.DialogData;
import com.example.messengerkursovaya.R;
import com.example.messengerkursovaya.UtilsClass;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MessagingActivity extends AppCompatActivity implements MessagingListFragment.OnListFragmentInteractionListener{

    private static final String TAG = "Messaging activity";
    private String mDialogId;
    private String mUserId;
    private FirebaseFirestore mDatabase;
    private EditText mMessageTextEdit;
    private ImageButton mImageButtonSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        initializeActivity();
        mUserId = UtilsClass.getCurrUserEmail();

        mMessageTextEdit = findViewById(R.id.message_text_edit);
        mImageButtonSend = findViewById(R.id.button_send_message);

        mDatabase = FirebaseFirestore.getInstance();

        if (mDialogId != null && mUserId != null) {
            setDialogReaden(mDialogId, mUserId);
        }
        mImageButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUserId == null || mDialogId == null) {
                    return;
                }
                String msgText = mMessageTextEdit.getText().toString().trim();
                if(!msgText.isEmpty()) {
                    sendMessage(mDialogId, mUserId, msgText);
                }
            }
        });
    }

    private void initializeActivity() {
        Bundle intentExtras = getIntent().getExtras();
        if (intentExtras != null) {
            try {
                mDialogId = intentExtras.get("dialogId").toString();
                setTitle(intentExtras.get("dialogTitle").toString());
            } catch (Exception e) {
                setTitle(TAG);
                Log.e(TAG, e.toString());
            }
        } else {
            setTitle(TAG);
        }
    }

    @Override
    public void onBackPressed() {
        setDialogReaden(mDialogId, mUserId);
        super.onBackPressed();
    }

    private void setDialogReaden(final String dialogId, final String userId) {
        mDatabase.collection("dialogs")
                .document(dialogId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot == null || !documentSnapshot.exists()) return;
                        boolean isReadenByAnother = true;
                        String sender = userId;
                        try {
                            isReadenByAnother = documentSnapshot.getBoolean("isReadenByAnother");
                            sender = documentSnapshot.getDocumentReference("sender").getId();
                        } catch (Exception e) {
                            Toast.makeText(MessagingActivity.this, "error", Toast.LENGTH_LONG).show();
                            Log.e(TAG, e.toString());
                        }

                        if (!isReadenByAnother && !sender.equals(userId)) {
                            setDialogReadenInDB(dialogId);
                        }
                    }
                });
    }

    private void setDialogReadenInDB(final String dialogId) {
        mDatabase.collection("dialogs")
                .document(dialogId)
                .update("numOfUnreaden", 0,
                        "isReadenByAnother", true);

    }

    private void sendMessage(final String dialogId, final String userId, final String msgText) {
        mDatabase.collection("dialogs")
                .document(dialogId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        //if (documentSnapshot == null || documentSnapshot.exists()) return;
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
                })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MessagingActivity.this, R.string.network_error, Toast.LENGTH_LONG).show();
                Log.e(TAG, e.toString());
            }
        });
    }

    private void addMsgToDialog(String dialogId, String userId, String msgText, long oldIndex, long oldUnreadenCount) {
        long newIndex = oldIndex + 1;
        Map<String, Object> data = new HashMap<>();
        data.put("date", new Date());
        data.put("msgText", msgText);
        data.put("sender", mDatabase.collection("users").document(userId));

        //todo successful and failure listeners
        mDatabase.collection("dialogs")
                .document(dialogId)
                .collection("messages")
                .document(String.format(Locale.US,"%019d", newIndex))
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mMessageTextEdit.setText("");
            }
        });

        data.put("isReadenByAnother", false);
        data.put("lastMsgIndex", String.format(Locale.US,"%019d", newIndex));
        data.put("numOfUnreaden", oldUnreadenCount + 1);
        mDatabase.collection("dialogs")
                .document(dialogId)
                .set(data);
    }

    @Override
    public void onListFragmentInteraction(MessageData data){
    }
}


