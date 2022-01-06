package com.codewithshadow.linkedin_clone.adapters;

import static com.codewithshadow.linkedin_clone.constants.Constants.ALL_POSTS;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.codewithshadow.linkedin_clone.R;
import com.codewithshadow.linkedin_clone.models.PostModel;
import com.codewithshadow.linkedin_clone.utils.AppSharedPreferences;
import com.github.pgreze.reactions.PopupGravity;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luseen.autolinklibrary.AutoLinkMode;
import com.luseen.autolinklibrary.AutoLinkTextView;
import org.jetbrains.annotations.NotNull;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
    private Context aCtx;
    private List<PostModel> list;
    private final String[] strings = {
            "Like", "Celebrate", "Support", "Love", "Insightful", "Idea"};
    AppSharedPreferences appSharedPreferences;


    public PostAdapter(Context aCtx, List<PostModel> list) {
        this.aCtx = aCtx;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(aCtx).inflate(R.layout.card_post, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        appSharedPreferences = new AppSharedPreferences(aCtx);
        GestureDetector gestureDetector;


        gestureDetector = new GestureDetector(aCtx, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                addLike(user.getUid(), list.get(position).getKey(), ref, holder);
                holder.like_clr.setTextColor(ContextCompat.getColor(aCtx, R.color.main_color));
                return super.onSingleTapUp(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                ReactionsConfig config = new ReactionsConfigBuilder(aCtx)
                        .withReactions(new int[]{
                                R.drawable.ic_link_like,
                                R.drawable.ic_link_celebrate,
                                R.drawable.ic_link_care,
                                R.drawable.ic_link_love,
                                R.drawable.ic_link_idea,
                                R.drawable.ic_link_curious
                        })
                        .withPopupAlpha(255)
                        .withReactionTexts(position -> strings[position])
                        .withTextBackground(new ColorDrawable(Color.WHITE))
                        .withTextColor(Color.BLACK)
                        .withPopupGravity(PopupGravity.PARENT_RIGHT)
                        .withTextSize(aCtx.getResources().getDimension(R.dimen.reactions_text_size))
                        .build();

                ReactionPopup popup = new ReactionPopup(aCtx, config, (position) -> {
                    return true; // true is closing popup, false is requesting a new selection
                });

                popup.setReactionSelectedListener((position) -> {
                    if (position == (0)) {
                        holder.ic_like.setImageDrawable(ContextCompat.getDrawable(aCtx, R.drawable.ic_link_like));
                    } else if (position == 1) {
                        holder.ic_like.setImageDrawable(ContextCompat.getDrawable(aCtx, R.drawable.ic_link_celebrate));
                    } else if (position == 2) {
                        holder.ic_like.setImageDrawable(ContextCompat.getDrawable(aCtx, R.drawable.ic_link_care));
                    } else if (position == 3) {
                        holder.ic_like.setImageDrawable(ContextCompat.getDrawable(aCtx, R.drawable.ic_link_love));
                    } else if (position == 4) {
                        holder.ic_like.setImageDrawable(ContextCompat.getDrawable(aCtx, R.drawable.ic_link_idea));
                    } else {
                        holder.ic_like.setImageDrawable(ContextCompat.getDrawable(aCtx, R.drawable.ic_link_curious));
                    }
                    // Close selector if not invalid item (testing purpose)
                    return position != 3;
                });
                addLike(user.getUid(), list.get(position).getKey(), ref, holder);
                holder.btn_like.setOnTouchListener(popup);
                super.onLongPress(e);
            }
        }
        );


        holder.btn_like.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });


        holder.desc.addAutoLinkMode(AutoLinkMode.MODE_HASHTAG);
        holder.desc.setHashtagModeColor(aCtx.getResources().getColor(R.color.main_color));
        holder.desc.setAutoLinkText(list.get(position).getDescription());


        Glide.with(aCtx).load(list.get(position).getUser_profile()).into(holder.userImage);
        holder.userName.setText(list.get(position).getUsername());

        if (list.get(position).getImgUrl().equals("")) {
            holder.ll.setVisibility(View.GONE);
        } else {
            Glide.with(aCtx).load(list.get(position).getImgUrl()).into(holder.postImage);
            holder.ll.setVisibility(View.VISIBLE);
        }

        String userKey = user.getUid();
        String postKey = list.get(position).getKey();

        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference().child(ALL_POSTS).child(postKey).child("Likes");
        likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    if (snapshot1.getKey().equals(userKey)) {
                        holder.ic_like.setImageDrawable(ContextCompat.getDrawable(aCtx, R.drawable.ic_link_like));
                        holder.like_clr.setTextColor(ContextCompat.getColor(aCtx, R.color.main_color));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        isLikes(holder.likesTxt, holder.commentTxt, postKey);

    }

    private void isLikes(TextView textView, TextView commentcount, String postkey) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(ALL_POSTS)
                .child(postkey);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                textView.setText(snapshot.child("Likes").getChildrenCount() + "");
                commentcount.setText(snapshot.child("Comments").getChildrenCount() + " comments");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addLike(String uid, String key, DatabaseReference ref, MyViewHolder holder) {
        ref = FirebaseDatabase.getInstance().getReference().child(ALL_POSTS).child(key).child("Likes");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        Map<String, Object> map = new HashMap();
        map.put("time", sdf.format(date));
        map.put("username", appSharedPreferences.getUserName());
        map.put("imgUrl", appSharedPreferences.getImgUrl());
        ref.child(uid).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                holder.ic_like.setImageDrawable(ContextCompat.getDrawable(aCtx, R.drawable.ic_link_like));
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        AutoLinkTextView desc;
        ImageView postImage, ic_like;
        LinearLayout btn_like;
        CardView ll;
        CircleImageView userImage;
        TextView userName, likesTxt, commentTxt, like_clr;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            desc = itemView.findViewById(R.id.text_1);
            postImage = itemView.findViewById(R.id.post_img);
            btn_like = itemView.findViewById(R.id.btn_like);
            ll = itemView.findViewById(R.id.ll);
            ic_like = itemView.findViewById(R.id.ic_like);
            userImage = itemView.findViewById(R.id.button_image);
            userName = itemView.findViewById(R.id.username);
            likesTxt = itemView.findViewById(R.id.likesTxt);
            commentTxt = itemView.findViewById(R.id.commentTxt);
            like_clr = itemView.findViewById(R.id.like_clr);

        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
    }
}
