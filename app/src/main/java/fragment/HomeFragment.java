  package fragment;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.onaopemipodimowo.O4Homes.Favourites.FavouriteActivity;
import com.onaopemipodimowo.O4Homes.Favourites.FavouriteDatabase;
import com.onaopemipodimowo.O4Homes.Home.Home;
import com.onaopemipodimowo.O4Homes.Home.HomeAdapter;
import com.onaopemipodimowo.O4Homes.Home.HomeDetailsActivity;
import com.onaopemipodimowo.O4Homes.R;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


  public class HomeFragment extends Fragment {
      private HomeAdapter adapter;

      private List<Home> homes;
      private ArrayList<Home> homesFiltered;
      private String TAG = "HomeFragment";

      private RecyclerView list;
      private String myResponse1;

      private ImageView filterImage;
      private TextInputLayout minPrice, maxPrice;
      private RelativeLayout setPriceLayout, includeLayout;
      private Button priceGoBtn;
      private SearchView houseSearchView;
      String minimumPrice, maximumPrice;

      public static FavouriteDatabase favouriteDatabase;

      // MainClass main = new MainClass(getContext());


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_home, container, false);


        list = view.findViewById(R.id.list);
        filterImage = view.findViewById(R.id.filterImage);
        minPrice = view.findViewById(R.id.minPricetextField);
        maxPrice = view.findViewById(R.id.maxPricetextField);
        setPriceLayout = view.findViewById(R.id.minMax);
        includeLayout = view.findViewById(R.id.include);
        priceGoBtn = view.findViewById(R.id.priceGoBtn);
        houseSearchView = view.findViewById(R.id.searchView);


        homes = new ArrayList<>();

        // Set the adapter on the recycler view
        adapter = new HomeAdapter(getContext(), homes);
        adapter.setOnItemClickListener(new HomeAdapter.OnItemClickListener() {
            @Override
            public void onClick(Home items) {
                String listingId = items.getListing_id();

                Intent intent = new Intent(getContext(), HomeDetailsActivity.class);
                intent.putExtra("listing_id", listingId);
              //Fix Later keeps crashing  //startActivity(intent);
            }
        });

        list.setAdapter(adapter);

        //Set a layout Manager on the recycler view
        list.setLayoutManager(new LinearLayoutManager(getContext()));


        //store data in database
        storeData();

        favouriteDatabase= Room.databaseBuilder(getContext(),FavouriteDatabase.class,"housefavdb").allowMainThreadQueries().build();

        // below line is to call set on query text listener method.
        houseSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                filter(newText);
                return false;
            }
        });

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


                setPriceLayout.setVisibility(View.GONE);
                includeLayout.setVisibility(View.VISIBLE);

                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                //get text from textinput
                 minimumPrice = minPrice.getEditText().getText().toString().trim();
                 maximumPrice = maxPrice.getEditText().getText().toString().trim();

                 //hide keyboard

                filterPrice(minimumPrice, maximumPrice);
            }
        });

        // Inflate the layout for this fragment
        return view;



    }



    //method to filter items in recycler view on search
      private void filter(String text) {
          // creating a new array list to filter our data.
          ArrayList<Home> filteredlist = new ArrayList<>();

          // running a for loop to compare elements.
          for (Home item : homes) {
              // checking if the entered string matched with any item of our recycler view.
              if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                  // if the item is matched we are
                  // adding it to our filtered list.
                  filteredlist.add(item);
              }
          }
          if (filteredlist.isEmpty()) {
              // if no item is added in filtered list we are
              // displaying a toast message as no data found.
              Toast.makeText(getContext(), "No Data Found..", Toast.LENGTH_SHORT).show();
          } else {
              // at last we are passing that filtered
              // list to our adapter class.
              adapter.filterList(filteredlist);
          }
      }



      public void showPopup(View v) {
          PopupMenu popup = new PopupMenu(getContext(), v);
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
                      filterDogPreference("true");
                  }
                  if (item.getItemId() == R.id.dogNo){
                      filterDogPreference("false");
                  }
                  if (item.getItemId() == R.id.catNo){
                      filterCatPreference("false");
                  }
                  if (item.getItemId() == R.id.catYes){
                      filterCatPreference("true");
                  }
                  if (item.getItemId() == R.id.checkFavourites){
                      Intent intent = new Intent(getContext(), FavouriteActivity.class);
                      startActivity(intent);
                  }

                  return false;
              }
          });

          popup.show();
      }




      //method to filter price based on users input
      public void filterPrice(String minPrice, String maxPrice){
         homesFiltered = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(myResponse1);

            JSONObject dataObject = jsonObject.getJSONObject("data");
            JSONObject homeSearchObject = dataObject.getJSONObject("home_search");


            JSONArray resultArray = homeSearchObject.getJSONArray("results");

            for (int i = 0; i < resultArray.length(); i++) {

                //if result is equal to price specified save in the homeFiltered array
                if(resultArray.getJSONObject(i).getString("list_price_min").contains(minPrice)  && resultArray.getJSONObject(i).getString("list_price_max").contains(maxPrice)) {

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


        }catch (JSONException e) {
            e.printStackTrace();
        }

          // Set the adapter on the recycler view
          adapter = new HomeAdapter(getContext(), homesFiltered);

          adapter.setOnItemClickListener(new HomeAdapter.OnItemClickListener() {
              @Override
              public void onClick(Home items) {
                  String listingId =  items.getListing_id();

                  Intent intent = new Intent(getContext(), HomeDetailsActivity.class);
                  intent.putExtra("listing_id", listingId);
                  startActivity(intent);
              }
          });


          list.setAdapter(adapter);

    }




      //method to filter price based on users input
      public void filterDogPreference(String preference){
          homesFiltered = new ArrayList<>();

          try {
              JSONObject jsonObject = new JSONObject(myResponse1);

              JSONObject dataObject = jsonObject.getJSONObject("data");
              JSONObject homeSearchObject = dataObject.getJSONObject("home_search");


              JSONArray resultArray = homeSearchObject.getJSONArray("results");

              for (int i = 0; i < resultArray.length(); i++) {


                  Log.d("Work", String.valueOf(resultArray.getJSONObject(i).getJSONObject("pet_policy").getString("dogs")));

                  //if result is equal to dogpreference specified save in the homeFiltered array
                  if(resultArray.getJSONObject(i).getJSONObject("pet_policy").getString("dogs").contains(preference)) {


                      JSONObject resultObject = resultArray.getJSONObject(i);
                      JSONObject petPolicyObject = resultObject.getJSONObject("pet_policy");
                      JSONObject locationObject = resultObject.getJSONObject("location");
                      JSONObject addressObject = locationObject.getJSONObject("address");

                      JSONObject descriptionObject = resultObject.getJSONObject("description");
                      JSONObject dataObject6 = resultObject.getJSONObject("primary_photo");

                      JSONArray photosArray = resultObject.getJSONArray("photos");


                      JSONObject photoObject1 = photosArray.getJSONObject(0);
                      JSONObject photoObject2 = photosArray.getJSONObject(1);
                      JSONObject photoObject3 = photosArray.getJSONObject(2);


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

              // Set the adapter on the recycler view
              adapter = new HomeAdapter(getContext(), homesFiltered);
              adapter.setOnItemClickListener(new HomeAdapter.OnItemClickListener() {
                  @Override
                  public void onClick(Home items) {
                      String listingId =  items.getListing_id();

                      Intent intent = new Intent(getContext(), HomeDetailsActivity.class);
                      intent.putExtra("listing_id", listingId);
                      startActivity(intent);
                  }
              });

              list.setAdapter(adapter);

          }catch (JSONException e) {
              e.printStackTrace();
          }



      }


      //method to filter price based on users input
      public void filterCatPreference(String preference){
          homesFiltered = new ArrayList<>();

          try {
              JSONObject jsonObject = new JSONObject(myResponse1);

              JSONObject dataObject = jsonObject.getJSONObject("data");
              JSONObject homeSearchObject = dataObject.getJSONObject("home_search");


              JSONArray resultArray = homeSearchObject.getJSONArray("results");

              for (int i = 0; i < resultArray.length(); i++) {

                  //if result is equal to cat preference specified save in the homeFiltered array
                  if(resultArray.getJSONObject(i).getJSONObject("pet_policy").getString("cats").contains(preference)) {

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

              // Set the adapter on the recycler view
              adapter = new HomeAdapter(getContext(), homesFiltered);
              adapter.setOnItemClickListener(new HomeAdapter.OnItemClickListener() {
                  @Override
                  public void onClick(Home items) {
                      String listingId =  items.getListing_id();

                      Intent intent = new Intent(getContext(), HomeDetailsActivity.class);
                      intent.putExtra("listing_id", listingId);
                      startActivity(intent);
                  }
              });

              list.setAdapter(adapter);

          }catch (JSONException e) {
              e.printStackTrace();
          }



      }




    public void storeData(){

        OkHttpClient client = new OkHttpClient();

        String url = "https://us-real-estate.p.rapidapi.com/v2/for-rent?city=Sunnyvale&state_code=CA";
        boolean apikey = false;

        try {
            DB snappydb = DBFactory.open(requireContext());
            apikey = snappydb.exists(url);
            if (apikey){
                myResponse1 = snappydb.get(url);
                getDatafromDB();
                Log.d("Url", myResponse1);

                Log.i(TAG,"fromcache");
            } else {

                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .addHeader("X-RapidAPI-Key", "b9b6e83a73msh284bf2ee0af9f72p1b3302jsnbce6e1f38d7c")
                        .addHeader("X-RapidAPI-Host", "us-real-estate.p.rapidapi.com")
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                    }


                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()){
                            final String myResponse = response.body().string();
                            Log.i(TAG,"JSON" + myResponse);
                            try {
                                DB snappydb = DBFactory.open(getContext());
                                snappydb.put(url,myResponse);
                                snappydb.close();
                                storeData();
                            } catch (SnappydbException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
            }

        } catch (SnappydbException e) {
            e.printStackTrace();
        }

    }

    public void getDatafromDB() {

        try {
            JSONObject jsonObject = new JSONObject(myResponse1);

            JSONObject dataObject = jsonObject.getJSONObject("data");
            JSONObject homeSearchObject = dataObject.getJSONObject("home_search");


            JSONArray resultArray = homeSearchObject.getJSONArray("results");


            for (int i = 0; i <= resultArray.length(); i++) {
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


                homes.add(new Home(
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

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

  }



