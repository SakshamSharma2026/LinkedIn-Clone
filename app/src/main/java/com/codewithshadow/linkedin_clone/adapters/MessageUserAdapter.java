package com.codewithshadow.linkedin_clone.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.codewithshadow.linkedin_clone.R;
import com.codewithshadow.linkedin_clone.models.UserModel;
import java.util.List;

public class MessageUserAdapter extends RecyclerView.Adapter<MessageUserAdapter.MyViewHolder> {
    private Context aCtx;
    private List<UserModel> list;


    public MessageUserAdapter(Context aCtx, List<UserModel> list) {
        this.aCtx = aCtx;
        this.list = list;
    }

    @NonNull
    @Override
    public MessageUserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(aCtx).inflate(R.layout.card_useritem, parent, false);
        return new MessageUserAdapter.MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MessageUserAdapter.MyViewHolder holder, int position) {
        holder.name.setText(list.get(position).getUsername());
        Glide.with(aCtx).load(list.get(position).getImageUrl()).into(holder.userImage);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView userImage;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_text);
            userImage = itemView.findViewById(R.id.item_image);
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


