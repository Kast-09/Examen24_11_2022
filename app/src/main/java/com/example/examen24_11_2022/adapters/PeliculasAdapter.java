package com.example.examen24_11_2022.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.examen24_11_2022.DetallePeliculaActivity;
import com.example.examen24_11_2022.R;
import com.example.examen24_11_2022.entities.Pelicula;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PeliculasAdapter extends RecyclerView.Adapter {

    List<Pelicula> data;

    public PeliculasAdapter(List<Pelicula> data){
        this.data = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());//aquí llamamos al contexto

        View itemView = inflater.inflate(R.layout.item_peliculas, parent, false);//aquí hacemos referencia al item creado

        return new PeliculasViewHolder(itemView);//aquí retornamos el itemView creado
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Pelicula pelicula = data.get(position);

        TextView tvTitulo = holder.itemView.findViewById(R.id.tvTitulo);
        tvTitulo.setText(data.get(position).titulo);

        TextView tvSinopsis = holder.itemView.findViewById(R.id.tvSinopsis);
        tvSinopsis.setText(data.get(position).sinopsis);

        ImageView ivPelicula = holder.itemView.findViewById(R.id.ivPelicula);
        Picasso.get().load(data.get(position).imagen).into(ivPelicula);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), DetallePeliculaActivity.class);
                intent.putExtra("PELICULA_DATA", new Gson().toJson(pelicula));
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class PeliculasViewHolder extends RecyclerView.ViewHolder {
        public PeliculasViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
