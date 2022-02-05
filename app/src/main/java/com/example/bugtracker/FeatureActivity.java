package com.example.bugtracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeatureActivity extends AppCompatActivity {

    private String imageUrl;
    private ExtendedFloatingActionButton addStep;
    private ExtendedFloatingActionButton addPicture;
    private ExtendedFloatingActionButton complete;
    private LinearLayout addStepLayout;
    private Uri imageUri;
    private ImageView imageAdded;
    private String menuItemChosen;
    private TextInputEditText featureDescription;
    private ProgressDialog pd;
    private Map<String, Object> stepsDescription;
    //firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String data_source;
    private String single_data_source;
    private DocumentReference featuresMenuDoc = db.collection("featuresMenu").document("va4E2IaMGqDlqLDLIFV3");
    private int stepCounter = 0;
    String[] items = {"item1", "item2", "item3", "item4"};


    AutoCompleteTextView autoCompleteText;
    ArrayAdapter<String> adapterItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature);
        stepsDescription = new HashMap<>();
        imageAdded = findViewById(R.id.image_added);
        addPicture = findViewById(R.id.add_picture);
        addStepLayout = findViewById(R.id.add_step_layout);
        complete = findViewById(R.id.complete);
        addStep = findViewById(R.id.add_step);
        autoCompleteText = findViewById(R.id.dropdown_menu);
        featureDescription = findViewById(R.id.feature_description_text);
        LinearLayout linearLayout = findViewById(R.id.root_layout);
        HashMap<String, Object> allData = null;
        Serializable extras = getIntent().getSerializableExtra("allData");
        if(extras != null) {
            allData = (HashMap<String, Object>) extras;
            if(allData.get("isDashboardActivity") != null)
                Log.e("S",  allData.get("isDashboardActivity").toString());
        }
        if (extras != null && allData.get("isDashboardActivity") == null ) {

            complete.setVisibility(View.INVISIBLE);
            addPicture.setVisibility(View.INVISIBLE);
            addStep.setVisibility(View.INVISIBLE);


            featureDescription.setText(allData.get("featureDescription").toString());
            autoCompleteText.setText(allData.get("featureCategory").toString());
            if(allData.get("featureImage").toString().length() > 0)
                Picasso.get().load(allData.get("featureImage").toString()).placeholder(R.mipmap.ic_launcher).into(imageAdded);


            HashMap<String, Object> myStepsDescription = (HashMap<String, Object>) allData.get("featureSteps");
            if(!myStepsDescription.isEmpty()) {
                Log.e("", (String) myStepsDescription.get("1"));
                for (int i = 1; i <= myStepsDescription.size(); i++) {
                    int index = linearLayout.indexOfChild(addStepLayout);
                    View stepItem = getLayoutInflater().inflate(R.layout.step_item, null, false);
                    linearLayout.addView(stepItem, index);
                    TextInputEditText textInputEditText = findViewById(R.id.step_item);
                    textInputEditText.setId(i);
                    TextInputLayout textInputLayout = (TextInputLayout) (textInputEditText.getParent().getParent());
                    textInputLayout.setHint("Step " + i);
                    textInputEditText.setText(myStepsDescription.get(String.valueOf(i)).toString());
                }
            }
        } else {
            if(extras != null ){
                if (allData.get("isDashboardActivity") != null) {
                    if ((boolean) allData.get("isDashboardActivity")){
                        data_source = "bugs";
                        single_data_source = "bug";
                    }
                }
            }
            else {
                data_source = "features";
                single_data_source = "feature";
            }
            //Menu selection
            fetchFeaturesMenuData();
            addPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addPicture();
                }
            });
            addStep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //if (stepCounter <= 8) {
                        View stepItem = getLayoutInflater().inflate(R.layout.step_item, null, false);
                        int indexOfmyBtn = linearLayout.indexOfChild(addStepLayout);
                        linearLayout.addView(stepItem, indexOfmyBtn);
                        indexOfmyBtn = linearLayout.indexOfChild(findViewById(R.id.add_step_layout));
                        // Toast.makeText(FeatureActivity.this, "c "+indexOfmyBtn, Toast.LENGTH_SHORT).show();
                        TextInputLayout textInputLayout = (TextInputLayout) ((LinearLayout) (linearLayout.getChildAt(indexOfmyBtn - 1))).getChildAt(0);
                        TextInputEditText textInputEditText = findViewById(R.id.step_item);
                        stepCounter++;
                        textInputEditText.setId(stepCounter);
                        textInputLayout.setHint("Step " + stepCounter);
                        stepsDescription.put(String.valueOf(stepCounter), "");
//                    } else {
//                        Toast.makeText(FeatureActivity.this, "You've reached maximum amount of steps available!" + stepsDescription.toString(), Toast.LENGTH_LONG).show();
//                    }
                }
            });

            complete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    upload();
                }
            });

        }
    }

    private void addPicture() {
        CropImage.activity().start(FeatureActivity.this);
    }

    private void fetchFeaturesMenuData(){
        featuresMenuDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                                menuItemChosen = parent.getItemAtPosition(position).toString();
                                Toast.makeText(getApplicationContext(), "Item "+menuItemChosen, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else Toast.makeText(FeatureActivity.this, "FAILURE NO DOC: ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void upload() {
        pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();
        if(imageUri != null){
            StorageReference filePth = FirebaseStorage.getInstance().getReference(data_source).child(System.currentTimeMillis() + "." + getFileExtensions(imageUri));
            StorageTask uploadTask = filePth.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return filePth.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri downloadUri = task.getResult();
                    imageUrl = downloadUri.toString();
                    addDataToFS();

                }
            });
        } else {
            imageUrl = "";
            addDataToFS();
        }
    }
    private void addDataToFS(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String featureId = db.collection(data_source).document().getId();
        HashMap<String, Object> map = new HashMap<>();
        map.put(single_data_source + "Id", featureId);
        map.put("imageUrl", imageUrl);
        if(TextUtils.isEmpty(menuItemChosen))
            menuItemChosen = "";
        map.put(single_data_source + "Category", menuItemChosen);
        map.put(single_data_source + "Description", featureDescription.getText().toString());
        map.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());

        for (int i = 1 ; i <= stepCounter ; i++)
            stepsDescription.put(String.valueOf(i),((TextInputEditText)findViewById(i)).getText().toString());
        map.put("stepsDescription", stepsDescription);
        Toast.makeText(FeatureActivity.this, "DONE !"+stepsDescription.toString(), Toast.LENGTH_LONG).show();
        db.collection(data_source).add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()){
                    pd.dismiss();
                    Toast.makeText(FeatureActivity.this, "Successful(firestore)!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(FeatureActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(FeatureActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtensions(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            imageAdded.setImageURI(imageUri);
        }else{
            Toast.makeText(this, "Try again!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(FeatureActivity.this, FeatureActivity.class));
            finish();
        }

    }
}