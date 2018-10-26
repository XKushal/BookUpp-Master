package com.example.amitgyawali.bookupp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by amitgyawali on 3/20/18.
 */

public class updataProfile extends AppCompatActivity implements View.OnClickListener
{
    private Button buttonUpdate;

    private EditText editfirstName;
    private EditText editlastName;
    private EditText editemailAddress;
    private EditText editAddress;
    private EditText editPasscode;
    private EditText editphoneNumber;
    private EditText editcityName;
    private EditText editstateName;
    private EditText editzipCode;


    private ProgressDialog progressDialog;

    //stores email and password only
    private FirebaseAuth firebaseAuth;

    //stores other details
    private DatabaseReference mDatabase;

    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_profile);

        progressDialog = new ProgressDialog(this);

        firebaseAuth =FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("User Profile info");

        //exctracting values from our activity_profile and assigning them
        buttonUpdate =(Button) findViewById(R.id.updateButton);
        editfirstName =(EditText) findViewById(R.id.firstName);
        editlastName = (EditText) findViewById(R.id.lastName);
        editemailAddress = (EditText) findViewById(R.id.emailAddress);
        editAddress =(EditText) findViewById(R.id.address);
        editPasscode =(EditText) findViewById(R.id.passcode);
        editphoneNumber =(EditText) findViewById(R.id.phoneNumber);
        editcityName =(EditText) findViewById(R.id.cityName);
        editstateName =(EditText) findViewById(R.id.stateName);
        editzipCode =(EditText) findViewById(R.id.zipCode);

        //when the button is clicked
        buttonUpdate.setOnClickListener(this);
    }


    private void updateUserProfile() {
        final String firstName = editfirstName.getText().toString();
        final String lastName = editlastName.getText().toString();
        final String emailAddress = editemailAddress.getText().toString();
        final String address = editAddress.getText().toString();
        final String passcode = editPasscode.getText().toString();
        final String phoneNumber = editphoneNumber.getText().toString();
        final String cityName = editcityName.getText().toString();
        final String stateName = editstateName.getText().toString();
        final String zipCode = editzipCode.getText().toString();

        if (TextUtils.isEmpty(firstName)) {
            //firstname is empty
            Toast.makeText(this, "Please enter First Name", Toast.LENGTH_SHORT).show();

            //stopping the execution of function further
            return;
        }

        if (TextUtils.isEmpty(lastName)) {
            //last name is empty
            Toast.makeText(this, "Please enter Last Name", Toast.LENGTH_SHORT).show();

            //stopping the execution of function further
            return;
        }

        if (TextUtils.isEmpty(emailAddress)) {
            //username is empty
            Toast.makeText(this, "Please enter email Address", Toast.LENGTH_SHORT).show();

            //stopping the execution of function further
            return;
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            //Email address is empty
            Toast.makeText(this, "Please enter phone Number", Toast.LENGTH_SHORT).show();

            //stopping the execution of function further
            return;

        }

        if (TextUtils.isEmpty(address)) {
            //password is empty
            Toast.makeText(this, "Please enter address ", Toast.LENGTH_SHORT).show();

            //stopping the execution of function further
            return;
        }

        if (TextUtils.isEmpty(passcode)) {
            //password is empty
            Toast.makeText(this, "Please enter password ", Toast.LENGTH_SHORT).show();

            //stopping the execution of function further
            return;
        }

        if (TextUtils.isEmpty(cityName)) {
            //Confirm Password is Empty
            Toast.makeText(this, "Please enter city name", Toast.LENGTH_SHORT).show();

            //stopping the execution of function further
            return;
        }

        if (TextUtils.isEmpty(stateName)) {
            //Confirm Password is Empty
            Toast.makeText(this, "Please enter state name", Toast.LENGTH_SHORT).show();

            //stopping the execution of function further
            return;
        }

        if (TextUtils.isEmpty(zipCode)) {
            //Confirm Password is Empty
            Toast.makeText(this, "Please enter zip code", Toast.LENGTH_SHORT).show();

            //stopping the execution of function further
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(emailAddress, passcode).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String user_id = firebaseAuth.getCurrentUser().getUid();
                    DatabaseReference current_user_db = mDatabase.child(user_id);

                    current_user_db.child("First Name").setValue(firstName);
                    current_user_db.child("Last Name").setValue(lastName);
                    current_user_db.child("Email Address").setValue(emailAddress);
                    current_user_db.child("Phone Number").setValue(phoneNumber);
                    current_user_db.child("Address").setValue(address);
                    current_user_db.child("City Name").setValue(cityName);
                    current_user_db.child("State name").setValue(stateName);
                    current_user_db.child("Zip Code").setValue(zipCode);

                    progressDialog.dismiss();
                    finish();
                    startActivity(new Intent(getApplicationContext(), finalProfile.class));

                }

            }
        });

        //if validation is ok
        progressDialog.setMessage("Updating Profile. ..");
        progressDialog.show();
    }

    @Override
    public void onClick(View v)
    {
        if (v == buttonUpdate)
        {
            updateUserProfile();
        }

    }
}