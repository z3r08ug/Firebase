package com.example.chris.firebase;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MovieActivity extends AppCompatActivity
{
    
    private static final String TAG = MovieActivity.class.getSimpleName() + "_TAG";
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    EditText etMovieName, etMovieDirector, etMovieYear;
    String movieName, movieDirector, movieYear;
    private FirebaseUser user;
    List<Movie> movies;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        
        etMovieName = findViewById(R.id.etMovieName);
        etMovieDirector = findViewById(R.id.etMovieDirector);
        etMovieYear = findViewById(R.id.etMovieYear);
        recyclerView = findViewById(R.id.rvMovies);
        
        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        
        user = FirebaseAuth.getInstance().getCurrentUser();
        myRef = database.getReference("movies");
        movies = new ArrayList<>();
    
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
    
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);
    }
    
    public void onAddMovie(View view)
    {
        
        
        Movie movie = getMovie();
        myRef
                .child(user.getUid())
                .child("FavoriteMovies")
                .child(movie.getOriginalTitle())
                .setValue(movie)
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        Toast.makeText(MovieActivity.this, "Movie Added", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast.makeText(MovieActivity.this, "Movie not added.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    @Override
    protected void onResume()
    {
        super.onResume();
        movies.clear();
        readFromTheDatabase();
    }
    
    private Movie getMovie()
    {
        movieName = etMovieName.getText().toString();
        movieDirector = etMovieDirector.getText().toString();
        movieYear = etMovieYear.getText().toString();
        
        return new Movie(movieName, movieDirector, movieYear, movieName);
    }
    
    private void readFromTheDatabase()
    {
        Log.d(TAG, "readFromTheDatabase: here");
        myRef
                .child(user.getUid())
                .child("FavoriteMovies")
                .addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        movies.clear();
                        for (DataSnapshot snapshot: dataSnapshot.getChildren())
                        {
                            Movie movie = snapshot.getValue(Movie.class);
                            Log.d(TAG, "onDataChange: " + movie.getTitle());
                            movies.add(movie);
                        }
                        recyclerAdapter = new RecyclerAdapter(movies);
                        recyclerView.setAdapter(recyclerAdapter);
                    }
                    
                    @Override
                    public void onCancelled(DatabaseError error)
                    {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
    }
}
