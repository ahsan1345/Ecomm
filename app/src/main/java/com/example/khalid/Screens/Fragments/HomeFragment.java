package com.example.khalid.Screens.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.khalid.Screens.LoginActivity;
import com.example.khalid.Screens.ProductsActivity;
import com.example.khalid.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    FirebaseAuth myAuth = FirebaseAuth.getInstance();
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference db = firebaseDatabase.getReference();

    String Userid;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(getLayoutInflater(), container, false);
        preferences = getContext().getSharedPreferences( "myData",MODE_PRIVATE);
        editor = preferences.edit();
        Userid = preferences.getString("Userid", null);
        db.child( "Users").child(Userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String roleCheck = snapshot.child( "role").getValue().toString().trim();
                    binding.roleTextview.setText(roleCheck);
                    binding.nameTextview.setText(snapshot.child( "name").getValue().toString().trim());
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
                startActivity(new Intent(getContext(), LoginActivity.class));
                getActivity().finish();

            }
        });
        binding.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ProductsActivity.class));
            }
        });
        return binding.getRoot();
    }
}