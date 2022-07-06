package com.onaopemipodimowo.O4Homes;

import android.content.Context;
import android.util.Log;

import com.onaopemipodimowo.O4Homes.Home.Home;
import com.onaopemipodimowo.O4Homes.Home.HomeAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainClass {

    Context cxt;
    private HomeAdapter adapter;
    private ArrayList<Home> homesFiltered;
    //String myResponse;



    public MainClass(Context context /*,String myResponse */){
        this.cxt = context;
        //this.myResponse = myResponse;
    }

    //method to filter price based on users input
    public void filterPrice(String minPrice, String maxPrice, String myResponse) {
        homesFiltered = new ArrayList<>();

        try {

            JSONObject jsonObject = new JSONObject(myResponse);

            JSONObject dataObject = jsonObject.getJSONObject("data");
            JSONObject homeSearchObject = dataObject.getJSONObject("home_search");


            JSONArray resultArray = homeSearchObject.getJSONArray("results");

            for (int i = 0; i < resultArray.length(); i++) {

                //if result is equal to price specified save in the homeFiltered array
                if (resultArray.getJSONObject(i).getString("list_price_min").contains(minPrice) && resultArray.getJSONObject(i).getString("list_price_max").contains(maxPrice)) {

                    Log.d("Works", String.valueOf(resultArray.getJSONObject(i).getString("list_price_min").contains(minPrice)));
                    JSONObject resultObject = resultArray.getJSONObject(i);
                    JSONObject locationObject = resultObject.getJSONObject("location");
                    JSONObject addressObject = locationObject.getJSONObject("address");

                    JSONObject descriptionObject = resultObject.getJSONObject("description");
                    JSONObject dataObject6 = resultObject.getJSONObject("primary_photo");

                    JSONArray photosArray = resultObject.getJSONArray("photos");


                    JSONObject photoObject1 = photosArray.getJSONObject(0);
                    JSONObject photoObject2 = photosArray.getJSONObject(1);
                    JSONObject photoObject3 = photosArray.getJSONObject(2);

                    JSONObject petPolicyObject = resultObject.getJSONObject("pet_policy");


                    homesFiltered.add(new Home(
                            resultObject.getString("listing_id"),
                            descriptionObject.getString("name"),
                            addressObject.getString("city"),
                            addressObject.getString("state_code"),
                            resultObject.getString("permalink"),
                            descriptionObject.getString("type"),
                            resultObject.getString("list_price_min"),
                            resultObject.getString("list_price_max"),
                            dataObject6.getString("href"),
                            photoObject1.getString("href"),
                            photoObject2.getString("href"),
                            photoObject3.getString("href"),
                            petPolicyObject.getBoolean("cats"),
                            petPolicyObject.getBoolean("dogs"),
                            descriptionObject.getInt("baths_min"),
                            descriptionObject.getInt("baths_max"),
                            descriptionObject.getInt("beds_min"),
                            descriptionObject.getInt("beds_max")
                    ));

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}


