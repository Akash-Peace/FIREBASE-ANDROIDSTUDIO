package com.example.whitecup;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class aboutus extends AppCompatActivity {
    ImageView github, linkedin, insta, twitter, menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        github = findViewById(R.id.github);
        linkedin = findViewById(R.id.linkedin);
        insta = findViewById(R.id.insta);
        twitter = findViewById(R.id.twitter);
        menu = findViewById(R.id.imageView3);

        menu.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://github.com/Akash-Peace");
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });
        linkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.linkedin.com/in/akash-2000-cse/");
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });
        insta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.instagram.com/akash.a.2020/");
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://twitter.com/AkashA53184506");
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), Menu.class));
    }
}