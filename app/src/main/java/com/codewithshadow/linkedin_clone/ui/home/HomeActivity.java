package com.codewithshadow.linkedin_clone.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.codewithshadow.linkedin_clone.R;
import com.codewithshadow.linkedin_clone.base.BaseActivity;
import com.codewithshadow.linkedin_clone.constants.Constants;
import com.codewithshadow.linkedin_clone.models.UserModel;
import com.codewithshadow.linkedin_clone.ui.fragments.HomeFragment;
import com.codewithshadow.linkedin_clone.ui.fragments.JobsFragment;
import com.codewithshadow.linkedin_clone.ui.fragments.NetworkFragment;
import com.codewithshadow.linkedin_clone.ui.fragments.NotificationFragment;
import com.codewithshadow.linkedin_clone.ui.message_user.MessageUsersActivity;
import com.codewithshadow.linkedin_clone.ui.profile.ProfileActivity;
import com.codewithshadow.linkedin_clone.ui.share_post.SharePostActivity;
import com.codewithshadow.linkedin_clone.utils.AppSharedPreferences;
import com.codewithshadow.linkedin_clone.utils.UniversalImageLoderClass;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

public class HomeActivity extends BaseActivity {
    DrawerLayout drawerLayout;
    ImageView profileImg, messageBtn, nav_img, nav_close_img;
    NavigationView mNavigationView;
    TextView tt, nav_name;
    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment = null;
    AppSharedPreferences appSharedPreferences;
    DatabaseReference userRef;
    FirebaseUser user;
    UserModel model;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        appSharedPreferences = new AppSharedPreferences(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference().child(Constants.USER_CONSTANT).child(user.getUid());
        drawerLayout = findViewById(R.id.drawerLayout);
        profileImg = findViewById(R.id.img);
        messageBtn = findViewById(R.id.messageBtn);
        mNavigationView = findViewById(R.id.nav_view);


        //UniversalImageLoaderClass
        UniversalImageLoderClass universalImageLoderClass = new UniversalImageLoderClass(this);
        ImageLoader.getInstance().init(universalImageLoderClass.getConfig());


        // Header
        View header = mNavigationView.getHeaderView(0);
        nav_name = header.findViewById(R.id.user_name);
        nav_img = header.findViewById(R.id.img);
        nav_close_img = header.findViewById(R.id.close_img);
        tt = header.findViewById(R.id.tt);

        //Open Profile Activity
        tt.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ProfileActivity.class)));

        // Set Header Data
        Glide.with(this).load(appSharedPreferences.getImgUrl()).into(profileImg);
        Glide.with(this).load(appSharedPreferences.getImgUrl()).into(nav_img);
        nav_name.setText(appSharedPreferences.getUserName());


        //NavBar Close
        nav_close_img.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START);
        });


        // Open Drawer Layout
        profileImg.setOnClickListener(v -> {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.openDrawer(Gravity.START);
            else drawerLayout.closeDrawer(Gravity.END);
        });

        // Open Message Activity
        messageBtn.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MessageUsersActivity.class);
            startActivity(intent);
        });


        //BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation_bar);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationSelectedListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();


        // Get Data from Firebase
        userRef.child("Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                model = snapshot.getValue(UserModel.class);
                appSharedPreferences.setUsername(model.getUsername());
                appSharedPreferences.setImgUrl(model.getImageUrl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navigationSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.nav_network:
                    selectedFragment = new NetworkFragment();
                    break;
                case R.id.nav_uplod:
                    selectedFragment = null;
                    startActivity(new Intent(HomeActivity.this, SharePostActivity.class));
                    overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                    break;
                case R.id.nav_notification:
                    selectedFragment = new NotificationFragment();
                    break;
                case R.id.nav_jobs:
                    selectedFragment = new JobsFragment();
                    break;
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, selectedFragment).commit();
            }
            return true;
        }
    };

    @Override
    public void onBackPressed() {
        BottomNavigationView mBottomNavigationView = findViewById(R.id.bottom_navigation_bar);
        if (mBottomNavigationView.getSelectedItemId() == R.id.nav_home) {
            super.onBackPressed();
            finish();
        } else {
            mBottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

}