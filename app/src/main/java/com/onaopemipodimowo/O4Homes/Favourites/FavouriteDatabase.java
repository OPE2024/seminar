package com.onaopemipodimowo.O4Homes.Favourites;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities={FavouriteList.class},version = 1)
public abstract class FavouriteDatabase extends RoomDatabase {
    public abstract FavouritesDao favouriteDao();
}
