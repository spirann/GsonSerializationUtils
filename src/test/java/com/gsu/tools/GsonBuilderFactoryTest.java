package com.gsu.tools;


import org.testng.annotations.Test;

import testModels.Actor;
import testModels.Movie;


public class GsonBuilderFactoryTest {
    @Test(timeOut=1000)
    public void simpleTest(){
        Movie movie = new Movie();
        Actor actor = new Actor();
        
        actor.setMovie(movie);
        movie.setActor(actor);
        
        System.out.println(GsonBuilderFactory.getGsonBuilder(Movie.class).create().toJson(movie));
    }
}