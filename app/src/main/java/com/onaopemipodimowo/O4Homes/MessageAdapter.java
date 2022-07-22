package com.onaopemipodimowo.O4Homes;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private String TAG = "MessageAdapter";


    public int result;

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Chat> mChat;
    private String imageUrl;


    FirebaseUser firebaseUser;

    public MessageAdapter(@NonNull Context mContext, List<Chat> mChat, String imageurl) {
        this.mContext = mContext;
        this.mChat = mChat;
        this.imageUrl = imageurl;

    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        Chat chat = mChat.get(position);

        holder.showMessage.setText(chat.getMessage());
        if (imageUrl.equals("")) {
            Glide.with(mContext)
                    .load(R.drawable.ic_profile)
                    .transform(new CircleCrop())
                    .into(holder.profile_image);
        } else {
            Glide.with(mContext)
                    .load(imageUrl)
                    .transform(new CircleCrop())
                    .into(holder.profile_image);



        }


        if (position == mChat.size()-1) {
            if (chat.isIsseen()) {
                holder.tvSeen.setText("Seen");
            } else {
                holder.tvSeen.setText("Delivered");
            }
        } else {
            holder.tvSeen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView showMessage;
        public ImageView profile_image;
        public TextView tvSeen;





        public ViewHolder(View itemView) {
            super(itemView);

            showMessage = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            tvSeen = itemView.findViewById(R.id.txt_seen);

        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.i(TAG, "Fuser; " + firebaseUser + "now: " + mChat.get(position).getSender());
        if (mChat.get(position).getSender().equals(firebaseUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }



}
