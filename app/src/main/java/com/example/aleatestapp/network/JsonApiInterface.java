package com.example.aleatestapp.network;

import com.example.aleatestapp.data.Album;
import com.example.aleatestapp.data.Comments;
import com.example.aleatestapp.data.Posts;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface JsonApiInterface {

    @GET("posts/{id}/comments")
    Call<List<Comments>> getComments(@Path("id") int id);

    @GET("posts")
    Call<List<Posts>> getPosts();

    @GET("photos")
    Call<List<Album>> getAlbums(@Query("_start") int _start, @Query("_limit") int _limit);
}
