//package com.example.bugtracker.Fragments;
//
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import com.example.bugtracker.Adapter.FeatureAdapter;
//import com.example.bugtracker.DashboardActivity;
//import com.example.bugtracker.FeatureActivity;
//import com.example.bugtracker.Model.Feature;
//import com.example.bugtracker.R;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.android.material.textfield.MaterialAutoCompleteTextView;
//import com.google.android.material.textfield.TextInputEditText;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//public class SearchFragment extends androidx.fragment.app.Fragment {
//
//    private RecyclerView recyclerView;
//    private List<Feature> mFeatures;
//    private FeatureAdapter featureAdapter;
//    private MaterialAutoCompleteTextView searchBar;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.activity_main, container, false);
//        Log.e("", "inside on create features");
//        recyclerView = view.findViewById(R.id.recycler_view_features);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        mFeatures = new ArrayList<>();
//        featureAdapter = new FeatureAdapter(getContext(), mFeatures, true);
//        recyclerView.setAdapter(featureAdapter);
//        searchBar = view.findViewById(R.id.search_bar);
//        readFeatures();
//        return view;
//    }
//
//    private void readFeatures() {
//        Log.e("", "inside read features");
//
//        Task<QuerySnapshot> db = FirebaseFirestore.getInstance().collection("features").get();
//        db.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if(task.isSuccessful()) {
//                    if(TextUtils.isEmpty(searchBar.getText().toString())){
//                        mFeatures.clear();
//                        for(QueryDocumentSnapshot snapshot : task.getResult()){
//                            Feature feature = snapshot.toObject(Feature.class);
//                            mFeatures.add(feature);
//                        }
//
//                        featureAdapter.notifyDataSetChanged();
//                    }
//                }
//            }
//        });
//    }
//}