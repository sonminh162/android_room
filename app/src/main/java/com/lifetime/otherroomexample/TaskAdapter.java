package com.lifetime.otherroomexample;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private Context mCtx;
    private List<Task> taskList;

    public TaskAdapter(Context mCtx, List<Task> taskList) {
        this.mCtx = mCtx;
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_tasks,parent,false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.bindView(task);
        if(task.isFinished()){
            holder.textViewStatus.setText("Finished");
            holder.textViewStatus.setBackgroundColor(0xFF93C47D);
        }
        else{
            holder.textViewStatus.setText("Studying");
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewStatus, textViewTask, textViewDesc, textViewFinishBy, textViewBirth, textViewGender;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            textViewTask = itemView.findViewById(R.id.textViewTask);
            textViewDesc = itemView.findViewById(R.id.textViewDesc);
            textViewFinishBy = itemView.findViewById(R.id.textViewFinishBy);
            textViewBirth = itemView.findViewById(R.id.textViewBirth);
            textViewGender = itemView.findViewById(R.id.textViewGender);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Task task = taskList.get(getAdapterPosition());

            Intent intent = new Intent(mCtx,UpdateTaskActivity.class);
            intent.putExtra("task",task);

            mCtx.startActivity(intent);
        }

        public void bindView(Task task){
            textViewTask.setText(task.getTask());
            textViewDesc.setText(task.getDesc());
            textViewFinishBy.setText(task.getFinishBy());
            textViewBirth.setText(task.getBirthday());
            textViewGender.setText(task.getGender());
        }
    }
}
