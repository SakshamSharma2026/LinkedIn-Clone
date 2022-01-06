package com.codewithshadow.linkedin_clone.ui.fragments;

import static com.codewithshadow.linkedin_clone.constants.Constants.ALL_POSTS;
import static com.codewithshadow.linkedin_clone.constants.Constants.INFO;
import static com.codewithshadow.linkedin_clone.constants.Constants.STORY;
import static com.codewithshadow.linkedin_clone.constants.Constants.USER_CONSTANT;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.codewithshadow.linkedin_clone.R;
import com.codewithshadow.linkedin_clone.adapters.PostAdapter;
import com.codewithshadow.linkedin_clone.adapters.StoryAdapter;
import com.codewithshadow.linkedin_clone.models.PostModel;
import com.codewithshadow.linkedin_clone.models.StoryModel;
import com.codewithshadow.linkedin_clone.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.todkars.shimmer.ShimmerRecyclerView;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class HomeFragment extends Fragment {
    FirebaseUser user;
    List<PostModel> list;
    PostAdapter adapter;
    ShimmerRecyclerView recyclerView, recyclerViewStory;
    DatabaseReference ref;
    List<StoryModel> storyModelList;
    StoryAdapter storyAdapter;
    private List<String> followingList;


    public HomeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.post_recycler);
        recyclerViewStory = view.findViewById(R.id.story_recycler);
        user = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference();
        ref.keepSynced(true);
        list = new ArrayList<>();
        storyModelList = new ArrayList<>();
        followingList = new ArrayList<>();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Post RecyclerView
        recyclerView.showShimmer();
        adapter = new PostAdapter(getContext(), list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setNestedScrollingEnabled(false);

        //Story RecyclerView
        storyAdapter = new StoryAdapter(getActivity(), storyModelList);
        recyclerViewStory.setHasFixedSize(true);
        recyclerViewStory.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        recyclerViewStory.setAdapter(storyAdapter);
        recyclerViewStory.setNestedScrollingEnabled(false);

        //Functions
        Read_Posts();
        GetAllUsersId();

    }

    //----------------------------------Read Posts--------------------------------//
    private void Read_Posts() {
        recyclerView.hideShimmer();
        ref.child(ALL_POSTS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    PostModel model = dataSnapshot.child(INFO).getValue(PostModel.class);
                    list.add(model);
                }
                Collections.reverse(list);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    //--------------------------------Get All Users Id--------------------------------//
    private void GetAllUsersId() {
        followingList = new ArrayList<>();
        ref.child(USER_CONSTANT).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingList.clear();
                UserModel model = null;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    model = dataSnapshot.child(INFO).getValue(UserModel.class);
                    assert model != null;
                    if (!model.getKey().equals(user.getUid())) {
                        followingList.add(dataSnapshot.getKey());
                    }
                }
                readStory();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //-----------------------------Read Story------------------------//

    private void readStory() {
        ref.child(STORY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long timeCurrent = System.currentTimeMillis();
                storyModelList.clear();
                storyModelList.add(new StoryModel("", 0, 0, FirebaseAuth.getInstance().getCurrentUser().getUid(), "", ""));
                for (String id : followingList) {
                    int countStory = 0;
                    StoryModel storyModel = null;
                    for (DataSnapshot snapshot2 : snapshot.child(id).getChildren()) {
                        storyModel = snapshot2.getValue(StoryModel.class);
                        if (timeCurrent > storyModel.getTimeStart() && timeCurrent < storyModel.getTimeEnd()) {
                            countStory++;
                        }
                    }
                    if (countStory > 0) {
                        storyModelList.add(storyModel);
                    }
                }
                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}