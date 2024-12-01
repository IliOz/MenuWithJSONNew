package com.example.menuwithjson;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Sign_up extends AppCompatActivity {

    private ArrayList<UserInfo> users;
    private EditText username, password;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeViews();

        users = getAllUsers();
    }

    public void initializeViews() {
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
    }

    // Save all the accounts
    public void saveAccount() {
        try {
            JSONArray jsonArray = new JSONArray();
            for (UserInfo user : users) {
                jsonArray.put(user.toJSON());
            }

            FileOutputStream fileOutputStream = openFileOutput(Constants.USERS_PATH, Context.MODE_PRIVATE);
            fileOutputStream.write(jsonArray.toString().getBytes(StandardCharsets.UTF_8));
            fileOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException("Error saving accounts", e);
        }
    }

    // Read all the accounts and return an array list of them
    public ArrayList<UserInfo> getAllUsers() {
        users = new ArrayList<>();
        try {
            FileInputStream fileInputStream = openFileInput(Constants.USERS_PATH);
            int size = fileInputStream.available();
            byte[] buffer = new byte[size];
            fileInputStream.read(buffer);
            fileInputStream.close();
            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String username = jsonObject.getString(Constants.USERNAME_TAG);
                String password = jsonObject.getString(Constants.PASSWORD_TAG);
                JSONArray recipesArray = jsonObject.getJSONArray(Constants.RECIPE_TAG);

                ArrayList<Recipe> recipes = new ArrayList<>();
                for (int j = 0; j < recipesArray.length(); j++) {
                    JSONObject recipeObject = recipesArray.getJSONObject(j);
                    recipes.add(new Recipe(recipeObject)); // Assuming Recipe has a constructor that takes a JSONObject
                }

                UserInfo user = new UserInfo(username, password, recipes);
                users.add(user);
            }
        } catch (Exception e) {
            //throw new RuntimeException("Error reading accounts", e);
            return null;
        }
        return users;
    }


    // Add an account, return false if the user exist, true otherwise
    public boolean addUser(UserInfo user) {
        users = getAllUsers();
        if (users == null) {
            users = new ArrayList<>();
        }
        for (UserInfo existingUser : users) {
            if (existingUser.getUserName().equals(user.getUserName())) {
                Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        users.add(user);
        saveAccount();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.REMEMBER_USER, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.USERNAME_TAG, user.getUserName());  // Save the username
        editor.apply();

        // Go to MainActivity after successful login/signup
        Intent intent = new Intent(Sign_up.this, MainActivity.class);
        intent.putExtra(Constants.USERNAME_TAG, user.getUserName());  // Send the username
        startActivity(intent);
        finish();  // Optionally finish the current activity to prevent the user from returning to it

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem create_recipe = menu.findItem(R.id.create_recipe);
        MenuItem favorite = menu.findItem(R.id.favorite);
        MenuItem log_out = menu.findItem(R.id.log_out);

        users = getAllUsers();
        boolean firstTimeUser = (users == null || users.isEmpty());

        if (firstTimeUser) {
            create_recipe.setTitle("Register");
            favorite.setVisible(false);
            log_out.setVisible(false);
        } else {
            create_recipe.setTitle("Register");
            favorite.setTitle("Go Back");
            log_out.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.create_recipe) {
            // Register user

            if (username.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                Toast.makeText(Sign_up.this, "Fill all the slots", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
            }

            ArrayList<Recipe> recipes = new ArrayList<>();

            boolean b = addUser(new UserInfo(username.getText().toString(), password.getText().toString(), recipes));
            if (b){
                Intent intent = new Intent(Sign_up.this, MainActivity.class);
                intent.putExtra(Constants.USERNAME_TAG, username.getText().toString());
                startActivity(intent);
            }

        }
        else if (item.getItemId() == R.id.favorite){
            // Go back to Sign_up
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}