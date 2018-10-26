package com.example.amitgyawali.bookupp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.amitgyawali.bookupp.Database.Database;
import com.example.amitgyawali.bookupp.Preference.PreferenceActivity;
import com.example.amitgyawali.bookupp.Tools.BookCache;
import com.example.amitgyawali.bookupp.Tools.BookInformation;
import com.example.amitgyawali.bookupp.Tools.InternetMonitor;
import com.example.amitgyawali.bookupp.Tools.ViewMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BookViewerActivity extends AppCompatActivity implements BookListFragment.OnBookListListener {
    private int currentMode;
    private ViewPager pager;

    //firebase
    ProgressBar mProgress;
    //FIREBASE AUTHENTICATION FIELDS
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    //FIREBASE DATABASE FIELDS
    DatabaseReference mUserDatabse;
    DatabaseReference mChildDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_viewer);

        //firebase
        mAuth = FirebaseAuth.getInstance();

        if (savedInstanceState != null && savedInstanceState.containsKey("currentState")) {
            currentMode = savedInstanceState.getInt("currentState");
        } else {
            currentMode = getIntent().getIntExtra("options", ViewMode.LIST_MODE);
        }

        if(deviceUsesDualPane()){
            addMode(ViewMode.DUAL_MODE);
            if(currentModeIs(ViewMode.LIST_MODE)){
                removeMode(ViewMode.LIST_MODE);
                addMode(ViewMode.DETAIL_MODE);
            }
            Log.d("BookCatalog", "Entered Dual Mode: " + ViewMode.convertToString(currentMode));
        }
        else{
            removeMode(ViewMode.DUAL_MODE);
            Log.d("BookCatalog", "Exited Dual Mode: " + ViewMode.convertToString(currentMode));
        }

        if(!InternetMonitor.isConnected(getApplicationContext())) {
            CharSequence text = "Can't get book images. No Internet.";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(getApplicationContext(), text, duration);
            toast.show();
        }
        if(getIntent().getBooleanExtra("notFound", false)){
            Toast toast = Toast.makeText(getApplicationContext(), "Book Not Found.", Toast.LENGTH_LONG);
            toast.show();
        }






        //PROGRESS DIALOG
        //mProgress = new ProgressDialog(this);

        //FIREBASE DATABASE INSTANCE
        mAuth =FirebaseAuth.getInstance();

        mUserDatabse = FirebaseDatabase.getInstance().getReference().child("book_info");


        setUpToolbar();
        buildViews();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("currentState", currentMode);
        super.onSaveInstanceState(outState);
    }

    private void buildViews() {
        if (currentModeIs(ViewMode.LIST_MODE) || currentModeIs(ViewMode.DUAL_MODE)) {
            Log.d("BookCatalog", "Adding List Fragment: " + ViewMode.convertToString(currentMode));
            attachBookListFragment();
        }
        if(!currentModeIs(ViewMode.LIST_MODE)){
            Log.d("BookCatalog", "Adding pager: " + ViewMode.convertToString(currentMode));
            setUpViewPager(0);
        }
    }

    public void onBookCountAvailable(int bookCount){
        setBookCount(bookCount);
    }

    private void setUpToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back);
        setSupportActionBar(toolbar);
        setToolbarTitle();
    }


    private void setToolbarTitle(){
        ActionBar bar = getSupportActionBar();
        if(currentModeIs(ViewMode.ADD_MODE)){
            bar.setTitle("Add Books");
        }
        else if (currentModeIs(ViewMode.EDIT_MODE)){
            bar.setTitle("Edit");
        }
        else{
            bar.setTitle("My Books");
        }
    }



    private void attachBookListFragment() {
        findViewById(R.id.book_list_container).setVisibility(View.VISIBLE);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        BookListFragment bookListFrag = new BookListFragment();
        bookListFrag.setArguments(getIntent().getExtras());
        ft.add(R.id.book_list_container, bookListFrag);
        ft.commit();
    }

    private void detachBookListFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.book_list_container);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
        findViewById(R.id.book_list_container).setVisibility(View.GONE);
    }

    private void setUpViewPager(int position) {
        BookPagerAdapter adapter = new BookPagerAdapter(getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setVisibility(View.VISIBLE);
        if (!currentModeIs(ViewMode.DETAIL_MODE)) {
            adapter.setEditable(true);
        }
        if (getIntent().getSerializableExtra("books") == null) {
            adapter.setBooks(getApplicationContext());
        } else {
            adapter.setBooks((ArrayList<BookInformation>) getIntent().getSerializableExtra("books"));
        }
        pager.setCurrentItem(position);
        setBookCount(pager.getAdapter().getCount());
        setUpPagerListener();
    }

    private void setUpPagerListener() {
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                BookPagerAdapter adapter = (BookPagerAdapter) pager.getAdapter();
                if (adapter != null) {
                    if (adapter.isEditable()) {
                        BookEditFragment bookFrag = (BookEditFragment) adapter.instantiateItem(pager, pager.getCurrentItem());
                        if (bookFrag != null) {
                            bookFrag.setBookFromUI();
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    public void onBookSelected(int position) {
        if(currentModeIs(ViewMode.LIST_MODE)) {
            detachBookListFragment();
            setMode(ViewMode.DETAIL_MODE);
            setUpViewPager(position);
            invalidateOptionsMenu();
        }
        else{
            BookPagerAdapter adapter = (BookPagerAdapter) pager.getAdapter();
            if (adapter != null) {
                if (adapter.isEditable()) {
                    BookEditFragment bookFrag = (BookEditFragment) adapter.instantiateItem(pager, pager.getCurrentItem());
                    if (bookFrag != null) {
                        bookFrag.setBookFromUI();
                    }
                }
            }
            pager.setCurrentItem(position);
        }
    }

    private void onSave()
    {
        BookPagerAdapter adapter = (BookPagerAdapter) pager.getAdapter();
        if (adapter != null) {
            if (adapter.isEditable()) {
                BookEditFragment bookFrag = (BookEditFragment) adapter.instantiateItem(pager, pager.getCurrentItem());
                if (bookFrag != null) {
                    bookFrag.setBookFromUI();
                }
            }
        }
        ArrayList<BookInformation> books = BookCache.getBooks();
        for (BookInformation book : books) {
            if (!book.title.isEmpty())
            {
                FirebaseUser user = mAuth.getCurrentUser();


                //String content ="LetCheck";


                Date date = new Date();
                book.timeLastUpdated = DateFormat.getDateInstance().format(date);
                //Toast.makeText(this,)
                {
                    if (user != null)
                    {
                        String user_id = mAuth.getCurrentUser().getUid();

                        //database
                        DatabaseReference current_user_db = mUserDatabse.child(user_id);
                        current_user_db.child("title").setValue(book.title);
                        //mUserDatabse.child("userid").setValue(mAuth.getCurrentUser().getUid());
                    }

                }




                Database.insertBook(book, getApplicationContext());
                Toast.makeText(this,"Successfully updated to database",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onDelete() {
        BookPagerAdapter adapter = (BookPagerAdapter) pager.getAdapter();
        adapter.removeBook(pager.getCurrentItem());
        setBookCount(adapter.getCount());
    }

    private void setBookCount(int numBooks){
        String output = String.valueOf(numBooks);
        if(numBooks == 1){
            output += " book";
        }
        else{
            output += " books";
        }
        getSupportActionBar().setSubtitle(output);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (currentModeIs(ViewMode.DETAIL_MODE)) {
            getMenuInflater().inflate(R.menu.detail_menu, menu);
        } else if (currentModeIs(ViewMode.EDIT_MODE) || currentModeIs(ViewMode.ADD_MODE)) {
            getMenuInflater().inflate(R.menu.edit_menu, menu);
        } else {
            getMenuInflater().inflate(R.menu.main_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, PreferenceActivity.class);
            startActivity(intent);
        }
        if (id == R.id.save_button) {
            onSave();
            if (currentModeIs(ViewMode.ADD_MODE)) {
                finish();
            }
            if(!currentModeIs(ViewMode.DUAL_MODE)) {
                setMode(ViewMode.LIST_MODE);
                pager.setVisibility(View.GONE);
                attachBookListFragment();
                invalidateOptionsMenu();
            }
            else{
                removeMode(ViewMode.EDIT_MODE);
                addMode(ViewMode.DETAIL_MODE);
                detachBookListFragment();
                attachBookListFragment();
                setUpViewPager(pager.getCurrentItem());
                invalidateOptionsMenu();
            }
            setToolbarTitle();
        }
        if (id == R.id.delete_button) {
            int nextPosition = getPositionAfterDelete();
            onDelete();
            setUpViewPager(nextPosition);
            if(currentModeIs(ViewMode.DUAL_MODE)){
                detachBookListFragment();
                attachBookListFragment();
            }
        }
        if (id == R.id.edit_button) {
            if(currentModeIs(ViewMode.DUAL_MODE)){
                removeMode(ViewMode.DETAIL_MODE);
                addMode(ViewMode.EDIT_MODE);
            }
            else {
                setMode(ViewMode.EDIT_MODE);
            }
            setUpViewPager(pager.getCurrentItem());
            invalidateOptionsMenu();
            setToolbarTitle();
        }
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private int getPositionAfterDelete() {
        if (pager.getCurrentItem() == pager.getChildCount() - 1) {
            return pager.getCurrentItem() - 1;
        }
        return pager.getCurrentItem();
    }

    @Override
    public void onBackPressed() {
        BookCache.clearBooks();
        if (currentModeIs(ViewMode.LIST_MODE) || currentModeIs(ViewMode.ADD_MODE) || (currentModeIs(ViewMode.DUAL_MODE) && currentModeIs(ViewMode.DETAIL_MODE))) {
            finish();
        } else if (currentModeIs(ViewMode.DUAL_MODE) && currentModeIs(ViewMode.EDIT_MODE)){
            setMode(ViewMode.DUAL_MODE | ViewMode.DETAIL_MODE);
            setUpViewPager(pager.getCurrentItem());
            invalidateOptionsMenu();
        }
        else {
            setMode(ViewMode.LIST_MODE);
            pager.setVisibility(View.GONE);
            attachBookListFragment();
            invalidateOptionsMenu();
        }
        setToolbarTitle();
    }

    private boolean deviceUsesDualPane(){
        Resources res = getResources();
        return res.getBoolean(R.bool.dual_pane) && (res.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
    }

    private void removeMode(int modeToRemove){
        currentMode = ViewMode.removeMode(currentMode, modeToRemove);
    }

    private void addMode(int modeToAdd){
        currentMode |= modeToAdd;
        currentMode = ViewMode.addMode(currentMode, modeToAdd);
    }

    private boolean currentModeIs(int modeToCheck) {
        return ViewMode.currentModeIs(currentMode, modeToCheck);
    }

    private void setMode(int mode) {
        currentMode = mode;
    }
}
