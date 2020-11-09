package com.example.aleatestapp.paging;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import com.example.aleatestapp.R;
import com.example.aleatestapp.data.Album;
import com.example.aleatestapp.network.JsonApiInterface;
import com.example.aleatestapp.util.MyApplication;
import com.example.aleatestapp.util.ServiceGenerator;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlbumDataSource extends PageKeyedDataSource<Integer, Album> {

    //Load only 20 items: setting the initial and max load
    public static final int INIT_LOAD = 0;
    public static final int MAX_LOAD = 20;

    //Creating Service generator instance
    public static JsonApiInterface albumCall = ServiceGenerator.createService(JsonApiInterface.class);
    public ErrorHandler errorHandler;

    //This will be called once to load the initial data 0 to 20
    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, Album> callback) {
        albumCall.getAlbums(INIT_LOAD, MAX_LOAD).enqueue(new Callback<List<Album>>() {
            @Override
            public void onResponse(@NonNull Call<List<Album>> call, @NonNull Response<List<Album>> response) {
                if (response.body() != null) {
                    callback.onResult(response.body(), null, MAX_LOAD);
                } else {
                    errorHandler.getResponseCode("loadInitialResponseEmpty", response.code(), response.errorBody());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Album>> call, @NonNull Throwable t) {
                errorHandler.getErrorMessageOnFailure("loadInitialOnFailure", t.getMessage(), t);
            }
        });
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Album> callback) {
        Integer adjacentKey = (params.key > 20) ? params.key - 20 : null;
        albumCall.getAlbums(params.key, 5).enqueue(new Callback<List<Album>>() {
            @Override
            public void onResponse(@NonNull Call<List<Album>> call, @NonNull Response<List<Album>> response) {
                //If the current page is greater than one we are on decrementing the page number
                //Else there is no previous page
                if (response.body() != null) {
                    callback.onResult(response.body(), adjacentKey);
                } else {
                    errorHandler.getResponseCode("loadBeforeResponseEmpty", response.code(), response.errorBody());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Album>> call, @NonNull Throwable t) {
                errorHandler.getErrorMessageOnFailure("loadBeforeOnFailure", t.getMessage(), t);
            }
        });

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Album> callback) {
        int loadAfterInt = MAX_LOAD;
        albumCall.getAlbums(params.key, loadAfterInt).enqueue(new Callback<List<Album>>() {
            @Override
            public void onResponse(@NonNull Call<List<Album>> call, @NonNull Response<List<Album>> response) {
                if (response.body() != null) {
                    //Each time we are increasing the initial count by 20 while the loadAfterInt remains 20
                    //In that way we are loading 20 items at a time
                    Integer key = params.key + loadAfterInt;
                    callback.onResult(response.body(), key);
                    Toast.makeText(MyApplication.applicationContext, R.string.loading_more, Toast.LENGTH_SHORT).show();
                } else {
                    errorHandler.getResponseCode("loadAfterResponseEmpty", response.code(), response.errorBody());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Album>> call, @NonNull Throwable t) {
                errorHandler.getErrorMessageOnFailure("loadAfterOnFailure", t.getMessage(), t);
            }
        });
    }

    //Get the error and handle it in activity
    public interface ErrorHandler {
        void getErrorMessageOnFailure(String methodName, String errorMessage, Throwable t);
        void getResponseCode(String methodName, int responseCode, ResponseBody errorBody);
    }
}
