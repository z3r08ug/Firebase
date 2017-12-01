package com.example.chris.firebase;

/**
 * Created by chris on 11/30/2017.
 */

public class Movie
{
    String title;
    String director;
    String year;
    String originalTitle;
    
    public Movie()
    {
    
    }
    
    public Movie(String title, String director, String year)
    {
        this.title = title;
        this.director = director;
        this.year = year;
    }
    public Movie(String title, String director, String year, String originalTitle)
    {
        this.title = title;
        this.director = director;
        this.year = year;
        this.originalTitle = originalTitle;
    }
    
    public String getTitle()
    {
        return title;
    }
    
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public String getDirector()
    {
        return director;
    }
    
    public void setDirector(String director)
    {
        this.director = director;
    }
    
    public String getYear()
    {
        return year;
    }
    
    public void setYear(String year)
    {
        this.year = year;
    }
    
    public String getOriginalTitle()
    {
        return originalTitle;
    }
    
    public void setOriginalTitle(String originalTitle)
    {
        this.originalTitle = originalTitle;
    }
}
