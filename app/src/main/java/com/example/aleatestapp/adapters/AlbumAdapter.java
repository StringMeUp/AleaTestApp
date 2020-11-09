package com.example.aleatestapp.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.aleatestapp.R;
import com.example.aleatestapp.data.Album;
import com.example.aleatestapp.databinding.AlbumCardBinding;

import java.util.Objects;

public class AlbumAdapter extends PagedListAdapter<Album, AlbumAdapter.ItemViewHolder> {

    public AlbumAdapter() {
        super(DIFF_CALLBACK);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public AlbumCardBinding albumCardBinding;

        public ItemViewHolder(@NonNull AlbumCardBinding albumBinding) {
            super(albumBinding.getRoot());
            this.albumCardBinding = albumBinding;
        }

        private void bind(Album album) {
            //Binding data to corresponding views. (DataBinding)
            if (album != null) {
                String albumId = "Album " + album.getAlbumId();
                String photoId = "Photo " + album.getId();
                albumCardBinding.albumId.setText(albumId);
                albumCardBinding.photoId.setText(photoId);
                albumCardBinding.titleTextView.setText(album.getTitle().toUpperCase());
                //There is an issue with User-Agent header parameters.
                //This is the only possible option/workaround to get the photos.
                GlideUrl url = new GlideUrl(album.getUrl(), new LazyHeaders.Builder()
                        .addHeader("User-Agent", "your-user-agent")
                        .build());

                Glide.with(albumCardBinding.albumImage)
                        .load(url)
                        .into(albumCardBinding.albumImage);
            }
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AlbumCardBinding albumBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.album_card, parent, false);
        return new ItemViewHolder(albumBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Album album = getItem(position);
        holder.bind(album);
    }

    //DiffUtil is a utility class that can calculate the difference between two lists.
    private static DiffUtil.ItemCallback<Album> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Album>() {
                //Called by the DiffUtil to decide whether two object represent the same Item.
                @Override
                public boolean areItemsTheSame(@NonNull Album oldItem, @NonNull Album newItem) {
                    return oldItem == newItem;
                }

                //Checks whether two items have the same data.
                @Override
                public boolean areContentsTheSame(@NonNull Album oldItem, @NonNull Album newItem) {
                    return Objects.equals(oldItem, newItem);
                }
            };
}
