package com.codewithshadow.linkedin_clone;

import androidx.recyclerview.widget.DiffUtil;

import com.codewithshadow.linkedin_clone.models.story.StoryModel;
import com.codewithshadow.linkedin_clone.models.user.UserModel;

import java.util.List;

public class StoryDataDiffUtil extends DiffUtil.Callback {
    private List<StoryModel> oldList;

    public StoryDataDiffUtil(List<StoryModel> oldList, List<StoryModel> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    private List<StoryModel> newList;

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition) == newList.get(newItemPosition);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition) == newList.get(newItemPosition);
    }


}

