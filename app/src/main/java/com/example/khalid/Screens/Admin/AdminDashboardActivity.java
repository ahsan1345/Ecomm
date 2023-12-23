package com.example.khalid.Screens.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.khalid.R;
import com.example.khalid.Screens.DashboardActivity;
import com.example.khalid.Screens.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminDashboardActivity extends AppCompatActivity {

    ImageView logout;
    FirebaseAuth myAuth = FirebaseAuth.getInstance();
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference db = firebaseDatabase.getReference();

    String Userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        preferences = getSharedPreferences( "myData",MODE_PRIVATE);
        editor = preferences.edit();
        logout = findViewById(R.id.logout);
        Userid = preferences.getString("Userid", null);
        db.child( "Users").child(Userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String roleCheck = snapshot.child( "role").getValue().toString().trim();
                    if(roleCheck.equals("User")){
                        startActivity(new Intent(AdminDashboardActivity.this, DashboardActivity.class));
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
                startActivity(new Intent(AdminDashboardActivity.this, LoginActivity.class));
                finish();

            }
        });

    }
}