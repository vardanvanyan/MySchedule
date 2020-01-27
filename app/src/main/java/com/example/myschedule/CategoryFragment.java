package com.example.myschedule;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myschedule.Activities.CategoryActivity;
import com.example.myschedule.Adapters.CategoryAdapter;

import java.util.ArrayList;
import java.util.Objects;


import butterknife.BindView;
import butterknife.ButterKnife;


public class CategoryFragment extends Fragment {

    @BindView(R.id.rec_view)
    RecyclerView recyclerView;
    private DataBaseHelper databaseHelper;
    EditText categoryName;
    Button save;
    Button close;
    Dialog dialog;
    CategoryModel model;
    ArrayList<CategoryModel> list;
    CategoryAdapter adapter;
    private ImageView colorImg;
    private View colorView;
    private Bitmap bitmap;
    int color;


    public CategoryFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.category_fragment, container, false);
        int permissionStatus = ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.WRITE_CALENDAR);
        if (!(permissionStatus == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.WRITE_CALENDAR}, 0);
        }
        categoryName = view.findViewById(R.id.category_name);
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(view1 -> myDialog());
        ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        databaseHelper = CategoryActivity.getInstance().getDatabaseInstance();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        list = new ArrayList<>();

        list.add(new CategoryModel("Sport", R.drawable.ic_pool_24dp, Color.RED));
        list.add(new CategoryModel("Transport", R.drawable.ic_bus_24dp, Color.BLUE));
        list.add(new CategoryModel("Payments", R.drawable.ic_balance_24dp, Color.GREEN));
        list.add(new CategoryModel("Meal", R.drawable.ic_dining_24dp, Color.RED));
        list.add(new CategoryModel("Journey", R.drawable.ic_flight_24dp, Color.BLUE));
        list.add(new CategoryModel("Beach", R.drawable.ic_beach_24dp, Color.YELLOW));

        list.addAll(databaseHelper.getDataDao().getAllData());
        adapter = new CategoryAdapter(list);
        recyclerView.setAdapter(adapter);
    }


    @SuppressLint("ClickableViewAccessibility")
    public void myDialog() {
        dialog = new Dialog(Objects.requireNonNull(getContext()));
        dialog.setContentView(R.layout.category_dialog);
        dialog.setCancelable(false);
        dialog.show();
        save = dialog.findViewById(R.id.save_btn);
        close = dialog.findViewById(R.id.close_btn);
        categoryName = dialog.findViewById(R.id.category_name);
        colorImg = dialog.findViewById(R.id.color_img);
        colorView = dialog.findViewById(R.id.colorView);

        colorImg.setDrawingCacheEnabled(true);
        colorImg.buildDrawingCache(true);
        colorImg.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                bitmap = colorImg.getDrawingCache();
            }
            try {
                int pixel = bitmap.getPixel((int) event.getX(), (int) event.getY());
                int r = Color.red(pixel);
                int g = Color.green(pixel);
                int b = Color.blue(pixel);
                colorView.setBackgroundColor(Color.rgb(r, g, b));
                color = Color.rgb(r, g, b);
            } catch (Exception ignored) {

            }
            return true;
        });

        close.setOnClickListener(v -> dialog.cancel());

        save.setOnClickListener(v -> {
            model = new CategoryModel(categoryName.getText().toString(), R.drawable.ic_pool_24dp, color);

            if (checkCategoryDialog(model)) {
                if (model.getName().isEmpty()) {
                    Toast.makeText(getContext(), "Write a category name", Toast.LENGTH_SHORT).show();
                } else {
                    databaseHelper.getDataDao().insert(model);
                    list.add(model);
                    adapter.notifyDataSetChanged();
                    dialog.cancel();
                }
            } else {
                Toast.makeText(getContext(), "Such name already exists", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean checkCategoryDialog(CategoryModel model) {
        for (CategoryModel a : list) {
            if (a.getName().equals(model.getName())) {
                return false;
            }
        }
        return true;
    }
}
