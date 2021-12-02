package com.bill.cashmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bill.cashmanager.MainActivity;
import com.bill.cashmanager.R;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.main_progressbar);
        progressBar.setVisibility(View.GONE);

        if(firebaseAuth.getCurrentUser() != null){
            progressBar.setVisibility(View.VISIBLE);
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
            Toast.makeText(this, "please wait you are already logged in", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void login(View view) {
        startActivity(new Intent(HomeActivity.this,LoginActivity.class));
    }

    public void registration(View view) {
        startActivity(new Intent(HomeActivity.this,RegistrationActivity.class));
    }
}