package testModels;


import java.util.Random;

import com.gsu.annotations.Simplified;

public class Actor {

    private Integer id;
    
    @Simplified
    private Movie movie;
    
    private String name;
    private String lastname;
    
    public Actor(){
        setId(new Random().nextInt(1000));
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}