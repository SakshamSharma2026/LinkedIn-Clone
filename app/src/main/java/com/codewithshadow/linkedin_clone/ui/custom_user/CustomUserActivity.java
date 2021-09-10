package com.codewithshadow.linkedin_clone.ui.custom_user;

import static com.codewithshadow.linkedin_clone.constants.Constants.REQUEST;
import static com.codewithshadow.linkedin_clone.constants.Constants.USER_CONSTANT;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.codewithshadow.linkedin_clone.R;
import com.codewithshadow.linkedin_clone.models.user.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomUserActivity extends AppCompatActivity {
    CircleImageView profileImg;
    TextView txt_name, txt_title, txt_work, txt_location, item_search_input;
    CardView connectBtn;
    DatabaseReference database;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_user);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        UserModel userModel = intent.getParcelableExtra("user_data");


        txt_name = findViewById(R.id.txt_name);
        txt_title = findViewById(R.id.txt_title);
        txt_work = findViewById(R.id.txt_work);
        txt_location = findViewById(R.id.txt_location);
        profileImg = findViewById(R.id.profileImg);
        item_search_input = findViewById(R.id.item_search_input);
        connectBtn = findViewById(R.id.connectBtn);


        item_search_input.setText(userModel.getUsername());
        txt_name.setText(userModel.getUsername());
        txt_location.setText(userModel.getLocation());
        Glide.with(this).load(userModel.getImageUrl()).into(profileImg);


        connectBtn.setOnClickListener(v -> {
            database.child(USER_CONSTANT).child(userModel.getKey()).child(REQUEST).child(user.getUid()).setValue(true);
            connectBtn.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray));
            connectBtn.setEnabled(false);
        });
    }
}