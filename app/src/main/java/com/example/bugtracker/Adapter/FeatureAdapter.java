package com.example.bugtracker.Adapter;

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
import com.example.bugtracker.MainActivity;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Feature feature = mFeatures.get(position);
        holder.featureCategory.setText(feature.getFeatureCategory());
        holder.featureDescription.setText(feature.getFeatureDescription());
        holder.stepsDescriptionFirst.setText(feature.getStepsDescription().values().toArray()[0].toString());
        if(TextUtils.isEmpty(feature.getImageUrl()) || feature.getImageUrl() == null) {
            //Picasso.get().load(R.drawable.icons_no_img).placeholder(R.mipmap.ic_launcher).into(holder.featureImage);
            Log.e("","SSS");
        }
        else
            Picasso.get().load(feature.getImageUrl()).placeholder(R.mipmap.ic_launcher).into(holder.featureImage);

        holder.deleteFeature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("aa","aloha1"+holder.featureDescription.getText().toString());
                deleteFeature(holder, feature, position);
            }
        });
        holder.showDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("bb","aloha2");

            }
        });
    }

    private void deleteFeature(@NonNull ViewHolder holder, Feature myFeature, int pos){
        Task<QuerySnapshot> db = FirebaseFirestore.getInstance().collection("features").get();
        db.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {

                    for(QueryDocumentSnapshot snapshot : task.getResult()){
                        Feature feature = snapshot.toObject(Feature.class);
                        Log.e("DATA:: ", feature.getFeatureId() + "  ddd  " + myFeature.getFeatureId());
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            featureCategory = itemView.findViewById(R.id.feature_category);
            featureDescription = itemView.findViewById(R.id.feature_description_text);
            stepsDescriptionFirst = itemView.findViewById(R.id.feature_step_one);
            featureImage = itemView.findViewById(R.id.feature_image);
            showDetails = itemView.findViewById(R.id.show_details);
            deleteFeature = itemView.findViewById(R.id.delete_feature);
        }
    }
}
