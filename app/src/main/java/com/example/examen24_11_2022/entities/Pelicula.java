package com.example.examen24_11_2022.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Pelicula")
public class Pelicula {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "titulo")
    public String titulo;
    @ColumnInfo(name = "sinopsis")
    public String sinopsis;
    @ColumnInfo(name = "imagen")
    public String imagen;
}
