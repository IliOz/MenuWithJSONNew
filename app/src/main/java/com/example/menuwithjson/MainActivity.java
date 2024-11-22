package com.example.menuwithjson;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class MainActivity extends AppCompatActivity {

    private String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Return the username of the user that logged in or signed up
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Constants.USERNAME_TAG)){
            username = intent.getStringExtra(Constants.USERNAME_TAG);
        }

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.REMEMBER_USER, MODE_PRIVATE);
        if (sharedPreferences.contains(Constants.USERNAME_TAG)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            username = sharedPreferences.getString(Constants.USERNAME_TAG, "");
            editor.apply();
        }
        Toast.makeText(this, "Username= " + username + " Password= " + password, Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.log_out) {
            Intent intent = new Intent(MainActivity.this, LogIn.class);
            intent.putExtra(Constants.SHARED_PREF_NAME, "mypref");
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.create_recipe) {
            /*Intent intent = new Intent(this, Settings.class);
            startActivity(intent);*/
        }
        else if (item.getItemId() == R.id.favorite) {
   /*         Intent intent = new Intent(this, Settings.class);
            startActivity(intent);*/
        }

        return super.onOptionsItemSelected(item);
    }
}