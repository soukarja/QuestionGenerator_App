package com.questionanswersgenerator.questiongenerator;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class subjectAdapter extends RecyclerView.Adapter<subjectAdapter.viewHolder> {

    ArrayList<ArrayList<String>> subjectList;
    Activity activity;
    Context context;
    int mode;
    databaseHelper db;

    public subjectAdapter(ArrayList<ArrayList<String>> subjectList, Activity activity) {
        this.subjectList = subjectList;
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.mode = 1;
        db = new databaseHelper(context);
    }

    public subjectAdapter(ArrayList<ArrayList<String>> subjectList, Activity activity, int mode) {
        this.subjectList = subjectList;
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.mode = mode;
        db = new databaseHelper(context);
    }

    @NonNull
    @Override
    public subjectAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subjects_row, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull subjectAdapter.viewHolder holder, int position) {
        String id = subjectList.get(position).get(0);
        String subjectName = subjectList.get(position).get(1);

        holder.setData(id, subjectName);
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView name;
        LinearLayout box;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            box = itemView.findViewById(R.id.box);
        }

        public void setData(String id, String subjectNameText) {
            name.setText(subjectNameText);

            box.setOnClickListener(view -> {
                Intent intent;
                if (mode == 1) {
                    intent = new Intent(activity, ChaptersActivity.class);

                } else {
                    intent = new Intent(activity, QuestionsActivity.class);
                }

                intent.putExtra("subject_id", id);
                intent.putExtra("subject_name", subjectNameText);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            });
        }
    }
}
