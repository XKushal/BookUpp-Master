package com.example.amitgyawali.bookupp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class sellBooks extends AppCompatActivity
{
    private EditText mbookTitle;
    private TextView mTitletext;
    private TextView mAuthorText;
    private TextView mIsbnNumber;
    private TextView mDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_books);

        mbookTitle = (EditText)findViewById(R.id.bookName);
        mTitletext = (TextView)findViewById(R.id.titleText);
        mAuthorText = (TextView)findViewById(R.id.authorText);
        mIsbnNumber =(TextView)findViewById(R.id.isbnNum);
        mDescription=(TextView)findViewById(R.id.bookDescription);
    }

    public void searchBooks(View view)
    {
        // Get the search string from the input field.
        String queryString = mbookTitle.getText().toString();

        // Hide the keyboard when the button is pushed.

        InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(mbookTitle.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        // Check the status of the network connection.
        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();


        // If the network is active and the search field is not empty, start a FetchBook AsyncTask.
        if (networkInfo != null && networkInfo.isConnected() && queryString.length()!=0) {
            new FetchBook(mTitletext, mAuthorText, mbookTitle,mIsbnNumber,mDescription).execute(queryString);
        }
        // Otherwise update the TextView to tell the user there is no connection or no search term.
        else {
            if (queryString.length() == 0) {
                mAuthorText.setText("");
                mTitletext.setText("Not Found!");
            } else {
                mAuthorText.setText("");
                mTitletext.setText("Not Found!");
            }
        }



    }




}


