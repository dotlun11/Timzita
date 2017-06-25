package com.example.dotlun.timzita;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import models.TimzitaModel;

public class ProductActivity extends AppCompatActivity {
    private ImageView ivMovieIcon;
    private TextView tvView;
    private TextView tvTitle;
    private TextView tvSlug;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // setting up text views and stuff
        setUpUIViews();

        // recovering data from MainActivity, sent via intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String json = bundle.getString("timzitaModel"); // getting the model from MainActivity send via extras
            TimzitaModel movieModel = new Gson().fromJson(json, TimzitaModel.class);
            Picasso.with(getApplicationContext()).load("http://timzita.com/" + movieModel.getImage()).into(ivMovieIcon);

            tvView.setText(movieModel.getView());

            tvTitle.setText(movieModel.getTitle());
            tvSlug.setText(movieModel.getSlug());
        }
        //Bottom navigation
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_add:
                        Toast.makeText(ProductActivity.this, "Action Add Clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_add2:
                        Toast.makeText(ProductActivity.this, "Action Add1 Clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_add3:
                        Toast.makeText(ProductActivity.this, "Action Add2 Clicked", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
    }

    private void setUpUIViews() {
        ivMovieIcon = (ImageView) findViewById(R.id.ivIcon);

        tvView = (TextView) findViewById(R.id.tvView);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvSlug = (TextView) findViewById(R.id.tvSlug);
       // progressBar = (ProgressBar)findViewById(R.id.progressBar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_main_action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        switch (item.getItemId()) {
            case R.id.search_id:
                Toast.makeText(getApplicationContext(), "Setting options slected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.out_id:
                Toast.makeText(getApplicationContext(), "Log out options slected", Toast.LENGTH_SHORT).show();
                return true;
            default:
        return super.onOptionsItemSelected(item);
    }
}
}