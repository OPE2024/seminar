package com.onaopemipodimowo.O4Homes.Home.HomeDetails;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.onaopemipodimowo.O4Homes.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HomeDetailsAdapter extends RecyclerView.Adapter<HomeDetailsAdapter.ViewHolder>{
    Context context;
    List<HomeDetails> homeDetails;

    public HomeDetailsAdapter(Context context, List<HomeDetails>homeDetails){
        this.context = context;
        this.homeDetails = homeDetails;
    }

    // inflates a layout from xml and returns the holder
    @NonNull
    @Override
    public HomeDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View homeView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_details,parent,false);
        return new HomeDetailsAdapter.ViewHolder(homeView);
    }

    //populates data into the item through holder
    @Override
    public void onBindViewHolder(@NonNull HomeDetailsAdapter.ViewHolder holder, int position){
        Log.d("HomeAdapter","onBindViewHolder" + position);
        // get home at the passed in position
        HomeDetails homeDetail = homeDetails.get(position);
        // bind the home data into the VH
        holder.bind(homeDetail);
    }

    @Override
    public int getItemCount(){return homeDetails.size();}
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView tvImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvImage = itemView.findViewById(R.id.cardImage);

        }

        public void bind(HomeDetails homeDet) {
            Picasso.get().load(homeDet.getHouseImage()).fit().into(tvImage);
        }
    }
}
