package com.example.messengerkursovaya.MessagingActivity;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.example.messengerkursovaya.MessagingActivity.MessagingListFragment.OnListFragmentInteractionListener;
import com.example.messengerkursovaya.R;
import com.example.messengerkursovaya.UtilsClass;

import java.util.Iterator;
import java.util.LinkedList;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * {@link RecyclerView.Adapter} that can display a and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MessagingListRecyclerViewAdapter extends RecyclerView.Adapter<MessagingListRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "Messaging List Adapter";
    private Context mContext;
    private LinkedList<MessageData> mDataset;
    private OnListFragmentInteractionListener mListener;
    private String mCurrUserId;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private TextView mTextViewMessage;
        private TextView mTextViewDate;
        private ImageView mImageViewIsSent;
        private Space mLeftSpace;
        private Space mRightSpace;
        private LinearLayout mMessageMainLayout;
        private LinearLayout mMessageInfoLayout;

        private ViewHolder(View view) {
            super(view);
            mView = view;
            mTextViewMessage = view.findViewById(R.id.text_view_msg_text);
            mTextViewDate = view.findViewById(R.id.text_view_msg_date);
            mImageViewIsSent = view.findViewById(R.id.image_is_sent);
            mLeftSpace = view.findViewById(R.id.left_space);
            mRightSpace = view.findViewById(R.id.rigth_space);
            mMessageMainLayout = view.findViewById(R.id.message_item_main_layout);
            mMessageInfoLayout = view.findViewById(R.id.message_info_layout);
        }
    }

    MessagingListRecyclerViewAdapter(Context context, OnListFragmentInteractionListener listener, String currUserId) {
        mContext = context;
        mDataset = new LinkedList<>();
        mListener = listener;
        mCurrUserId = currUserId;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.messaging_list_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final MessageData data = mDataset.get(position);
        initialiseView(holder, data);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(data);
                }
            }
        });
    }

    private void initialiseView(ViewHolder holder, MessageData data) {
        //TODO: add dialog image
        if (data == null) return;
        holder.mView.setBackgroundColor(ContextCompat.
                getColor(mContext, !data.isReadenByAnother() ? R.color.colorDialogNotReaden : R.color.colorTransparent));
        holder.mTextViewMessage.setText(data.getMsgText());
        holder.mTextViewDate.setText(UtilsClass.getStrDate(data.getDate()));
        if (data.isSent() != 2) {
            holder.mImageViewIsSent
                    .setImageResource(data.isSent() == 1 ? R.drawable.ic_check_24px : R.drawable.ic_close_24px);
        }

        if (data.getSender().getId().equals(mCurrUserId)) {
            holder.mRightSpace.setVisibility(View.GONE);
            holder.mMessageMainLayout.setGravity(Gravity.END);
            holder.mMessageInfoLayout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.message_background_me));
        } else {
            holder.mLeftSpace.setVisibility(View.GONE);
            holder.mMessageMainLayout.setGravity(Gravity.START);
            holder.mMessageInfoLayout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.message_background_other));
        }
    }

    public void addData(int pos, MessageData data) {
        if (pos < 0 || pos >= mDataset.size()) return;
        mDataset.add(pos, data);
        notifyItemInserted(pos);
    }

    public void pushFrontData(MessageData data) {
        mDataset.offerFirst(data);
        notifyItemInserted(0);
    }

    public void pushBackData(MessageData data) {
        mDataset.offerLast(data);
        notifyItemInserted(mDataset.size()-1);
    }

    public void setDataAt(int pos, MessageData data) {
        if (pos < 0 || pos >= mDataset.size()) return;
        mDataset.set(pos, data);
        notifyItemChanged(pos);
    }

    public void moveData(int oldPos, int newPos) {
        if (oldPos < 0 || newPos < 0 || oldPos == newPos || oldPos >= mDataset.size() || newPos >= mDataset.size()) return;
        MessageData data = mDataset.get(oldPos);
        mDataset.remove(oldPos);
        mDataset.add(newPos, data);
        notifyItemMoved(oldPos, newPos);
    }

    public void removeDataAt(int pos) {
        if (pos < 0 || pos >= mDataset.size()) return;
        mDataset.remove(pos);
        notifyItemRemoved(pos);
    }

    public MessageData getDataAt(int pos) {
        if (pos < 0 || pos >= mDataset.size()) return null;
        return mDataset.get(pos);
    }

    public void setReadenState(boolean state, int length) {
        Iterator iter = mDataset.descendingIterator();
        int i = 0;
        while (iter.hasNext() && length >= 0) {
            try {
                ((MessageData) iter.next()).setReadenByAnother(state);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                e.printStackTrace();
            }
            i++;
            length--;
        }
        notifyItemRangeChanged(mDataset.size() - 1 - i, i);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
