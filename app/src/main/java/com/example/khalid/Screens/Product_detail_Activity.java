package com.example.khalid.Screens;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.khalid.R;
import com.example.khalid.databinding.ActivityProductDetailBinding;
import com.example.khalid.databinding.ActivityProductsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Product_detail_Activity extends AppCompatActivity {

   ActivityProductDetailBinding binding;
   String PID = "";
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference db = firebaseDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle extra = getIntent().getExtras();
        if(extra != null){
            PID = extra.getString( "pid");

        }
        db.child( "Products").child(PID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Glide.with(Product_detail_Activity.this).load(snapshot.child( "pImage").getValue().toString().trim()).into(binding.pImage);
                    binding.pName.setText(snapshot.child( "pName").getValue().toString().trim());
                    binding.pStock.setText(snapshot.child( "pStock").getValue().toString().trim()+ " Stock");
                    binding.pDesc.setText(snapshot.child( "pDesc").getValue().toString().trim());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Product_detail_Activity.super.onBackPressed();
            }
        });

    }
}