package com.onaopemipodimowo.O4Homes.Favourites;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.onaopemipodimowo.O4Homes.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.ViewHolder> {
   Context context;
   List<FavouriteList> favouriteLists;
   FavouriteAdapter.OnItemClickListener listener;

   public void setOnItemClickListener(FavouriteAdapter.OnItemClickListener listener){
      this.listener = listener;
   }

   public interface OnItemClickListener {
      public void onClick(FavouriteList items);
   }

   public FavouriteAdapter(Context context, List<FavouriteList> favouriteLists){
      this.context = context;
      this.favouriteLists = favouriteLists;
   }


   /*// method for filtering our recyclerview items.
   public void filterList(ArrayList<FavouriteList> filterList) {
      // below line is to add our filtered
      // list in our course array list.
      favouriteLists = filterList;
      // below line is to notify our adapter
      // as change in recycler view data.
      notifyDataSetChanged();
   }

    */



   // inflates a layout from xml and returns the holder
   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home,parent,false);
      return new ViewHolder(view);
   }

   //populates data into the item through holder
   @Override
   public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      // get home at the passed in position
      FavouriteList data = favouriteLists.get(position);

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
   }

   @Override
   public int getItemCount(){return favouriteLists.size();}

   public class ViewHolder extends RecyclerView.ViewHolder {
      TextView tvTitle, tvCity, tvStateCode, tvPropertyType, tvLocation, tvListPriceMin, tvListPriceMax, tvPrimaryPhotoHref, tvImage1Href, tvImage2Href, tvImage3Href, tvCatsPolicy, tvDogsPolicy, tvBathsMin, tvBathsMax, tvBedsMin, tvBedsMax;
      ImageView cardImage, likeBtn;
      CardView houseCardView;

      public ViewHolder(@NonNull View itemView) {
         super(itemView);
         houseCardView = itemView.findViewById(R.id.houseCardView);
         tvTitle = itemView.findViewById(R.id.tvTitle);
         tvPropertyType = itemView.findViewById(R.id.tvPropertyType);
         tvLocation = itemView.findViewById(R.id.tvAddress);
         tvListPriceMin = itemView.findViewById(R.id.tvPrice);
         tvListPriceMax = itemView.findViewById(R.id.tvPrice2);
         tvCatsPolicy = itemView.findViewById(R.id.tvCatPolicy);
         tvDogsPolicy = itemView.findViewById(R.id.tvDogPolicy);
         tvBathsMin = itemView.findViewById(R.id.tvBaths);
         tvBathsMax = itemView.findViewById(R.id.tvBaths2);
         tvBedsMin = itemView.findViewById(R.id.tvBeds);
         tvBedsMax = itemView.findViewById(R.id.tvBeds2);
         cardImage = itemView.findViewById(R.id.cardImage);
         likeBtn = itemView.findViewById(R.id.likeBtn);

      }

      public void bind(FavouriteList favouriteList) {
         tvTitle.setText(favouriteList.getHouseName());
         tvPropertyType.setText(favouriteList.getPropertyType());
         tvLocation.setText(favouriteList.getHouseAddress());
         tvListPriceMin.setText(context.getString(R.string.dollarSign, favouriteList.getHouseMinPrice()));
         tvListPriceMax.setText(context.getString(R.string.dollarSign, favouriteList.getHouseMaxPrice()));
         tvCatsPolicy.setText(favouriteList.getCatPolicy());
         tvDogsPolicy.setText(favouriteList.getDogPolicy());
         tvBathsMin.setText(String.valueOf(favouriteList.getBathsMin()));
         tvBathsMax.setText(String.valueOf(favouriteList.getBathsMax()));
         tvBedsMin.setText(String.valueOf(favouriteList.getBedsMin()));
         tvBedsMax.setText(String.valueOf(favouriteList.getBedsMax()));
         Picasso.get().load(favouriteList.getImageHref()).fit().into(cardImage);
         likeBtn.setVisibility(View.INVISIBLE);
      }
   }


   }
