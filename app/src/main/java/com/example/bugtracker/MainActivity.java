package com.example.bugtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.bugtracker.Fragments.AddFragment;
import com.example.bugtracker.Fragments.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;
    private Button addFeature;
    private Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addFeature = findViewById(R.id.add_feature);
        logout = findViewById(R.id.logout);

        //bottomNavigationView = findViewById(R.id.bottom_navigation);
//        bottomNavigationView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this, StartActivity.class));
                    finish();
                }
            });
            addFeature.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, FeatureActivity.class));
//                    finish();
                }
            });
//            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//                @Override
//                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                    switch (item.getItemId()) {
//                        case R.id.nav_home:
//                            selectorFragment = new HomeFragment();
//                            break;
//                        case R.id.nav_add:
//                            selectorFragment = null;
//                            startActivity(new Intent(MainActivity.this, FeatureActivity.class));
//                            break;
//                        default:
//                            break;
//                    }
//
//                    if (selectorFragment != null)
//                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectorFragment).commit();
//
//                    return true;
//                }
//            });
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }else {
            startActivity(new Intent(MainActivity.this, StartActivity.class));
            finish();
        }
    }


//    @Override
//    protected void onStart() {
//        super.onStart();
//        if(FirebaseAuth.getInstance().getCurrentUser() != null)
//            startActivity(new Intent());
//    }
}
