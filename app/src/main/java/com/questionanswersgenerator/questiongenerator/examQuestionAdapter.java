package com.questionanswersgenerator.questiongenerator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class examQuestionAdapter extends RecyclerView.Adapter<examQuestionAdapter.viewHolder> {

    ArrayList<ArrayList<String>> questionList;
    boolean isAnswerShown;

    public examQuestionAdapter(ArrayList<ArrayList<String>> questionList, boolean isAnswerShown) {
        this.questionList = questionList;
        this.isAnswerShown = isAnswerShown;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_answers_row, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        holder.setData(questionList.get(position).get(0), questionList.get(position).get(1), questionList.get(position).get(2), position);
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

        public void setData(String id, String question, String answer, int qNo) {
            questionTV.setText("Q" + String.valueOf(qNo + 1) + ") " + question);
            answerTV.setText(answer);

            if (!answer.equals("")) {
                if (isAnswerShown)
                    answerBox.setVisibility(View.VISIBLE);
                else
                    answerBox.setVisibility(View.GONE);
            }
        }
    }
}
