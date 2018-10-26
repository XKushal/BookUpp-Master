package com.example.amitgyawali.bookupp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by amitgyawali on 3/21/18.
 */

public class HomePage extends AppCompatActivity
{
    private DrawerLayout mDrawerLayout;
    private Button mdivein;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        mdivein =(Button) findViewById(R.id.divein);

        mdivein.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                finish();
                Intent intent = new Intent(HomePage.this, BookViewerActivity.class);
                startActivity(intent);

                // Handle view click here
            }
        });




        mDrawerLayout =(DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Respond when the drawer is opened
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        // Respond when the drawer is closed
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                }
        );



        NavigationView navigationView =findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @SuppressWarnings("StatementWithEmptyBody")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {

                int id = item.getItemId();

                if(id == R.id.navAccount)
                {
                   // android.support.v4.app.FragmentTransaction ft =getSupportFragmentManager().beginTransaction();
                   // ft.replace(R.id.new_content,new profileFragment());
                    //ft.commit();
                    finish();
                    startActivity(new Intent(getApplicationContext(), updataProfile.class));

                } else if(id ==R.id.sellItem) {
                finish();
                startActivity(new Intent(getApplicationContext(),barcodeScan.class));
            }

               /* else if(id == R.id.buyItem)
                {
                    android.support.v4.app.FragmentTransaction ft =getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.new_content,new buyFragment());
                    ft.commit();

                }*/

                if(id == R.id.logOut){
                    //if(task.isSuccessful()){
                        //start the profile activity
                        finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    //android.support.v4.app.FragmentTransaction ft =getSupportFragmentManager().beginTransaction();
                   // ft.replace(R.id.userId, new MainActivity());
                    //ft.commit();

                }
                    //Toast.makeText(getApplicationContext(), "You are in Logout", Toast.LENGTH_SHORT).show();



                mDrawerLayout.closeDrawer(GravityCompat.START);

                return true;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }




}