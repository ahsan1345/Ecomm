package com.example.khalid.Screens;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.khalid.MainActivity;
import com.example.khalid.R;
import com.example.khalid.Screens.Models.ProductModel;
import com.example.khalid.databinding.ActivityProductsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.utilities.Validation;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductsActivity extends AppCompatActivity {


    ActivityProductsBinding binding;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String Userid;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference db = firebaseDatabase.getReference();
    FirebaseStorage storage;

    StorageReference mStorage;
    StorageTask uploadTask;
    Uri imageUri;
    ArrayList<ProductModel> datalist = new ArrayList<>();
    //Dialog Componets;
    Dialog loaddialog;
    CircleImageView image;
    ImageButton imageAdd;
    TextView imageErrTextview, title;
    TextInputLayout pName, pDescription, pPrice, pStock;
    TextInputEditText pNameEditText, pDescriptionEditText, pPriceEditText, pStockEditText;
    Button cancelBtn, saveChangesBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferences = getSharedPreferences("myData", MODE_PRIVATE);
        editor = preferences.edit();
        Userid = preferences.getString("Userid", null);
        mStorage = FirebaseStorage.getInstance().getReference();
        db.child("Users").child(Userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String roleCheck = snapshot.child("role").getValue().toString().trim();
                    if (roleCheck.equals("user")) {
                        binding.addProductBtn.setVisibility(View.GONE);
                    } else if (roleCheck.equals("admin")) {

                        binding.addProductBtn.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProductsActivity.super.onBackPressed();
            }
        });

        binding.addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productForm("add","");
            }
        });

     db.child( "Products").addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot snapshot) {
             if(snapshot.exists()){
                 datalist.clear();
                 for (DataSnapshot ds: snapshot.getChildren()){
                     ProductModel model = new ProductModel(ds.getKey(),ds.child( "pName").getValue().toString(),
                     ds.child( "pPrice").getValue().toString(),
                     ds.child( "pStock").getValue().toString(),
                             ds.child( "pDesc").getValue().toString(),
                             ds.child( "pImage").getValue().toString(),
                             ds.child( "status").getValue().toString()
                     );
                     datalist.add(model);
                 }
                 Collections.reverse(datalist);
                 MyAdapter adapter = new MyAdapter(ProductsActivity.this,datalist);
                 binding.gridview.setAdapter(adapter);
             }
         }

         @Override
         public void onCancelled(@NonNull DatabaseError error) {

         }
     });
    }

