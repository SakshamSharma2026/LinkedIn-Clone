package com.codewithshadow.linkedin_clone.ui.share_post;

import static com.codewithshadow.linkedin_clone.constants.Constants.INFO;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.codewithshadow.linkedin_clone.R;
import com.codewithshadow.linkedin_clone.base.BaseActivity;
import com.codewithshadow.linkedin_clone.ui.home.HomeActivity;
import com.codewithshadow.linkedin_clone.utils.AppSharedPreferences;
import com.codewithshadow.linkedin_clone.utils.LoadingDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class SharePostActivity extends BaseActivity {
    EditText edit_text;
    ImageView post_img, btn_select_img, profileImg, closeImg;
    TextView userName;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    FirebaseAuth auth;
    FirebaseUser user;
    TextView btn_post;
    AppSharedPreferences appSharedPreferences;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_post);
        appSharedPreferences = new AppSharedPreferences(this);
        loadingDialog = new LoadingDialog(this);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        edit_text = findViewById(R.id.edit_text);
        post_img = findViewById(R.id.post_img);
        btn_select_img = findViewById(R.id.img3);
        btn_post = findViewById(R.id.btn_post);
        userName = findViewById(R.id.user_name);
        profileImg = findViewById(R.id.user_img);
        closeImg = findViewById(R.id.close_img);

        userName.setText(appSharedPreferences.getUserName());
        Glide.with(this).load(appSharedPreferences.getImgUrl()).into(profileImg);

        // Close Activity
        closeImg.setOnClickListener(v -> finish());

        // Select Image
        btn_select_img.setOnClickListener(v -> openFileChooser());


        edit_text.requestFocus();
        edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btn_post.setTextColor(Color.BLACK);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Post Button
        btn_post.setOnClickListener(v ->
        {
            if (mImageUri != null) {
                loadingDialog.startLoadingDialog();
                uploadFile(mImageUri);
            } else {
                if (!edit_text.getText().toString().isEmpty())
                    loadingDialog.startLoadingDialog();
                uploadData(edit_text.getText().toString());
            }
        });
    }

    private void uploadData(String toString) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("AllPosts");
        String key = ref.push().getKey();
        HashMap<String, Object> map = new HashMap<>();
        map.put("description", toString);
        map.put("imgUrl", "");
        map.put("username", appSharedPreferences.getUserName());
        map.put("user_profile", appSharedPreferences.getImgUrl());
        map.put("key", key);
        ref.child(key).child(INFO).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                loadingDialog.dismissDialog();
                Intent intent = new Intent(SharePostActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//makesure user cant go back
                startActivity(intent);
            }
        });
    }


    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null) {
            mImageUri = data.getData();
            CropImage.activity(mImageUri)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                Glide.with(this).load(mImageUri)
                        .into(post_img);
                btn_post.setTextColor(Color.BLACK);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(SharePostActivity.this, "" + error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    //-------------------------------Upload User Image-------------------------------//
    private void uploadFile(Uri mImageUri) {
        if (mImageUri != null) {
            final StorageReference reference = mStorageRef.child(user.getUid()).child("Files/" + System.currentTimeMillis());
            reference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("AllPosts");
                                    String key = ref.push().getKey();
                                    HashMap<String, Object> map = new HashMap<>();
                                    String imageUrl = uri.toString();
                                    map.put("imgUrl", imageUrl);
                                    map.put("description", edit_text.getText().toString());
                                    map.put("username", appSharedPreferences.getUserName());
                                    map.put("user_profile", appSharedPreferences.getImgUrl());
                                    map.put("key", key);
                                    ref.child(key).child(INFO).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                                            loadingDialog.dismissDialog();
                                            Intent intent = new Intent(SharePostActivity.this, HomeActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//makesure user cant go back
                                            startActivity(intent);
                                        }
                                    });

                                }
                            });
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SharePostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }
}