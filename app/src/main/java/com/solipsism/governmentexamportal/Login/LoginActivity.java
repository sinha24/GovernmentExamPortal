package com.solipsism.governmentexamportal.Login;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.solipsism.governmentexamportal.MainActivity;
import com.solipsism.governmentexamportal.R;


public class LoginActivity extends AppCompatActivity {

    AutoCompleteTextView email, password;
    Button loginBtn, signUpBtn;
    String sEmail, sPassword, rPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        email = (AutoCompleteTextView) findViewById(R.id.login_email);
        password = (AutoCompleteTextView) findViewById(R.id.login_password);
        loginBtn = (Button) findViewById(R.id.login_button);
        signUpBtn = (Button) findViewById(R.id.login_signup_button);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOnline()) {
                    sEmail = email.getText().toString();
                    sPassword = password.getText().toString();
                    if (sEmail.length() > 0) {
                        if (sPassword.length() > 0) {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("Users").child(sEmail).child("password");
                            myRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // This method is called once with the initial value and again
                                    // whenever data at this location is updated.
                                    String value = dataSnapshot.getValue(String.class);
                                    rPassword = value;
                                    Log.e("Login", "Value is: " + value);
                                    if (sPassword.equals(rPassword)) {
                                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                        i.putExtra("sEmail", sEmail);
                                        LoginActivity.this.finish();
                                        startActivity(i);
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                    // Failed to read value
                                    Log.e("Login", "Failed to read value.", error.toException());
                                }
                            });

                        } else {
                            password.requestFocus();
                            password.setError("Check Password");
                        }
                    } else {
                        email.requestFocus();
                        email.setError("Check Email");
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Network isnt avialable", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}