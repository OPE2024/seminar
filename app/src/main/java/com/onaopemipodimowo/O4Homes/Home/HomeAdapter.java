package com.onaopemipodimowo.O4Homes.Home;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.onaopemipodimowo.O4Homes.Favourites.FavouriteList;
import com.onaopemipodimowo.O4Homes.MainActivity;
import com.onaopemipodimowo.O4Homes.MessageActivity;
import com.onaopemipodimowo.O4Homes.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import fragment.HomeFragment;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder>{

    Context context;
    List<Home> homes;
    OnItemClickListener listener;
    boolean click = true;
    ImageView mapicon;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public interface OnItemClickListener {
        public void onClick(Home items);
    }


    public HomeAdapter(Context context, List<Home>homes /*List<Home>homeListFiltered*/){
        this.context = context;
        this.homes = homes;
        // this.homeListFiltered = homeListFiltered;
    }

    // method for filtering our recyclerview items.
    public void filterList(ArrayList<Home> filterllist) {
        // below line is to add our filtered
        // list in our course array list.
        homes = filterllist;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }

    // inflates a layout from xml and returns the holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        Log.d("HomeAdapter","onCreativeViewHolder");
        View homeView = LayoutInflater.from(context).inflate(R.layout.item_home,parent,false);
        return new ViewHolder(homeView);
    }

    //populates data into the item through holder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,int position) {
        Log.d("HomeAdapter", "onBindViewHolder" + position);
        // get home at the passed in position
        Home data = homes.get(position);

        holder.bind(data);

        //when user clicks on the card move them to new fragment and pass the listing id to the new fragment
        holder.houseCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onClick(data);
                }

            }

        });


        holder.ivMapIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("page_number", 1);

                DatabaseReference addressDB = FirebaseDatabase.getInstance().getReference().child("New Address");
                addressDB.child("address").setValue(data.getLine());
                context.startActivity(intent);
            }
        });



        final Animation animation = AnimationUtils.loadAnimation(context,R.anim.like_button_action);

        holder.likeBtn.setOnClickListener(new DoubleClickListener() {

            @Override
            public void onDoubleClick() {

                holder.likeBtn.startAnimation(animation);

                FavouriteList favoriteList=new FavouriteList();

                String listing_id=data.getListing_id();
                String name=data.getName();
                String propertyType=data.getProperty_type();
                String location=data.getLine();
                String minPrice=data.getList_price_min();
                String maxPrice=data.getList_price_max();
                String catsPolicy=data.getCats_policy();
                String dogPolicy=data.getDogs_policy();
                String bathsMin=String.valueOf(data.getBaths_min());
                String bathsMax=String.valueOf(data.getBaths_max());
                String bedsMin=String.valueOf(data.getBeds_min());
                String bedsMax=String.valueOf(data.getBeds_max());
                String imageHref = String.valueOf(data.getPrimary_photo_href());

                favoriteList.setListingId(listing_id);
                favoriteList.setHouseName(name);
                favoriteList.setPropertyType(propertyType);
                favoriteList.setHouseAddress(location);
                favoriteList.setHouseMinPrice(minPrice);
                favoriteList.setHouseMaxPrice(maxPrice);
                favoriteList.setCatPolicy(catsPolicy);
                favoriteList.setDogPolicy(dogPolicy);
                favoriteList.setBathsMin(bathsMin);
                favoriteList.setBathsMax(bathsMax);
                favoriteList.setBedsMin(bedsMin);
                favoriteList.setBedsMax(bedsMax);
                favoriteList.setImageHref(imageHref);

                if(HomeFragment.favouriteDatabase.favouriteDao().exists(listing_id)){
                    HomeFragment.favouriteDatabase.favouriteDao().delete(favoriteList);
                    holder.likeBtn.setBackground(context.getDrawable(R.drawable.ic_heart_button));
                } else{
                    HomeFragment.favouriteDatabase.favouriteDao().addData(favoriteList);
                    holder.likeBtn.setBackground(context.getDrawable(R.drawable.ic_blue_heart));

                }


            }
        });

        holder.rentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("userid", "WymgYKwO0Nbr2kSPBy6poI20Qj73");
                intent.putExtra("listingId", data.getListing_id());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount(){return homes.size();}
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvTitle, tvCity, tvStateCode, tvPropertyType, tvLocation, tvListPriceMin, tvListPriceMax, tvPrimaryPhotoHref, tvImage1Href, tvImage2Href, tvImage3Href, tvCatsPolicy, tvDogsPolicy, tvBathsMin, tvBathsMax, tvBedsMin, tvBedsMax;
        ImageView cardImage, likeBtn;
        ImageView ivMapIcon;
        CardView houseCardView;
        Button rentBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            houseCardView = itemView.findViewById(R.id.houseCardView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
           /* tvCity = itemView.findViewById(R.id.tvCity);
            tvStateCode = itemView.findViewById(R.id.tvStateCode);



            */

            ivMapIcon = itemView.findViewById(R.id.mapicon);
            tvPropertyType = itemView.findViewById(R.id.tvPropertyType);
            tvLocation = itemView.findViewById(R.id.tvAddress);
            tvListPriceMin = itemView.findViewById(R.id.tvPrice);
            tvListPriceMax = itemView.findViewById(R.id.tvPrice2);
           /* tvImage1Href = itemView.findViewById(R.id.tvImage1);
            tvImage2Href = itemView.findViewById(R.id.tvImage2);
            tvImage3Href = itemView.findViewById(R.id.tvImage3);

            */
            tvCatsPolicy = itemView.findViewById(R.id.tvCatPolicy);
            tvDogsPolicy = itemView.findViewById(R.id.tvDogPolicy);
            tvBathsMin = itemView.findViewById(R.id.tvBaths);
            tvBathsMax = itemView.findViewById(R.id.tvBaths2);
            tvBedsMin = itemView.findViewById(R.id.tvBeds);
            tvBedsMax = itemView.findViewById(R.id.tvBeds2);
            cardImage = itemView.findViewById(R.id.cardImage);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            rentBtn = itemView.findViewById(R.id.enqureBtn);

        }

        public void bind(Home home){
            tvTitle.setText(home.getName());
            tvPropertyType.setText(home.getProperty_type());
            tvLocation.setText(home.getLine());
            tvListPriceMin.setText(context.getString(R.string.dollarSign, home.getList_price_min()));
            tvListPriceMax.setText(context.getString(R.string.dollarSign,home.getList_price_max()));
            tvCatsPolicy.setText(home.getCats_policy());
            tvDogsPolicy.setText(home.getDogs_policy());
            tvBathsMin.setText(String.valueOf(home.getBaths_min()));
            tvBathsMax.setText(String.valueOf(home.getBaths_max()));
            tvBedsMin.setText(String.valueOf(home.getBeds_min()));
            tvBedsMax.setText(String.valueOf(home.getBeds_max()));
            Picasso.get().load(home.getPrimary_photo_href()).fit().into(cardImage);

            if(HomeFragment.favouriteDatabase.favouriteDao().exists(home.getListing_id())){
                likeBtn.setBackground(context.getDrawable(R.drawable.ic_blue_heart));
            } else{
                likeBtn.setBackground(context.getDrawable(R.drawable.ic_heart_button));

            }
        }



    }

    public abstract class DoubleClickListener implements View.OnClickListener {

        // The time in which the second tap should be done in order to qualify as
        // a double click
        private static final long DEFAULT_QUALIFICATION_SPAN = 200;
        private long doubleClickQualificationSpanInMillis;
        private long timestampLastClick;

        public DoubleClickListener() {
            doubleClickQualificationSpanInMillis = DEFAULT_QUALIFICATION_SPAN;
            timestampLastClick = 0;
        }

        public DoubleClickListener(long doubleClickQualificationSpanInMillis) {
            this.doubleClickQualificationSpanInMillis = doubleClickQualificationSpanInMillis;
            timestampLastClick = 0;
        }

        @Override
        public void onClick(View v) {
            if((SystemClock.elapsedRealtime() - timestampLastClick) < doubleClickQualificationSpanInMillis) {
                onDoubleClick();
            }
            timestampLastClick = SystemClock.elapsedRealtime();
        }

        public abstract void onDoubleClick();

    }



}
