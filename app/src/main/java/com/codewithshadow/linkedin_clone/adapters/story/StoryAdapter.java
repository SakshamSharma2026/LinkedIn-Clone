package com.codewithshadow.linkedin_clone.adapters.story;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.codewithshadow.linkedin_clone.R;
import com.codewithshadow.linkedin_clone.models.story.StoryModel;
import com.codewithshadow.linkedin_clone.ui.story.AddStoryActivity;
import com.codewithshadow.linkedin_clone.ui.story.StoryActivity;
import com.codewithshadow.linkedin_clone.utils.UniversalImageLoderClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.List;
import java.util.Objects;
import de.hdodenhof.circleimageview.CircleImageView;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.MyViewHolder> {
    final private Context aCtx;
    final private List<StoryModel> list;

    public StoryAdapter(Context aCtx, List<StoryModel> list) {
        this.aCtx = aCtx;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(aCtx).inflate(R.layout.card_mystory, parent, false);
        } else {
            view = LayoutInflater.from(aCtx).inflate(R.layout.card_story, parent, false);
        }
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        StoryModel model = list.get(position);
        userInfo(holder, model.getUserId(), position);

        if (holder.getAdapterPosition() != 0) {
            seenStory(holder, model.getUserId());
        }
        if (holder.getAdapterPosition() == 0) {
            myStory(holder.addStory_text, holder.story_plus, false, holder);
        }

        holder.itemView.setOnClickListener(view -> {
            if (holder.getAdapterPosition() == 0) {
                myStory(holder.addStory_text, holder.story_plus, true, holder);
            } else {
                Intent intent = new Intent(aCtx, StoryActivity.class);
                intent.putExtra("userid", model.getUserId());
                aCtx.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView story_username, addStory_text;
        ImageView story_plus;
        CircleImageView story_photo;
        RelativeLayout story_photo_seen_layout;
        CardView white_card;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            story_username = itemView.findViewById(R.id.story_username);
            addStory_text = itemView.findViewById(R.id.mystorytext);
            story_plus = itemView.findViewById(R.id.add_story);
            story_photo = itemView.findViewById(R.id.button_image);
            story_photo_seen_layout = itemView.findViewById(R.id.button_click_parent);
            white_card = itemView.findViewById(R.id.white_card);


        }
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        }
        return 1;
    }


    private void userInfo(final MyViewHolder holder, final String userid, final int position) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("Info");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("username").getValue(String.class);
                String img = snapshot.child("imageUrl").getValue(String.class);
                UniversalImageLoderClass.setImage(img, holder.story_photo, null);

                if (position != 0) {
                    UniversalImageLoderClass.setImage(img, holder.story_photo, null);
                    holder.story_username.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void myStory(TextView textView, ImageView imageView, boolean click, MyViewHolder holder) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Story").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                long timeCurrent = System.currentTimeMillis();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    StoryModel storyModel = snap.getValue(StoryModel.class);
                    assert storyModel != null;
                    if (timeCurrent > storyModel.getTimeStart() && timeCurrent < storyModel.getTimeEnd()) {
                        count++;
                    }
                }

                if (click) {
                    if (count > 0) {
                        AlertDialog alertDialog = new AlertDialog.Builder(aCtx).create();
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "View Story",
                                (dialogInterface, i) -> {
                                    Intent intent = new Intent(aCtx, StoryActivity.class);
                                    intent.putExtra("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    aCtx.startActivity(intent);
                                    dialogInterface.dismiss();
                                });
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add Story",
                                (dialogInterface, i) -> {
                                    Intent intent = new Intent(aCtx, AddStoryActivity.class);
                                    aCtx.startActivity(intent);
                                    dialogInterface.dismiss();
                                });
                        alertDialog.show();
                    } else {
                        Intent intent = new Intent(aCtx, AddStoryActivity.class);
                        aCtx.startActivity(intent);
                    }
                } else {
                    if (count > 0) {
                        textView.setText("Your Story");
                        holder.story_photo_seen_layout.setBackground(aCtx.getResources().getDrawable(R.drawable.profile_picture_gradient));
                        imageView.setVisibility(View.GONE);
                        holder.white_card.setVisibility(View.GONE);

                    } else {
                        textView.setText("Add Story");
                        imageView.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void seenStory(MyViewHolder holder, String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Story").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    if (!snapshot1.child("views")
                            .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).exists() && System.currentTimeMillis() < Objects.requireNonNull(snapshot1.getValue(StoryModel.class)).getTimeEnd()) {
                        i++;
                    }
                }
                if (i > 0) {
                    holder.story_photo.setVisibility(View.VISIBLE);
                    holder.story_photo_seen_layout.setBackground(aCtx.getResources().getDrawable(R.drawable.profile_picture_gradient));

                } else {
                    holder.story_photo_seen_layout.setBackgroundColor(Color.GRAY);
                    holder.story_photo.setVisibility(View.VISIBLE);

                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}






