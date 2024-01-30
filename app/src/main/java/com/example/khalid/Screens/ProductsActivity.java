package com.example.khalid.Screens;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.khalid.R;
import com.example.khalid.databinding.ActivityProductsBinding;
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
    //Dialog Componets
    CircleImageView image;
    ImageButton imageAdd;
    TextView imageErrTextview;
    TextInputLayout pName, pDescription, pPrice, pStock;
    TextInputEditText pNameEditText, pDescriptionEditText, pPriceEditText, pStockEditText;
    Button cancelBtn, saveChangesBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        binding = ActivityProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferences = getSharedPreferences("myData", MODE_PRIVATE);
        editor = preferences.edit();
        Userid = preferences.getString("Userid", null);
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
        binding.addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog loaddialog = new Dialog(ProductsActivity.this);
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

                loaddialog.show();

            }
        });

        imageAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, 420);
            }

        });

        // if (uploadTask != null && uploadTask.isInProgress()) {

        //Toast.makeText(ProductsActivity.this, "Image upload in Process!!!", Toast.LENGTH_SHORT).show();

        //} else {
        //  Validation("false");

        // }


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


        } else if (!Patterns.PHONE.matcher(content).matches()) {
            pStock.setError("Enter only number!!!");
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
    protected void OnActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 420 && resultCode == RESULT_OK) {
            imageUri = data.getData();
            image.setImageURI(imageUri);
        }

    }

    private String getFileExtension(Uri uri) {

        ContentResolver or = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(or.getType(uri));


    }

    private void validation(String imageStatus) {

        boolean imageErr = false;
        if (imageStatus.equals("true")) {

            imageErr = imageValidation();
        } else {
            imageErr = imageValidation();

        }
        if ((pnameValidation() && pdescValidation() && ppriceValidation() && pstockValidation() && imageErr) == true) {
            product();

        }

    }
     private void product(){

        if(imageUri != null){

          uploadTask = mStorage.child( "Images/"+System.currentTimeMillis()+"."+getFileExtension(imageUri)).putFile(imageUri).addOnSuccessListener()
        }

     }

}




