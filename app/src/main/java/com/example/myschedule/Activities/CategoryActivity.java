package com.example.myschedule.Activities;

import android.arch.persistence.room.Room;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.myschedule.CategoryFragment;
import com.example.myschedule.DataBaseHelper;
import com.example.myschedule.R;


public class CategoryActivity extends AppCompatActivity {

    private static CategoryActivity instance;
    private DataBaseHelper db;


    public static CategoryActivity getInstance() {
        return instance;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        instance = this;
        db = Room.databaseBuilder(getApplicationContext(), DataBaseHelper.class, "data-database")
                .allowMainThreadQueries()
                .build();
        CategoryFragment categoryFragment = new CategoryFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction().replace(R.id.container, categoryFragment);
        fragmentTransaction.commit();

    }


    public DataBaseHelper getDatabaseInstance() {
        return db;
    }
}
