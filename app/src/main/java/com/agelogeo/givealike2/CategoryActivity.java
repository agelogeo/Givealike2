package com.agelogeo.givealike2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

public class CategoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);


        setTitle("Select category...");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void sendMeHome(View v){
        Intent i = new Intent();
        String message = "";
        i.putExtra("Data",message);
        setResult(RESULT_OK,i);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickCategory(View v){
        Intent i = new Intent();
        i.putExtra("Category",v.getTag().toString());
        setResult(RESULT_OK,i);
        finish();
    }
}
