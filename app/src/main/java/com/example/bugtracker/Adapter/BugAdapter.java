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
        if(TextUtils.isEmpty(bug.getImageUrl()) || bug.getImageUrl() == null) {
            //Picasso.get().load(R.drawable.icons_no_img).placeholder(R.mipmap.ic_launcher).into(holder.BugImage);
            Log.e("","SSS");
        }
        else
            Picasso.get().load(bug.getImageUrl()).placeholder(R.mipmap.ic_launcher).into(holder.bugImage);

        holder.deleteBug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("aa","aloha1"+holder.bugDescription.getText().toString());
                deleteBug(holder, bug, position);
            }
        });
        holder.viewBugs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("aa","aloha1"+holder.bugDescription.getText().toString());
                viewBugs(holder, bug, position);
            }
        });
        holder.showDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("bb","aloha2");
//                BugActivity BugActivity = new BugActivity();
                //TextInputEditText BugDescription = BugActivity.findViewById(Bug_description_text);
                //BugDescription.setText(holder.BugDescription.getText().toString());
                Intent i = new Intent(mContext, FeatureActivity.class);
                HashMap<String, Object> allData = new HashMap<>();
                allData.put("bugCategory", bug.getBugCategory());
                allData.put("bugDescription", bug.getBugDescription());
                allData.put("bugSteps", bug.getStepsDescription());
                allData.put("bugImage", bug.getImageUrl());
                i.putExtra("allData", allData);
                mContext.startActivity(i);
//                ((Activity)mContext).finish();
            }
        });
    }

    private void viewBugs(ViewHolder holder, Bug bug, int position) {
        Intent i = new Intent(mContext, DashboardActivity.class);
        HashMap<String, Object> allData = new HashMap<>();
        allData.put("isDashboardActivity", true);
        i.putExtra("allData", allData);
        mContext.startActivity(i);
    }

    private void deleteBug(@NonNull ViewHolder holder, Bug myBug, int pos){
        Log.e("TAGSG ", "delete bug");
        Task<QuerySnapshot> db = FirebaseFirestore.getInstance().collection("bugs").get();
        db.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {

                    for(QueryDocumentSnapshot snapshot : task.getResult()){
                        Bug bug = snapshot.toObject(Bug.class);
                        Log.e("DATA:: ", bug.getBugId() + "  ddd  " + myBug.getBugId());
                        if(bug.getBugId().equals(myBug.getBugId()))
                            FirebaseFirestore.getInstance().collection("bugs").document(snapshot.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bugCategory = itemView.findViewById(R.id.feature_category);
            bugDescription = itemView.findViewById(R.id.feature_description_text);
            stepsDescriptionFirst = itemView.findViewById(R.id.feature_step_one);
            bugImage = itemView.findViewById(R.id.feature_image);
            showDetails = itemView.findViewById(R.id.show_details);
            deleteBug = itemView.findViewById(R.id.delete_feature);
            viewBugs = itemView.findViewById(R.id.view_bugs);
        }
    }
}
