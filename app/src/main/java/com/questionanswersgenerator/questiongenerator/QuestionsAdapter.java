package com.questionanswersgenerator.questiongenerator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.viewHolder> {
    ArrayList<ArrayList<String>> questionList;
    Activity activity;
    Context context;
    boolean isAnswerShown;

    public QuestionsAdapter(ArrayList<ArrayList<String>> questionList, Activity activity, boolean isAnswerShown) {
        this.questionList = questionList;
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.isAnswerShown = isAnswerShown;
    }

    @NonNull
    @Override
    public QuestionsAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_answers_row, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionsAdapter.viewHolder holder, int position) {
        String id = questionList.get(position).get(0);
        String question = questionList.get(position).get(1);
        String answer = questionList.get(position).get(2);

        holder.setData(id, question, answer);
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        LinearLayout answerBox, questionBox;
        TextView questionTV, answerTV;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            questionTV = itemView.findViewById(R.id.question);
            answerTV = itemView.findViewById(R.id.answer);
            answerBox = itemView.findViewById(R.id.answerBox);
            questionBox = itemView.findViewById(R.id.questionBox);
        }

        public void setData(String id, String question, String answer) {
            questionTV.setText(question);
            answerTV.setText(answer);

            if (!answer.equals("")) {
                if (isAnswerShown)
                    answerBox.setVisibility(View.VISIBLE);
                else
                    answerBox.setVisibility(View.GONE);
            }


            questionBox.setOnClickListener(view -> {

                if (answer.equals(""))
                    return;

                if (answerBox.getVisibility() == View.VISIBLE)
                    answerBox.setVisibility(View.GONE);
                else
                    answerBox.setVisibility(View.VISIBLE);
            });


            questionBox.setOnLongClickListener(view -> {

                Intent intent = new Intent(activity, NewQuestionActivity.class);
                intent.putExtra("chapter_id", "-1");
                intent.putExtra("question_id", id);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

                return true;
            });
        }
    }
}
