package com.example.todo;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;


public class TaskFragment extends Fragment {

    final String TAG = "TaskFragment";
    public List<TodoTask> tasks;
    public RecyclerView rv;
    public TaskAdapter adapter;

    public TaskFragment() {
        // Required empty public constructor
    }

    public static TaskFragment newInstance()
    {
        TaskFragment fragment = new TaskFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task, container, false);
        rv = view.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TaskAdapter(tasks);
        rv.setAdapter(adapter);
        return view;
    }



    class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskHolder>
    {
        List<TodoTask> tasks;

        public TaskAdapter(List<TodoTask> tasks)
        {
            this.tasks = tasks;
        }
        @NonNull
        @Override
        public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //将我们自定义的item布局R.layout.item_one转换为View
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.todo_task, parent, false);
            TaskHolder holder = new TaskHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull TaskHolder holder, int position) {
            holder.bind(tasks.get(position));
        }

        @Override
        public int getItemCount() {
            return tasks.size();
        }

        public class TaskHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
            public TodoTask task;
            public Button btn;
            public CheckBox cb;
            public CheckBox star;
            public TaskHolder(@NonNull View itemView) {
                super(itemView);
                btn = itemView.findViewById(R.id._1btn);
                btn.setOnClickListener(this);
                cb = itemView.findViewById(R.id.check);
                cb.setOnCheckedChangeListener(this);
                star = itemView.findViewById(R.id.star);
                star.setOnCheckedChangeListener(this);
            }

            public void bind(TodoTask t)
            {
                task = t;
                if(task == null) return;
                btn.setText(task.name);
                cb.setChecked(task.isDone);
                star.setChecked(task.isStared);
            }

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),ThirdActivity.class);

                Button btn = (Button)v;

                TaskHolder holder = (TaskHolder) rv.getChildViewHolder((View)v.getParent().getParent());

                intent.putExtra("task",holder.task);
                startActivityForResult(intent,1);
            }

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CheckBox check = (CheckBox)buttonView;

                TaskHolder holder = (TaskHolder) rv.getChildViewHolder((View)check.getParent().getParent());

                if(holder == null) return;
                if(check == holder.cb)
                {
                    holder.task.isDone = isChecked;
                }

                if(check == holder.star)
                {
                    holder.task.isStared = isChecked;
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TodoTask task = (TodoTask) data.getSerializableExtra("task");
        boolean d = data.getBooleanExtra("delete",false);
        if(d)
        {
            int taskID = data.getIntExtra("taskID",0);
            if(taskID != 0)
            {
                for(int i=0;i<tasks.size();i++)
                {
                    TodoTask t = tasks.get(i);
                    if(t.id == taskID)
                    {
                        tasks.remove(i);
                        Log.d(TAG, "onActivityResult: removed");
                        break;
                    }
                }
            }
        }
        else
        {
            for(int i=0;i<tasks.size();i++)
            {
                TodoTask t = tasks.get(i);
                if(t.id == task.id)
                {
                    tasks.set(i,task);
                    break;
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}

