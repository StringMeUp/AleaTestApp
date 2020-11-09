package com.example.aleatestapp.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.aleatestapp.R;
import com.example.aleatestapp.adapters.AlbumAdapter;
import com.example.aleatestapp.adapters.CommentsAdapter;
import com.example.aleatestapp.data.Comments;
import com.example.aleatestapp.databinding.ActivityDetailsBinding;
import com.example.aleatestapp.paging.AlbumDataSource;
import com.example.aleatestapp.util.Constants;
import com.example.aleatestapp.util.MyApplication;
import com.example.aleatestapp.util.NetworkConnection;

import java.util.ArrayList;

import okhttp3.ResponseBody;

public class DetailsActivity extends AppCompatActivity implements AlbumDataSource.ErrorHandler {
    //viewModel and binding
    private DetailsViewModel viewModel;
    private ActivityDetailsBinding activityBinding;
    //views
    private CommentsAdapter commentsAdapter;
    private AlbumAdapter albumAdapter;
    //userId parameter used in the comments call
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize dataBinding class
        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_details);
        //Instantiate the viewModel
        viewModel = new ViewModelProvider(this).get(DetailsViewModel.class);
        //Reference the viewModel in the dataBinding class
        activityBinding.setViewModel(viewModel);
        //Set lifeCycleOwner
        activityBinding.setLifecycleOwner(this);
        //Get extra
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userId = bundle.getInt("userIdentifier");
        }
        showComments(userId);
        showAlbumPhotos();
        observeOnZeroItemsLoaded();
    }

    private void showComments(int userId) {
        //Initialize the adapter with an empty list
        commentsAdapter = new CommentsAdapter(new ArrayList<>());
        //Use the it as a parameter and make a call to fetch the data
        viewModel.getComments(userId);
        //Observe the LiveData
        observeComments();
    }

    private void showAlbumPhotos() {
        //Animation
        YoYo.with(Techniques.Bounce)
                .duration(1000)
                .repeat(3)
                .playOn(activityBinding.buttonLoadImages);
        //Function will be executed on button click (paged contents is loaded on scroll)
        activityBinding.buttonLoadImages.setOnClickListener(v -> {
            //Create the Adapter
            albumAdapter = new AlbumAdapter();
            //Set Layout Manager
            activityBinding.recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
            activityBinding.recyclerView.setHasFixedSize(true);
            //Set the adapter
            activityBinding.recyclerView.setAdapter(albumAdapter);
            observeAlbumPhotos();
            //disable button click after initial call
            activityBinding.buttonLoadImages.setClickable(false);
        });
    }

    private void observeComments() {
        viewModel.mutableCommentsList.observe(this, commentsModels -> {
            //Update adapter list with received data (layout manager set in xml)
            commentsAdapter.updateList((ArrayList<Comments>) commentsModels);
            activityBinding.commentsRecyclerView.setHasFixedSize(true);
            activityBinding.commentsRecyclerView.setAdapter(commentsAdapter);
        });
    }

    private void observeAlbumPhotos() {
       if(NetworkConnection.isNetworkAvailable(this)){
           viewModel.albumList.observe(this, items -> {
               //In case of any changes update the adapter
               if (items != null) {
                   albumAdapter.submitList(items);
               }
           });
       }else{
           Toast.makeText(this, Constants.NOT_CONNECTED, Toast.LENGTH_SHORT).show();
       }
    }

    //Observe ZeroItemsLoaded case and update user
    public void observeOnZeroItemsLoaded() {
        viewModel.isNull.observe(this, aBoolean -> {
            if (aBoolean) {
                Toast.makeText(MyApplication.applicationContext, Constants.ZERO_ITEMS_LOADED, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Handle errors and update user
    @Override
    public void getErrorMessageOnFailure(String methodName, String errorMessage, Throwable t) {
        Toast.makeText(this, Constants.TOAST_ERROR, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getResponseCode(String methodName, int responseCode, ResponseBody errorBody) {
        Toast.makeText(this, Constants.TOAST_ERROR, Toast.LENGTH_SHORT).show();
        switch (responseCode) {
            case Constants.BAD_REQUEST:
                Toast.makeText(this, Constants.BAD_REQUEST_MESSAGE, Toast.LENGTH_SHORT).show();
                break;
            case Constants.NOT_AUTHORIZED:
                Toast.makeText(this, Constants.NOT_AUTHORIZED_MESSAGE, Toast.LENGTH_SHORT).show();
                break;
            case Constants.NOT_FOUND:
                Toast.makeText(this, Constants.NOT_FOUND_MESSAGE, Toast.LENGTH_SHORT).show();
                break;
            case Constants.REQUEST_TIMEOUT:
                Toast.makeText(this, Constants.REQUEST_TIMEOUT_MESSAGE, Toast.LENGTH_SHORT).show();
                break;
            case Constants.SERVICE_UNAVAILABLE:
                Toast.makeText(this, Constants.SERVICE_UNAVAILABLE_MESSAGE, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}