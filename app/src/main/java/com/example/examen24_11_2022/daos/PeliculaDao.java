package com.example.examen24_11_2022.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.examen24_11_2022.entities.Pelicula;

import java.util.List;

@Dao
public interface PeliculaDao {

    @Query("SELECT * FROM Pelicula")
    List<Pelicula> getAll();

    @Query("SELECT * FROM Pelicula where id = :id")
    Pelicula find(int id);

    @Insert
    void create(Pelicula pelicula);

    @Update
    void update(Pelicula pelicula);

    @Delete
    void delete(Pelicula pelicula);
}
