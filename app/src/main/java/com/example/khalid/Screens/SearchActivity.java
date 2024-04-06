package com.example.khalid.Screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.khalid.MainActivity;
import com.example.khalid.R;
import com.example.khalid.Screens.Models.ProductModel;
import com.example.khalid.databinding.ActivitySearchBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    ActivitySearchBinding binding;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference db = firebaseDatabase.getReference();
    ArrayList<ProductModel> datalist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchActivity.super.onBackPressed();
            }
        });
        binding.clearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.searchEditText.setText(null);
            }
        });
        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
       db.child( "products").addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.exists()){
                   datalist.clear();
                  for(DataSnapshot ds : snapshot.getChildren()){
                      ProductModel model = new ProductModel(
                      ds.getKey(),
                      ds.child( "pName").getValue().toString().trim(),
                      ds.child( "pPrice").getValue().toString().trim(),
                      ds.child( "pStock").getValue().toString().trim(),
                      ds.child( "pDesc").getValue().toString().trim(),
                      ds.child( "pImage").getValue().toString().trim(),
                      ds.child( "status").getValue().toString().trim()

                     );
                      datalist.add(model);

                  }
                  if(datalist.size() < 0 ){
                     binding.gridView.setVisibility(View.VISIBLE);
                     binding.searchContainer.setVisibility(View.GONE);
                      MyAdapter adapter = new MyAdapter(SearchActivity.this,datalist);
                      binding.gridView.setAdapter(adapter);
                  }

               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
    }
  public class MyAdapter extends BaseAdapter{

        Context context;

        ArrayList<ProductModel> data;

      public MyAdapter(Context context, ArrayList<ProductModel> data) {
          this.context = context;
          this.data = data;
      }

      @Override
      public int getCount() {
          return data.size();
      }

      @Override
      public Object getItem(int i) {
          return null;
      }

      @Override
      public long getItemId(int i) {
          return 0;
      }

      @Override
      public View getView(int i, View view, ViewGroup viewGroup) {
          View ProductItem = LayoutInflater.from(context).inflate(R.layout.product_listview, null);
          ImageView pImage, wishlistBtn;
          TextView pName, pRating, pStock, pPrice;
          LinearLayout options, item;
          pImage = ProductItem.findViewById(R.id.pImage);
          wishlistBtn = ProductItem.findViewById(R.id.wishlistBtn);
          pName = ProductItem.findViewById(R.id.pName);
          pRating = ProductItem.findViewById(R.id.pRating);
          pStock = ProductItem.findViewById(R.id.pStock);
          pPrice = ProductItem.findViewById(R.id.pPrice);
          options = ProductItem.findViewById(R.id.options);
          item = ProductItem.findViewById(R.id.item);

          pName.setText(data.get(i).getpName());
          pStock.setText(data.get(i).getpStock()+" Stock");
          pPrice.setText("$"+data.get(i).getpPrice());
          Glide.with(context).load(data.get(i).getpImage()).into(pImage);

          options.setVisibility(view.VISIBLE);


          return ProductItem;
      }
  }

}
