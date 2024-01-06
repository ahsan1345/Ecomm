package com.example.khalid.Screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.khalid.R;
import com.example.khalid.Screens.Admin.AdminDashboardActivity;
import com.example.khalid.Screens.Fragments.AccountFragment;
import com.example.khalid.Screens.Fragments.CardFragment;
import com.example.khalid.Screens.Fragments.FavoriteFragment;
import com.example.khalid.Screens.Fragments.HomeFragment;
import com.example.khalid.databinding.ActivityDashboardBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardActivity extends AppCompatActivity {



    FirebaseAuth myAuth = FirebaseAuth.getInstance();
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference db = firebaseDatabase.getReference();

    String Userid;


     ActivityDashboardBinding binding;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        preferences = getSharedPreferences( "myData",MODE_PRIVATE);
        editor = preferences.edit();



        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Userid = preferences.getString("Userid", null);
        db.child( "Users").child(Userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String roleCheck = snapshot.child( "role").getValue().toString().trim();
                    binding.roleTextview.setText(roleCheck);
                    binding.nameTextview.setText(snapshot.child( "name").getValue().toString().trim());
                    if(roleCheck.equals("Admin")){
                        startActivity(new Intent(DashboardActivity.this, AdminDashboardActivity.class));
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAuth.signOut();
                editor.clear();
                editor.commit();
                startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                finish();

            }
        });
       replaceFragment(new HomeFragment());
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            String title = item.getTitle().toString();
            switch (title){
                case "Home":
                    replaceFragment(new HomeFragment());
                    break;
                case "cart":
                    replaceFragment(new CardFragment());
                    break;
                case "favorite":
                    replaceFragment(new FavoriteFragment());
                    break;
                case "account":
                    replaceFragment(new AccountFragment());
                    break;
                    

            }
            return true;
        });

    }
    public void replaceFragment(Fragment fragment){

       getSupportFragmentManager().beginTransaction().replace(R.id.frame,fragment).commit();
    }
}