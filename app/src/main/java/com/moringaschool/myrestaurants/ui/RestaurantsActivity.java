package com.moringaschool.myrestaurants.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.moringaschool.myrestaurants.R;
import com.moringaschool.myrestaurants.adapters.RestaurantListAdapter;
import com.moringaschool.myrestaurants.models.Restaurant;
import com.moringaschool.myrestaurants.services.YelpService;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;



public class RestaurantsActivity extends AppCompatActivity {
    public static final String TAG = RestaurantsActivity.class.getSimpleName();
    //@BindView(R.id.locationTextView) TextView mLocationTextView;
    //@BindView(R.id.listView) ListView mListView;
    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;
    private RestaurantListAdapter mAdapter;
    private ArrayList<Restaurant> restaurants = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String location = intent.getStringExtra("location");
        //mLocationTextView.setText("Here are all the restaurants near: " + location);
        getRestaurants(location);
    }

    private void getRestaurants(String location){
        final YelpService yelpService = new YelpService();
        yelpService.findRestaurants(location, new Callback(){

            @Override
            public void onFailure(Call call, IOException e){
                e.printStackTrace();
            }

                        @Override
                        public void onResponse(Call call, Response response) {
                            restaurants = yelpService.processResults(response);

                            RestaurantsActivity.this.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    mAdapter = new RestaurantListAdapter(getApplicationContext(), restaurants);
                                    mRecyclerView.setAdapter(mAdapter);
                                    RecyclerView.LayoutManager layoutManager =
                                            new LinearLayoutManager(RestaurantsActivity.this);
                                    mRecyclerView.setLayoutManager(layoutManager);
                                    mRecyclerView.setHasFixedSize(true);
                                }
                            });
                        }
                    });
                }
            }

