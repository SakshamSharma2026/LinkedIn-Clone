package com.codewithshadow.linkedin_clone.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.codewithshadow.linkedin_clone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class EditProfileIntroActivity extends AppCompatActivity {
    EditText editTextFirstName, editTextLastName, editTextHeadline, editTextPosition, editTextEducation, editTextLocation;
    String stringUserId, stringUserName, stringUserImgUrl, stringUserEmail, stringUserLocation;

    DatabaseReference ref;
    FirebaseUser user;
    TextView saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_intro);
        user = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        editTextFirstName = findViewById(R.id.edit_first_name);
        editTextLastName = findViewById(R.id.edit_last_name);
        editTextHeadline = findViewById(R.id.edit_headline);
        editTextPosition = findViewById(R.id.edit_position);
        editTextEducation = findViewById(R.id.edit_education);
        editTextLocation = findViewById(R.id.edit_location);

        saveBtn = findViewById(R.id.save_btn);

        Intent intent = getIntent();
        stringUserName = intent.getStringExtra("user_name");
        stringUserImgUrl = intent.getStringExtra("user_imgUrl");
        stringUserLocation = intent.getStringExtra("user_location");


        String[] split = stringUserName.split(" ");
        editTextFirstName.setText(split[0]);
        editTextLastName.setText(split[1]);

        editTextLocation.setText(stringUserLocation);
        saveBtn.setOnClickListener(v -> {
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("education", editTextEducation.getText().toString());
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        });

    }
}