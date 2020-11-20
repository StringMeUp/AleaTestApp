package com.example.aleatestapp.ui;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.example.aleatestapp.data.Album;
import com.example.aleatestapp.data.Comments;
import com.example.aleatestapp.network.JsonApiInterface;
import com.example.aleatestapp.paging.AlbumDataSource;
import com.example.aleatestapp.util.Constants;
import com.example.aleatestapp.util.MyApplication;
import com.example.aleatestapp.util.ServiceGenerator;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsViewModel extends ViewModel implements AlbumDataSource.LoadingIndicator, AlbumDataSource.ErrorHandler {
    //MutableLiveData and LiveData is observed in the activity
    public MutableLiveData<List<Comments>> mutableCommentsList = new MutableLiveData<>();
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    public MutableLiveData<Boolean> isNull = new MutableLiveData<>();
    public LiveData<PagedList<Album>> albumList;

    public void getComments(int id) {
        JsonApiInterface call = ServiceGenerator.createService(JsonApiInterface.class);
        isLoading.setValue(true);
        call.getComments(id).enqueue(new Callback<List<Comments>>() {
            @Override
            public void onResponse(@NonNull Call<List<Comments>> call, @NonNull Response<List<Comments>> response) {
                isLoading.setValue(false);
                if (!response.isSuccessful()) {
                    Log.d("Debug Response Code", "onResponse: " + response.code());
                } else {
                    mutableCommentsList.postValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Comments>> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                t.printStackTrace();
            }
        });
    }

    public DetailsViewModel() {
        //Generating DataSourceFactory
        DataSource.Factory<Integer, Album> factory = new DataSource.Factory<Integer, Album>() {
            @NonNull
            @Override
            public DataSource<Integer, Album> create() {
                return new AlbumDataSource(DetailsViewModel.this, DetailsViewModel.this);
            }
        };
        //Getting PagedList Config
        PagedList.Config pagedListConfig = new PagedList.Config.Builder().setPageSize(1).build();
        //Building the paged list using LivePagedListBuilder
        //Error handling with Boundary Callback
        LivePagedListBuilder<Integer, Album> builder = new LivePagedListBuilder<>(factory, pagedListConfig).setBoundaryCallback(new PagedList.BoundaryCallback<Album>() {
            @Override
            public void onZeroItemsLoaded() {
                super.onZeroItemsLoaded();
                //Mutable live data that will be observed if zero items have been loaded
                isNull.postValue(true);
            }

            @Override
            public void onItemAtFrontLoaded(@NonNull Album itemAtFront) {
                super.onItemAtFrontLoaded(itemAtFront);
            }

            @Override
            public void onItemAtEndLoaded(@NonNull Album itemAtEnd) {
                super.onItemAtEndLoaded(itemAtEnd);

            }
        });
        albumList = builder.build();
    }

    @Override
    public void showHideLoading(Boolean inProgress) {
        if (inProgress) {
            isLoading.postValue(true);
        } else {
            isLoading.postValue(false);
        }
    }

    @Override
    public void getErrorMessageOnFailure(String methodName, String errorMessage, Throwable t) {
        Toast.makeText(MyApplication.applicationContext, Constants.TOAST_ERROR, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getResponseCode(String methodName, int responseCode, ResponseBody errorBody) {
        switch (responseCode) {
            case Constants.BAD_REQUEST:
                Toast.makeText(MyApplication.applicationContext, Constants.BAD_REQUEST_MESSAGE + " " + "Response Code: " + responseCode, Toast.LENGTH_SHORT).show();
                break;
            case Constants.NOT_AUTHORIZED:
                Toast.makeText(MyApplication.applicationContext, Constants.NOT_AUTHORIZED_MESSAGE + " " + "Response Code: " + responseCode, Toast.LENGTH_SHORT).show();
                break;
            case Constants.NOT_FOUND:
                Toast.makeText(MyApplication.applicationContext, Constants.NOT_FOUND_MESSAGE + " " + "Response Code: " + responseCode, Toast.LENGTH_SHORT).show();
                break;
            case Constants.REQUEST_TIMEOUT:
                Toast.makeText(MyApplication.applicationContext, Constants.REQUEST_TIMEOUT_MESSAGE + " " + "Response Code: " + responseCode, Toast.LENGTH_SHORT).show();
                break;
            case Constants.SERVICE_UNAVAILABLE:
                Toast.makeText(MyApplication.applicationContext, Constants.SERVICE_UNAVAILABLE_MESSAGE + " " + "Response Code: " + responseCode, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}