//    Form Dialog
    public void productForm(String purpose, String productId){
        loaddialog = new Dialog(ProductsActivity.this);
        loaddialog.setContentView(R.layout.dialog_add_product);
        loaddialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loaddialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loaddialog.getWindow().setGravity(Gravity.CENTER);
        loaddialog.setCancelable(false);
        loaddialog.setCanceledOnTouchOutside(false);
        image = loaddialog.findViewById(R.id.image);
        imageAdd = loaddialog.findViewById(R.id.imageAdd);
        pName = loaddialog.findViewById(R.id.pName);
        pDescription = loaddialog.findViewById(R.id.pDescription);
        pPrice = loaddialog.findViewById(R.id.pPrice);
        pStock = loaddialog.findViewById(R.id.pStock);
        pNameEditText = loaddialog.findViewById(R.id.pNameEditText);
        pDescriptionEditText = loaddialog.findViewById(R.id.pDescriptionEditText);
        pPriceEditText = loaddialog.findViewById(R.id.pPriceEditText);
        pStockEditText = loaddialog.findViewById(R.id.pStockEditText);
        imageErrTextview = loaddialog.findViewById(R.id.imageErrTextview);
        saveChangesBtn = loaddialog.findViewById(R.id.saveChangesBtn);
        cancelBtn = loaddialog.findViewById(R.id.cancelBtn);
        title = loaddialog.findViewById(R.id.title);


        imageAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, 420);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loaddialog.dismiss();
            }
        });

        saveChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(uploadTask != null && uploadTask.isInProgress()){
                    Toast.makeText(ProductsActivity.this, "Image Upload In Process!!!", Toast.LENGTH_SHORT).show();
                } else {
                    if(purpose.equals("add")){
                        validation("false",purpose, productId);
                    } else if(purpose.equals("edit")){
                        validation("true",purpose, productId);
                    }
                }
            }
        });

        pNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pnameValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        pDescriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pdescValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        pPriceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ppriceValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        pStockEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pstockValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if(purpose.equals("edit")){
            title.setText("Edit Product");
            db.child("Products").child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        Glide.with(ProductsActivity.this).load(snapshot.child("pImage").getValue().toString().trim()).into(image);
                        pNameEditText.setText(snapshot.child("pName").getValue().toString().trim());
                        pDescriptionEditText.setText(snapshot.child("pDesc").getValue().toString().trim());
                        pPriceEditText.setText(snapshot.child("pPrice").getValue().toString().trim());
                        pStockEditText.setText(snapshot.child("pStock").getValue().toString().trim());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        loaddialog.show();
    }

    public boolean pnameValidation() {
        String name = pNameEditText.getText().toString().trim();
        if (name.isEmpty()) {
            pName.setError("Name is Required!!!");
            return false;
        } else if (!Pattern.compile("^[a-zA-Z\\s]*$").matcher(name).matches()) {
            pName.setError("Name is only Text!!!");
            return false;
        } else {
            pName.setError(null);
            return true;
        }
    }

    public boolean pdescValidation() {
        String content = pDescriptionEditText.getText().toString().trim();
        if (content.isEmpty()) {
            pDescription.setError("Description is Required!!!");
            return false;
        } else {
            pDescription.setError(null);
            return true;
        }
    }

    public boolean ppriceValidation() {
        String content = pPriceEditText.getText().toString().trim();
        if (content.isEmpty()) {
            pPrice.setError("Price is required!!!");
            return false;
        } else if (!Patterns.PHONE.matcher(content).matches()) {
            pStock.setError("Enter Valid number!!!");
            return false;
        } else {
            pPrice.setError(null);
            return true;
        }
    }

    public boolean pstockValidation() {

        String content = pStockEditText.getText().toString().trim();
        if (content.isEmpty()) {
            pStock.setError("Stock is required!!!");
            return false;
        } else {
            pStock.setError(null);
            return true;
        }
    }

    public boolean imageValidation() {
        if (imageUri == null) {
            imageErrTextview.setText("plant image is Required!!!");
            imageErrTextview.setVisibility(View.VISIBLE);
            return false;
        } else {
            imageErrTextview.setText("");
            imageErrTextview.setVisibility(View.GONE);
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 420 && resultCode == RESULT_OK){
            imageUri = data.getData();
            image.setImageURI(imageUri);
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private void validation(String imageStatus, String purpose, String productId) {

        boolean nameErr = false, descErr = false, priceErr = false, stockErr = false, imageErr = false;
        nameErr = pnameValidation();
        descErr = pdescValidation();
        priceErr = ppriceValidation();
        stockErr = pstockValidation();
        if (imageStatus.equals("true")) {
            imageErr = true;
        } else {
            imageErr = imageValidation();
        }
        if ((nameErr && descErr && priceErr && stockErr && imageErr) == true) {
            product(purpose, productId);
        }

    }

     private void product(String purpose, String productId){
        if(imageUri != null){
            Dialog loading = new Dialog(ProductsActivity.this);
            loading.setContentView(R.layout.dialo_loading);
            loading.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            loading.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            loading.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            loading.getWindow().setGravity(Gravity.CENTER);
            loading.setCancelable(false);
            loading.setCanceledOnTouchOutside(false);
            TextView message = loading.findViewById(R.id.message);
            if(purpose.equals("edit")){
                message.setText("Modifying...");
            } else {
                message.setText("Creating...");
            }
            loading.show();
          uploadTask = mStorage.child( "Images/"+System.currentTimeMillis()+"."+getFileExtension(imageUri)).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
              @Override
              public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                  Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                  task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                      @Override
                      public void onSuccess(Uri uri) {
                          loading.dismiss();
                         String Photolink = uri.toString();

                          Dialog alertdialog = new Dialog(ProductsActivity.this);
                          alertdialog.setContentView(R.layout.dialog_sucess);
                          alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                          alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                          alertdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                          alertdialog.getWindow().setGravity(Gravity.CENTER);
                          alertdialog.setCancelable(false);
                          alertdialog.setCanceledOnTouchOutside(false);
                          TextView message = alertdialog.findViewById(R.id.message);
                          alertdialog.show();

                          if(purpose.equals("add")){
                              String uploadId = db.child("Plant").push().getKey();
                              HashMap<String, String> myData = new HashMap<String,String>();
                              myData.put("pImage","" + Photolink);
                              myData.put("pName", pNameEditText.getText().toString());
                              myData.put("pDesc", pDescriptionEditText.getText().toString());
                              myData.put("pStock", pStockEditText.getText().toString());
                              myData.put("pPrice", pPriceEditText.getText().toString());
                              myData.put("status", "1");
                              db.child( "Products").child(uploadId).setValue(myData);
                              message.setText("Product Added Successfully!!!");
                          }  else if(purpose.equals("edit")){
                              db.child("Products").child(productId).child("pImage").setValue(Photolink);
                              db.child("Products").child(productId).child("pName").setValue(pNameEditText.getText().toString().trim());
                              db.child("Products").child(productId).child("pDesc").setValue(pDescriptionEditText.getText().toString().trim());
                              db.child("Products").child(productId).child("pStock").setValue(pStockEditText.getText().toString().trim());
                              db.child("Products").child(productId).child("pPrice").setValue(pPriceEditText.getText().toString().trim());
                              message.setText("Product Edit Successfully!!!");
                          }

                          new Handler().postDelayed(new Runnable() {
                              @Override
                              public void run() {
                                  alertdialog.dismiss();
                                  loaddialog.dismiss();
                              }
                          },2000);
                      }
                  });
              }
          });
        } else {
            Dialog alertdialog = new Dialog(ProductsActivity.this);
            alertdialog.setContentView(R.layout.dialog_sucess);
            alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            alertdialog.getWindow().setGravity(Gravity.CENTER);
            alertdialog.setCancelable(false);
            alertdialog.setCanceledOnTouchOutside(false);
            TextView message = alertdialog.findViewById(R.id.message);
            message.setText("Product Edit Successfully!!!");
            alertdialog.show();

            if(purpose.equals("edit")){
                db.child("Products").child(productId).child("pName").setValue(pNameEditText.getText().toString().trim());
                db.child("Products").child(productId).child("pDesc").setValue(pDescriptionEditText.getText().toString().trim());
                db.child("Products").child(productId).child("pStock").setValue(pStockEditText.getText().toString().trim());
                db.child("Products").child(productId).child("pPrice").setValue(pPriceEditText.getText().toString().trim());
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    alertdialog.dismiss();
                    loaddialog.dismiss();
                }
            },2000);
        }

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
          // count of Adapter
          return data.size();
      }

      @Override
      public Object getItem(int position) {
          return null;
      }

      @Override
      public long getItemId(int position) {
          return 0;
      }

      @Override
      public View getView(int i, View view, ViewGroup viewGroup) {
          // Declare Product Layout
          View ProductItem = LayoutInflater.from(context).inflate(R.layout.product_listview, null);
          ImageView pImage, wishlistBtn, editBtn, deleteBtn;
          TextView pName, pRating, pStock, pPrice;
          LinearLayout options, item;
          pImage = ProductItem.findViewById(R.id.pImage);
          wishlistBtn = ProductItem.findViewById(R.id.wishlistBtn);
          editBtn = ProductItem.findViewById(R.id.editBtn);
          deleteBtn = ProductItem.findViewById(R.id.deleteBtn);
          pName = ProductItem.findViewById(R.id.pName);
          pRating = ProductItem.findViewById(R.id.pRating);
          pStock = ProductItem.findViewById(R.id.pStock);
          pPrice = ProductItem.findViewById(R.id.pPrice);
          options = ProductItem.findViewById(R.id.options);
          item = ProductItem.findViewById(R.id.item);

          // if(!data.get(i).getpDiscount().equals("0")){
          //pDiscount.setVisibility(View.VISIBLE);
          //pDiscount.setText(data.get(i).getpDiscount()+"% OFF");
          //} else {
          //pPrice.setVisibility(View.GONE);
          //}

          pName.setText(data.get(i).getpName());
          pStock.setText(data.get(i).getpStock()+" Stock");
          pPrice.setText("$"+data.get(i).getpPrice());
          Glide.with(context).load(data.get(i).getpImage()).into(pImage);

         // double discount = Double.parseDouble(data.get(i).getpDiscount())/100;
         // double calcDiscount = Double.parseDouble(data.get(i).getpPrice()) * discount;
         // double totalPrice = Double.parseDouble(data.get(i).getpPrice()) - calcDiscount;
         // pPrice.setText("$"+Math.round(totalPrice));

          options.setVisibility(view.VISIBLE);

          deleteBtn.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  Dialog loaddialog = new Dialog(context);
                  loaddialog.setContentView(R.layout.dialog_confirm);
                  loaddialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                  loaddialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                  loaddialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                  loaddialog.getWindow().setGravity(Gravity.CENTER);
                  loaddialog.setCancelable(false);
                  loaddialog.setCanceledOnTouchOutside(false);
                  Button cancelBtn, yesBtn;
                  yesBtn = loaddialog.findViewById(R.id.yesBtn);
                  cancelBtn = loaddialog.findViewById(R.id.cancelBtn);
                  cancelBtn.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View view) {
                          loaddialog.dismiss();
                      }
                  });
                  yesBtn.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View view) {
                          loaddialog.dismiss();
                          db.child("Products").child(data.get(i).getId()).removeValue();
                      }
                  });

                  loaddialog.show();
              }
          });

          db.child("Users").child(Userid).addListenerForSingleValueEvent(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {
                  if(snapshot.exists()){
                      String roleCheck = snapshot.child("role").getValue().toString().trim();
                      if(roleCheck.equals("user")){
                          options.setVisibility(View.GONE);
                      } else if(roleCheck.equals("admin")){
                          options.setVisibility(View.VISIBLE);
                      }
                  }
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {

              }
          });

          editBtn.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  productForm("edit",""+data.get(i).getId());
              }
          });

          return ProductItem;

      }
  }
}




