package com.example.bugtracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.Serializable;
import java.util.HashMap;
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
    private String addBugToFeatureDocumentId;
    private String data_source;
    private String single_data_source;
    private DocumentReference featuresMenuDoc = db.collection("featuresMenu").document("va4E2IaMGqDlqLDLIFV3");
    private int stepCounter = 0;


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
        }
            if(extras != null ){
                if(allData.get("isFeatureAdapter") == null ) {
                    if (allData.get("isBugAdapter") != null) {
                        single_data_source = "bug";
                        data_source = "bugs";
                        showDetails(allData, linearLayout);
                    }
                }else if(allData.get("isBugAdapter") == null){
                    if (allData.get("isFeatureAdapter") != null) {


                        single_data_source = "feature";
                        data_source = "features";
                        showDetails(allData, linearLayout);
                    }

                }
                if (allData.get("isAddBug") != null) {
                    if ((boolean) allData.get("isAddBug")){
                        data_source = "bugs";
                        single_data_source = "bug";
                        TextInputLayout textInputLayout = new TextInputLayout(new ContextThemeWrapper(this, R.style.Widget_MaterialComponents_TextInputLayout_OutlinedBox));
                        textInputLayout.setBoxBackgroundColor(ContextCompat.getColor(this, android.R.color.black));
                        textInputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_FILLED);

                        TextInputEditText editText = new TextInputEditText(textInputLayout.getContext());
                        editText.setText("Add new Bug");
                        linearLayout.addView(editText, 0);
                        try {
                            Class.forName("dalvik.system.CloseGuard")
                                    .getMethod("setEnabled", boolean.class)
                                    .invoke(null, true);
                        } catch (ReflectiveOperationException e) {
                            throw new RuntimeException(e);
                        }
                        String featureId = (String) allData.get("featureId");
                        FirebaseFirestore.getInstance().collection("features").whereEqualTo("featureId",featureId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    if(!task.getResult().isEmpty())
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            addBugToFeatureDocumentId = document.getId();
                                            try {
                                                finalize();
                                            } catch (Throwable throwable) {
                                                throwable.printStackTrace();
                                            }
                                        }
                                } else {
                                Log.e("docs", "Error getting documents: ", task.getException());
                                }
                            }
                        });
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
                        View stepItem = getLayoutInflater().inflate(R.layout.step_item, null, false);
                        int indexOfmyBtn = linearLayout.indexOfChild(addStepLayout);
                        linearLayout.addView(stepItem, indexOfmyBtn);
                        indexOfmyBtn = linearLayout.indexOfChild(findViewById(R.id.add_step_layout));
                        TextInputLayout textInputLayout = (TextInputLayout) ((LinearLayout) (linearLayout.getChildAt(indexOfmyBtn - 1))).getChildAt(0);
                        TextInputEditText textInputEditText = findViewById(R.id.step_item);
                        stepCounter++;
                        textInputEditText.setId(stepCounter);
                        textInputLayout.setHint("Step " + stepCounter);
                        stepsDescription.put(String.valueOf(stepCounter), "");
                }
            });

            complete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    upload();
                }
            });


    }

    private void addPicture() {
        CropImage.activity().start(FeatureActivity.this);
    }
    private void showDetails(HashMap<String, Object> allData, LinearLayout linearLayout){
        complete.setVisibility(View.INVISIBLE);
        addPicture.setVisibility(View.INVISIBLE);
        addStep.setVisibility(View.INVISIBLE);
        featureDescription.setText(allData.get(single_data_source + "Description").toString());
        autoCompleteText.setText(allData.get(single_data_source + "Category").toString());
        if(allData.get(single_data_source + "Image").toString().length() > 0)
            Picasso.get().load(allData.get(single_data_source + "Image").toString()).placeholder(R.mipmap.ic_launcher).into(imageAdded);


        HashMap<String, Object> myStepsDescription = (HashMap<String, Object>) allData.get(single_data_source + "Steps");
        if(!myStepsDescription.isEmpty()) {
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
        if(data_source == "bugs") {
            for (int i = 1 ; i <= stepCounter ; i++)
                stepsDescription.put(String.valueOf(i),((TextInputEditText)findViewById(i)).getText().toString());
            db.collection("features").document(addBugToFeatureDocumentId).update("bugIds", FieldValue.arrayUnion(featureId));
        }

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