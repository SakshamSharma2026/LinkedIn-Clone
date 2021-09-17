package com.codewithshadow.linkedin_clone.ui.message_user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codewithshadow.linkedin_clone.R;
import com.codewithshadow.linkedin_clone.adapters.message_users_list.MessageUserAdapter;
import com.codewithshadow.linkedin_clone.base.BaseActivity;
import com.codewithshadow.linkedin_clone.constants.Constants;
import com.codewithshadow.linkedin_clone.models.user.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageUsersActivity extends BaseActivity {
    List<UserModel> list;
    DatabaseReference ref;
    FirebaseUser user;
    MessageUserAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_users);
        recyclerView = findViewById(R.id.user_recycler);
        user = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference();
        list = new ArrayList<>();

        //User RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Function
        readUsers();

    }

    //----------------------------------Read Users--------------------------------//
    private void readUsers() {
        ref.child(Constants.USER_CONSTANT).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserModel model = dataSnapshot.child(Constants.INFO).getValue(UserModel.class);
                    if (!model.getKey().equals(user.getUid())) {
                        list.add(model);
                    }
                }
                Collections.reverse(list);
                adapter = new MessageUserAdapter(MessageUsersActivity.this, list);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}