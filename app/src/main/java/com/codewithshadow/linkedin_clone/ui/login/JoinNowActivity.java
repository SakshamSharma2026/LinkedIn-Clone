package com.codewithshadow.linkedin_clone.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.codewithshadow.linkedin_clone.R;
import com.codewithshadow.linkedin_clone.base.BaseActivity;
import com.codewithshadow.linkedin_clone.models.UserModel;
import com.codewithshadow.linkedin_clone.ui.home.HomeActivity;
import com.codewithshadow.linkedin_clone.ui.location.LocationActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.jetbrains.annotations.NotNull;
public class JoinNowActivity extends BaseActivity {

    RelativeLayout googleBtn;
    FirebaseAuth auth;
    GoogleSignInClient client;
    private final static int Rc_Sign_in = 1;
    private final static String TAG = "JoinActivity";
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_now);
        auth = FirebaseAuth.getInstance();

        googleBtn = findViewById(R.id.card_google_btn);
        googleBtn.setOnClickListener(v -> googlesignIn());
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");


        //-----------------Google Button-----------------//


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();

        client = GoogleSignIn.getClient(this, gso);
    }

//-----------------GOOGLE FUNCTION----------------//

    private void googlesignIn() {
        Intent intent = client.getSignInIntent();
        startActivityForResult(intent, Rc_Sign_in);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == Rc_Sign_in) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                String email = account.getEmail();
                String user_name = account.getDisplayName();
                String imageUrl = account.getPhotoUrl().toString();
                imageUrl = imageUrl.substring(0, imageUrl.length() - 5) + "s400-c";

                //Url Change HoSakta hai
                firebaseAuthWithGoogle(account.getIdToken(), user_name, email, imageUrl);
            } catch (ApiException e) {
                Toast.makeText(JoinNowActivity.this, "Signin Error", Toast.LENGTH_LONG).show();
                // Google Sign In failed, update UI appropriately
                Log.w("Error", "Google sign in failed", e);
            }
        }

    }

    private void firebaseAuthWithGoogle(String idToken, String username, String emailAddress, String finalImageUrl) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(auth.getCurrentUser().getUid())) {
                                startActivity(new Intent(JoinNowActivity.this, HomeActivity.class));
                            } else {
                                UserModel model = new UserModel();
                                model.setEmailAddress(emailAddress);
                                model.setImageUrl(finalImageUrl);
                                model.setUsername(username);
                                model.setKey(auth.getCurrentUser().getUid());
                                databaseReference.child(auth.getCurrentUser().getUid()).child("Info").setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                        startActivity(new Intent(JoinNowActivity.this, LocationActivity.class));

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCustomToken:failure", task.getException());

                }
            }
        });
    }
}