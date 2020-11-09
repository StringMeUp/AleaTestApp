package com.example.aleatestapp.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aleatestapp.R;
import com.example.aleatestapp.data.Posts;
import com.example.aleatestapp.databinding.PostsCardBinding;

import java.util.ArrayList;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostsViewHolder> {

    private ArrayList<Posts> postsArrayList;
    public ClickEvent clickEvent;

    public PostsAdapter(ArrayList<Posts> posts, ClickEvent clickEvent) {
        this.postsArrayList = posts;
        this.clickEvent = clickEvent;
    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder {
        public PostsCardBinding postsCardBinding;
        public ClickEvent clickEvent;

        private PostsViewHolder(PostsCardBinding binding, ClickEvent clickEvent) {
            super(binding.getRoot());
            this.postsCardBinding = binding;
            this.clickEvent = clickEvent;
        }

        //Referencing the auto-generated dataBinding class and inflating the view.
        @SuppressLint("SetTextI18n")
        public void bind(Posts model) {
            postsCardBinding.userId.setText(Integer.toString(model.getUserId()));
            postsCardBinding.id.setText(Integer.toString(model.getId()));
            postsCardBinding.title.setText(model.getTitle().toUpperCase());
            postsCardBinding.body.setText(model.getBody());
            //On click event will trigger the interface and obtain userId that is further used in the activity that implements the interface.
            itemView.setOnClickListener(v -> clickEvent.setClickListener(model.getUserId()));
        }
    }

    @NonNull
    @Override
    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PostsCardBinding cardBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.posts_card, parent, false);
        return new PostsViewHolder(cardBinding, clickEvent);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsViewHolder holder, int position) {
        holder.bind(postsArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return postsArrayList.size();
    }

    public void updateList(ArrayList<Posts> updatedList) {
        postsArrayList.clear();
        postsArrayList.addAll(updatedList);
    }

    //Interface callback that will be implemented in the activity.
    public interface ClickEvent {
        void setClickListener(int id);
    }
}

