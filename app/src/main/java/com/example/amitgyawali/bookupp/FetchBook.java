package com.example.amitgyawali.bookupp;

import android.net.Uri;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by amitgyawali on 3/23/18.
 */


public class FetchBook extends AsyncTask<String, String, String>
{
    // Variables for the search input field, and results TextViews
    private EditText mBookInput;
    private TextView mTitleText;
    private TextView mAuthorText;
    private TextView mIsbnNumber;
    private TextView mDescription;

    // Class name for Log tag
    private static final String LOG_TAG = FetchBook.class.getSimpleName();

    public FetchBook(TextView titleText, TextView authorText, EditText bookInput, TextView isbn,TextView description)
    {
        this.mTitleText = titleText;
        this.mAuthorText = authorText;
        this.mBookInput = bookInput;
        this.mIsbnNumber = isbn;
        this.mDescription =description;


    }


    @Override
    protected String doInBackground(String... params)
    {
        // Get the search string
        String queryString = params[0];

        // Set up variables for the try block that need to be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String bookJSONString = null;

        // Attempt to query the Books API.
        try {
            // Base URI for the Books API.
            final String BOOK_BASE_URL = "https://www.googleapis.com/books/v1/volumes?";

            final String QUERY_PARAM = "q"; // Parameter for the search string.
            final String MAX_RESULTS = "maxResults"; // Parameter that limits search results.
            final String PRINT_TYPE = "printType"; // Parameter to filter by print type.

            // Build up your query URI, limiting results to 10 items and printed books.
            Uri builtURI = Uri.parse(BOOK_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, queryString)
                    .appendQueryParameter(MAX_RESULTS, "10")
                    .appendQueryParameter(PRINT_TYPE, "books")
                    .build();
            URL requestURL = new URL(builtURI.toString());

            //Open the network connection.
            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Get the InputStream.
            InputStream inputStream = urlConnection.getInputStream();

            // Read the response string into a StringBuilder.
            StringBuilder builder = new StringBuilder();

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null)
            {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // but it does make debugging a *lot* easier if you print out the completed buffer for debugging.
                builder.append(line + "\n");
            }

            if (builder.length() == 0)
            {
                // Stream was empty.  No point in parsing.
                // return null;
                return null;
            }

            bookJSONString = builder.toString();

            // Catch errors.
        } catch (IOException e) {
            e.printStackTrace();

            // Close the connections.

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        // Return the raw response.
        return bookJSONString;
    }

    @Override
    protected void onPostExecute(String s)
    {
        super.onPostExecute(s);
        try {
            // Convert the response into a JSON object.
            JSONObject jsonObject = new JSONObject(s);
            // Get the JSONArray of book items.
            JSONArray itemsArray = jsonObject.getJSONArray("items");



            // Initialize iterator and results fields.
            int i = 0;
            String title = null;
            String authors = null;
            String description =null;
            String isbn =null;

            while (i < itemsArray.length() || (authors == null && title == null)) {
                // Get the current item information.
                JSONObject book = itemsArray.getJSONObject(i);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");
                JSONArray bookId = jsonObject.getJSONArray("industryIdentifiers");
                JSONObject booknum =bookId.getJSONObject(0);




                // Try to get the author and title from the current item,
                // catch if either field is empty and move on.
                try {
                    title = volumeInfo.getString("title");
                    authors = volumeInfo.getString("authors");
                    description= volumeInfo.getString("description");
                    isbn = booknum.getString("identifier");

                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Move to the next item.


                i++;
            }

            // If both are found, display the result.
            if (title != null && authors != null){
                mTitleText.setText(title);
                mAuthorText.setText(authors);
                mIsbnNumber.setText(isbn);
                mBookInput.setText("");
                mDescription.setText(description);
            } else {
                // If none are found, update the UI to show failed results.
                mTitleText.setText("No result found");
                mAuthorText.setText("");
                mBookInput.setText("");
                mDescription.setText("");
            }

        } catch (Exception e){
            // If onPostExecute does not receive a proper JSON string,
            // update the UI to show failed results.
            mTitleText.setText("No result");
            mAuthorText.setText("");
            e.printStackTrace();

        }




    }
}
