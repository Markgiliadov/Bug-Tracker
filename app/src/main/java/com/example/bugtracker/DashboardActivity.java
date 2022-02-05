package com.example.bugtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.FragmentTransaction;
import android.content.Intent;
import android.drm.DrmManagerClient;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.bugtracker.Adapter.BugAdapter;
import com.example.bugtracker.Adapter.FeatureAdapter;
import com.example.bugtracker.Fragments.SearchFragment;
import com.example.bugtracker.Model.Bug;
import com.example.bugtracker.Model.Feature;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {
    private MaterialButton showDetails;
    private MaterialButton deleteFeature;
    private RecyclerView recyclerView;
    private FeatureAdapter featureAdapter;
    private BugAdapter bugAdapter;
    private MaterialAutoCompleteTextView searchBar;
    private List<Feature> mFeatures;
    private List<Bug> mBugs;
    private Button logout;
    private Button addFeature;
    private Button addBug;
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onStart() {
        super.onStart();
//        if(mFeatures != null ) {
//            mFeatures.clear();
//            readFeatures();
//        }
//        else if(mBugs != null ) {
//            mBugs.clear();
//            readBugs();
//        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        HashMap<String, Object> allData = null;
        Serializable extras = getIntent().getSerializableExtra("allData");

        addBug = findViewById(R.id.add_bug);
        addFeature = findViewById(R.id.add_feature);
        searchBar = findViewById(R.id.search_bar);


        if(extras != null) {
            allData = (HashMap<String, Object>) extras;
            Log.e("S",  allData.get("isFeatureAdapter").toString());
        }
        if (extras != null && (boolean) allData.get("isFeatureAdapter")) {
            Log.e("","NONE");
            recyclerView = findViewById(R.id.recycler_view_features);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            mBugs = new ArrayList<>();
            bugAdapter = new BugAdapter(this, mBugs, true);
            recyclerView.setAdapter(bugAdapter);
            readBugs();
        }else {
            addBug = findViewById(R.id.add_bug);
            addFeature = findViewById(R.id.add_feature);
            searchBar = findViewById(R.id.search_bar);
            //sss
            recyclerView = findViewById(R.id.recycler_view_features);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            mFeatures = new ArrayList<>();
            featureAdapter = new FeatureAdapter(this, mFeatures, true);
            recyclerView.setAdapter(featureAdapter);
            readFeatures();
            //sss
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
//                    finish();
                }
            });
            addBug.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(DashboardActivity.this, FeatureActivity.class);
                    HashMap<String, Object> allData = new HashMap<>();
                    allData.put("isDashboardActivity", true);
                    i.putExtra("allData", allData);
                    startActivity(i);
//                    finish();
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
    }

    private void readBugs() {
        Log.e("", "inside read bugs");

        Task<QuerySnapshot> db = FirebaseFirestore.getInstance().collection("bugs").get();
        db.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    if(TextUtils.isEmpty(searchBar.getText().toString())){
//                        mFeatures.clear();
                        mBugs.clear();
                        for(QueryDocumentSnapshot snapshot : task.getResult()){
                            Log.e("DATA:: ", snapshot.toString());
                            Bug bug = snapshot.toObject(Bug.class);
                            mBugs.add(bug);
                        }

                        bugAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void readFeatures() {
        Log.e("", "inside read features");

        Task<QuerySnapshot> db = FirebaseFirestore.getInstance().collection("features").get();
        db.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    if(TextUtils.isEmpty(searchBar.getText().toString())){
                    mFeatures.clear();
                    for(QueryDocumentSnapshot snapshot : task.getResult()){
                        Log.e("DATA:: ", snapshot.toString());
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
        Query query = FirebaseFirestore.getInstance().collection("features").orderBy("featureDescription").startAt(s).endAt(s + "\uf8ff");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    mFeatures.clear();
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
//                        Log.e("DATA:: ", snapshot.toString());
                        Feature feature = snapshot.toObject(Feature.class);
                        mFeatures.add(feature);
                        Log.e("fID ", feature.getFeatureId());

                    }
                    featureAdapter.notifyDataSetChanged();
                }
            }
        });

    }
}