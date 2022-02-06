package com.example.bugtracker.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bugtracker.DashboardActivity;
import com.example.bugtracker.FeatureActivity;
import com.example.bugtracker.Model.Feature;
import com.example.bugtracker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class FeatureAdapter extends RecyclerView.Adapter<FeatureAdapter.ViewHolder>{

    private Context mContext;
    private List<Feature> mFeatures;
    private boolean isFragment;

    public FeatureAdapter(Context mContext, List<Feature> mFeatures, boolean isFragment) {
        this.mContext = mContext;
        this.mFeatures = mFeatures;
        this.isFragment = isFragment;
    }

    private FirebaseUser firebaseUser;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.feature_item, parent, false);
        return new FeatureAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Feature feature = mFeatures.get(position);
        holder.featureCategory.setText(feature.getFeatureCategory());
        holder.featureDescription.setText(feature.getFeatureDescription());
        if(!feature.getStepsDescription().isEmpty())
            holder.stepsDescriptionFirst.setText(feature.getStepsDescription().values().toArray()[0].toString());
        if(!(TextUtils.isEmpty(feature.getImageUrl()) || feature.getImageUrl() == null))
            Picasso.get().load(feature.getImageUrl()).placeholder(R.mipmap.ic_launcher).into(holder.featureImage);

        holder.deleteFeature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFeature(holder, feature, position);
            }
        });
        holder.viewBugs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewBugs(holder, feature, position);
            }
        });
        holder.showDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, FeatureActivity.class);
                HashMap<String, Object> allData = new HashMap<>();
                allData.put("isFeatureAdapter", true);
                allData.put("featureCategory", feature.getFeatureCategory());
                allData.put("featureDescription", feature.getFeatureDescription());
                allData.put("featureSteps", feature.getStepsDescription());
                allData.put("featureImage", feature.getImageUrl());
                i.putExtra("allData", allData);
                mContext.startActivity(i);
            }
        });

        holder.addBug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, FeatureActivity.class);
                HashMap<String, Object> allData = new HashMap<>();
                allData.put("isAddBug", true);
                allData.put("featureId", feature.getFeatureId());
                i.putExtra("allData", allData);
                mContext.startActivity(i);
            }
        });
    }

    private void viewBugs(ViewHolder holder, Feature feature, int position) {
        Intent i = new Intent(mContext, DashboardActivity.class);
        HashMap<String, Object> allData = new HashMap<>();
        allData.put("isFeatureAdapter", true);
        allData.put("featureId", feature.getFeatureId());
        i.putExtra("allData", allData);
        mContext.startActivity(i);
    }

    private void deleteFeature(@NonNull ViewHolder holder, Feature myFeature, int pos){
        Task<QuerySnapshot> db = FirebaseFirestore.getInstance().collection("features").get();
        db.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot snapshot : task.getResult()){
                        Feature feature = snapshot.toObject(Feature.class);
                        if(feature.getFeatureId().equals(myFeature.getFeatureId()))
                            FirebaseFirestore.getInstance().collection("features").document(snapshot.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
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
        mFeatures.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mFeatures.size());
    }
    @Override
    public int getItemCount() {
        return mFeatures.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView featureCategory;
        public TextView featureDescription;
        public String featureId;
        public ImageView featureImage;
        public String publisher;
        public TextView stepsDescriptionFirst;
        public MaterialButton showDetails;
        public MaterialButton deleteFeature;
        public MaterialButton addBug;
        public MaterialButton viewBugs;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            addBug = itemView.findViewById(R.id.add_bug);
            featureCategory = itemView.findViewById(R.id.feature_category);
            featureDescription = itemView.findViewById(R.id.feature_description_text);
            stepsDescriptionFirst = itemView.findViewById(R.id.feature_step_one);
            featureImage = itemView.findViewById(R.id.feature_image);
            showDetails = itemView.findViewById(R.id.show_details);
            deleteFeature = itemView.findViewById(R.id.delete_feature);
            viewBugs = itemView.findViewById(R.id.view_bugs);
        }
    }
}
