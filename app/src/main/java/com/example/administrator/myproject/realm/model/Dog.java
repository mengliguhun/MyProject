package com.example.administrator.myproject.realm.model;

import io.realm.RealmObject;

/**
 * Created by jack on 16-12-29.
 */

public class Dog extends RealmObject{
    private String name;
    private int age;
    private String pic;

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
