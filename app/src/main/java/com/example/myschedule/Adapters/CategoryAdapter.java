package com.example.myschedule.Adapters;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myschedule.CategoryModel;
import com.example.myschedule.R;
import com.example.myschedule.TaskFragment;

import java.util.List;

import butterknife.ButterKnife;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {


    private List<CategoryModel> list;
    public static final String CATEGORY_NAME_KEY = "CATEGORY_NAME";


    public CategoryAdapter(List<CategoryModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.category_item_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        CategoryModel categoryModel = list.get(position);
        viewHolder.name.setText(categoryModel.getName());
        viewHolder.name.setTextColor(categoryModel.getColor());
        viewHolder.img.setImageResource(categoryModel.getImage());
        viewHolder.img.setColorFilter(categoryModel.getColor());
        int color = list.get(position).getColor();

        Drawable drawable = ContextCompat.getDrawable(viewHolder.img.getContext(), R.drawable.img_background);
        assert drawable != null;
        drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        viewHolder.img.setBackground(drawable);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView img;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            name = itemView.findViewById(R.id.name);
            img = itemView.findViewById(R.id.image);

            itemView.setOnClickListener(v -> {


                TaskFragment taskFragment = new TaskFragment();
                Bundle bundle = new Bundle();
                bundle.putString(CATEGORY_NAME_KEY, list.get(getAdapterPosition()).name);
                taskFragment.setArguments(bundle);
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager()
                        .beginTransaction().replace(R.id.container, taskFragment).addToBackStack(null);
                fragmentTransaction.commit();
            });
        }
    }
}