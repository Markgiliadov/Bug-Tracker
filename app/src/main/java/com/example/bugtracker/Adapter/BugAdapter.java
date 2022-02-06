package com.example.bugtracker.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bugtracker.DashboardActivity;
import com.example.bugtracker.FeatureActivity;
import com.example.bugtracker.MainActivity;
import com.example.bugtracker.Model.Bug;
import com.example.bugtracker.Model.Feature;
import com.example.bugtracker.R;
import com.example.bugtracker.StartActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BugAdapter extends RecyclerView.Adapter<BugAdapter.ViewHolder>{

    private Context mContext;
    private List<Bug> mBugs;
    private boolean isFragment;

    public BugAdapter(Context mContext, List<Bug> mBugs, boolean isFragment) {
        this.mContext = mContext;
        this.mBugs = mBugs;
        this.isFragment = isFragment;
    }

    private FirebaseUser firebaseUser;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.feature_item, parent, false);
        return new BugAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Bug bug = mBugs.get(position);
        holder.bugCategory.setText(bug.getBugCategory());
        holder.bugDescription.setText(bug.getBugDescription());
        if(!bug.getStepsDescription().isEmpty())
            holder.stepsDescriptionFirst.setText(bug.getStepsDescription().values().toArray()[0].toString());
        if(!(TextUtils.isEmpty(bug.getImageUrl()) || bug.getImageUrl() == null))
            Picasso.get().load(bug.getImageUrl()).placeholder(R.mipmap.ic_launcher).into(holder.bugImage);

        holder.deleteBug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBug(holder, bug, position);
            }
        });
        holder.showDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, FeatureActivity.class);
                HashMap<String, Object> allData = new HashMap<>();
                allData.put("isBugAdapter", true);
                allData.put("bugCategory", bug.getBugCategory());
                allData.put("bugDescription", bug.getBugDescription());
                allData.put("bugSteps", bug.getStepsDescription());
                allData.put("bugImage", bug.getImageUrl());
                i.putExtra("allData", allData);
                mContext.startActivity(i);
            }
        });
    }

    private void deleteBug(@NonNull ViewHolder holder, Bug myBug, int pos){
        Task<QuerySnapshot> db = FirebaseFirestore.getInstance().collection("bugs").get();
        db.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    //
                    for(QueryDocumentSnapshot snapshot : task.getResult()){
                        Bug bug = snapshot.toObject(Bug.class);
                        if(bug.getBugId().equals(myBug.getBugId()))
                            FirebaseFirestore.getInstance().collection("bugs").document(snapshot.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // read Features - delete bug id
//                                    FirebaseFirestore.getInstance().collection("features").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                            if(task.isSuccessful()) {
//                                                Log.e("S", "SS");
//                                                for(QueryDocumentSnapshot snapshot : task.getResult()) {
//                                                    Feature feature = snapshot.toObject(Feature.class);
//                                                    if (feature.getBugIds() != null){
//                                                        String id = snapshot.getReference().getId();
//                                                        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("features").document(id);
//
//                                                        Log.e("S", "id: " + id + " hgolderid: " + holder.itemView.getId());
//                                                    for (int i = 0; i < feature.getBugIds().size(); i++) {
//                                                        if (feature.getBugIds().get(i).equals(myBug.getBugId())) {
////                                                            Log.e("PRINTIT", " " + String.valueOf(snapshot.getReference().getId()+snapshot.getReference().getPath()));
////                                                            documentReference.update("bugIds", FieldValue.arrayRemove(40));
//
////                                                                        FirebaseFirestore.getInstance().collection("features").document(snapshot.getReference().getId().toString()).set(new String[]{"S", "S"});
////                                                                                .update(
////                                                                                "bugIds",FieldValue.arrayRemove(30));
//                                                        }
//                                                    }
//                                                }
//                                                }
//                                            }
//                                        }
//                                    });

                                    Log.d("deleted ", "DocumentSnapshot successfully deleted!");
                                    removeAt(pos);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("fail delete", "Error deleting document", e);
                                }
                            });
                    }

                }
            }
        });
    }
    @Override
    public int getItemViewType(int position){
        return position;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    public void removeAt(int position) {
        mBugs.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mBugs.size());
    }
    @Override
    public int getItemCount() {
        return mBugs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView bugCategory;
        public TextView bugDescription;
        public String bugId;
        public ImageView bugImage;
        public String publisher;
        public TextView stepsDescriptionFirst;
        public MaterialButton showDetails;
        public MaterialButton deleteBug;
        public MaterialButton viewBugs;
        public MaterialButton addBug;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bugCategory = itemView.findViewById(R.id.feature_category);
            bugDescription = itemView.findViewById(R.id.feature_description_text);
            stepsDescriptionFirst = itemView.findViewById(R.id.feature_step_one);
            bugImage = itemView.findViewById(R.id.feature_image);
            showDetails = itemView.findViewById(R.id.show_details);
            deleteBug = itemView.findViewById(R.id.delete_feature);
            viewBugs = itemView.findViewById(R.id.view_bugs);
            addBug = itemView.findViewById(R.id.add_bug);
            addBug.setVisibility(View.GONE);
            viewBugs.setVisibility(View.GONE);
        }
    }
}
