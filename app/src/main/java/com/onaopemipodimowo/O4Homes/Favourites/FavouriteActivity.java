package com.onaopemipodimowo.O4Homes.Favourites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.onaopemipodimowo.O4Homes.Home.HomeDetailsActivity;
import com.onaopemipodimowo.O4Homes.R;

import java.util.ArrayList;
import java.util.List;

import fragment.HomeFragment;

public class FavouriteActivity extends AppCompatActivity {

    private FavouriteAdapter adapter;

    private List<FavouriteList> favouriteList;
    private ArrayList<FavouriteList> favouriteFiltered;
    private String TAG = "FavouriteActivity";

    private RecyclerView list;

    private ImageView filterImage;
    private TextInputLayout minPrice, maxPrice;
    private TextView dataNotFoundTextView;
    private RelativeLayout setPriceLayout, includeLayout;
    private Button priceGoBtn;
    private SearchView houseSearchView;
    String minimumPrice, maximumPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);

        list = findViewById(R.id.list);
        filterImage = findViewById(R.id.filterImage);
        minPrice = findViewById(R.id.minPricetextField);
        maxPrice = findViewById(R.id.maxPricetextField);
        setPriceLayout = findViewById(R.id.minMax);
        includeLayout = findViewById(R.id.include);
        priceGoBtn = findViewById(R.id.priceGoBtn);
        houseSearchView = findViewById(R.id.searchView);
        dataNotFoundTextView = findViewById(R.id.dataNotFoundText);

        favouriteList = new ArrayList<>();


        //Set a layout Manager on the recycler view
        list.setLayoutManager(new LinearLayoutManager(this));

        getFavData();

        list.setAdapter(adapter);

        adapter.setOnItemClickListener(new FavouriteAdapter.OnItemClickListener() {
            @Override
            public void onClick(FavouriteList items) {
                String listingId = items.getListingId();

                Intent intent = new Intent(getApplicationContext(), HomeDetailsActivity.class);
                intent.putExtra("listing_id", listingId);
                startActivity(intent);
            }
        });

       houseSearchView.setVisibility(View.GONE);

        //onclicklistener for filter icon
        filterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view);
            }
        });

        //onclicklistener for go button to filter price
        priceGoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(minPrice.getEditText().toString().isEmpty() || maxPrice.getEditText().toString().isEmpty()){
                    return;
                }

                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                setPriceLayout.setVisibility(View.GONE);
                includeLayout.setVisibility(View.VISIBLE);

                //get text from textinput
                minimumPrice = minPrice.getEditText().getText().toString().trim();
                maximumPrice = maxPrice.getEditText().getText().toString().trim();

                //hide keyboard

                filterPrice(minimumPrice, maximumPrice);
            }
        });





    }

    //method to return data based on house price
    public void filterPrice(String minPrice, String maxPrice){

            List<FavouriteList> favouriteFiltered = HomeFragment.favouriteDatabase.favouriteDao().getFavouriteDataHousePrice(minPrice, maxPrice);
            adapter=new FavouriteAdapter(getApplicationContext(), favouriteFiltered);
            list.setAdapter(adapter);




    }

    //method to return data based on dog policy
    public void filterDogPolicy(String dogPolicy){

            List<FavouriteList> favouriteFiltered = HomeFragment.favouriteDatabase.favouriteDao().getFavouriteDataDogPolicy(dogPolicy);
            adapter=new FavouriteAdapter(getApplicationContext(), favouriteFiltered);
            list.setAdapter(adapter);


    }

    //method to return data based on cat policy
    public void filterCatPolicy(String catPolicy){

            List<FavouriteList> favouriteFiltered = HomeFragment.favouriteDatabase.favouriteDao().getFavouriteDataCatPolicy(catPolicy);
            adapter=new FavouriteAdapter(getApplicationContext(), favouriteFiltered);
            list.setAdapter(adapter);


    }

    private void getFavData() {
        List<FavouriteList> favoriteLists = HomeFragment.favouriteDatabase.favouriteDao().getFavouriteData();

        adapter=new FavouriteAdapter(getApplicationContext(), favoriteLists);
        list.setAdapter(adapter);
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(getApplicationContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.filter_menu, popup.getMenu());

        //Set on click listener for the menu
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId()== R.id.byPrice){
                    includeLayout.setVisibility(View.GONE);
                    setPriceLayout.setVisibility(View.VISIBLE);
                }
                if (item.getItemId() == R.id.dogYes){
                    filterDogPolicy("Yes");
                }
                if (item.getItemId() == R.id.dogNo){
                    filterDogPolicy("No");
                }
                if (item.getItemId() == R.id.catNo){
                    filterCatPolicy("No");
                }
                if (item.getItemId() == R.id.catYes){
                    filterCatPolicy("Yes");
                }

                return false;
            }
        });

        popup.show();
    }



}