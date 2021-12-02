package com.bill.cashmanager.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bill.cashmanager.R;
import com.bill.cashmanager.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    Button signUp;
    EditText name,email,password;
    TextView signIn;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        progressBar = findViewById(R.id.regis_progressbar);
        progressBar.setVisibility(View.GONE);

        signUp = findViewById(R.id.regis_btn);
        name = findViewById(R.id.regis_name);
        email = findViewById(R.id.regis_email);
        password = findViewById(R.id.regis_password);
        signIn = findViewById(R.id.sign_in);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this,LoginActivity.class));
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createUser();
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    private void createUser() {

        String userName = name.getText().toString();
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();

        if(TextUtils.isEmpty(userName)){
            Toast.makeText(this, "Name is Empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(userEmail)){
            Toast.makeText(this, "Email is Empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(userPassword)){
            Toast.makeText(this, "Password is Empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if(userPassword.length() < 6){
            Toast.makeText(this, "Passowrd Length must be greater then 6 letter", Toast.LENGTH_SHORT).show();
            return;
        }

        //create user
        firebaseAuth.createUserWithEmailAndPassword(userEmail,userPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            UserModel userModel = new UserModel(userName,userEmail,userPassword);
                            String id = task.getResult().getUser().getUid();
                            database.getReference().child("Users").child(id).setValue(userModel);
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RegistrationActivity.this, "Registration Succesful", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RegistrationActivity.this, "Error: "+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}