package com.example.examen24_11_2022;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.examen24_11_2022.adapters.PeliculasAdapter;
import com.example.examen24_11_2022.database.AppDatabase;
import com.example.examen24_11_2022.entities.Pelicula;
import com.example.examen24_11_2022.services.PeliculaService;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    RecyclerView rvPeliculas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppDatabase db = AppDatabase.getInstance(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://6352ca44a9f3f34c3749009a.mockapi.io/")// -> Aquí va la URL sin el Path
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PeliculaService services = retrofit.create(PeliculaService.class);
        services.get().enqueue(new Callback<List<Pelicula>>() {
            @Override
            public void onResponse(Call<List<Pelicula>> call, Response<List<Pelicula>> response) {

                List<Pelicula> data = response.body();

                for(int i = 0;i<data.size();i++){
                    Pelicula peliculaAux = data.get(i);
                    if(peliculaAux != null){
                        db.peliculaDao().update(peliculaAux);
                    }
                    else{
                        db.peliculaDao().create(peliculaAux);
                    }
                }

                List<Pelicula> peliculas = db.peliculaDao().getAll();
                Log.i("MAIN_APP", new Gson().toJson(peliculas));

                rvPeliculas = findViewById(R.id.rvPeliculas);
                rvPeliculas.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                rvPeliculas.setAdapter(new PeliculasAdapter(peliculas));

                Log.i("MAIN_APP", "Response: "+response.body().size());
                Log.i("MAIN_APP", "Response: "+response.code());
            }

            @Override
            public void onFailure(Call<List<Pelicula>> call, Throwable t) {
                Log.i("MAIN_APP", "Fallo");
            }
        });
    }

    public void crearPelicula(View view){
        Intent intent = new Intent(getApplicationContext(), CrearPeliculaActivity.class);
        startActivity(intent);
    }
}