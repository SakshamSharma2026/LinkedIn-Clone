package com.codewithshadow.linkedin_clone.ui.profile;

import static com.codewithshadow.linkedin_clone.constants.Constants.INFO;
import static com.codewithshadow.linkedin_clone.constants.Constants.USER_CONSTANT;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.codewithshadow.linkedin_clone.R;
import com.codewithshadow.linkedin_clone.base.BaseActivity;
import com.codewithshadow.linkedin_clone.models.user.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends BaseActivity {
    ImageView img_edit_about, img_edit_profile, btn_back;
    RelativeLayout edit_about_layout;
    UserModel model;
    TextView name, location, aboutTxt, headlineTxt;
    CircleImageView profileImageView;
    DatabaseReference userRef;
    FirebaseUser user;
    TextView connectionsTxt;
    EditText editTextAbout, searchInput;
    CardView saveAboutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference().child(USER_CONSTANT).child(user.getUid());
        img_edit_about = findViewById(R.id.img_edit);
        saveAboutBtn = findViewById(R.id.save_btn);
        editTextAbout = findViewById(R.id.about_edittext);
        aboutTxt = findViewById(R.id.aboutTxt);
        connectionsTxt = findViewById(R.id.connections);
        img_edit_profile = findViewById(R.id.edit_profile);
        edit_about_layout = findViewById(R.id.edit_about_layout);
        name = findViewById(R.id.txt_name);
        headlineTxt = findViewById(R.id.headlineTxt);
        location = findViewById(R.id.txt_location);
        profileImageView = findViewById(R.id.profileImg);
        btn_back = findViewById(R.id.btn_back);
        searchInput = findViewById(R.id.item_search_input);
        model = new UserModel();


        // Back Button
        btn_back.setOnClickListener(v -> onBackPressed());

        //Get Data from Firebase
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                model = snapshot.child(INFO).getValue(UserModel.class);
                name.setText(model.getUsername());
                location.setText(model.getLocation());
                headlineTxt.setText(model.getHeadline());
                searchInput.setText(model.getUsername());
                Glide.with(getApplicationContext()).load(model.getImageUrl()).into(profileImageView);


                if (snapshot.child("Data").child("about").exists()) {
                    aboutTxt.setText(snapshot.child("Data").child("about").getValue(String.class));
                    aboutTxt.setLines(3);
                } else {
                    aboutTxt.setText(String.format("%s", "Add a summary about yourself"));
                    aboutTxt.setLines(1);
                }
                connectionsTxt.setText(snapshot.child("Connections").getChildrenCount() + " connections");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // Save Button
        saveAboutBtn.setOnClickListener(v -> {
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    userRef.child("Data").child("about").setValue(editTextAbout.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if (edit_about_layout.getVisibility() == View.VISIBLE) {
                                startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        });


        // Edit Profile
        img_edit_about.setOnClickListener(v -> edit_about_layout.setVisibility(View.VISIBLE));
        img_edit_profile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileIntroActivity.class);
            intent.putExtra("user_name", model.getUsername());
            intent.putExtra("user_imgUrl", model.getImageUrl());
            intent.putExtra("user_location", model.getLocation());
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (edit_about_layout.getVisibility() == View.VISIBLE) {
            startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
        }
    }
}