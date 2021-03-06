package com.example.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUserActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView registerUser,banner;
    private EditText editTextFullName, editTextAge, editTextEmail, editTextpassword;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
//    private DatabaseReference myRef;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        mAuth = FirebaseAuth.getInstance();

        myRef.setValue("Hello, World!");

        banner = findViewById(R.id.banner);
        banner.setOnClickListener(this);

        registerUser = findViewById(R.id.button_register_user);
        registerUser.setOnClickListener(this);

        editTextFullName = findViewById(R.id.EtextView_full_name);
        editTextAge = findViewById(R.id.EtextView_age);
        editTextEmail = findViewById(R.id.EtextView_email);
        editTextpassword = findViewById(R.id.EtextView_password);

        progressBar = findViewById(R.id.progressBar);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.banner:
                startActivity(new Intent(this,LoginActivity.class));
                break;
            case R.id.button_register_user:
                registerUser();
                break;
        }
    }

    private void registerUser(){

        String email = editTextEmail.getText().toString().trim();
        String password = editTextpassword.getText().toString().trim();
        String fullName = editTextFullName.getText().toString().trim();
        String age = editTextAge.getText().toString().trim();

        if (fullName.isEmpty()){
            editTextFullName.setError("Full name is required!");
            editTextFullName.requestFocus();
            return;
        }

        if (age.isEmpty()){
            editTextAge.setError("Age is required!");
            editTextAge.requestFocus();
            return;
        }

        if (email.isEmpty()){
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Invalid email!");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()){
            editTextpassword.setError("Email is required!");
            editTextpassword.requestFocus();
            return;
        }

        if(password.length() < 6){
            editTextpassword.setError("Password length must be of at least 6 characters!");
            editTextpassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                 if (task.isSuccessful()){
                     User user = new User(fullName, age, email);
                     Log.i("TAG", "Registration Successful");
                     myRef.child(mAuth.getCurrentUser().getUid())
                             .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task1) {
                             Log.i("TAG", "Database Updated");
                             if (task1.isSuccessful()){
                                 Toast.makeText(RegisterUserActivity.this, "User has been registered successfully", Toast.LENGTH_LONG).show();
                             }
                             else{
                                 Toast.makeText(RegisterUserActivity.this,"Failed to Register. Please Try again! ",Toast.LENGTH_LONG).show();
                             }
                             progressBar.setVisibility(View.GONE);
                         }
                     });



                 }
                 else{
                     Toast.makeText(RegisterUserActivity.this,"Failed to Register!",Toast.LENGTH_LONG).show();
                     progressBar.setVisibility(View.GONE);
                 }
            }
        });
    }
}