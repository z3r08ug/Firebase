package com.example.chris.firebase;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Admin on 11/27/2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>
{
    List<Movie> movies;

    public RecyclerAdapter(List<Movie> movies)
    {
        this.movies = movies;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_list_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, int position)
    {
        Movie movie = movies.get(position);
        if(movie != null)
        {
            holder.tvMovieInfo.setText(movie.getTitle());
        }
    }

    @Override
    public int getItemCount()
    {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView tvMovieInfo;
        public ViewHolder(final View itemView)
        {
            super(itemView);
            tvMovieInfo = itemView.findViewById(R.id.tvMovieInfo);
            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    String title = movies.get(getAdapterPosition()).getTitle();
                    String director = movies.get(getAdapterPosition()).getDirector();
                    String year = movies.get(getAdapterPosition()).getYear();
                    String original = movies.get(getAdapterPosition()).getOriginalTitle();
                    Intent intent = new Intent(itemView.getContext(), MovieInfoActivity.class);
                    intent.putExtra("title", title);
                    intent.putExtra("director", director);
                    intent.putExtra("year", year);
                    intent.putExtra("original", original);
                    
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
