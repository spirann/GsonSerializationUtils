package com.gsu.tools;


import java.util.*;

import static org.testng.Assert.*;

import com.gsu.annotations.Simplifiable;
import org.bson.types.ObjectId;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.gsu.annotations.Simple;

@Simplifiable
class A {

    Integer id;
    String name;
    
    @Simple
    B b;
}

@Simplifiable
class B {

    Integer id;
    
    String name;
    
    @Simple
    A a;
    
    List<A> aList = new ArrayList<A>();
    @Simple
    Map<ObjectId,A> amap = new HashMap<ObjectId,A>();
}

public class GsonBuilderFactoryTest {
    @Test(timeOut=1000)
    public void simpleTest(){
        B b = new B();
        A a = new A();
        
        a.b = b;
        a.name = "aname";
        b.a = a;
        b.name = "bname";
        
        int idB = new Random().nextInt(1000);
        b.id = idB;
        int idA = new Random().nextInt(1000);
        a.id = idA;
        
        try {
            Gson gson = GsonBuilderFactory.getComplexGsonBuilder(B.class).create();
            String jsonB = gson.toJson(b);
            
            System.out.println("jsonB: "+jsonB);
            System.out.println("id: "+idB);
            
            assertTrue(jsonB.contains("" + idB));

            B b2 = gson.fromJson(jsonB,B.class);
            System.out.println("b2: "+b2);
            System.out.println("b2.id: "+b2.id);
            System.out.println("b2.a.id: "+b2.a.id);
        } catch (Exception e){
            e.printStackTrace();
            fail();
        }
    }
    
    @Test
    public void mapTest(){
        B b = new B();
        b.name = "bname";
        
        int idB = new Random().nextInt(1000);
        b.id = idB;
        
        b.amap.put(new ObjectId(), new A());
        b.amap.put(new ObjectId(), new A());
        b.amap.put(new ObjectId(), new A());
        b.amap.put(new ObjectId(), new A());
        
        try {
            Gson gson = GsonBuilderFactory.getComplexGsonBuilder(B.class).create();
            String jsonB = gson.toJson(b);
            
            System.out.println(jsonB);
            
            assertTrue(jsonB.contains("" + idB));

            B b2 = gson.fromJson(jsonB,B.class);
            System.out.println("b2: "+b2);
            System.out.println("b2.id: "+b2.id);
            for(ObjectId id : b2.amap.keySet()){
                System.out.println(id + " => " + b2.amap.get(id));
            }
        } catch (Exception e){
            e.printStackTrace();
            fail();
        }
    }
}