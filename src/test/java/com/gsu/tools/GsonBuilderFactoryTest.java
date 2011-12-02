package com.gsu.tools;


import java.util.*;

import static org.testng.Assert.*;

import org.bson.types.ObjectId;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.gsu.annotations.Primitive;
import com.gsu.annotations.Simplified;

class A {
    @Primitive
    Integer id;
    String name;
    
    @Simplified
    B b;
}

class B {
    @Primitive
    Integer id;
    
    String name;
    
    @Simplified
    A a;
    
    List<A> aList = new ArrayList<A>();
    @Simplified
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
            
            System.out.println(jsonB);
            System.out.println(idB);
            
            assertTrue(jsonB.contains("" + idB));
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
        } catch (Exception e){
            e.printStackTrace();
            fail();
        }
    }
}