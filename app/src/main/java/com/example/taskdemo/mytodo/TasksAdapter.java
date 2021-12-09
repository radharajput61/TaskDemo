package com.example.taskdemo.mytodo;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.taskdemo.R;
import com.example.taskdemo.dto.Company;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TasksViewHolder> {

    private Context mCtx;
    private List<Task> taskList;

    public TasksAdapter(Context mCtx, List<Task> taskList) {
        this.mCtx = mCtx;
        this.taskList = taskList;
    }

    @Override
    public TasksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_list, parent, false);
        return new TasksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TasksViewHolder holder, int position) {
        Task t = taskList.get(position);
        holder.tvName.setText(t.getName());
        try {
            JSONObject Companyob =  new JSONObject (t.getCompany());
            JSONObject address = new JSONObject(t.getAddress());
            JSONObject Geoob =  new JSONObject (address.getString("geo"));

            Log.v("getObjectFromString",""+getObjectFromString(t.getCompany()));

//            holder.tvName.setText(t.getName()+" workin in "+Companyob.getString("name"));
//            holder.tvCity.setText("City : "+address.getString("city")+" ("+Geoob.getString("lat")+", "+Geoob.getString("lng")+")");
        } catch (JSONException e) {
            Log.v("gdfd",""+e.getMessage());
            e.printStackTrace();
        }


    }
    public void updateList(ArrayList<Task> list){
        taskList = list;
        notifyDataSetChanged();
    }
    public List<Company> getObjectFromString(String jsonString){

        Type listType = new TypeToken<ArrayList<Company>>(){}.getType();
        List<Company> list = new Gson().fromJson(jsonString, listType);
        return list;

    }
    @Override
    public int getItemCount() {
        return taskList.size();
    }

    class TasksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvName,tvCity;

        public TasksViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvCity = itemView.findViewById(R.id.tvCity);


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
//            Task task = taskList.get(getAdapterPosition());
//
//            Intent intent = new Intent(mCtx, UpdateTaskActivity.class);
//            intent.putExtra("task", task);
//
//            mCtx.startActivity(intent);
        }
    }
}