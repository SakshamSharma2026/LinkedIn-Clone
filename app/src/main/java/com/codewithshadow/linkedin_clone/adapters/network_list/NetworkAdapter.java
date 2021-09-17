package com.codewithshadow.linkedin_clone.adapters.network_list;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.codewithshadow.linkedin_clone.R;
import com.codewithshadow.linkedin_clone.models.user.UserModel;
import com.codewithshadow.linkedin_clone.ui.custom_user.CustomUserActivity;
import java.util.List;

public class NetworkAdapter extends RecyclerView.Adapter<NetworkAdapter.MyViewHolder> {
    private Context aCtx;
    private List<UserModel> list;


    public NetworkAdapter(Context aCtx, List<UserModel> list) {
        this.aCtx = aCtx;
        this.list = list;
    }

    @NonNull
    @Override
    public NetworkAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(aCtx).inflate(R.layout.card_network, parent, false);
        return new NetworkAdapter.MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull NetworkAdapter.MyViewHolder holder, int position) {
        holder.name.setText(list.get(position).getUsername());
        Glide.with(aCtx).load(list.get(position).getImageUrl()).into(holder.userImage);
        holder.headline.setText(list.get(position).getHeadline());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(aCtx, CustomUserActivity.class);
            intent.putExtra("user_data", list.get(position));
            aCtx.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, headline;
        ImageView userImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txt_name);
            userImage = itemView.findViewById(R.id.profileImg);
            headline = itemView.findViewById(R.id.text_headline);
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

