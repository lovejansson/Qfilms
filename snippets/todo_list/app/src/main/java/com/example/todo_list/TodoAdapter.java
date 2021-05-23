package com.example.todo_list;

import android.content.Context;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder>  {

    private ArrayList<String> todos = new ArrayList<String>();
    private LayoutInflater inflater;

    public TodoAdapter(Context context, ArrayList<String> todoList){
        this.todos = todoList;
        inflater = LayoutInflater.from(context);
    }
    

    class TodoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView todoDescr;
        public final ImageButton todoDelBtn;
        final TodoAdapter todoAdapter;

        public TodoViewHolder(View itemView, TodoAdapter adapter) {
            super(itemView);
            todoDescr = itemView.findViewById(R.id.text_todo);
            todoDelBtn = itemView.findViewById(R.id.delete_button);
            todoAdapter = adapter;

            todoDelBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            todos.remove(pos);
            notifyItemRemoved(pos);
            notifyItemRangeChanged(pos, todos.size());

        }
    }
    @NonNull
    @Override

    // Anropas när en ny view ska skapas i layouten om användaren scrollar.
    public TodoAdapter.TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View item = inflater.inflate(R.layout.item_todo, parent, false);
        return new TodoViewHolder(item, this);
    }


    // binder data i todos till views
    @Override
    public void onBindViewHolder(@NonNull TodoAdapter.TodoViewHolder holder, int position) {
        String text = todos.get(position);
        holder.todoDescr.setText(text);

    }

    @Override
    public int getItemCount() {
        return todos.size();
    }
}

