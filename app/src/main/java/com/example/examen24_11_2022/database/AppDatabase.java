package com.example.examen24_11_2022.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.examen24_11_2022.daos.PeliculaDao;
import com.example.examen24_11_2022.entities.Pelicula;

@Database(entities = {Pelicula.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract PeliculaDao peliculaDao();

    public static AppDatabase getInstance(Context context){
        return Room.databaseBuilder(context, AppDatabase.class, "examen24-11-2022")
                .allowMainThreadQueries()
                .build();
    }

}
