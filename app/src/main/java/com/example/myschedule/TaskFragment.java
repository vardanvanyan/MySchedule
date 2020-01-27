package com.example.myschedule;


import android.Manifest;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.myschedule.Adapters.CategoryAdapter;
import com.example.myschedule.Adapters.TaskAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class TaskFragment extends Fragment {

    RecyclerView recView;
    private DataBaseHelper db;
    private Dialog dialog;
    Button colorPriority;
    EditText name;
    EditText description;
    DatePicker date;
    TimePicker time;
    Button close;
    Button save;
    TaskModel taskModel;
    TaskAdapter taskAdapter;
    private ArrayList<TaskModel> list;
    int color;
    AlarmManager alarmManager;
    FrameLayout frameLayout;
    boolean checkUndo;


    public TaskFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.task_fragment, container, false);
        alarmManager = (AlarmManager) Objects.requireNonNull(getActivity()).getSystemService(Context.ALARM_SERVICE);
        frameLayout = view.findViewById(R.id.fr_task_id);
        FloatingActionButton fab = view.findViewById(R.id.task_fab);

        fab.setOnClickListener(view1 -> myTaskDialog());
        assert getArguments() != null;
        String name = getArguments().getString(CategoryAdapter.CATEGORY_NAME_KEY);
        assert name != null;
        db = Room.databaseBuilder(Objects.requireNonNull(getContext()), DataBaseHelper.class, name)
                .allowMainThreadQueries()
                .build();
        list = new ArrayList<>();
        list.addAll(db.getDataDao().getAllDataTask());
        list = sortList(list);
        recView = view.findViewById(R.id.task_rec_view);
        recView.setLayoutManager(new LinearLayoutManager(getContext()));
        taskAdapter = new TaskAdapter(list);
        recView.setAdapter(taskAdapter);


        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.
                SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder dragged,
                                  @NonNull RecyclerView.ViewHolder target) {

                int position_dragged = dragged.getAdapterPosition();
                int position_target = target.getAdapterPosition();
                Collections.swap(list, position_dragged, position_target);
                if (position_target == 0 || list.get(position_target - 1).getPriorityColor() == Color.RED) {
                    list.get(position_target).setPriorityColor(Color.RED);
                } else if (list.get(position_target - 1).getPriorityColor() == Color.BLUE)
                    list.get(position_target).setPriorityColor(Color.BLUE);
                else if (list.get(position_target - 1).getPriorityColor() == Color.GREEN) {
                    list.get(position_target).setPriorityColor(Color.GREEN);
                }
                TaskModel taskModel = list.get(position_target);
                db.getDataDao().replaceTask(taskModel);
                taskAdapter.notifyItemMoved(position_dragged, position_target);
                taskAdapter.notifyItemRangeChanged(position_target, taskAdapter.getItemCount());
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

            }
        });
        touchHelper.attachToRecyclerView(recView);

        enableSwipeToDeleteAndUndo();
        return view;
    }


    public void myTaskDialog() {
        dialog = new Dialog(Objects.requireNonNull(getContext()));
        dialog.setContentView(R.layout.task_dialog);
        dialog.setCancelable(false);
        dialog.show();
        name = dialog.findViewById(R.id.task_name);
        colorPriority = dialog.findViewById(R.id.task_priority_btn_color);
        save = dialog.findViewById(R.id.task_dialog_save_btn);
        close = dialog.findViewById(R.id.task_dialog_close_btn);
        description = dialog.findViewById(R.id.task_desc);
        String[] priority = {"High", "Mid", "Low"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, priority);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = dialog.findViewById(R.id.task_spinner);
        spinner.setAdapter(adapter);
        spinner.setPrompt("Title");
        spinner.setSelection(2);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    colorPriority.setBackgroundColor(Color.RED);
                    color = Color.RED;
                } else if (position == 1) {
                    colorPriority.setBackgroundColor(Color.BLUE);
                    color = Color.BLUE;
                } else {
                    colorPriority.setBackgroundColor(Color.GREEN);
                    color = Color.GREEN;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        close.setOnClickListener(v -> dialog.cancel());

        save.setOnClickListener(v -> {
            date = dialog.findViewById(R.id.task_date);
            time = dialog.findViewById(R.id.task_time);
            int day = date.getDayOfMonth();
            int month = date.getMonth();
            int year = date.getYear();
            int hour = time.getCurrentHour();
            int minute = time.getCurrentMinute();
            String checkMinute = "";
            if (minute < 10) {
                checkMinute = "0" + minute;

            } else checkMinute = String.valueOf(minute);

            Date date = new Date();
            int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss", Locale.US).format(date));
            taskModel = new TaskModel(name.getText().toString(), day + "/" + month + "/" + year, hour + ":" + checkMinute,
                    description.getText().toString(), color, id);

            if (checkTaskDialog(taskModel)) {
                if (taskModel.getName().isEmpty()) {
                    Toast.makeText(getContext(), "name is empty", Toast.LENGTH_SHORT).show();
                } else {
                    db.getDataDao().insert(taskModel);
                    list.add(taskModel);
                    list = sortList(list);
                    int permissionStatus = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CALENDAR);
                    if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                        long calId = 3;
                        long startMils;
                        long endMils;
                        Calendar beginTime = Calendar.getInstance();
                        beginTime.set(year, month, day, hour, minute);
                        startMils = beginTime.getTimeInMillis();
                        Calendar endTime = Calendar.getInstance();
                        endTime.set(year, month, day, hour, minute);
                        endMils = endTime.getTimeInMillis();
                        ContentResolver cr = Objects.requireNonNull(getActivity()).getContentResolver();
                        ContentValues values = new ContentValues();
                        values.put(CalendarContract.Events.DTSTART, startMils);
                        values.put(CalendarContract.Events.DTEND, endMils);
                        values.put(CalendarContract.Events.TITLE, taskModel.getName());
                        values.put(CalendarContract.Events.DESCRIPTION, taskModel.description);
                        values.put(CalendarContract.Events.CALENDAR_ID, calId);

                        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Armenia/Yerevan");
                        cr.insert(CalendarContract.Events.CONTENT_URI, values);
                    } else {
                        ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.WRITE_CALENDAR}, 0);

                    }

                    if (minute < 10) {
                        if (hour == 0) {
                            hour = hour + 23;
                            minute = minute + 50;
                            if (day == 1) {
                                if (month == 0) {
                                    year = year - 1;
                                    month = month + 12;
                                    day = day + 30;
                                } else if (
                                        month == 2) {
                                    if (year % 4 == 0) {
                                        day = day + 28;

                                    } else {
                                        day = day + 27;
                                    }
                                } else if (month == 4 || month == 6 || month == 9 || month == 11) {
                                    day = day + 29;

                                } else {
                                    day = day + 30;
                                }
                                month = month - 1;
                            } else {
                                day = day - 1;
                            }
                        } else {
                            hour = hour - 1;
                            minute = minute + 50;
                        }
                    } else {
                        minute = minute - 10;
                    }

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, day, hour, minute);
                    Intent intent = new Intent(getContext(), BroadReceiver.class);
                    intent.putExtra("NAME_KEY", taskModel.getName());
                    intent.putExtra("DESC_KEY", taskModel.getDescription());
                    intent.putExtra("ID", taskModel.getId());
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), taskModel.getId(), intent, PendingIntent.FLAG_ONE_SHOT);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    taskAdapter = new TaskAdapter(list);
                    recView.setAdapter(taskAdapter);
                    dialog.cancel();
                }
            } else {
                Toast.makeText(getContext(), "Such name already exists", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public ArrayList<TaskModel> sortList(ArrayList<TaskModel> list) {
        ArrayList<TaskModel> arrayList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getPriorityColor() == Color.RED) {
                arrayList.add(list.get(i));
            }

        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getPriorityColor() == Color.BLUE) {
                arrayList.add(list.get(i));
            }

        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getPriorityColor() == Color.GREEN) {
                arrayList.add(list.get(i));
            }

        }
        return arrayList;
    }

    private void enableSwipeToDeleteAndUndo() {

        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();

                final TaskModel taskModel = list.get(position);


                list.remove(position);
                taskAdapter.notifyDataSetChanged();

                checkUndo = true;
                Snackbar snackbar = Snackbar
                        .make(frameLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", view -> {
                    checkUndo = false;
                    list.add(position, taskModel);
                    taskAdapter.notifyDataSetChanged();
                    recView.scrollToPosition(position);
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
                new Thread(() -> {
                    try {
                        Intent intent = new Intent(getContext(), BroadReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), taskModel.getId(), intent, PendingIntent.FLAG_ONE_SHOT);
                        pendingIntent.cancel();
                        alarmManager.cancel(pendingIntent);
                        Thread.sleep(3000);
                        if (checkUndo) {
                            db.getDataDao().delete(taskModel);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recView);
    }

    public boolean checkTaskDialog(TaskModel model) {
        for (TaskModel a : list) {
            if (a.getName().equals(model.getName())) {
                return false;
            }
        }
        return true;
    }
}