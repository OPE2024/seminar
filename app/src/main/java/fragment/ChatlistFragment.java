package fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.onaopemipodimowo.O4Homes.Chat;
import com.onaopemipodimowo.O4Homes.Model.User;
import com.onaopemipodimowo.O4Homes.Notifications.Token;
import com.onaopemipodimowo.O4Homes.R;
import com.onaopemipodimowo.O4Homes.UserAdapter;

import org.testng.annotations.IFactoryAnnotation;

import java.util.ArrayList;
import java.util.List;

public class ChatlistFragment extends Fragment {

    private String TAG = "ChatlistFragment";
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;
    FirebaseUser firebaseUser;
    DatabaseReference dbReference;
    private List<String> usersList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatlist, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        usersList = new ArrayList<>();
        dbReference = FirebaseDatabase.getInstance().getReference("Chats");
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);

                    if (chat.getSender().equals(firebaseUser.getUid())){
                        usersList.add(chat.getReceiver());
                    }
                    if (chat.getReceiver().equals(firebaseUser.getUid())){
                        usersList.add(chat.getSender());
                    }

                }
                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        updateToken(FirebaseInstanceId.getInstance().getToken());


        return view;
    }

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Token");
        Token token1 = new Token(token);
        reference.child(firebaseUser.getUid()).setValue(token1);
    }

    private void readChats(){
        mUsers = new ArrayList<>();
        dbReference = FirebaseDatabase.getInstance().getReference("Users");
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);

                    Log.i(TAG, "Gotten user: " + user.getUsername());

                    //display 1 user from chats
                    for (String id : usersList){
                        Log.i(TAG, "ID: " + id);
                        if (user.getId().equals(id)){
                            Log.i(TAG, "USER ID: " + user.getId());
                            if (mUsers.size() != 0){
                                //CHECK THIS LATER
                                if (!mUsers.contains(user)) {
                                    mUsers.add(user);
                                }
                            }else {
                                mUsers.add(user);
                            }
                        }
                    }
                }
                userAdapter = new UserAdapter(getContext(), mUsers, true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}