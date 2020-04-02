package com.example.consumerapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.consumerapp.R;
import com.example.consumerapp.model.Movie;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private Context context;
    public ArrayList<Movie> listMovie = new ArrayList<>();
    private OnItemClickCallback onItemClickCallback;

    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    public MovieAdapter(Context context){
        this.context = context;
    }

    public ArrayList<Movie> getListMovie(){
        return listMovie;
    }

    public void setData(ArrayList<Movie> movies){
        listMovie.clear();
        listMovie.addAll(movies);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        listMovie.remove(position);
        notifyItemRemoved(position);
        int sizeOfNewItemsList = listMovie.size();
        notifyItemRangeChanged(position, sizeOfNewItemsList);
    }


    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemRow = LayoutInflater.from(context).inflate(R.layout.activity_movie_adapter, parent, false);
        return new MovieViewHolder(itemRow);
    }

    @Override
    public void onBindViewHolder(@NonNull final MovieViewHolder holder, final int position) {
        holder.bind(listMovie.get(position));

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickCallback.onItemClicked(listMovie.get(holder.getAdapterPosition()), holder, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return getListMovie().size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {

        public ImageView Photo;
        TextView tvTitle, tvYear;
        RatingBar tvRating;
        ConstraintLayout constraintLayout;

        MovieViewHolder(View itemView){
            super(itemView);

            Photo = itemView.findViewById(R.id.Photo);
            tvTitle = itemView.findViewById(R.id.Title);
            tvYear = itemView.findViewById(R.id.Year);
            tvRating = itemView.findViewById(R.id.Rating);
            constraintLayout = itemView.findViewById(R.id.constraint);
        }

        void bind(Movie movie) {
            Glide.with(context)
                    .load(movie.getPhoto())
                    .apply(new RequestOptions().transform(new RoundedCorners(40)))
                    .into(Photo);
            Photo.bringToFront();
            tvTitle.setText(movie.getTitle());
            tvYear.setText(movie.getYear());
            tvRating.setRating(movie.getRating());
        }
    }

    public interface OnItemClickCallback {
        void onItemClicked(Movie movie, MovieViewHolder holder, int position);

    }
}
