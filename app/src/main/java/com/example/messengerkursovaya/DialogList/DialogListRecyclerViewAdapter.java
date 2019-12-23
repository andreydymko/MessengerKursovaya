package com.example.messengerkursovaya.DialogList;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.messengerkursovaya.R;
import com.example.messengerkursovaya.DialogList.DialogListFragment.OnListFragmentInteractionListener;
import com.example.messengerkursovaya.UtilsClass;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class DialogListRecyclerViewAdapter extends RecyclerView.Adapter<DialogListRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private List<DialogData> mDataset;
    private OnListFragmentInteractionListener mListener;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private TextView mTextViewTitle;
        private TextView mTextViewMessage;
        private TextView mTextViewDate;
        private ImageView mImageViewDialogPhoto;
        private ImageView mImageViewIsSent;

        private ViewHolder(View view) {
            super(view);
            mView = view;
            mTextViewTitle = view.findViewById(R.id.text_view_dialog_title);
            mTextViewMessage = view.findViewById(R.id.text_view_last_message);
            mTextViewDate = view.findViewById(R.id.text_view_last_message_date);
            mImageViewDialogPhoto = view.findViewById(R.id.dialog_photo);
            mImageViewIsSent = view.findViewById(R.id.image_is_sent);
        }
    }

    DialogListRecyclerViewAdapter(Context context, List<DialogData> list, OnListFragmentInteractionListener listener) {
        mContext = context;
        mDataset = list;
        mListener = listener;
    }

    DialogListRecyclerViewAdapter(Context context, OnListFragmentInteractionListener listener) {
        mContext = context;
        mDataset = new ArrayList<>();
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dialog_list_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final DialogData data = mDataset.get(position);
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

    private void initialiseView(ViewHolder holder, DialogData data) {
        //TODO: add dialog image
        if (data == null) return;
        holder.mView.setBackgroundColor(ContextCompat.
                getColor(mContext, !data.isReadenMyself() ? R.color.colorDialogNotReaden : R.color.colorTransparent));
        holder.mTextViewMessage.setBackgroundColor(ContextCompat.
                getColor(mContext, !data.isReadenByAnother() ? R.color.colorDialogNotReaden : R.color.colorTransparent));
        holder.mTextViewTitle.setText(data.getTitle());
        holder.mTextViewMessage.setText(data.getLastMessage());
        holder.mTextViewDate.setText(UtilsClass.getStrDate(data.getLastMessageDate()));
        holder.mImageViewIsSent.setImageResource(data.isSent() ? R.drawable.ic_check_24px : R.drawable.ic_close_24px);
    }

    public void addData(int pos, DialogData data) {
        if (pos < 0 || pos >= mDataset.size()) return;
        mDataset.add(pos, data);
        notifyItemInserted(pos);
    }

    public void pushFrontData(DialogData data) {
        mDataset.add(0, data);
        notifyItemInserted(0);
    }

    public void setDataAt(int pos, DialogData data) {
        if (pos < 0 || pos >= mDataset.size()) return;
        mDataset.set(pos, data);
        notifyItemChanged(pos);
    }

    public void moveData(int oldPos, int newPos) {
        if (oldPos < 0 || newPos < 0 || oldPos == newPos || oldPos >= mDataset.size() || newPos >= mDataset.size()) return;
        DialogData data = mDataset.get(oldPos);
        mDataset.remove(oldPos);
        mDataset.add(newPos, data);
        notifyItemMoved(oldPos, newPos);
    }

    public void removeDataAt(int pos) {
        if (pos < 0 || pos >= mDataset.size()) return;
        mDataset.remove(pos);
        notifyItemRemoved(pos);
    }

    public DialogData getDataAt(int pos) {
        if (pos < 0 || pos >= mDataset.size()) return null;
        return mDataset.get(pos);
    }

    public int getIndexOfDialogById(String dialogId) {
        if (dialogId.isEmpty()) return -1;
        for (int i = 0; i < mDataset.size(); i++) {
            if (mDataset.get(i) != null && mDataset.get(i).getId().equals(dialogId)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
