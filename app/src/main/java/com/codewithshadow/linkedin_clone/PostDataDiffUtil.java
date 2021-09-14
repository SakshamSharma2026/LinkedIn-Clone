package com.codewithshadow.linkedin_clone;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.codewithshadow.linkedin_clone.models.post.PostModel;

import java.util.List;

public class PostDataDiffUtil extends DiffUtil.Callback {
    private List<PostModel> oldList;

    public PostDataDiffUtil(List<PostModel> oldList, List<PostModel> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    private List<PostModel> newList;

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

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {

        PostModel newModel = newList.get(newItemPosition);
        PostModel oldModel = oldList.get(oldItemPosition);

        Bundle diff = new Bundle();

        if (!newModel.getPostKey().equals(oldModel.getPostKey())) {
            diff.putString("postKey", newModel.getPostKey());
        }
        if (diff.size() == 0) {
            return null;
        }
        return diff;
        //return super.getChangePayload(oldItemPosition, newItemPosition);
    }

}
