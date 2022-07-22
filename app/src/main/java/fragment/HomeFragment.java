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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.onaopemipodimowo.O4Homes.BuildConfig;
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
    private String myResponse1, city, stateId;
    private ImageView filterImage;
    private EditText minPrice, maxPrice;
    private RelativeLayout setPriceLayout, includeLayout;
    private Button priceGoBtn;
    private SearchView houseSearchView;
    private FirebaseAuth auth;
    FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    int minimumPrice, maximumPrice;
    public static FavouriteDatabase favouriteDatabase;

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

        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        String userID = firebaseUser.getUid();

        getDetFromFirestore(userID);
        fetchData();

        // Set the adapter on the recycler view
        adapter = new HomeAdapter(getContext(), homes);

        adapter.setOnItemClickListener(new HomeAdapter.OnItemClickListener() {
            @Override
            public void onClick(Home items) {
                String listingId = items.getListing_id();

                Intent intent = new Intent(getContext(), HomeDetailsActivity.class);
                intent.putExtra("listing_id", listingId);
            }
        });

        list.setAdapter(adapter);

        //Set a layout Manager on the recycler view
        list.setLayoutManager(new LinearLayoutManager(getContext()));

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
                if(minPrice.getText().toString().isEmpty() || maxPrice.getText().toString().isEmpty()){
                    return;
                }

                setPriceLayout.setVisibility(View.GONE);
                includeLayout.setVisibility(View.VISIBLE);

                //get text from textinput
                minimumPrice = Integer.parseInt(minPrice.getText().toString().trim());
                maximumPrice = Integer.parseInt(maxPrice.getText().toString().trim());

                //hide keyboard
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

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
                    filterPetPreference("true");
                }
                if (item.getItemId() == R.id.dogNo){
                    filterPetPreference("false");
                }
                if (item.getItemId() == R.id.catNo){
                    filterPetPreference("false");
                }
                if (item.getItemId() == R.id.catYes){
                    filterPetPreference("true");
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
    public void filterPrice(int minPrice, int maxPrice){
        homesFiltered = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(myResponse1);

            JSONObject dataObject = jsonObject.getJSONObject("data");
            JSONObject homeSearchObject = dataObject.getJSONObject("home_search");


            JSONArray resultArray = homeSearchObject.getJSONArray("results");

            for (int i = 0; i < resultArray.length(); i++) {

                //if result is equal to price specified save in the homeFiltered array
                if(resultArray.getJSONObject(i).getInt("list_price_min") >= minPrice  && resultArray.getJSONObject(i).getInt("list_price_max") <= maxPrice) {

                    JSONObject resultObject = resultArray.getJSONObject(i);

                    JSONObject locationObject = resultObject.getJSONObject("location");
                    JSONObject addressObject = locationObject.getJSONObject("address");

                    JSONObject descriptionObject = resultObject.getJSONObject("description");
                    JSONObject primaryPhotoObject = resultObject.getJSONObject("primary_photo");

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
                            primaryPhotoObject.getString("href"),
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

            setAdapter(homesFiltered);
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //method to filter pets based on users input
    public void filterPetPreference(String preference){
        filterData("pet_policy","dogs",preference);
        filterData("pet_policy", "cats", preference);
    }


    //method to filter data based on users input
    public void filterData(String jsonObjectName, String valueName, String preference) {
        homesFiltered = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(myResponse1);

            JSONObject dataObject = jsonObject.getJSONObject("data");
            JSONObject homeSearchObject = dataObject.getJSONObject("home_search");


            JSONArray resultArray = homeSearchObject.getJSONArray("results");

            for (int i = 0; i < resultArray.length(); i++) {


                Log.d("Work", String.valueOf(resultArray.getJSONObject(i).getJSONObject(jsonObjectName).getString(valueName)));


                if (resultArray.getJSONObject(i).getJSONObject(jsonObjectName).getString(valueName).contains(preference)) {


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

            setAdapter(homesFiltered);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void setAdapter(ArrayList<Home> homesFiltered){
        // Set the adapter on the recycler view
        adapter = new HomeAdapter(getContext(), homesFiltered);
        adapter.setOnItemClickListener(new HomeAdapter.OnItemClickListener() {
            @Override
            public void onClick(Home items) {
                String listingId = items.getListing_id();

                Intent intent = new Intent(getContext(), HomeDetailsActivity.class);
                intent.putExtra("listing_id", listingId);
                startActivity(intent);
            }
        });

        list.setAdapter(adapter);
    }

    public void fetchData(){

        OkHttpClient client = new OkHttpClient();
        boolean apikey = false;

        try {
            DB snappydb = DBFactory.open(requireContext());

            String city=snappydb.get("city");
            String stateCode=snappydb.get("stateCode");

            String url = "https://us-real-estate.p.rapidapi.com/v2/for-rent?city="+city+"&state_code="+stateCode;

            apikey = snappydb.exists(url);
            Log.d("DB", url);
            if (apikey){
                myResponse1 = snappydb.get(url);
                getDatafromDB();
                Log.d("Url", myResponse1);

            } else {
                fetchDataFromApi(url);
            }

        } catch (SnappydbException e) {
            e.printStackTrace();
        }

    }

    public void fetchDataFromApi(String url){
        OkHttpClient client = new OkHttpClient();


        String consumerKey = BuildConfig.CONSUMER_KEY;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("X-RapidAPI-Key", consumerKey)
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
                        fetchData();
                    } catch (SnappydbException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }


    public void getDetFromFirestore(String uid){

        DocumentReference docRef = db.collection("Users").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        city = document.getString("city");
                        stateId = document.getString("stateId");
                    } else {
                        Log.d("LOGGER", "No such document");
                    }
                } else {
                    Log.d("LOGGER", "get failed with ", task.getException());
                }
            }
        });

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



            adapter = new HomeAdapter(getContext(), homes);

            adapter.setOnItemClickListener(new HomeAdapter.OnItemClickListener() {
                @Override
                public void onClick(Home items) {
                    String listingId = items.getListing_id();

                    Intent intent = new Intent(getContext(), HomeDetailsActivity.class);
                    intent.putExtra("listing_id", listingId);
                    startActivity(intent);
                }
            });

            list.setAdapter(adapter);



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}



