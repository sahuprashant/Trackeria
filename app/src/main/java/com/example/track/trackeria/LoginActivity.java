package com.example.track.trackeria;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    EditText editUser,editPassword;
    ProgressBar progressbar;
    Button loginbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this,Main2Activity.class));
        }
        progressbar = findViewById(R.id.progressbar);
        editUser = findViewById(R.id.editUserName);
        editPassword = findViewById(R.id.editPassword);
        loginbutton = findViewById(R.id.loginUser);
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });

    }

    private void userLogin() {
        final String username = editUser.getText().toString();
        final String password = editPassword.getText().toString();

        if (TextUtils.isEmpty(username)) {
            editUser.setError("Please enter your username");
            editUser.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editPassword.setError("Please enter your password");
            editPassword.requestFocus();
            return;
        }
        if (username.toString().equals("user") && password.toString().equals("pass")) {
            Toast.makeText(this, "Username and password is correct", Toast.LENGTH_SHORT).show();
            User user = new User(1,"user","email","gender");
            SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
            finish();
            startActivity(new Intent(this,Main2Activity.class));
        }
    }
}
