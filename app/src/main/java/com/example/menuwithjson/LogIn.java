package com.example.menuwithjson;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class LogIn extends AppCompatActivity {

    // Declare the UI elements
    private EditText usernameEditText;
    private EditText passwordEditText;
   // private CheckBox rememberMeCheckBox;

    private String username;
    private String password;
    private ArrayList<UserInfo> users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        initializeUIReferences();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Constants.SHARED_PREF_NAME)) {
            rememberUser(false); // Clear remembered user if intent signals a logout
        }

       /* rememberMeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.REMEMBER_USER, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (isChecked) {
                    // Save the user credentials if they are not empty
                    String username = usernameEditText.getText().toString();
                    String password = passwordEditText.getText().toString();

                    if (!username.isEmpty() && !password.isEmpty()) {
                        editor.putString(Constants.USERNAME_TAG, username);
                        editor.putString(Constants.PASSWORD_TAG, password);
                        editor.putBoolean(Constants.REMEMBER_ME_CHECKED, true);  // Remember the checkbox state
                    }
                } else {
                    // If not checked, clear the saved credentials
                    editor.putString(Constants.USERNAME_TAG, "");
                    editor.putString(Constants.PASSWORD_TAG, "");
                    editor.putBoolean(Constants.REMEMBER_ME_CHECKED, false);  // Reset checkbox state
                }
                editor.apply();
            }
        });*/
    }

/*    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.REMEMBER_USER, MODE_PRIVATE);

        String rememberedUsername = sharedPreferences.getString(Constants.USERNAME_TAG, "");
        String rememberedPassword = sharedPreferences.getString(Constants.PASSWORD_TAG, "");

        if (!rememberedUsername.isEmpty() && !rememberedPassword.isEmpty()) {
            usernameEditText.setText(rememberedUsername);
            passwordEditText.setText(rememberedPassword);
            //rememberMeCheckBox.setChecked(true);
        } else {
            usernameEditText.setText("");
            passwordEditText.setText("");
            //rememberMeCheckBox.setChecked(false);
        }
    }*/

    @Override
    protected void onStart() {
        super.onStart();

        if (getAllUsers() == null){
            Intent intent = new Intent(LogIn.this, Sign_up.class);
            startActivity(intent);
        }
    }

    public void rememberUser(boolean shouldRemember) {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.REMEMBER_USER, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (shouldRemember) {
            editor.putString(Constants.USERNAME_TAG, usernameEditText.getText().toString());
            editor.putString(Constants.PASSWORD_TAG, passwordEditText.getText().toString());
        } else {
            editor.putString(Constants.USERNAME_TAG, "");
            editor.putString(Constants.PASSWORD_TAG, "");
            editor.apply();
        }
        editor.apply();
    }


    // Initialize the UI elements by finding them by their IDs
    public void initializeUIReferences() {
        usernameEditText = findViewById(R.id.Username);
        passwordEditText = findViewById(R.id.Password);
        //rememberMeCheckBox = findViewById(R.id.remember_me);
    }

    // If info is correct transition to the MainActivity
    public void transitionToNextActivity(){
        Intent intent = new Intent(LogIn.this ,MainActivity.class);
        intent.putExtra(Constants.USERNAME_TAG, usernameEditText.getText().toString());
        intent.putExtra(Constants.PASSWORD_TAG, passwordEditText.getText().toString());
        startActivity(intent);
    }

    // Read all the accounts and return an array list of them
    public ArrayList<UserInfo> getAllUsers() {
        users = new ArrayList<>();
        try {
            // Check if the file exists before trying to read
            FileInputStream fileInputStream = openFileInput(Constants.USERS_PATH);

            // Check if file is empty
            int size = fileInputStream.available();
            if (size == 0) {
                Toast.makeText(LogIn.this, "No users found. Please sign up first.", Toast.LENGTH_SHORT).show();
                return null;  // No users to load
            }

            byte[] buffer = new byte[size];
            fileInputStream.read(buffer);
            fileInputStream.close();

            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(jsonString);

            // Parsing JSON and creating UserInfo objects
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String username = jsonObject.getString(Constants.USERNAME_TAG);
                String password = jsonObject.getString(Constants.PASSWORD_TAG);
                UserInfo user = new UserInfo(username, password, null);
                users.add(user);
            }

        } catch (IOException e) {
            // Error reading the file
            return null;
        } catch (JSONException e) {
            // Error parsing JSON
            return null;
        }
        return users;
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem signInMenuItem = menu.findItem(R.id.log_out); // Renaming for "Sign in"
        MenuItem signUpMenuItem = menu.findItem(R.id.create_recipe);
        MenuItem favoriteMenuItem = menu.findItem(R.id.favorite);

        //SharedPreferences sharedPreferences = getSharedPreferences(Constants.REMEMBER_USER, MODE_PRIVATE);
        //String rememberedUsername = sharedPreferences.getString(Constants.USERNAME_TAG, "");

/*        if (sharedPreferences.contains(Constants.USERNAME_TAG) && !rememberedUsername.isEmpty()) {
            transitionToNextActivity();*/
        //} else {
            // User is logged out
            signInMenuItem.setTitle("Sign in");
            signUpMenuItem.setTitle("Sign up");
        //}

        favoriteMenuItem.setVisible(false); // Assuming this is not used for now

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.REMEMBER_USER, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (item.getItemId() == R.id.log_out) {
            if (item.getTitle().equals("Sign in")) {
                if (usernameEditText.getText().toString().isEmpty() || passwordEditText.getText().toString().isEmpty()) {
                    Toast.makeText(LogIn.this, "Fill all the slots", Toast.LENGTH_SHORT).show();
                } else {
                    username = usernameEditText.getText().toString();
                    password = passwordEditText.getText().toString();

                    if (!(checkIfUserExist(username, password))) {
                        //Toast.makeText(LogIn.this, "User doesn't exist", Toast.LENGTH_SHORT).show();
                        return super.onOptionsItemSelected(item);
                    }

                   /* // Save user credentials
                    editor.putString(Constants.USERNAME_TAG, username);
                    editor.putString(Constants.PASSWORD_TAG, password);
                    //editor.putBoolean(Constants.REMEMBER_ME_CHECKED, rememberMeCheckBox.isChecked());
                    editor.apply();*/

                    //Toast.makeText(LogIn.this, "Signed in successfully!", Toast.LENGTH_SHORT).show();
                    transitionToNextActivity();
                }
            } else {
                // Handle logout
                rememberUser(false); // Clear saved credentials
                //Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show();
            }
        } else if (item.getItemId() == R.id.create_recipe) {
            startActivity(new Intent(LogIn.this, Sign_up.class)); // Handle sign up
        }

        return super.onOptionsItemSelected(item);
    }

    // Let's check if the user already exist
    public boolean checkIfUserExist(String username, String password) {
        users = getAllUsers();

        if (users == null) {
            //Toast.makeText(LogIn.this , "No users found. Please sign up first.", Toast.LENGTH_SHORT).show();
            return false;
        }

        for (UserInfo user : users) {
            if (user.getUserName().equals(username) && user.getPassword().equals(password)) {
                return true;
            }
        }

        Toast.makeText(LogIn.this, "Wrong password or username", Toast.LENGTH_SHORT).show();

        return false;
    }
}