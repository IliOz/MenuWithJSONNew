package com.example.menuwithjson;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class LogIn extends AppCompatActivity {

    // Declare the UI elements
    private TextView logInText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private CheckBox rememberMeCheckBox;
    private Button logInButton;
    private Button registerButton;


    private String username;
    private String password;
    private boolean rememberMe;
    private UserInfo rememberedUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        initializeUIReferences();
        Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Constants.SHARED_PREF_NAME)){
            rememberUser(false);
        }

        rememberMeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rememberMe = isChecked;
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogIn.this, Sign_up.class));
            }
        });

        rememberMeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rememberUser(isChecked);
            }
        });

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usernameEditText.getText().toString().isEmpty() || passwordEditText.getText().toString().isEmpty()) {
                    Toast.makeText(LogIn.this, "Fill all the slots", Toast.LENGTH_SHORT).show();
                } else {
                    username = usernameEditText.getText().toString();
                    password = passwordEditText.getText().toString();

                    readFromJSON();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //writeDefaultJSON();


    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.REMEMBER_USER, MODE_PRIVATE);
        String rememberedUsername = sharedPreferences.getString(Constants.USERNAME_TAG, null);
        String rememberedPassword = sharedPreferences.getString(Constants.PASSWORD_TAG, null);

        if (rememberedUsername != null && rememberedPassword != null) {
            // User is remembered, redirect to MainActivity
            transitionToNextActivity();
        }
    }

    // Save remembered user in SharedPreferences to skip log in
    public void rememberUser(boolean b) {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.REMEMBER_USER, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (b) {
            Toast.makeText(LogIn.this, "Remember Me", Toast.LENGTH_SHORT).show();
            editor.putString(Constants.USERNAME_TAG, usernameEditText.getText().toString());
            editor.putString(Constants.PASSWORD_TAG, passwordEditText.getText().toString());
            editor.apply();
        } else {
            editor.clear();
            editor.apply();
        }
    }


    // Initialize the UI elements by finding them by their IDs
    public void initializeUIReferences() {
        logInText = findViewById(R.id.log_in);
        usernameEditText = findViewById(R.id.Username);
        passwordEditText = findViewById(R.id.Password);
        rememberMeCheckBox = findViewById(R.id.remember_me);
        logInButton = findViewById(R.id.log_in_button);
        registerButton = findViewById(R.id.register);
        rememberMe = false;
        rememberedUser = null;
    }

    // If info is correct transition to the MainActivity
    public void transitionToNextActivity(){
        Intent intent = new Intent(this ,MainActivity.class);
        intent.putExtra(Constants.USERNAME_TAG, username);
        intent.putExtra(Constants.PASSWORD_TAG, password);
        startActivity(intent);
    }

    // We need to check if there isn't double usernames.
    public void readFromJSON() {
        try {
            // Read the JSON file
            FileInputStream fileInputStream = openFileInput(Constants.USERS_PATH);
            int size = fileInputStream.available();
            byte[] buffer = new byte[size];
            fileInputStream.read(buffer);
            fileInputStream.close();

            // Parse the JSON file into a JSONObject
            String json = new String(buffer, StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(json);

            // Get the JSON array from the JSON object
            JSONArray jsonArray = jsonObject.getJSONArray(Constants.USER_TAG);

            // Flag to track username existence
            boolean userFound = false;

            // Loop through users and check credentials
            for (int index = 0; index < jsonArray.length(); index++) {
                JSONObject obj = jsonArray.getJSONObject(index);
                String name = obj.getString(Constants.USERNAME_TAG);
                String pass = obj.getString(Constants.PASSWORD_TAG);

                if (username.equals(name)) {
                    userFound = true;
                    if (pass.equals(password)) {
                        if (rememberMe) {
                            rememberUser(true);
                        }
                        Toast.makeText(LogIn.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                        transitionToNextActivity();
                        break; // Exit the loop after successful login
                    } else {
                        Toast.makeText(LogIn.this, "Wrong Password!", Toast.LENGTH_SHORT).show();
                        break; // Exit the loop after unsuccessful login attempt for this user
                    }
                }
            }

            // Handle no username match
            if (!userFound) {
                Toast.makeText(LogIn.this, "Invalid Username or Password!", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException | IOException e) {
            throw new RuntimeException(e);
        }
    }

/*    public void readJSONAsset() {
        AssetManager assetManager = getAssets();
        try {
            //InputStream inputStream = assetManager.open(Constants.FILE_PATH);
            int size = inputStream.available(); // How much byte there are
            byte[] buffer = new byte[size]; // Create an array of bytes
            inputStream.read(buffer);
            inputStream.close();
            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(jsonString);

            String name = json.getString("name");
            String age = json.getString("age");

            Toast.makeText(this, "Name= " + name + "\n age=" + age, Toast.LENGTH_SHORT).show();
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }*/

/*    public void writeInternalJSON() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("name", "sadasf");

            String json = jsonObject.toString();
            Toast.makeText(this, json, Toast.LENGTH_SHORT).show();
            FileOutputStream fileOutputStream = openFileOutput(Constants.USERS_PATH, Context.MODE_PRIVATE);
            OutputStreamWriter outputStream = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);

            BufferedWriter bufferedWriter = new BufferedWriter(outputStream);
            bufferedWriter.write(json);
            bufferedWriter.close();
            Toast.makeText(this, json, Toast.LENGTH_SHORT).show();
        } catch (JSONException | IOException e) {
            throw new RuntimeException(e);
        }
    }*/
}