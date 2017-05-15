package com.solipsism.governmentexamportal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.solipsism.governmentexamportal.Login.LoginActivity;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ListView listview;
    String sName, sAddress, sPhone, sPassword, sAge, sGender, sMarks10, sMarks12, sMarksGrad, eAge, eDesc, eGender, eMarks10, eMarks12, eMarksGrad;
    boolean resume = false;
    static String sEmail;
    ArrayList<Exam> dataList;
    ArrayList<String> titleList;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataList = new ArrayList<>();
        titleList = new ArrayList<>();
        progressDialog = new ProgressDialog(MainActivity.this);
//        progressDialog.setCancelable(false);
//        progressDialog.setTitle("Loading");
//        progressDialog.setMessage("Fetching Data");

        final Intent i = getIntent();
        sEmail = i.getStringExtra("sEmail");
        Log.e("sEmail", "" + sEmail);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(sEmail);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                sName = dataSnapshot.child("name").getValue(String.class);
                sAddress = dataSnapshot.child("location").getValue(String.class);
                sPassword = dataSnapshot.child("password").getValue(String.class);
                sPhone = dataSnapshot.child("phone").getValue(String.class);
                if (dataSnapshot.hasChild("resume")) {
                    Log.e("resume", "yes");
                    resume = true;
                    sAge = dataSnapshot.child("resume").child("age").getValue().toString();
                    sGender = dataSnapshot.child("resume").child("gender").getValue().toString();
                    sMarks10 = dataSnapshot.child("resume").child("marks10").getValue().toString();
                    sMarks12 = dataSnapshot.child("resume").child("marks12").getValue().toString();
                    sMarksGrad = dataSnapshot.child("resume").child("marksGrad").getValue().toString();
                    Log.e("sAge", dataSnapshot.getKey() + sAge + sGender + sMarks10 + sMarks12 + sMarksGrad + "  ");
                } else {
                    Log.e("resume", "no");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });


        DatabaseReference myRef2 = database.getReference("Exams");
        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    String name = (String) messageSnapshot.getKey();
                    eAge = messageSnapshot.child("age").getValue().toString();
                    eDesc = messageSnapshot.child("desc").getValue().toString();
                    eGender = messageSnapshot.child("gender").getValue().toString();
                    eMarks10 = messageSnapshot.child("marks10").getValue().toString();
                    eMarks12 = messageSnapshot.child("marks12").getValue().toString();
                    eMarksGrad = messageSnapshot.child("marksGrad").getValue().toString();
                    Log.e("desc", name + eAge + eDesc + eGender + eMarks10 + eMarks12 + eMarksGrad);
                    Exam exam = new Exam();
                    exam.setAge(eAge);
                    exam.setDesc(eDesc);
                    exam.setGender(eGender);
                    exam.setMarks10(eMarks10);
                    exam.setMarks12(eMarks12);
                    exam.setMarksGrad(eMarksGrad);
                    int count = 0;
                    if (resume) {
                        if (!eAge.equals("NULL")) {
                            if (Integer.parseInt(eAge) <= Integer.parseInt(sAge)) {
                                count++;
                            }
                        } else {
                            count++;
                        }
                        if (!eMarks10.equals("NULL")) {
                            if (Integer.parseInt(eMarks10) <= Integer.parseInt(sMarks10)) {
                                count++;
                            }
                        } else {
                            count++;
                        }
                        if (!eMarks12.equals("NULL")) {
                            if (Integer.parseInt(eMarks12) <= Integer.parseInt(sMarks12)) {
                                count++;
                            }
                        } else {
                            count++;
                        }
                        if (!eMarksGrad.equals("NULL")) {
                            if (Integer.parseInt(eMarksGrad) <= Integer.parseInt(sMarksGrad)) {
                                count++;
                            }
                        } else {
                            count++;
                        }
                        if (!eGender.equals("NULL")) {
                            if (eGender.equals(sGender)) {
                                count++;
                            }
                        } else {
                            count++;
                        }
                        if (count == 5) {
                            dataList.add(exam);
                            titleList.add(name);
                        }
                    } else {
                        dataList.add(exam);
                        titleList.add(name);
                    }
//                    titleList.add(name);
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, titleList);
                listview.setAdapter(arrayAdapter);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
        listview = (ListView) findViewById(R.id.List_view);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, titleList);
        listview.setAdapter(arrayAdapter);
        progressDialog.dismiss();
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                progressDialog.setCancelable(true);
                progressDialog.setTitle(""+titleList.get(position));
                Exam exam = new Exam();
                exam = dataList.get(position);
                progressDialog.setMessage(""+exam.getDesc()+"\nAge Criteria - "+exam.getAge()+"\nGender Criteria - "+exam.getGender()+"\n10th Marks Criteria - "+exam.getMarks10()+"\n12th Marks Criteria - "+exam.getMarks12()+"\nGraduation Marks Criteria - "+exam.getMarksGrad());
                progressDialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // show dash_menu when dash_menu button is pressed
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dash_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            this.finish();
            startActivity(i);
        }
        if (item.getItemId() == R.id.resume) {
            Intent i = new Intent(MainActivity.this, ResumeActivity.class);
            this.finish();
            startActivity(i);
        }
        return true;
    }
}