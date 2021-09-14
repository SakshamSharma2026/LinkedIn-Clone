package com.codewithshadow.linkedin_clone.ui.story;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.codewithshadow.linkedin_clone.R;
import com.codewithshadow.linkedin_clone.base.BaseActivity;
import com.codewithshadow.linkedin_clone.constants.Constants;
import com.codewithshadow.linkedin_clone.utils.UniversalImageLoderClass;
import com.codewithshadow.linkedin_clone.models.story.StoryModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.shts.android.storiesprogressview.StoriesProgressView;

public class StoryActivity extends BaseActivity implements StoriesProgressView.StoriesListener {
    int counter = 0;
    long presstime = 0L;
    long limit = 500L;
    StoriesProgressView storiesProgressView;
    ImageView story_photo;
    TextView story_username, timetxt;
    ProgressBar progressBar;
    FirebaseUser user;

    List<String> images;
    List<String> storyids;

    String userid;
    CircleImageView imageView;


    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    presstime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - presstime;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        storiesProgressView = findViewById(R.id.stories);
        user = FirebaseAuth.getInstance().getCurrentUser();

        imageView = findViewById(R.id.img);
        story_photo = findViewById(R.id.storyimage);
        story_username = findViewById(R.id.username);
        timetxt = findViewById(R.id.time);

        userid = getIntent().getStringExtra("userid");


        userInfo(userid);
        getStories(userid);

        View reverse = findViewById(R.id.reverse);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storiesProgressView.reverse();
            }
        });
        reverse.setOnTouchListener(onTouchListener);


        View skip = findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storiesProgressView.skip();
            }
        });
        skip.setOnTouchListener(onTouchListener);
    }


    @Override
    public void onNext() {
        UniversalImageLoderClass.setImage(images.get(++counter), story_photo, progressBar);
        addView(storyids.get(counter));

    }

    @Override
    public void onPrev() {
        if ((counter - 1) < 0)
            return;
        UniversalImageLoderClass.setImage(images.get(--counter), story_photo, progressBar);
    }

    @Override
    public void onComplete() {
        finish();
    }

    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
    }


    @Override
    protected void onPause() {
        storiesProgressView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        storiesProgressView.resume();
        super.onResume();
    }

    private void getStories(String userid) {
        images = new ArrayList<>();
        storyids = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Story").child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                images.clear();
                storyids.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    StoryModel model = snap.getValue(StoryModel.class);
                    long timecurrent = System.currentTimeMillis();

                    if (timecurrent > model.getTimeStart() && timecurrent < model.getTimeEnd()) {
                        images.add(model.getStoryImg());
                        storyids.add(model.getStoryId());
                        covertTimeToText(model.getTimeUpload(), timetxt);
                    }
                }

                storiesProgressView.setStoriesCount(images.size());
                storiesProgressView.setStoryDuration(5000L);
                storiesProgressView.setStoriesListener(StoryActivity.this);
                storiesProgressView.startStories(counter);

                UniversalImageLoderClass.setImage(images.get(counter), story_photo, null);
                addView(storyids.get(counter));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void userInfo(String userid) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.USER_CONSTANT).child(userid)
                .child(Constants.INFO);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.child("username").getValue(String.class);
                String img = snapshot.child("imgurl").getValue(String.class);
                UniversalImageLoderClass.setImage(img, imageView, null);
                story_username.setText(username);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void addView(String storyid) {
        if (!userid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            FirebaseDatabase.getInstance().getReference().child("Story").child(userid).child(storyid)
                    .child("views").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
        }
    }


    public String covertTimeToText(String dataDate, TextView timetxt) {

        String convTime = null;

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date pasTime = dateFormat.parse(dataDate);

            Date nowTime = new Date();

            long dateDiff = nowTime.getTime() - pasTime.getTime();

            long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
            long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
            long hour = TimeUnit.MILLISECONDS.toHours(dateDiff);
            if (second < 60) {
                convTime = second + "s ";
            } else if (minute < 60) {
                convTime = minute + "m ";
            } else if (hour < 24) {
                convTime = hour + "h ";
            }
            timetxt.setText(convTime);


        } catch (ParseException e) {
            e.printStackTrace();
        }

        return convTime;
    }
}