package com.onaopemipodimowo.O4Homes.Favourites;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavouritesDao {

 @Insert
 public void addData(FavouriteList favouriteList);

 @Query("select * from favouritelist")
 public List<FavouriteList> getFavouriteData();

 @Query("SELECT EXISTS(SELECT * FROM favouritelist WHERE listingId=:listingId)")
 public Boolean exists(String listingId);

 @Query("select * from favouritelist where houseMinPrice=:houseMinPrice or houseMaxPrice=:houseMaxPrice")
 public List<FavouriteList> getFavouriteDataHousePrice(String houseMinPrice, String houseMaxPrice);

 @Query("SELECT EXISTS(SELECT * FROM favouritelist WHERE houseMinPrice=:houseMinPrice or houseMaxPrice=:houseMaxPrice)")
 public Boolean housePriceExists(String houseMinPrice, String houseMaxPrice);

 @Query("select * from favouritelist where dogPolicy=:dogPolicy")
 public List<FavouriteList> getFavouriteDataDogPolicy(String dogPolicy);

 @Query("SELECT EXISTS(SELECT * FROM favouritelist WHERE dogPolicy=:dogPolicy)")
 public Boolean houseDogPolicyExists(String dogPolicy);

 @Query("select * from favouritelist where catPolicy=:catPolicy")
 public List<FavouriteList> getFavouriteDataCatPolicy(String catPolicy);

 @Query("SELECT EXISTS(SELECT * FROM favouritelist WHERE catPolicy=:catPolicy)")
 public Boolean houseCatPolicyExists(String catPolicy);

 @Delete
 public void delete(FavouriteList favouriteList);

}
