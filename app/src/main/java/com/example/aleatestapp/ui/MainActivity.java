package com.example.aleatestapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aleatestapp.R;
import com.example.aleatestapp.adapters.PostsAdapter;
import com.example.aleatestapp.data.Posts;
import com.example.aleatestapp.databinding.ActivityMainBinding;
import com.example.aleatestapp.util.Constants;
import com.example.aleatestapp.util.NetworkConnection;

import java.util.ArrayList;
import java.util.List;

//implements onClickEvent interface
public class MainActivity extends AppCompatActivity implements PostsAdapter.ClickEvent{

    private ActivityMainBinding mainBinding;
    private MainViewModel mainViewModel;
    private PostsAdapter postsAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Check Network Connection
        if (!NetworkConnection.isNetworkAvailable(this)) {
            Toast.makeText(this, Constants.NOT_CONNECTED, Toast.LENGTH_SHORT).show();
        }
        //Initialize dataBindingClass
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        //Initialize viewModel
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        //Set viewModel to dataBinding
        mainBinding.setViewModel(mainViewModel);
        //Set lifeCycle Owner
        mainBinding.setLifecycleOwner(this);
        showPosts();
    }

    public void showPosts() {
        //Fetch remote data (make the call)
        mainViewModel.getPosts();
        //Initialize adapter and layoutManager
        postsAdapter = new PostsAdapter(new ArrayList<>(), this);
        layoutManager = new LinearLayoutManager(this);
        //Listen for data changes once it has been received
        observePostsLiveData();
    }

    public void observePostsLiveData() {
        mainViewModel.mutablePostList.observe(this, new Observer<List<Posts>>() {
            @Override
            public void onChanged(List<Posts> posts) {
                if (posts != null) {
                    postsAdapter.updateList((ArrayList<Posts>) posts);
                    mainBinding.postsRecyclerView.setLayoutManager(layoutManager);
                    mainBinding.postsRecyclerView.setAdapter(postsAdapter);
                }
            }
        });
    }

    //Implementation of interface method (starts new activity and sends extra data)
    @Override
    public void setClickListener(int userId) {
        if (!NetworkConnection.isNetworkAvailable(this)) {
            Toast.makeText(this, Constants.CONNECTION_ERROR, Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra("userIdentifier", userId);
            startActivity(intent);
        }
    }
}