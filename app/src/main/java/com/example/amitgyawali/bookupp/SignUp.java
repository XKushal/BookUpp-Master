package com.example.amitgyawali.bookupp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by amitgyawali on 3/20/18.
 */

public class


SignUp extends AppCompatActivity implements View.OnClickListener
{
    private Button buttonRegister;

    private EditText editfirstName;
    private EditText editlastName;
    private EditText edituserName;
    private EditText editemailAddress;
    private EditText editpassword;
    private EditText editconfirmPassword;
    private TextView clickLogin;

    private ProgressDialog progressDialog;

    //stores email and password only
    private FirebaseAuth firebaseAuth;

    //stores other details
    private DatabaseReference mDatabase;

    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        progressDialog = new ProgressDialog(this);

        firebaseAuth =FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("user_info");

        //exctracting values from our activity_sign_up and assigning them
        buttonRegister =(Button) findViewById(R.id.Register);
        editfirstName =(EditText) findViewById(R.id.firstName);
        editlastName = (EditText) findViewById(R.id.lastName2);
        edituserName =(EditText) findViewById(R.id.userName);
        editemailAddress = (EditText) findViewById(R.id.emailAddress);
        editpassword =(EditText) findViewById(R.id.password);
        editconfirmPassword =(EditText) findViewById(R.id.confirmPassword);
        clickLogin = (TextView) findViewById(R.id.logIn);

        //when the button is clicked
        buttonRegister.setOnClickListener(this);
        clickLogin.setOnClickListener(this);

    }


    private void registerUser()
    {
        final String firstName = editfirstName.getText().toString();
        final String lastName = editlastName.getText().toString();
        final String userName = edituserName.getText().toString();
        final String emailAddress = editemailAddress.getText().toString();
        final String password = editpassword.getText().toString();
        final String confirmPassword = editconfirmPassword.getText().toString();

        if(TextUtils.isEmpty(firstName))
        {
            //firstname is empty
            Toast.makeText(this, "Please enter First Name", Toast.LENGTH_SHORT).show();

            //stopping the execution of function further
            return;
        }

        if (TextUtils.isEmpty(lastName))
        {
            //last name is empty
            Toast.makeText(this, "Please enter Last Name", Toast.LENGTH_SHORT).show();

            //stopping the execution of function further
            return;
        }

        if (TextUtils.isEmpty(userName))
        {
            //username is empty
            Toast.makeText(this, "Please enter student ID", Toast.LENGTH_SHORT).show();

            //stopping the execution of function further
            return;
        }

        if(TextUtils.isEmpty(emailAddress))
        {
            //Email address is empty
            Toast.makeText(this, "Please enter email address", Toast.LENGTH_SHORT).show();

            //stopping the execution of function further
            return;

        }

        if (TextUtils.isEmpty(password))
        {
            //password is empty
            Toast.makeText(this, "Please enter password ", Toast.LENGTH_SHORT).show();

            //stopping the execution of function further
            return;
        }

        if (TextUtils.isEmpty(confirmPassword))
        {
            //Confirm Password is Empty
            Toast.makeText(this, "Please enter Confirm Password", Toast.LENGTH_SHORT).show();

            //stopping the execution of function further
            return;
        }

        //check if password matches
        if (password.equals(confirmPassword)) {
            firebaseAuth.createUserWithEmailAndPassword(emailAddress, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String user_id = firebaseAuth.getCurrentUser().getUid();
                        DatabaseReference current_user_db = mDatabase.child(user_id);

                        current_user_db.child("First_Name").setValue(firstName);
                        current_user_db.child("Last_Name").setValue(lastName);
                        current_user_db.child("Student_ID").setValue(userName);
                        current_user_db.child("Email_Address").setValue(emailAddress);

                        progressDialog.dismiss();

                    }else{
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(SignUp.this, "Error Registration", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }else{

            Toast.makeText(this, "Passwords doesn't match", Toast.LENGTH_SHORT).show();

            //stopping the execution of function further
            return;
        }

        //if validation is ok
        progressDialog.setMessage("Registerting User. ..");
        progressDialog.show();

    }

    @Override
    public void onClick(View v)
    {
        if (v == buttonRegister)
        {
            registerUser();
        }
        if(v == clickLogin)

        {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

    }

}