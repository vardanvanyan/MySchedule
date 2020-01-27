package com.example.myschedule;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class CategoryModel {
    @PrimaryKey
    @NonNull
    public String name;
    private int image;
    private int color;


    CategoryModel() {
    }

    CategoryModel(@NonNull String name, int image, int color) {
        this.name = name;
        this.image = image;
        this.color = color;

    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    void setImage(int image) {
        this.image = image;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }


}
