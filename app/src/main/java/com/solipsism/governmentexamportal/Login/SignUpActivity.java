package com.solipsism.governmentexamportal.Login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.solipsism.governmentexamportal.R;

import java.util.Hashtable;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    AutoCompleteTextView email, name, address, phone, password, cPassword;
    Button signUp;
    String sEmail, sName, sAddress, sPhone, sPassword, sCPassword;
    ProgressDialog progressDialog;
    DatabaseReference database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        email = (AutoCompleteTextView) findViewById(R.id.signup_email);
        name = (AutoCompleteTextView) findViewById(R.id.signup_name);
        address = (AutoCompleteTextView) findViewById(R.id.signup_address);
        phone = (AutoCompleteTextView) findViewById(R.id.signup_phone);
        password = (AutoCompleteTextView) findViewById(R.id.signup_password);
        cPassword = (AutoCompleteTextView) findViewById(R.id.signup_cpassword);
        signUp = (Button) findViewById(R.id.signup_button);

        database = FirebaseDatabase.getInstance().getReference().child("Users");

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isOnline()) {
                    sEmail = email.getText().toString().trim();
                    sName = name.getText().toString().trim();
                    sAddress = address.getText().toString().trim();
                    sPhone = phone.getText().toString().trim();
                    sPassword = password.getText().toString().trim();
                    sCPassword = cPassword.getText().toString().trim();

                    if (sEmail.length() > 0 && sEmail.contains("@")) {
                        if (sName.length() > 0) {
                            if (sAddress.length() > 0) {
                                if (sPhone.length() == 10) {
                                    if (sPassword.length() > 5) {
                                        if (sCPassword.equals(sPassword)) {
                                            progressDialog = new ProgressDialog(SignUpActivity.this);
                                            progressDialog.setTitle("Registering..");
                                            progressDialog.setCancelable(false);
                                            progressDialog.show();
                                            SignUp();
                                        } else {
                                            cPassword.requestFocus();
                                            cPassword.setError("Passwords differ");
                                        }
                                    } else {
                                        password.requestFocus();
                                        password.setError("Password should be greater than 6 letters");
                                    }

                                } else {
                                    phone.requestFocus();
                                    phone.setError("Please enter Phone number");
                                }
                            } else {
                                address.requestFocus();
                                address.setError("Please enter Address");
                            }
                        } else {
                            name.requestFocus();
                            name.setError("Please enter Name");
                        }
                    } else {
                        email.requestFocus();
                        email.setError("Please enter email id");
                    }
                } else {
                    Toast.makeText(SignUpActivity.this, "Network isn't available", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void onGettingResponse() {
        progressDialog.dismiss();
        Toast.makeText(SignUpActivity.this, "Login to continue ", Toast.LENGTH_LONG).show();
        Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
        this.finish();
        startActivity(i);
    }

    public void SignUp() {
        Map<String, String> params = new Hashtable<>();
        params.put("name", sName);
        params.put("email", sEmail);
        params.put("phone", sPhone);
        params.put("location", sAddress);
        params.put("password", sPassword);

        database.child(sEmail).setValue(params).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    onGettingResponse();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(SignUpActivity.this, "Error...", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(SignUpActivity.this, "Error - " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
