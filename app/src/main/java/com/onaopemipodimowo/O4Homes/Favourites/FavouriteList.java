package com.onaopemipodimowo.O4Homes.Favourites;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName="favouritelist")
public class FavouriteList {

    @PrimaryKey
    @NonNull
    private String listingId;

    @ColumnInfo(name="houseName")
    private String houseName;

    @ColumnInfo(name="imageHref")
    private String imageHref;

    @ColumnInfo(name="houseMinPrice")
    private String houseMinPrice;

    @ColumnInfo(name="houseMaxPrice")
    private String houseMaxPrice;

    @ColumnInfo(name="houseAddress")
    private String houseAddress;

    @ColumnInfo(name="propertyType")
    private String propertyType;

    @ColumnInfo(name="catPolicy")
    private String catPolicy;

    @ColumnInfo(name="dogPolicy")
    private String dogPolicy;

    @ColumnInfo(name="bathsMin")
    private String bathsMin;

    @ColumnInfo(name="bathsMax")
    private String bathsMax;

    @ColumnInfo(name="bedsMin")
    private String bedsMin;

    @ColumnInfo(name="bedsMax")
    private String bedsMax;

    public String getListingId(){
        return listingId;
    }

    public void setListingId(String listingId){
        this.listingId = listingId;
    }

    public String getHouseName(){
        return houseName;}

    public void setHouseName(String houseName){
        this.houseName = houseName;
    }

    public String getImageHref(){
        return imageHref;}

    public void setImageHref(String imageHref){
        this.imageHref = imageHref;
    }

    public String getHouseMinPrice(){
        return houseMinPrice;}

    public void setHouseMinPrice(String houseMinPrice){
        this.houseMinPrice = houseMinPrice;
    }

    public String getHouseMaxPrice(){
        return houseMaxPrice;}

    public void setHouseMaxPrice(String houseMaxPrice){
        this.houseMaxPrice = houseMaxPrice;
    }

    public String getHouseAddress(){
        return houseAddress;}

    public void setHouseAddress(String houseAddress){
        this.houseAddress = houseAddress;
    }

    public String getPropertyType(){
        return propertyType;}

    public void setPropertyType(String propertyType){
        this.propertyType = propertyType;
    }

    public String getCatPolicy(){
        return catPolicy;}

    public void setCatPolicy(String catPolicy){
        this.catPolicy = catPolicy;
    }

    public String getDogPolicy(){
        return dogPolicy;}

    public void setDogPolicy(String dogPolicy){
        this.dogPolicy = dogPolicy;
    }

    public String getBathsMin(){
        return bathsMin;}

    public void setBathsMin(String bathsMin){
        this.bathsMin = bathsMin;
    }

    public String getBathsMax(){
        return bathsMax;}

    public void setBathsMax(String bathsMax){
        this.bathsMax = bathsMax;
    }

    public String getBedsMin(){
        return bedsMin;}

    public void setBedsMin(String bedsMin){
        this.bedsMin = bedsMin;
    }

    public String getBedsMax(){
        return bedsMax;}

    public void setBedsMax(String bedsMax){
        this.bedsMax = bedsMax;
    }
}
