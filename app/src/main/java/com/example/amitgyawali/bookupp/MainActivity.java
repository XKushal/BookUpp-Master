package com.example.amitgyawali.bookupp;

    import android.os.Vibrator;

        import android.app.ProgressDialog;
        import android.content.Intent;
        import android.support.annotation.NonNull;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.text.method.HideReturnsTransformationMethod;
        import android.text.method.PasswordTransformationMethod;
        import android.widget.CompoundButton;
        import android.widget.CompoundButton.OnCheckedChangeListener;
        import android.text.TextUtils;
        import android.view.View;
        import android.widget.Button;
        import android.widget.CheckBox;
        import android.widget.EditText;
     //   import android.widget.TextView;
        import android.widget.Toast;

        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.AuthResult;
        import com.google.firebase.auth.FirebaseAuth;


    import android.app.Activity;
    import android.app.PendingIntent;
    import android.content.BroadcastReceiver;
    import android.content.Context;
    import android.content.Intent;
    import android.content.IntentFilter;
    import android.nfc.NfcAdapter;
    import android.nfc.tech.IsoDep;
    import android.nfc.tech.MifareClassic;
    import android.nfc.tech.MifareUltralight;
    import android.nfc.tech.Ndef;
    import android.nfc.tech.NfcA;
    import android.nfc.tech.NfcB;
    import android.nfc.tech.NfcF;
    import android.nfc.tech.NfcV;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.Menu;
    import android.widget.TextView;



public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    //defining views
    String a;
    boolean b;
    private Button buttonLogin;
    private EditText edituserName;
    private EditText editpassword;
    private CheckBox showPasswd;
    private Button buttonSingUp;
    private Button buttonNFC;
    private final String[][] techList = new String[][] {
            new String[]{
                    NfcA.class.getName(),
                    NfcB.class.getName(),
                    NfcF.class.getName(),
                    NfcV.class.getName(),
                    IsoDep.class.getName(),
                    MifareClassic.class.getName(),
                    MifareUltralight.class.getName(), Ndef.class.getName()
            }
            };


   @Override
    protected void onResume() {
        super.onResume();
        b=false;
        // creating pending intent:
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        // creating intent receiver for NFC events:
        IntentFilter filter = new IntentFilter();
        filter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
        // enabling foreground dispatch for getting intent from NFC event:
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, new IntentFilter[]{filter}, this.techList);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // disabling foreground dispatch:
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.disableForegroundDispatch(this);
    }

   // @Override
    protected void onNewIntent(Intent intent) {
        if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED))
        {


            finish();
            startActivity(new Intent(getApplicationContext(), HomePage.class));


            // vibe.vibrate(100);

        }
    }

    private String ByteArrayToHexString(byte [] inarray) {
        int i, j, in;
        String [] hex = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
        String out= "";

        for(j = 0 ; j < inarray.length ; ++j)
        {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }
    //firebase auth object
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;
//    Vibrator vibe = (Vibrator) getSystemService(getBaseContext().VIBRATOR_SERVICE);


    //  Converting byte[] to hex string:



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // b=false;

        //getting firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        //initializing views
        buttonLogin = findViewById(R.id.loginButton);
        edituserName = findViewById(R.id.username);
        editpassword = findViewById(R.id.password);
        showPasswd = findViewById(R.id.checkboxPaswd);
        buttonSingUp = findViewById(R.id.signUpButton);
        //buttonNFC = findViewById(R.id.nfc);


        progressDialog = new ProgressDialog(this);

        //attaching click listener
        buttonLogin.setOnClickListener(this);
        buttonSingUp.setOnClickListener(this);
        // buttonNFC.setOnClickListener(this);
        // add onCheckedListener on checkbox
        // when user clicks on this checkbox, this is the handler.




        showPasswd.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // checkbox status is changed from uncheck to checked.
                if (!isChecked) {
                    // show password
                    editpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    // hide password
                    editpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

    }

    //method for user login
    private void userLogin() {
        String email = edituserName.getText().toString().trim();
        String password = editpassword.getText().toString().trim();


        //checking if email and passwords are empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show();
            return;
        }

        //logging in the user
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        //if the task is successfull
                        if (task.isSuccessful()) {
                            //start the profile activity
                            finish();
                            startActivity(new Intent(getApplicationContext(), HomePage.class));
                        }
                    }
                });

    }

    @Override
    public void onClick(View view) {
        if (view == buttonLogin) {
            userLogin();
        }

        if (view == buttonSingUp) {
            finish();
            startActivity(new Intent(this, SignUp.class));
        }


    }


}



