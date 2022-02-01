package com.example.bugtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class FeatureActivity extends AppCompatActivity {
    private ExtendedFloatingActionButton addStep;
    //firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference docRef = db.collection("features").document("pkyJFEhWHsr4bVrguFIC");

    private int stepCounter = 0;
    String[] items = {"item1", "item2", "item3", "item4"};

    AutoCompleteTextView autoCompleteText;

    ArrayAdapter<String> adapterItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature);
        addStep = findViewById(R.id.add_step);
        autoCompleteText = findViewById(R.id.dropdown_menu);
        LinearLayout linearLayout = findViewById(R.id.root_layout);
        addStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                TextInputLayout newStep = new TextInputLayout(FeatureActivity.this);
//                newStep.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                newStep.setGravity(Gravity.CENTER);
//                TextInputEditText newStepDescription = new TextInputEditText(newStep.getContext());
                if(stepCounter < 6) {
                    View stepItem = getLayoutInflater().inflate(R.layout.step_item, null, true);

                    //stepItem.setId(stepCounter);
//                    Toast.makeText(FeatureActivity.this, "num of views: " + ((ViewGroup)linearLayout).getChildCount(), Toast.LENGTH_SHORT).show();
                    int indexOfmyBtn = linearLayout.indexOfChild(addStep);
                    linearLayout.addView(stepItem, indexOfmyBtn);
                    indexOfmyBtn = linearLayout.indexOfChild(findViewById(R.id.add_step));
//                    linearLayout.addView(this);
//                    TextInputLayout textInputLayout = (TextInputLayout) ((ViewGroup)linearLayout).getChildAt(((ViewGroup)linearLayout).getChildCount());
                    TextInputLayout textInputLayout = (TextInputLayout)((LinearLayout) (linearLayout.getChildAt(indexOfmyBtn-1 ))).getChildAt(0);
                    stepCounter++;
                    Toast.makeText(FeatureActivity.this, "c "+indexOfmyBtn, Toast.LENGTH_SHORT).show();
                    //Toast.makeText(FeatureActivity.this, "id: " + textInputLayout.getId(), Toast.LENGTH_LONG).show();

                    textInputLayout.setHint("Step " + stepCounter);
                }else{
                    Toast.makeText(FeatureActivity.this, "You've reached maximum amount of steps available!", Toast.LENGTH_LONG).show();
                }
            }
        });
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()) {
                        Toast.makeText(FeatureActivity.this, "Successful: " + document.getData(), Toast.LENGTH_SHORT).show();
                        String [] collectedItems = document.getData().values().toArray(new String[0]);
                        adapterItems = new ArrayAdapter<String>(FeatureActivity.this, R.layout.list_item, collectedItems);
                        autoCompleteText.setAdapter(adapterItems);


                        autoCompleteText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String item = parent.getItemAtPosition(position).toString();
                                Toast.makeText(getApplicationContext(), "Item "+item, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else Toast.makeText(FeatureActivity.this, "FAILURE NO DOC: ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}