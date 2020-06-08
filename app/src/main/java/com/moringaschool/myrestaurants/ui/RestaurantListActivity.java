package com.moringaschool.myrestaurants.ui;


import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.moringaschool.myrestaurants.Constants;
import com.moringaschool.myrestaurants.R;
import com.moringaschool.myrestaurants.YelpBusinessesSearchResponse;
import com.moringaschool.myrestaurants.adapters.RestaurantListAdapter;
import com.moringaschool.myrestaurants.models.Business;
import com.moringaschool.myrestaurants.models.Restaurant;
import com.moringaschool.myrestaurants.network.YelpApi;
import com.moringaschool.myrestaurants.network.YelpClient;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RestaurantListActivity extends AppCompatActivity {

    private static final String TAG = RestaurantListActivity.class.getSimpleName();
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.errorTextView)
    TextView mErrorTextView;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;


    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private String mRecentAddress;

    private RestaurantListAdapter mAdapter;
    public List<Business> restaurants;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants);
        ButterKnife.bind(this);

        Intent intent = getIntent();

        String location = intent.getStringExtra("location");

            YelpApi client = YelpClient.getClient();
        //mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mRecentAddress = mSharedPreferences.getString(Constants.PREFERENCES_LOCATION_KEY, null);
        if (mRecentAddress != null) {
            //client .getRestaurants(mRecentAddress);
        }

            Call<YelpBusinessesSearchResponse> call = client.getRestaurants(mRecentAddress);

            call.enqueue(new Callback<YelpBusinessesSearchResponse>(){
                @Override
                public void onResponse(Call<YelpBusinessesSearchResponse> call, Response<YelpBusinessesSearchResponse> response) {
                    hideProgressBar();

                    if (response.isSuccessful()) {
                        restaurants = response.body().getBusinesses() ;
                        mAdapter = new RestaurantListAdapter(RestaurantListActivity.this, restaurants);
                        mAdapter = new RestaurantListAdapter(getApplicationContext(), restaurants);
                        mRecyclerView.setAdapter(mAdapter);
                        RecyclerView.LayoutManager layoutManager =
                                new LinearLayoutManager(RestaurantListActivity.this);
                        mRecyclerView.setLayoutManager(layoutManager);
                        mRecyclerView.setHasFixedSize(true);

                        showRestaurants();
                    } else {
                        showUnsuccessfulMessage();
                    }
                }

                @Override
                public void onFailure(Call<YelpBusinessesSearchResponse> call, Throwable t) {
                    hideProgressBar();
                    showFailureMessage();
                }


            });


        }

        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_search, menu);
            ButterKnife.bind(this);

            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            mEditor = mSharedPreferences.edit();

            MenuItem menuItem = menu.findItem(R.id.action_search);
            SearchView searchView = (SearchView) menuItem.getActionView() ;
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String location) {
                    addToSharedPreferences(location);
                    YelpApi client = YelpClient.getClient();
                    Call<YelpBusinessesSearchResponse> call = client.getRestaurants(location);

                    call.enqueue(new Callback<YelpBusinessesSearchResponse>(){
                        @Override
                        public void onResponse(Call<YelpBusinessesSearchResponse> call, Response<YelpBusinessesSearchResponse> response) {
                            hideProgressBar();

                            if (response.isSuccessful()) {
                                restaurants = response.body().getBusinesses() ;
                                mAdapter = new RestaurantListAdapter(RestaurantListActivity.this, restaurants);
                                mAdapter = new RestaurantListAdapter(getApplicationContext(), restaurants);
                                mRecyclerView.setAdapter(mAdapter);
                                RecyclerView.LayoutManager layoutManager =
                                        new LinearLayoutManager(RestaurantListActivity.this);
                                mRecyclerView.setLayoutManager(layoutManager);
                                mRecyclerView.setHasFixedSize(true);

                                showRestaurants();
                            } else {
                                showUnsuccessfulMessage();
                            }
                        }

                        @Override
                        public void onFailure(Call<YelpBusinessesSearchResponse> call, Throwable t) {
                            hideProgressBar();
                            showFailureMessage();
                        }


                    });

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }

            });
            return true;
        }
    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        return super.onOptionsItemSelected(item);
    }
    private void addToSharedPreferences (String location){
        mEditor.putString(Constants.PREFERENCES_LOCATION_KEY, location).apply();
    }



        private void showFailureMessage () {
            mErrorTextView.setText("Something went wrong. Please check your Internet connection and try again later");
            mErrorTextView.setVisibility(View.VISIBLE);
        }

        private void showUnsuccessfulMessage () {
            mErrorTextView.setText("Something went wrong. Please try again later");
            mErrorTextView.setVisibility(View.VISIBLE);
        }

        private void showRestaurants () {
            mRecyclerView.setVisibility(View.VISIBLE);
        }

        private void hideProgressBar () {
            mProgressBar.setVisibility(View.GONE);
        }
    }
