package com.onaopemipodimowo.O4Homes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.onaopemipodimowo.O4Homes.Model.User;
import com.onaopemipodimowo.O4Homes.Notifications.Client;
import com.onaopemipodimowo.O4Homes.Notifications.Data;
import com.onaopemipodimowo.O4Homes.Notifications.MyResponse;
import com.onaopemipodimowo.O4Homes.Notifications.Sender;
import com.onaopemipodimowo.O4Homes.Notifications.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fragment.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {

    private String TAG = "MessageActivity";

    private ImageView profileImage;
    private ImageView backButton;
    private TextView username;
    private TextView textViewStatus;
    private ImageButton btnSend;
    private EditText editTextSend;

    MessageAdapter messageAdapter;
    List<Chat> mChat;


    RecyclerView recyclerView;

    FirebaseUser firebaseUser;

    String currentUser;
    String userid;
    DatabaseReference dbReference;
    Intent intent;

    ValueEventListener seenListener;

    APIService apiService;

    boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        profileImage = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btnSend = findViewById(R.id.btn_send);
        editTextSend = findViewById(R.id.text_send);
        backButton = findViewById(R.id.back_btn);
        textViewStatus = findViewById(R.id.status);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        intent = getIntent();
        userid = intent.getStringExtra("userid");
        Log.i(TAG, "Gotten user: " + userid);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(intent.hasExtra("listingId")){
            String listingId = intent.getStringExtra("listingId");
            editTextSend.setText("Hey, I would like to rent a home for my internship with listing id "+listingId);
        }

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                String msg = editTextSend.getText().toString();
                if (!msg.equals("")){
                    sendMessage(firebaseUser.getUid(),userid,msg);
                }else {
                    Toast.makeText(MessageActivity.this, "You can't send an empty message", Toast.LENGTH_SHORT).show();
                }
                editTextSend.setText("");
            }
        });

        if (userid == null) {
            userid = getIntent().getStringExtra("userFullId");
        }

        dbReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if (user.getImageURL().equals("default")){
                    profileImage.setImageResource(R.drawable.ic_profile);
                }else {
                    Glide.with(getApplicationContext()).load(user.getImageURL())
                            .transform(new CircleCrop())
                            .into(profileImage);
                }

                readMessages(firebaseUser.getUid(),userid,user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        seenMessage(userid);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void seenMessage(final String userid){
        dbReference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void sendMessage(String sender, String receiver, String message){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        String randomKey = reference.getRef().push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);
        reference.child("Chats").push().setValue(hashMap);

        final String msg = message;

        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Log.i("fcm","notify");
                if (notify) {
                    sendNotification(receiver, user.getUsername(), msg);
                    Log.i("fcm", "sent notification");
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendNotification(String receiver, final String username,final String message){
        Log.i("fcm","srart sending");
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Token");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Log.i("fcm","snapshot");
                    Token token = snapshot.getValue(Token.class);
                    Log.i("op","userid: "+userid+"fuser: "+firebaseUser.getUid());
                    Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher,username+": "+message, "New Message",
                            userid);
                    Sender sender = new Sender(data,token.getToken());
                    Log.i("fcm","about to send notification");
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200){
                                        if (response.body().success !=1){
                                            Toast.makeText(MessageActivity.this,"Failed!",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void readMessages(String myid, String userid, String imageurl) {
        mChat = new ArrayList<>();

        dbReference = FirebaseDatabase.getInstance().getReference("Chats");
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datSnapshot) {
                mChat.clear();
                for (DataSnapshot snapshot : datSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){
                        mChat.add(chat);
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, mChat, imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void status(String status){
        dbReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        dbReference.updateChildren(hashMap);
    }

    @Override
    protected void onResume(){
        super.onResume();
        status("online");
    }
    @Override
    protected void onPause(){
        super.onPause();
        dbReference.removeEventListener(seenListener);
        status("offline");
    }
}