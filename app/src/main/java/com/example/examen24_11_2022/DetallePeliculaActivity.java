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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetallePeliculaActivity extends AppCompatActivity {

    EditText etEditTitulo, etEditSinopsis;

    private ImageView ivEditPhoto;

    public String link;

    Pelicula peliculaEdit;

    int cont = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_pelicula);

        etEditTitulo = findViewById(R.id.etEditTitulo);
        etEditSinopsis = findViewById(R.id.etEditSinopsis);
        ivEditPhoto = findViewById(R.id.ivEditPhoto);

        Intent intent = getIntent();
        String peliculaJson = intent.getStringExtra("PELICULA_DATA");

        if(peliculaJson!= null){
            peliculaEdit = new Gson().fromJson(peliculaJson, Pelicula.class);
            etEditTitulo.setText(peliculaEdit.titulo);
            etEditSinopsis.setText(peliculaEdit.sinopsis);
            Picasso.get().load(peliculaEdit.imagen).into(ivEditPhoto);
        }
        if (peliculaEdit == null) return;
    }

    public void editarFoto(View view){
        if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            abrirCamara();
        }
        else{
            requestPermissions(new String[] {Manifest.permission.CAMERA}, 1000);//un número cualquiera
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
            ivEditPhoto.setImageBitmap(imageBitmap);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();

            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            cont++;

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

    public void editarPelicula(View view){
        String titulo = etEditTitulo.getText().toString();
        String sinopsis = etEditSinopsis.getText().toString();

        if(titulo != null && sinopsis != null){

            Pelicula pelicula = new Pelicula();
            pelicula.titulo = titulo;
            pelicula.sinopsis = sinopsis;

            if(cont == 0) pelicula.imagen = peliculaEdit.imagen;
            else pelicula.imagen = link;

            Log.i("MAIN_APP", "Se actualizo la BD");

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://6352ca44a9f3f34c3749009a.mockapi.io/")// -> Aquí va la URL sin el Path
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            PeliculaService service = retrofit.create(PeliculaService.class);
            service.update(peliculaEdit.id, pelicula).enqueue(new Callback<Pelicula>() {
                @Override
                public void onResponse(Call<Pelicula> call, Response<Pelicula> response) {

                    AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                    db.peliculaDao().update(pelicula);

                    Log.i("MAIN_APP", String.valueOf(response.code()));
                    Toast.makeText(getApplicationContext(), "Se edito correctamente", Toast.LENGTH_SHORT).show();

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
        else Toast.makeText(getApplicationContext(), "No pueden haber datos vacíos", Toast.LENGTH_SHORT).show();

    }

    public void eliminarPelicula(View view){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://6352ca44a9f3f34c3749009a.mockapi.io/")// -> Aquí va la URL sin el Path
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PeliculaService service = retrofit.create(PeliculaService.class);
        service.delete(peliculaEdit.id).enqueue(new Callback<Pelicula>() {
            @Override
            public void onResponse(Call<Pelicula> call, Response<Pelicula> response) {
                Log.i("MAIN_APP", String.valueOf(response.code()));
                Log.i("MAIN_APP", "Se elimino correctamente");

                AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                db.peliculaDao().delete(peliculaEdit);

                Toast.makeText(getApplicationContext(), "Se elimino correctamente", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<Pelicula> call, Throwable t) {
                Log.i("MAIN_APP", "No se elimino");
                Toast.makeText(getApplicationContext(), "No se creo correctamente", Toast.LENGTH_SHORT).show();
            }
        });
    }
}