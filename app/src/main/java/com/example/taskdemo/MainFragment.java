package com.example.taskdemo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskdemo.dto.APIResponse;
import com.example.taskdemo.dto.Address;
import com.example.taskdemo.dto.Company;
import com.example.taskdemo.mytodo.DatabaseClient;
import com.example.taskdemo.mytodo.Task;
import com.example.taskdemo.mytodo.TasksAdapter;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainFragment extends Fragment {

    public static TasksAdapter adapter;
    public static List<Task> arrayList = new ArrayList<>();
    SpinKitView spin_kit;
    AppCompatButton submit;
    Boolean IsAsc = false;
    TextView order;
    EditText etSearch;
    private RecyclerView recyclerView;

    public MainFragment() {
    }

    public static void filter(String text) {
        if (arrayList != null && arrayList.size() > 0) {

            ArrayList<Task> temp = new ArrayList();
            for (int i = 0; i < arrayList.size(); i++) {
                if (text.toLowerCase(Locale.getDefault()).equalsIgnoreCase(arrayList.get(i).getName().toLowerCase(Locale.getDefault()))
                        || arrayList.get(i).getName().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))
                ) {
                    temp.add(arrayList.get(i));
                }
            }
            try {
                adapter.updateList(temp);
            } catch (Exception e) {
            }
        } else {

        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_main, container, false);
        recyclerView = v.findViewById(R.id.rvList);
        etSearch = v.findViewById(R.id.etSearch);
        order = v.findViewById(R.id.order);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortList();

            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = etSearch.getText().toString().toLowerCase(Locale.getDefault());
                filter(text);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = etSearch.getText().toString().toLowerCase(Locale.getDefault());
                filter(text);

            }
        });
        return v;
    }

    private void sortList() {
        if (IsAsc) {
            IsAsc = false;
            order.setText("Asc");
           // Collections.sort(arrayList, (lhs, rhs) -> lhs.getName().compareTo(rhs.getName()));


            Collections.sort(arrayList, new Comparator<Task>() {
                @Override
                public int compare(Task lhs, Task rhs) {
                    return lhs.getName().compareTo(rhs.getName());
                }
            });
            adapter.notifyDataSetChanged();

        } else {
            IsAsc = true;
            order.setText("Dsc");
            Collections.sort(arrayList, new Comparator<Task>() {
                @Override
                public int compare(Task lhs, Task rhs) {
                    return rhs.getName().compareTo(lhs.getName());
                }
            });
           // Collections.sort(arrayList, (lhs, rhs) -> rhs.getName().compareTo(lhs.getName()));
            adapter.notifyDataSetChanged();

        }
    }

    private void HitApi() {
        Endpointinterface service = APIClient.getRetrofitInstance().create(Endpointinterface.class);
        Call<ArrayList<APIResponse>> call = service.getAllPhotos();
        call.enqueue(new Callback<ArrayList<APIResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<APIResponse>> call, Response<ArrayList<APIResponse>> response) {
                submit.setVisibility(View.VISIBLE);
                refreshDB();
                for (int i = 0; i < response.body().size(); i++) {

                    saveData(response.body().get(i));
                    Log.v("dggs", "" + response.body().get(i));
                }
                spin_kit.setVisibility(View.GONE);
                getTasks();
                Toast.makeText(getActivity(), "data saved  tap to view log to see that ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ArrayList<APIResponse>> call, Throwable t) {
                spin_kit.setVisibility(View.GONE);
                submit.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void refreshDB() {
        new ClearDB().execute();
    }

    private void getTasks() {
        class GetTasks extends AsyncTask<Void, Void, List<Task>> {
            @Override
            protected List<Task> doInBackground(Void... voids) {
                arrayList = DatabaseClient
                        .getInstance(getActivity())
                        .getAppDatabase()
                        .taskDao()
                        .getAll();
                return arrayList;
            }

            @Override
            protected void onPostExecute(List<Task> tasks) {
                super.onPostExecute(tasks);
                adapter = new TasksAdapter(getActivity(), tasks);
                recyclerView.setAdapter(adapter);
            }
        }
        GetTasks gt = new GetTasks();
        gt.execute();
    }

    public String stringFromAddress(List<Address> list) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(list);
        return jsonString;

    }

    public String stringFromCompany(List<Company> list) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(list);
        return jsonString;

    }

    private void saveData(APIResponse apiResponse) {
        final Integer id = apiResponse.getId();
        final String Name = apiResponse.getName();
        final String UserName = apiResponse.getUsername();
        final String Email = apiResponse.getEmail();
        // List<Address> address = (List<Address>) (Object) apiResponse.getAddress();
        final String Address = "" + apiResponse.getAddress();//stringFromAddress(address) ;
        final String Phone = apiResponse.getPhone();
        final String Website = apiResponse.getWebsite();
        // List<Company> company = (List<Company>) (Object) apiResponse.getCompany();
        final String Company = "" + apiResponse.getCompany();
        class SaveTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                Task task = new Task();
                task.setTaskid(id);
                task.setName(Name);
                task.setUserName(UserName);
                task.setEmail(Email);
                task.setAddress("" + Address);
                task.setPhone(Phone);
                task.setWebsite(Website);
                task.setCompany("" + Company);
                DatabaseClient.getInstance(getActivity()).getAppDatabase()
                        .taskDao()
                        .insert(task);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

            }
        }

        SaveTask st = new SaveTask();
        st.execute();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spin_kit = view.findViewById(R.id.spin_kit);
        submit = view.findViewById(R.id.tvSubmit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spin_kit.setVisibility(View.VISIBLE);
                submit.setVisibility(View.GONE);
                HitApi();

            }
        });

    }

    public class ClearDB extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            DatabaseClient.getInstance(getActivity()).getAppDatabase().taskDao()
                    .deleteAll();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}
