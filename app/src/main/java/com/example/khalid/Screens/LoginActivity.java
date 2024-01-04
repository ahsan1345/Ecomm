package com.example.khalid.Screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.khalid.R;
import com.example.khalid.Screens.Admin.AdminDashboardActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
      TextView Signup, forgotBtn;
    TextInputLayout emaillayout, passwordlayout;
    TextInputEditText  emailEdittext, passwordEdittext;
    Button loginbtn;


    CheckBox rememberMe;
    FirebaseAuth myAuth = FirebaseAuth.getInstance();
    SharedPreferences SharedPreferences;
    SharedPreferences.Editor editor;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference db = firebaseDatabase.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences = getSharedPreferences(  "myData",MODE_PRIVATE);
        editor = SharedPreferences.edit();
        Signup = findViewById(R.id.Sinup);
        emaillayout = findViewById(R.id.emaillayout);
        passwordlayout = findViewById(R.id.passwordlayout);
        emailEdittext = findViewById(R.id.emailEdittext);
        passwordEdittext = findViewById(R.id.passwordEdittext);
        rememberMe = findViewById(R.id.rememberMe);
        forgotBtn = findViewById(R.id.forgotBtn);
        loginbtn = findViewById(R.id.loginbtn);
        
        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });
        if (SharedPreferences.getString("loginStatus", "").equals("true")){

            startActivity(new Intent(LoginActivity.this,DashboardActivity.class));
            finish();

        }





        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
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

    public void validation(){
        boolean emailErr = false, passwordErr = false;
        emailErr = emailValidation();
        passwordErr = passwordValidation();
        if((emailErr && passwordErr) == true){
            Dialog loaddialog = new Dialog(LoginActivity.this);
            loaddialog.setContentView(R.layout.dialo_loading);
            loaddialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            loaddialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            loaddialog.getWindow().setGravity(Gravity.CENTER);
            loaddialog.setCancelable(false);
            loaddialog.setCanceledOnTouchOutside(false);
            TextView message = loaddialog.findViewById(R.id.message);
            message.setText("Login...");
            loaddialog.show();

            // Signup Here

            myAuth.signInWithEmailAndPassword(emailEdittext.getText().toString().trim(),passwordEdittext.getText().toString().trim())
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            loaddialog.dismiss();

                            FirebaseUser User = myAuth.getCurrentUser();
                            db.child( "Users").child(User.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        String role = snapshot.child("role").getValue().toString().trim();
                                       String status = snapshot.child( "status").getValue().toString().trim();
                                       if(status.equals("1")){
                                           Dialog alertdialog = new Dialog(LoginActivity.this);
                                           alertdialog.setContentView(R.layout.dialog_sucess);
                                           alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                           alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                           alertdialog.getWindow().setGravity(Gravity.CENTER);
                                           alertdialog.setCancelable(false);
                                           alertdialog.setCanceledOnTouchOutside(false);
                                           TextView message = alertdialog.findViewById(R.id.message);
                                           message.setText("Login Successfully!!!");
                                           alertdialog.show();

                                           if (rememberMe.isChecked()){
                                               editor.putString("loginStatus","true");
                                               editor.putString("Userid" ,User.getUid());
                                               editor.putString("role",role);
                                               editor.commit();
                                           }else {
                                               editor.putString("loginStatus","false");
                                               editor.commit();
                                           }



                                           new Handler().postDelayed(new Runnable() {
                                               @Override
                                               public void run() {
                                                   alertdialog.dismiss();
                                                   if(role.equals("User")){
                                                       startActivity(new Intent( LoginActivity.this, DashboardActivity.class));
                                                       finish();
                                                   }  else if(role.equals("Admin")){
                                                       startActivity(new Intent( LoginActivity.this, AdminDashboardActivity.class));
                                                       finish();
                                                   }
                                               }
                                           }, 2000);

                                       }else if(status.equals("0")){
                                           Dialog alertdialog = new Dialog(LoginActivity.this);
                                           alertdialog.setContentView(R.layout.dialog_error);
                                           alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                           alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                           alertdialog.getWindow().setGravity(Gravity.CENTER);
                                           alertdialog.setCancelable(false);
                                           alertdialog.setCanceledOnTouchOutside(false);
                                           TextView message = alertdialog.findViewById(R.id.message);
                                           message.setText("Your Account has been suspended by Admin!!!");
                                           alertdialog.show();

                                           new Handler().postDelayed(new Runnable() {
                                               @Override
                                               public void run() {
                                                   alertdialog.dismiss();
                                               }
                                           }, 2000);

                                       }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });



                            //SignupActivity.super.onBackPressed();
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loaddialog.dismiss();
                            Dialog alertdialog = new Dialog(LoginActivity.this);
                            alertdialog.setContentView(R.layout.dialog_error);
                            alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                            alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                            alertdialog.getWindow().setGravity(Gravity.CENTER);
                            alertdialog.setCancelable(false);
                            alertdialog.setCanceledOnTouchOutside(false);
                            TextView message = alertdialog.findViewById(R.id.message);
                            message.setText("Your Email OR Password is Wrong!!!");
                            alertdialog.show();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    alertdialog.dismiss();
                                }
                            }, 2000);


                        }
                    });


        }
    }
}