package com.onaopemipodimowo.O4Homes.Home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.onaopemipodimowo.O4Homes.Home.HomeDetails.HomeDetails;
import com.onaopemipodimowo.O4Homes.Home.HomeDetails.HomeDetailsAdapter;
import com.onaopemipodimowo.O4Homes.R;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeDetailsActivity extends AppCompatActivity {

    private String myResponse;
    private List<HomeDetails> homeDetails;
    private HomeDetailsAdapter adapter;
    private RecyclerView imageRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_details);


        homeDetails = new ArrayList<>();

        imageRecyclerView = findViewById(R.id.imageRecyclerView);

        // Set the adapter on the recycler view
        adapter = new HomeDetailsAdapter(getApplicationContext(), homeDetails);

        imageRecyclerView.setAdapter(adapter);

        //Set a layout Manager on the recycler view
        imageRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        //this method gets the json data stored in the snappyDB
        getDatafromDB();

        //this method arranges the json data
        arrangeDataFromDB();

    }

    public void getDatafromDB() {

        try {
            String url = "https://us-real-estate.p.rapidapi.com/v2/for-rent?city=Sunnyvale&state_code=CA";
            DB snappydb = DBFactory.open(getApplicationContext());
            myResponse = snappydb.get(url);

        } catch (SnappydbException e){
            e.printStackTrace();
        }

    }

    public void arrangeDataFromDB(){

        Intent intent = getIntent();
        String listingId = intent.getStringExtra("listing_id");

        try {
            JSONObject jsonObject = new JSONObject(myResponse);

            JSONObject dataObject = jsonObject.getJSONObject("data");
            JSONObject homeSearchObject = dataObject.getJSONObject("home_search");


            JSONArray resultArray = homeSearchObject.getJSONArray("results");


            for (int i = 0; i <= resultArray.length(); i++) {
                JSONObject resultObject = resultArray.getJSONObject(i);

                JSONArray photosArray = resultObject.getJSONArray("photos");

                for (int j = 0; j <= photosArray.length(); j++) {
                    if (resultObject.getString("listing_id").contains(listingId)) {
                        JSONObject photoObject = photosArray.getJSONObject(j);
                        homeDetails.add(new HomeDetails(
                                photoObject.getString("href")
                        ));
                        Log.d("Work", String.valueOf(photoObject.getString("href")));
                    }
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}