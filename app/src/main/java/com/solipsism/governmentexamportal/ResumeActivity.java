package com.solipsism.governmentexamportal;

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
import com.solipsism.governmentexamportal.Login.LoginActivity;
import com.solipsism.governmentexamportal.Login.SignUpActivity;

import java.util.Hashtable;
import java.util.Map;

import static com.solipsism.governmentexamportal.MainActivity.sEmail;


public class ResumeActivity extends AppCompatActivity {

    AutoCompleteTextView age, gender, marks10, marks12, marksGrad;
    Button upload;
    String sAge, sGender, sMarks10, sMarks12, sMarksGrad;
    ProgressDialog progressDialog;
    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume);


        age = (AutoCompleteTextView) findViewById(R.id.age);
        gender = (AutoCompleteTextView) findViewById(R.id.gender);
        marks10 = (AutoCompleteTextView) findViewById(R.id.marks10);
        marks12 = (AutoCompleteTextView) findViewById(R.id.marks12);
        marksGrad = (AutoCompleteTextView) findViewById(R.id.marksGrad);
        upload = (Button) findViewById(R.id.upload_btn);

        database = FirebaseDatabase.getInstance().getReference().child("Users");

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isOnline()) {
                    sAge = age.getText().toString().trim();
                    sGender = gender.getText().toString().trim();
                    sMarks10 = marks10.getText().toString().trim();
                    sMarks12 = marks12.getText().toString().trim();
                    sMarksGrad = marksGrad.getText().toString().trim();
                    if (age.length() > 0) {
                        if (gender.length() > 0) {
                            if (marks10.length() > 0) {
                                if (marks12.length() > 0) {
                                    if (marksGrad.length() > 0) {
                                        progressDialog = new ProgressDialog(ResumeActivity.this);
                                        progressDialog.setTitle("Registering..");
                                        progressDialog.setCancelable(false);
                                        progressDialog.show();
                                        Resume();
                                    } else {
                                        marksGrad.requestFocus();
                                        marksGrad.setError("Please enter Graduation Marks");
                                    }
                                } else {
                                    marks12.requestFocus();
                                    marks12.setError("Please enter 12 Marks");
                                }
                            } else {
                                marks10.requestFocus();
                                marks10.setError("Please enter 10th marks");
                            }
                        } else {
                            gender.requestFocus();
                            gender.setError("Please enter gender");
                        }
                    } else {
                        age.requestFocus();
                        age.setError("Please enter age");
                    }
                } else {
                    Toast.makeText(ResumeActivity.this, "Network isn't available", Toast.LENGTH_LONG).show();
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
        Toast.makeText(ResumeActivity.this, "Resume Uploaded", Toast.LENGTH_LONG).show();
        Intent i = new Intent(ResumeActivity.this, MainActivity.class);
        i.putExtra("sEmail",sEmail);
        this.finish();
        startActivity(i);
    }


    public void Resume() {
        Map<String, String> params = new Hashtable<>();
        params.put("age", sAge);
        params.put("gender", sGender);
        params.put("marks10", sMarks10);
        params.put("marks12", sMarks12);
        params.put("marksGrad", sMarksGrad);

        database.child(sEmail).child("resume").setValue(params).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    onGettingResponse();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(ResumeActivity.this, "Error...", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(ResumeActivity.this, "Error - " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
