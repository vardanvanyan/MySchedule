package com.example.myschedule.Adapters;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myschedule.R;
import com.example.myschedule.TaskModel;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    public TaskAdapter(List<TaskModel> modelsList) {
        this.modelsList = modelsList;
    }

    private List<TaskModel> modelsList;

    @NonNull
    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.task_item_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.ViewHolder viewHolder, int position) {
        TaskModel taskModel = modelsList.get(position);
        viewHolder.name.setText(taskModel.getName());
        viewHolder.desc.setText(taskModel.getDescription());
        viewHolder.date.setText(taskModel.getDate());
        viewHolder.time.setText(taskModel.getTime());
        Drawable drawable = ContextCompat.getDrawable(viewHolder.img.getContext(), R.drawable.button_background);
        assert drawable != null;
        drawable.setColorFilter(new PorterDuffColorFilter(taskModel.getPriorityColor(), PorterDuff.Mode.MULTIPLY));
        viewHolder.colorBtn.setBackground(drawable);
    }

    @Override
    public int getItemCount() {
        return modelsList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView img;
        TextView name;
        TextView desc;
        TextView date;
        TextView time;
        Button colorBtn;


        ViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.task_img);
            name = itemView.findViewById(R.id.task_name);
            desc = itemView.findViewById(R.id.task_desc);
            date = itemView.findViewById(R.id.task_date);
            time = itemView.findViewById(R.id.task_time);
            colorBtn = itemView.findViewById(R.id.color_btn);


        }
    }
}
