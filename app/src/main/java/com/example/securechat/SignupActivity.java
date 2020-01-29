package com.example.securechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    public EditText editTextEmailSu, editTextPwdSu, editTextNameSu, editTextMobileSu;
    public Button buttonSignup;
    public TextView textViewLogin;
    public Toolbar toolbarSignup;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private static final String TAG = "SignupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        editTextEmailSu = findViewById(R.id.editTextEmailSu);
        editTextPwdSu = findViewById(R.id.editTextPwdSu);
        editTextNameSu = findViewById(R.id.editTextName);
        editTextMobileSu = findViewById(R.id.editTextMobileSu);
        textViewLogin = findViewById(R.id.textViewLogin);
        buttonSignup = findViewById(R.id.buttonSignup);
        toolbarSignup = findViewById(R.id.toolbarSignup);

        setSupportActionBar(toolbarSignup);

        buttonSignup.setOnClickListener(this);
        textViewLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSignup: {
                signupUser();
                break;
            }
            case R.id.textViewLogin: {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    private void signupUser() {
        final String email = editTextEmailSu.getText().toString();
        String password = editTextPwdSu.getText().toString();
        final String name = editTextNameSu.getText().toString();
        final String mobile = editTextMobileSu.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, "Signup Success", Toast.LENGTH_SHORT).show();
                            String uid = mAuth.getCurrentUser().getUid();
                            User newUser = new User(name, email, mobile);
                            mDatabase.child("users").child(uid).setValue(newUser);


                            Intent intent = new Intent(SignupActivity.this, ChatListActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast.makeText(SignupActivity.this, "Oops, Something went wrong!", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }
}
