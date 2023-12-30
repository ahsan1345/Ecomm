package com.example.khalid.Screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.khalid.R;
import com.example.khalid.Screens.Admin.AdminDashboardActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardActivity extends AppCompatActivity {


    ImageView logout;
    FirebaseAuth myAuth = FirebaseAuth.getInstance();
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference db = firebaseDatabase.getReference();

    String Userid;

    TextView roleTextview, nameTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        preferences = getSharedPreferences( "myData",MODE_PRIVATE);
        editor = preferences.edit();
        logout = findViewById(R.id.logout);
        roleTextview = findViewById(R.id.roleTextview);
        nameTextview = findViewById(R.id.nameTextview);

        Userid = preferences.getString("Userid", null);
        db.child( "Users").child(Userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String roleCheck = snapshot.child( "role").getValue().toString().trim();
                    roleTextview.setText(roleCheck);
                    nameTextview.setText(snapshot.child( "name").getValue().toString().trim());
                    if(roleCheck.equals("admin")){
                        startActivity(new Intent(DashboardActivity.this, AdminDashboardActivity.class));
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAuth.signOut();
                editor.clear();
                editor.commit();
                startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                finish();

            }
        });

    }
}