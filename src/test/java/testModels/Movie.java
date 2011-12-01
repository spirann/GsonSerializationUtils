package testModels;


import java.util.Random;

import com.gsu.annotations.Primitive;
import com.gsu.annotations.Simplified;

public class Movie {
    @Primitive
    private Integer id;
    
    private String title;
    private String synopsis;
    
    @Simplified
    private Actor actor;
    
    public Movie(){
        setId(new Random().nextInt(1000));
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }
}