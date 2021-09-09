package com.codewithshadow.linkedin_clone.adapters.request;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codewithshadow.linkedin_clone.R;
import com.codewithshadow.linkedin_clone.models.request.RequestModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.MyViewHolder> {
    private Context aCtx;
    private List<RequestModel> list;


    public RequestAdapter(Context aCtx, List<RequestModel> list) {
        this.aCtx = aCtx;
        this.list = list;
    }

    @NonNull
    @Override
    public RequestAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(aCtx).inflate(R.layout.card_network_request, parent, false);
        return new RequestAdapter.MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RequestAdapter.MyViewHolder holder, int position) {

        holder.name.setText(list.get(position).getUsername());
        Glide.with(aCtx).load(list.get(position).getImageUrl()).into(holder.userImage);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());

        holder.connectOk.setOnClickListener(v -> {
            ref.child("Request").child(list.get(position).getKey()).removeValue();
            ref.child("Connections").child(list.get(position).getKey()).setValue(true);
        });

        holder.connectCancel.setOnClickListener(v -> {
            ref.child(list.get(position).getKey()).removeValue();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView userImage;
        CardView connectOk, connectCancel;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_text);
            userImage = itemView.findViewById(R.id.item_image);
            connectOk = itemView.findViewById(R.id.connect_ok);
            connectCancel = itemView.findViewById(R.id.connect_cancel);

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


