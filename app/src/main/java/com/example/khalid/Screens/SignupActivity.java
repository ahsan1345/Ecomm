package com.example.khalid.Screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.khalid.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {
      ImageView backbtn;
      TextInputLayout namelayout, emaillayout, passwordlayout, cpasswordlayout;
      TextInputEditText nameEdittext, emailEdittext, passwordEdittext, cpasswordEdittext;
      Button submitbtn;
      TextView loginbtn;

       FirebaseAuth myAuth = FirebaseAuth.getInstance();
       FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
       DatabaseReference db = firebaseDatabase.getReference();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        backbtn = findViewById(R.id.backbtn);
        namelayout = findViewById(R.id.namelayout);
        emaillayout = findViewById(R.id.emaillayout);
        passwordlayout = findViewById(R.id.passwordlayout);
        cpasswordlayout = findViewById(R.id.cpasswordlayout);
        nameEdittext = findViewById(R.id.nameEdittext);
        emailEdittext = findViewById(R.id.emailEdittext);
        passwordEdittext = findViewById(R.id.passwordEdittext);
        cpasswordEdittext = findViewById(R.id.cpasswordEdittext);
        loginbtn = findViewById(R.id.loginbtn);
        submitbtn = findViewById(R.id.submitbtn);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignupActivity.super.onBackPressed();
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignupActivity.super.onBackPressed();
            }
        });
        nameEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                 nameValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        emailEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        passwordEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        cpasswordEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               cpasswordValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
            }
        });

    }
    public boolean nameValidation(){
        String input = nameEdittext.getText().toString().trim();
        if(input.equals("")){
            namelayout.setError("Name is Required!!!");
            return false;
        } else if(!Pattern.compile("^[a-zA-Z\\s]*$").matcher(input).matches()){
            namelayout.setError("Name In Only Text!!!");
            return false;
        } else if(input.length() < 3){
            namelayout.setError("Name at least 3 characters!!!");
            return false;
        } else {
            namelayout.setError(null);
            return true;
        }
    }
    public boolean emailValidation(){
        String input = emailEdittext.getText().toString().trim();
        String pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if(input.equals("")){
            emaillayout.setError("Email Address is Required!!!");
            return false;
        } else if(!input.matches(pattern)){
            emaillayout.setError("Enter Valid Email Address!!!");
            return false;
        } else {
            emaillayout.setError(null);
            return true;
        }

    }
    public boolean passwordValidation(){
        String input = passwordEdittext.getText().toString().trim();
        if(input.equals("")){
            passwordlayout.setError("password is Requried!!!");
            return false;
        }else  if(input.length() < 8){
            passwordlayout.setError("password at least 8 character!!!");
            return false;
        }else {
            passwordlayout.setError(null);
            return true;
        }

    }

    public boolean cpasswordValidation(){
        String input = cpasswordEdittext.getText().toString().trim();
        String input2 = passwordEdittext.getText().toString().trim();
        if(input.equals("")){
            cpasswordlayout.setError(" confirm password is Requried!!!");
            return false;
        } else  if(input.length() < 8){
            cpasswordlayout.setError(" confirm password at least 8 character!!!");
            return false;
        } else  if(!input.equals(input2)){
            cpasswordlayout.setError(" confirm password is not matched!!!");
            return false;
        } else {
            cpasswordlayout.setError(null);
            return true;
        }

    }

    public void validation(){
        boolean nameErr = false, emailErr = false, passwordErr = false, cpasswordErr = false;
           nameErr = nameValidation();
        emailErr = emailValidation();
        passwordErr = passwordValidation();
        cpasswordErr = cpasswordValidation();
            if((nameErr && emailErr && passwordErr && cpasswordErr) == true){
                Dialog loaddialog = new Dialog(SignupActivity.this);
                loaddialog.setContentView(R.layout.dialo_loading);
                loaddialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                loaddialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                loaddialog.getWindow().setGravity(Gravity.CENTER);
                loaddialog.setCancelable(false);
                loaddialog.setCanceledOnTouchOutside(false);
                TextView message = loaddialog.findViewById(R.id.message);
                message.setText("Creating...");
                loaddialog.show();



                // Signup Here

                myAuth.createUserWithEmailAndPassword(emailEdittext.getText().toString().trim(),passwordEdittext.getText().toString().trim())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                  loaddialog.dismiss();
                                Dialog alertdialog = new Dialog(SignupActivity.this);
                                alertdialog.setContentView(R.layout.dialog_sucess);
                                alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                alertdialog.getWindow().setGravity(Gravity.CENTER);
                                alertdialog.setCancelable(false);
                                alertdialog.setCanceledOnTouchOutside(false);

                                alertdialog.show();


                                FirebaseUser User = myAuth.getCurrentUser();

                                HashMap<String,String> obj = new HashMap<String,String>();
                                obj.put("name",nameEdittext.getText().toString().trim());
                                obj.put("email",emailEdittext.getText().toString().trim());
                                obj.put("role","User");
                                obj.put("status","1");

                                db.child( "Users").child(User.getUid()).setValue(obj);

                                   new Handler().postDelayed(new Runnable() {
                                       @Override
                                       public void run() {
                                           alertdialog.dismiss();
                                           SignupActivity.super.onBackPressed();
                                       }
                                   }, 2000);
                                //SignupActivity.super.onBackPressed();
                            }
                        })

                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loaddialog.dismiss();
                                Dialog alertdialog = new Dialog(SignupActivity.this);
                                alertdialog.setContentView(R.layout.dialog_error);
                                alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                alertdialog.getWindow().setGravity(Gravity.CENTER);
                                alertdialog.setCancelable(false);
                                alertdialog.setCanceledOnTouchOutside(false);
                                 TextView message = alertdialog.findViewById(R.id.message);
                                 message.setText("Your is Already Exist");
                                alertdialog.show();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        alertdialog.dismiss();
                                        SignupActivity.super.onBackPressed();
                                    }
                                }, 2000);


                            }
                        });


        }
    }

}