package com.example.messengerkursovaya.DialogList;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.messengerkursovaya.R;
import com.example.messengerkursovaya.UtilsClass;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class DialogListFragment extends Fragment {

    private static final String TAG = "Dialog List Fragment";
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private DialogListRecyclerViewAdapter mAdapter;
    private List<ListenerRegistration> mListenerRegList;

    private FirebaseFirestore mDatabase;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DialogListFragment() {
    }

    // todo Customize parameter initialization
    // @SuppressWarnings("unused")
    public static DialogListFragment newInstance(int columnCount) {
        DialogListFragment fragment = new DialogListFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialoglist_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            mAdapter = new DialogListRecyclerViewAdapter(getContext(), mListener);
            recyclerView.setAdapter(mAdapter);
            String userId = UtilsClass.getCurrUserEmail();
            if (userId != null) {
                //getDialogsRefsList(userId);
                attachOnDialogCreatedListener(userId);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (ListenerRegistration reg : mListenerRegList) {
            reg.remove();
        }
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
        void onListFragmentInteraction(DialogData item);
    }


//    private void getDialogsRefsList (final String userId) {
//
//        mDatabase.collection("users")
//                .document(userId)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            DocumentSnapshot doc = task.getResult();
//                            if (doc.exists()) {
//                                List<DocumentReference> dialogsRefsList = (List<DocumentReference>) doc.get("dialogs");
//                                if (dialogsRefsList != null && dialogsRefsList.size() != 0) {
//                                    for (DocumentReference reference : dialogsRefsList) {
//                                        addDialogToView(reference, userId);
//                                    }
//                                }
//                            } else {
//                                Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

    private void attachOnDialogCreatedListener(final String userId) {
        mListenerRegList.add(mDatabase.collection("users")
                .document(userId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, e.toString());
                            return;
                        }
                        List<DocumentReference> dialogsRefsList;
                        try {
                            dialogsRefsList = (List<DocumentReference>) documentSnapshot.get("dialogs");
                        } catch (Exception ex) {
                            Log.e(TAG, ex.toString());
                            dialogsRefsList = null;
                        }

                        if (dialogsRefsList == null || dialogsRefsList.isEmpty()) return;
                        try {
                            for (DocumentReference docRef : dialogsRefsList) {
                                attachOnLastMsgChangeListener(docRef, userId);
                            }
                        } catch (Exception ex) {
                            Log.e(TAG, ex.toString());
                        }
                        // todo latest dialog up
                        //todo check on multiple dialogs and on zero dialogs
                    }
                }));
    }

//    private void addDialogToView(final DocumentReference docRef, final String userId) {
//        docRef//.collection("messages")
//                //.orderBy("date", Query.Direction.DESCENDING)
//                //.limit(1)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            // there should be only 1 item
//                            DocumentSnapshot docSnap = task.getResult();
//                            if (docSnap.exists()) {
//                                //get data map for dialog
//                                //Map<String, Object> mapAllData = docSnap.getData();
//                                //mAdapter.pushFrontData(null);
//                                //attachOnLastMsgChangeListener(docRef, userId);
//                            } else {
//                                Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

    private void attachOnLastMsgChangeListener(final DocumentReference docRef, final String userId) {
        mListenerRegList.add(docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot docSnap,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e(TAG, e.toString());
                    return;
                }

                //todo check on multiple dialogs
                if (docSnap != null && docSnap.exists()) {
                    DialogData dialogData = getDialogData(docRef.getId(), docSnap.getData(), userId);
                    if (mAdapter.getDataAt(0) != null && dialogData.getId().equals(mAdapter.getDataAt(0).getId())) {
                        mAdapter.setDataAt(0, dialogData);
                        return;
                    }
                    int oldIndex = mAdapter.getIndexOfDialogById(dialogData.getId());
                    if (oldIndex >= 0) {
                        mAdapter.setDataAt(oldIndex, dialogData);
                        mAdapter.moveData(oldIndex, 0);
                    } else {
                        mAdapter.pushFrontData(dialogData);
                    }
                }
            }
        }));
    }

    public DialogData getDialogData(String dialogId,
                                     Map<String, Object> mapAllData,
                                     String userId) {
        String[] usersIds = dialogId.split(" ", 2);
        String anotherUserId = usersIds[0].compareTo(userId) == 0 ? usersIds[1] : usersIds[0];
        boolean isReadenMySelf = true;
        try {
            if (!((DocumentReference) mapAllData.get("sender")).getId().equals(userId)) {
                isReadenMySelf = (Boolean) mapAllData.get("isReadenByAnother");
            }
        } catch (Exception e) {
            //Log.e(TAG, e.toString());
        }
        boolean isReadenByAnother = false;
        try {
            if (((DocumentReference) mapAllData.get("sender")).getId().equals(userId)) {
                isReadenByAnother = (Boolean) mapAllData.get("isReadenByAnother");
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        Date date = new Date();
        try {
            date = ((Timestamp) mapAllData.get("date")).toDate();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        return new DialogData(
                dialogId,
                anotherUserId,
                (String) mapAllData.get("msgText"),
                date,
                null, //TODO load bitmap
                isReadenMySelf,
                isReadenByAnother,
                true //todo
        );
    }
}
