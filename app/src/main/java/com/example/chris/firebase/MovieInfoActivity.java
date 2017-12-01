package com.example.chris.firebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MovieInfoActivity extends AppCompatActivity
{
    
    private static final String TAG = MovieInfoActivity.class.getSimpleName() + "_TAG";
    private EditText etMovieTitle;
    private EditText etMovieDirector;
    private EditText etMovieYear;
    private String title;
    private String director;
    private String year;
    private String original;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);
    
        title = getIntent().getStringExtra("title");
        director = getIntent().getStringExtra("director");
        year = getIntent().getStringExtra("year");
        original = getIntent().getStringExtra("original");
        bindViews();
    }
    
    private void bindViews()
    {
    
        etMovieTitle = findViewById(R.id.etMovieTitle);
        etMovieDirector = findViewById(R.id.etMovieDirector);
        etMovieYear = findViewById(R.id.etMovieYear);
    
        
        etMovieTitle.setText(title);
        etMovieDirector.setText(director);
        etMovieYear.setText(year);

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("movies");
    }
    
    
    public void onSaveChanges(View view)
    {
        Movie movie = new Movie(etMovieTitle.getText().toString(), etMovieDirector.getText().toString(), etMovieYear.getText().toString(), original);
        
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
                        Toast.makeText(MovieInfoActivity.this, "Movie Updated", Toast.LENGTH_SHORT).show();
                        MovieInfoActivity.this.startActivity(new Intent(MovieInfoActivity.this, MovieActivity.class));
                    }
                    
                })
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast.makeText(MovieInfoActivity.this, "Movie not updated.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
