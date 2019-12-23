package com.example.messengerkursovaya.MessagingActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.messengerkursovaya.MessagingActivity.MessagingListRecyclerViewAdapter;
import com.example.messengerkursovaya.R;
import com.example.messengerkursovaya.UtilsClass;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class MessagingListFragment extends Fragment {

    private static final String TAG = "Messaging List Fragment";
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private MessagingListRecyclerViewAdapter mAdapter;
    private List<ListenerRegistration> mListenerRegList;
    private RecyclerView mRecyclerView;

    private FirebaseFirestore mDatabase;
    private String dialogId;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MessagingListFragment() {
    }

    // todo Customize parameter initialization
    // @SuppressWarnings("unused")
    public static MessagingListFragment newInstance(int columnCount) {
        MessagingListFragment fragment = new MessagingListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        mListenerRegList = new ArrayList<>();
        mDatabase = FirebaseFirestore.getInstance();
        dialogId = getDialogId();
    }

    @Override
    public void onDestroy() {
        for (ListenerRegistration reg : mListenerRegList) {
            reg.remove();
        }
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messaginglist_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                //layoutManager.setReverseLayout(true);
                layoutManager.setStackFromEnd(true);
                mRecyclerView.setLayoutManager(layoutManager);
            } else {
                mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            //todo
            String userId = UtilsClass.getCurrUserEmail();
            if (userId == null) {
                return view;
            }
            mAdapter = new MessagingListRecyclerViewAdapter(getContext(), mListener, userId);
            mRecyclerView.setAdapter(mAdapter);

            setRecycleViewSrollAtKeyboardAppear(mRecyclerView, mAdapter);
            if (dialogId != null) {
                attachOnNewMessageListener(dialogId, userId);
                //attachOnMessageReadenListener(dialogId, userId);
            }
        }
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        for (ListenerRegistration reg : mListenerRegList) {
            reg.remove();
        }
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(MessageData item);
    }

    private void setRecycleViewSrollAtKeyboardAppear(final RecyclerView recyclerView, final RecyclerView.Adapter adapter) {
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v,
                                       int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.scrollToPosition(adapter.getItemCount()-1);
                        }
                    });
                }

            }
        });
    }

    private void attachOnMessageReadenListener(final String dialogId, final String userId) {
        mListenerRegList.add(mDatabase.collection("dialogs")
                .document(dialogId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, e.toString());
                            return;
                        }

                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            boolean isReadenByAnother = true;
                            String senderId = userId;
                            int numOfUnreaden = 0;
                            try {
                                isReadenByAnother = documentSnapshot.getBoolean("isReadenByAnother");
                                senderId = documentSnapshot.getDocumentReference("sender").getId();
                                numOfUnreaden = documentSnapshot.getLong("numOfUnreaden").intValue();
                            } catch (Exception ex) {
                                Log.e(TAG, ex.toString());
                            }

                            if (!isReadenByAnother && numOfUnreaden > 0) {
                                if (senderId.equals(userId)) {
                                    mAdapter.setReadenState(false, numOfUnreaden);
                                } else {
                                    mAdapter.setReadenState(true, numOfUnreaden);
                                    setDialogReaden(dialogId);
                                }
                            }
                        }
                    }
                }));
    }

    private void setDialogReaden(final String dialogId) {
        //todo failure listener
        mDatabase.collection("dialogs")
                .document(dialogId)
                .update("numOfUnreaden", 0, "isReadenByAnother", true);
    }

    private void attachOnNewMessageListener(final String dialogId, final String userId) {
        mListenerRegList.add(mDatabase.collection("dialogs")
                .document(dialogId)
                .collection("messages")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, e.toString());
                            return;
                        }

                        try {
                            for (DocumentChange docChange : queryDocumentSnapshots.getDocumentChanges()) {
                                if (docChange.getType() == DocumentChange.Type.ADDED) {
                                    mAdapter.pushBackData(getMessageData(
                                            docChange.getDocument().getId(),
                                            docChange.getDocument().getData(),
                                            userId
                                    ));
                                    mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
                                }
                            }
                        } catch (Exception ex) {
                            Log.e(TAG, ex.toString());
                        }
                    }
                }));
    }


    public MessageData getMessageData(String id, Map<String, Object> mapData, String userId) {
        DocumentReference sender = (DocumentReference) mapData.get("sender");
        byte isSent = 1;
        try {
            if (!sender.getId().equals(userId)) {
                isSent = 2;
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        return new MessageData(
                id,
                ((Timestamp) mapData.get("date")).toDate(),
                true,
                mapData.get("msgText").toString(),
                sender,
                isSent
        );
    }

    private String getDialogId() {
        String dialogId;
        try {
            //todo make better, maybe
            dialogId = getActivity().getIntent().getExtras().getString("dialogId");
        } catch (Exception e) {
            dialogId = null;
        }
        return dialogId;
    }
}
