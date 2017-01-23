package com.usc.cargotrackingsystem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.sql.Driver;

public class LoginActivity extends AppCompatActivity {

    Button loginButton;
    EditText username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //on click login button verify
                if(login()){
                    Intent intent = new Intent(LoginActivity.this, DriverActivity.class);
                    startActivity(intent);
                }
            }
        });

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
    }

    public boolean login(){
        String user = username.getText().toString();
        String pw = password.getText().toString();

        //check if credentials are present in db

        return true;
    }
}
