package com.example.aleatestapp.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aleatestapp.R;
import com.example.aleatestapp.data.Comments;
import com.example.aleatestapp.databinding.CommentsCardBinding;

import java.util.ArrayList;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder> {

    private ArrayList<Comments> postsModelArrayList;

    public CommentsAdapter(ArrayList<Comments> comments) {
        this.postsModelArrayList = comments;
    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder {
        public CommentsCardBinding commentsCardBinding;

        private CommentsViewHolder(CommentsCardBinding binding) {
            super(binding.getRoot());
            this.commentsCardBinding = binding;
        }

        //Binding data to corresponding views. (DataBinding)
        @SuppressLint("SetTextI18n")
        private void bind(Comments model) {
            commentsCardBinding.postId.setText(model.getPostId().toString());
            commentsCardBinding.id.setText(model.getId().toString());
            commentsCardBinding.postName.setText(model.getName());
            commentsCardBinding.eMailTextView.setText(model.getEmail());
            commentsCardBinding.postBody.setText(model.getBody());
        }
    }

    @NonNull
    @Override
    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Referencing the auto-generated dataBinding class and inflating the view.
        CommentsCardBinding commentsCardBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.comments_card, parent, false);
        return new CommentsViewHolder(commentsCardBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsViewHolder holder, int position) {
        //Getting position of each item.
        holder.bind(postsModelArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return postsModelArrayList.size();
    }

    //Updating the list in the adapter.
    public void updateList(ArrayList<Comments> updatedList) {
        postsModelArrayList.clear();
        postsModelArrayList.addAll(updatedList);
    }
}

