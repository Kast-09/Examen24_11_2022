package com.example.examen24_11_2022;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.examen24_11_2022.database.AppDatabase;
import com.example.examen24_11_2022.entities.Imagen;
import com.example.examen24_11_2022.entities.ImagenBase64;
import com.example.examen24_11_2022.entities.Pelicula;
import com.example.examen24_11_2022.services.ImagenServices;
import com.example.examen24_11_2022.services.PeliculaService;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CrearPeliculaActivity extends AppCompatActivity {

    EditText etTitulo, etSinopsis;
    private ImageView ivPhoto;

    public String link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_pelicula);

        etTitulo = findViewById(R.id.etTitulo);
        etSinopsis = findViewById(R.id.etSinopsis);
        ivPhoto = findViewById(R.id.ivPhoto);

    }

    public void takePhoto(View view){
        //antes de abrir, preguntar si tiene permisos
        if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            abrirCamara();
        } //pedir permisos
        else{
            requestPermissions(new String[] {Manifest.permission.CAMERA}, 100);//un número cualquiera
        }
    }

    public void abrirCamara(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//con esto es lo mínimo necesario para abrir la cámara
        startActivityForResult(intent, 1000);//se le pone cualquier número, sirve como código de respeusta

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1000 && resultCode == RESULT_OK){// el CAMERA_REQUEST es para validar que sea una petición de abrir la cámara y el RESULT_OK es para validar que al abrir la cámara todo salio bien y no hubo errores
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ivPhoto.setImageBitmap(imageBitmap);

            //esto sirve para convertir bitmap a base64
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();

            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.imgur.com/")// -> Aquí va la URL sin el Path
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ImagenBase64 imagen = new ImagenBase64();
            imagen.image = encoded;

            ImagenServices services = retrofit.create(ImagenServices.class);
            services.create(imagen).enqueue(new Callback<Imagen>() {
                @Override
                public void onResponse(Call<Imagen> call, Response<Imagen> response) {
                    Log.i("MAIN_APP", String.valueOf(response.code()));
                    Imagen data = response.body();
                    link = data.data.link;
                    Log.i("MAIN_APP", new Gson().toJson(data));
                }

                @Override
                public void onFailure(Call<Imagen> call, Throwable t) {
                    Log.i("MAIN_APP", "Fallo a obtener datos");
                }
            });
        }
    }

    public void guardarPelicula(View view){
        String titulo = etTitulo.getText().toString();
        String sinopsis = etSinopsis.getText().toString();

        Pelicula pelicula = new Pelicula();
        pelicula.titulo = titulo;
        pelicula.sinopsis = sinopsis;
        pelicula.imagen = link;

        AppDatabase db = AppDatabase.getInstance(this);
        db.peliculaDao().create(pelicula);
        Log.i("MAIN_APP", "Se guardo en BD");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://6352ca44a9f3f34c3749009a.mockapi.io/")// -> Aquí va la URL sin el Path
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PeliculaService service = retrofit.create(PeliculaService.class);
        service.create(pelicula).enqueue(new Callback<Pelicula>() {
            @Override
            public void onResponse(Call<Pelicula> call, Response<Pelicula> response) {
                Log.i("MAIN_APP", String.valueOf(response.code()));
                Toast.makeText(getApplicationContext(), "Se creo correctamente", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<Pelicula> call, Throwable t) {
                Log.i("MAIN_APP", "No se creo");
                Toast.makeText(getApplicationContext(), "No se creo correctamente", Toast.LENGTH_SHORT).show();
            }
        });
    }
}