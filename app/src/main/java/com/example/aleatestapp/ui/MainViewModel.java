package com.example.aleatestapp.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.aleatestapp.data.Posts;
import com.example.aleatestapp.network.JsonApiInterface;
import com.example.aleatestapp.util.ServiceGenerator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends ViewModel {
    public MutableLiveData<List<Posts>> mutablePostList = new MutableLiveData<>();
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public void getPosts() {
        isLoading.setValue(true);
        JsonApiInterface call = ServiceGenerator.createService(JsonApiInterface.class);
        call.getPosts().enqueue(new Callback<List<Posts>>() {
            @Override
            public void onResponse(@NonNull Call<List<Posts>> call, @NonNull Response<List<Posts>> response) {
                if (response.isSuccessful()) {
                    mutablePostList.postValue(response.body());
                }
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(@NonNull Call<List<Posts>> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                t.printStackTrace();
            }
        });
    }
}