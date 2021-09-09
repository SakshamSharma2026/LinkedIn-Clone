package com.codewithshadow.linkedin_clone.ui.login;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.codewithshadow.linkedin_clone.R;
import com.codewithshadow.linkedin_clone.adapters.onboarding.AppDescriptionSliderAdapter;
import com.codewithshadow.linkedin_clone.models.user.UserModel;
import com.codewithshadow.linkedin_clone.ui.home.HomeActivity;
import com.codewithshadow.linkedin_clone.ui.location.LocationActivity;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity {
    ViewPager viewPager;
    LinearLayout dotsLayout;

    AppDescriptionSliderAdapter appDescriptionSliderAdapter;
    TextView[] dots;

    private SignInClient oneTapClient;
    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    private final static String TAG = "LoginActivity";
    TextView btnSignIn;
    FirebaseAuth auth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        //Hooks
        viewPager = findViewById(R.id.viewPager);
        dotsLayout = findViewById(R.id.dots);

        //Call Adapter
        appDescriptionSliderAdapter = new AppDescriptionSliderAdapter(this);
        viewPager.setAdapter(appDescriptionSliderAdapter);

        //Dots
        addDots(0);
        viewPager.addOnPageChangeListener(changeListener);
        btnSignIn = findViewById(R.id.btn_signIn);
        btnSignIn.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, JoinNowActivity.class)));

        OneTapLogin();
    }


    private void addDots(int position) {
        dots = new TextView[3];
        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(45);
            dotsLayout.addView(dots[i]);
        }
        if (dots.length > 0) {
            dots[position].setTextColor(ContextCompat.getColor(this, R.color.black));
        }
    }

    ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            addDots(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    private void OneTapLogin() {
        oneTapClient = Identity.getSignInClient(this);
        // Your server's client ID, not your Android client ID.
        // Only show accounts previously used to sign in.
        // Automatically sign in when exactly one credential is retrieved.
        BeginSignInRequest signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                // Automatically sign in when exactly one credential is retrieved.
                .build();

        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, result -> {
                    try {
                        startIntentSenderForResult(
                                result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
                                null, 0, 0, 0);
                    } catch (IntentSender.SendIntentException e) {
                        Log.e(TAG, "Couldn't start One Tap UI: " + e.getLocalizedMessage());
                    }
                })
                .addOnFailureListener(this, e -> {
                    // No saved credentials found. Launch the One Tap sign-up flow, or
                    // do nothing and continue presenting the signed-out UI.
                    Log.d(TAG, e.getLocalizedMessage());
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_ONE_TAP) {
            try {
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = credential.getGoogleIdToken();
                String username = credential.getDisplayName();
                String emailAddress = credential.getId();
                String imageUrl = credential.getProfilePictureUri().toString();
                imageUrl = imageUrl.substring(0, imageUrl.length() - 5) + "s400-c";
                firebaseAuthWithGoogle(idToken, username, emailAddress, imageUrl);

                if (idToken != null) {
                    // Got an ID token from Google. Use it to authenticate
                    // with your backend.
                    Log.d(TAG, "Got ID token.");
                }
            } catch (ApiException e) {
                switch (e.getStatusCode()) {
                    case CommonStatusCodes.CANCELED:
                        Log.d(TAG, "One-tap dialog was closed.");
                        // Don't re-prompt the user.
                        break;
                    case CommonStatusCodes.NETWORK_ERROR:
                        Log.d(TAG, "One-tap encountered a network error.");
                        // Try again or just ignore.
                        break;
                    default:
                        Log.d(TAG, "Couldn't get credential from result." + e.getLocalizedMessage());
                        break;
                }
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken, String username, String emailAddress, String finalImageUrl) {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        AuthCredential authCredential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(authCredential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(auth.getCurrentUser().getUid())) {
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        } else {
                            UserModel model = new UserModel();
                            model.setEmailAddress(emailAddress);
                            model.setImageUrl(finalImageUrl);
                            model.setUsername(username);
                            model.setKey(auth.getCurrentUser().getUid());
                            databaseReference.child(auth.getCurrentUser().getUid()).child("Info").setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    startActivity(new Intent(LoginActivity.this, LocationActivity.class));
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithCustomToken:failure", task.getException());
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }
    }
}