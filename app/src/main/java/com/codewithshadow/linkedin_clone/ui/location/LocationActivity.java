package com.codewithshadow.linkedin_clone.ui.location;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.codewithshadow.linkedin_clone.R;
import com.codewithshadow.linkedin_clone.ui.home.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class LocationActivity extends AppCompatActivity {
    EditText editRegion, editHeadline;
    FrameLayout continueBtn;
    FirebaseAuth auth;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        editRegion = findViewById(R.id.editRegion);
        editHeadline = findViewById(R.id.edit_headline);
        continueBtn = findViewById(R.id.continue_btn);
        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference().child("Users");

        continueBtn.setOnClickListener(v -> {
            Map<String, Object> map = new HashMap<>();
            map.put("location", editRegion.getText().toString());
            map.put("headline", editHeadline.getText().toString());
            ref.child(auth.getCurrentUser().getUid()).child("Info").updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {
                    startActivity(new Intent(LocationActivity.this, HomeActivity.class));
                    finish();
                }
            });
        });


    }
}