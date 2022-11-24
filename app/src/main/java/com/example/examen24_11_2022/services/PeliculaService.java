package com.example.examen24_11_2022.services;

import androidx.room.Delete;

import com.example.examen24_11_2022.entities.Pelicula;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PeliculaService {

    @GET("peliculas2")
    Call<Pelicula> finById(@Path("peliculaId") int id);

    @GET("peliculas2")
    Call<List<Pelicula>> get();

    @POST("peliculas2")
    Call<Pelicula> create(@Body Pelicula pelicula);

    @PUT("peliculas2/{id}")
    Call<Pelicula> update(@Path("id") int id, @Body Pelicula pelicula);

    @DELETE("peliculas2/{id}")
    Call<Pelicula> delete(@Path("id") int id);
}
