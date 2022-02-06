package com.example.bugtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.bugtracker.Adapter.BugAdapter;
import com.example.bugtracker.Adapter.FeatureAdapter;
import com.example.bugtracker.Model.Bug;
import com.example.bugtracker.Model.Feature;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {
    private MaterialButton showDetails;
    private MaterialButton deleteFeature;
    private RecyclerView recyclerView;
    private RelativeLayout recyclerHolder;
    private FeatureAdapter featureAdapter;
    private BugAdapter bugAdapter;
    private MaterialAutoCompleteTextView searchBar;
    private List<Feature> mFeatures;
    private List<Bug> mBugs;
    private Button logout;
    private Button addFeature;
    boolean isToggled = false;
    private ExtendedFloatingActionButton toggleAll;
    //firebase
    private String data_source;
    private String single_data_source;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        HashMap<String, Object> allData = null;
        Serializable extras = getIntent().getSerializableExtra("allData");

        toggleAll = findViewById(R.id.toggle_all);
        addFeature = findViewById(R.id.add_feature);
        searchBar = findViewById(R.id.search_bar);
        recyclerHolder = findViewById(R.id.recycler_holder);
        recyclerView = findViewById(R.id.recycler_view_features);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(extras != null) {
            allData = (HashMap<String, Object>) extras;
        }
        if (extras != null && (boolean) allData.get("isFeatureAdapter")) {
            data_source = "bugs";
            single_data_source = "bug";
            mBugs = new ArrayList<>();
            bugAdapter = new BugAdapter(this, mBugs, true);
            recyclerView.setAdapter(bugAdapter);
            readBugs((String) allData.get("featureId"));

        }else {
            data_source = "features";
            single_data_source = "feature";
            mFeatures = new ArrayList<>();
            featureAdapter = new FeatureAdapter(this, mFeatures, true);
            recyclerView.setAdapter(featureAdapter);
            readFeatures();
        }
            logout = findViewById(R.id.logout);
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(DashboardActivity.this, MainActivity.class));
                    finish();
                }
            });

            addFeature.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(DashboardActivity.this, FeatureActivity.class));
                }
            });
        toggleAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!isToggled) {
                        isToggled = true;
                        mFeatures.clear();
                        toggleAll.setIcon(getResources().getDrawable(R.drawable.icons_below));
                        toggleAll.setText("features");
                        data_source = "bugs";
                        single_data_source = "bug";
                        mBugs = new ArrayList<>();
                        bugAdapter = new BugAdapter(DashboardActivity.this, mBugs, true);
                        recyclerView.setAdapter(bugAdapter);
                        readBugs();
                    } else {
                        isToggled = false;
                        mBugs.clear();
                        toggleAll.setIcon(getResources().getDrawable(R.drawable.icons_upward_arrow));
                        toggleAll.setText("all bugs");
                        data_source = "features";
                        single_data_source = "feature";
                        mFeatures = new ArrayList<>();
                        featureAdapter = new FeatureAdapter(DashboardActivity.this, mFeatures, true);
                        recyclerView.setAdapter(featureAdapter);
                        readFeatures();
                    }
                }
            });

            searchBar.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    searchFeature(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

    }

    private void readBugs(){
        Task<QuerySnapshot> db = FirebaseFirestore.getInstance().collection("bugs").orderBy("bugDescription").get();
        db.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    if(TextUtils.isEmpty(searchBar.getText().toString())){
                        mBugs.clear();
                        for(QueryDocumentSnapshot snapshot : task.getResult()){
                            Bug bug = snapshot.toObject(Bug.class);
                            mBugs.add(bug);
                        }
                        bugAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }
    private void readBugs(String featureId) {
        db.collection("features").whereEqualTo("featureId",featureId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    HashMap<String , Object> tempBugHolder = (HashMap<String , Object>) task.getResult().getDocuments().get(0).getData();
                    ArrayList bugsIds = new ArrayList();
                    tempBugHolder.forEach((s, o) -> {
                        if (s.equals("bugIds"))
                            bugsIds.addAll((Collection) o);
                    });
                    //read bugs
                    FirebaseFirestore.getInstance().collection("bugs").orderBy("bugDescription").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {
                                if(TextUtils.isEmpty(searchBar.getText().toString())){
                                    mBugs.clear();
                                    for (Object bugsId : bugsIds) {
                                        for(QueryDocumentSnapshot snapshot : task.getResult()){
                                            Bug bug = snapshot.toObject(Bug.class);
                                            if(bugsId.equals(bug.getBugId()))
                                                mBugs.add(bug);
                                        }
                                    }
                                    bugAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
                }
            }
        });


    }

    private void readFeatures() {
        Task<QuerySnapshot> db = FirebaseFirestore.getInstance().collection("features").orderBy("featureDescription").get();
        db.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    if(TextUtils.isEmpty(searchBar.getText().toString())){
                    mFeatures.clear();
                    for(QueryDocumentSnapshot snapshot : task.getResult()){
                        Feature feature = snapshot.toObject(Feature.class);
                        mFeatures.add(feature);
                    }
                    featureAdapter.notifyDataSetChanged();
                }
                }
            }
        });
    }

    private void searchFeature(String s){
        final Object[] obj = new Object[1];
        Query query = FirebaseFirestore.getInstance().collection(data_source).orderBy(single_data_source + "Description").startAt(s).endAt(s + "\uf8ff");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    if(single_data_source == "feature")
                        mFeatures.clear();
                    else
                        mBugs.clear();
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        if(single_data_source == "feature") {
                            obj[0] = snapshot.toObject(Feature.class);
                            mFeatures.add((Feature) obj[0]);
                        }
                        else {
                            obj[0] = snapshot.toObject(Bug.class);
                            mBugs.add((Bug) obj[0]);
                        }
                    }
                    if(single_data_source == "feature")
                        featureAdapter.notifyDataSetChanged();
                    else
                        bugAdapter.notifyDataSetChanged();
                }
            }
        });

    }
}