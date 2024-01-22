package com.bzu.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bzu.auth.activity.MainScreen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.bzu.auth.Data.Database;

import java.util.Objects;


public class MainActivity1 extends AppCompatActivity {

    FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        container = findViewById(R.id.container);
        getLayoutInflater().inflate(R.layout.layout_login, container, true);

        setSignUpFromSignIn();

    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = Database.auth.getCurrentUser();
        if(user == null) {
            container.removeAllViews();
            getLayoutInflater().inflate(R.layout.layout_login, container, true);
            setSignUpFromSignIn();
        } else {
            Intent intent = new Intent(MainActivity1.this, MainScreen.class);
            startActivity(intent);
            finish();
        }
    }

    private void setSignUpFromSignIn() {
        TextView signUpFromSignIn = findViewById(R.id.signUpFromSignIn);
        EditText email = findViewById(R.id.editTextEmail);
        EditText password = findViewById(R.id.editTextPassword);
        TextView error = findViewById(R.id.errorSignUp);
        Button signInB = findViewById(R.id.cirLoginButton);

        TextView forgetPassword = findViewById(R.id.forgotPassword);

        forgetPassword.setOnClickListener(v -> {
            if(email.getText().toString().equals(""))
                error.setText("You should enter email");
            else {
                String emailTxt = email.getText().toString();
                Database.auth.sendPasswordResetEmail(emailTxt).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        error.setText("Check your email to reset your password");
                    } else {
                        error.setText("Email is wrong");
                    }
                });
            }
        });

        signInB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().equals("") || password.getText().toString().equals(""))
                    error.setText("You should enter email and password");
                else {
                    String emailTxt = email.getText().toString();
                    String passwordTxt = password.getText().toString();
                    Database.auth.signInWithEmailAndPassword(emailTxt, passwordTxt).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                saveUserOnSharedPrefs();
                                Intent intent = new Intent(MainActivity1.this, MainScreen.class);
                                MainActivity1.this.startActivity(intent);
                            } else {
                                error.setText("Email or password are wrong");
                            }
                        }
                    });
                }
            }
        });
        signUpFromSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                container.removeAllViews();
                getLayoutInflater().inflate(R.layout.layout_register, container, true);
                setSignInFromSignUp();
            }
        });
    }
    private void saveUserOnSharedPrefs() {
        FirebaseUser u = Database.auth.getCurrentUser();
        assert u != null;
        SharedPreferences sp = MainActivity1.this.getSharedPreferences("main", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Log.d("email", Objects.requireNonNull(u.getEmail()));
        editor.putString("currUser", u.getEmail());
        editor.commit();

    }
    private void setSignInFromSignUp() {
        TextView signInFromSignUp = findViewById(R.id.signInFromSignUp);
        signInFromSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                container.removeAllViews();
                getLayoutInflater().inflate(R.layout.layout_login, container, true);
                setSignUpFromSignIn();
            }
        });

        Button signUpB = findViewById(R.id.signUpB);
        EditText email = findViewById(R.id.signUpEmail);
        TextView error = findViewById(R.id.errorSignUp);
        EditText pass1 = findViewById(R.id.signUpPass);
        EditText pass2 = findViewById(R.id.repeatpass);


        signUpB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().equals("") || pass1.getText().toString().equals("") || pass2.getText().toString().equals("")){
                    error.setText("all fields are required");
                    return;
                }
                if(!pass1.getText().toString().equals(pass2.getText().toString())) {
                    error.setText("Both passwords should be the same");
                } else {
                    String emailTXT = email.getText().toString();
                    String password = pass2.getText().toString();

                    Database.auth.createUserWithEmailAndPassword(emailTXT, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                error.setText(task.getException().getMessage());
                            }
                        }
                    });
                    Database.auth.signOut();
                }
            }
        });

    }

}