package com.codewithshadow.linkedin_clone.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.codewithshadow.linkedin_clone.R;
import com.codewithshadow.linkedin_clone.utils.AppSharedPreferences;


public class JobsFragment extends Fragment {
    ImageView profileImg;
    AppSharedPreferences appSharedPreferences;

    public JobsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_jobs, container, false);
        profileImg = view.findViewById(R.id.user_img);
        appSharedPreferences = new AppSharedPreferences(requireContext());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Glide.with(requireContext()).load(appSharedPreferences.getImgUrl()).into(profileImg);
    }
}