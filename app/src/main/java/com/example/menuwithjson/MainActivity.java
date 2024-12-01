package com.example.menuwithjson;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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


public class MainActivity extends AppCompatActivity {

    private String username;
    private ListView listView;
    private Toolbar toolbar;
    private CustomAdapter adapter;
    private ArrayList<Recipe> recipes;
    private ArrayList<UserInfo> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setSupportActionBar(toolbar);

        // Return the username of the user that logged in or signed up
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Constants.USERNAME_TAG)){
            username = intent.getStringExtra(Constants.USERNAME_TAG);
        }

        /*SharedPreferences sharedPreferences = getSharedPreferences(Constants.REMEMBER_USER, MODE_PRIVATE);

        // Retrieve the username and remember me checkbox state
        String username = sharedPreferences.getString(Constants.USERNAME_TAG, "");
        boolean rememberMeChecked = sharedPreferences.getBoolean(Constants.REMEMBER_ME_CHECKED, false);

        // If the "Remember Me" checkbox was checked and the username exists, stay logged in
        if (rememberMeChecked && !username.isEmpty()) {
            // Username exists and Remember Me was checked, proceed to the main activity
            // You can show the main screen or any necessary behavior here
            // For example, you can navigate to the home screen if needed
            Toast.makeText(this, "Welcome back, " + username, Toast.LENGTH_SHORT).show();
        } else {
            // If no username is saved or Remember Me was not checked, redirect to the login activity
            Intent intent1 = new Intent(MainActivity.this, LogIn.class);
            startActivity(intent1);
            finish();
        }*/



        users = getAllUsers();
        recipes = new ArrayList<>();
        for (UserInfo user : users) {
            if (user.getUserName().equals(username)) {
                recipes = user.getRecipes();
            }
        }

        adapter = new CustomAdapter(this, recipes, users, username);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // If the recipe is already favorite, toggle it off
                Recipe clickedRecipe = recipes.get(i);
                if (clickedRecipe.isFavorite()) {
                    clickedRecipe.setFavorite(false);
                    view.setBackgroundColor(Color.WHITE);
                } else {
                    // Handle setting favorite and background color
                    boolean canBeFavorite = true;
                    for (Recipe recipe : recipes) {
                        if (recipe.isFavorite()) {
                            canBeFavorite = false;
                            break;
                        }
                    }

                    if (canBeFavorite) {
                        clickedRecipe.setFavorite(true);
                        view.setBackgroundColor(Color.GREEN);
                        Toast.makeText(MainActivity.this, "Favorite recipe: " + clickedRecipe.getName(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "You can't have more than one favorite recipe", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is logged in
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.REMEMBER_USER, MODE_PRIVATE);
        String username = sharedPreferences.getString(Constants.USERNAME_TAG, null);

        if (username == null && username.isEmpty()) {
            Intent intent = new Intent(MainActivity.this, LogIn.class);
            startActivity(intent);
            finish();
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
                ArrayList<Recipe> recipes = new ArrayList<>();
                JSONArray recipesArray = jsonObject.getJSONArray(Constants.RECIPE_TAG);

                // Parse recipes
                for (int j = 0; j < recipesArray.length(); j++) {
                    JSONObject recipeObject = recipesArray.getJSONObject(j);
                    recipes.add(new Recipe(recipeObject));
                }

                // Create UserInfo object with recipes
                UserInfo user = new UserInfo(username, password, recipes);
                users.add(user);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    // Initialize views
    public void initializeViews() {
        listView = findViewById(R.id.recipe_list);
        users = new ArrayList<>();
        listView.setAdapter(adapter);
        toolbar = findViewById(R.id.toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem log_out = menu.findItem(R.id.log_out);
        log_out.setTitle("Log out");

        MenuItem create_recipe = menu.findItem(R.id.create_recipe);
        create_recipe.setTitle("Create recipe");

        MenuItem favorite = menu.findItem(R.id.favorite);
        favorite.setTitle("Favorite");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.log_out) {
/*            SharedPreferences sharedPreferences = getSharedPreferences(Constants.REMEMBER_USER, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Constants.USERNAME_TAG, "");
            editor.putString(Constants.PASSWORD_TAG, "");
            editor.apply();*/

            Intent intent = new Intent(MainActivity.this, LogIn.class);
            intent.putExtra(Constants.SHARED_PREF_NAME, Constants.SHARED_PREF_NAME);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.create_recipe) {
            // Send current username
            Intent intent = new Intent(this, CreateRecipe.class);
            intent.putExtra(Constants.USERNAME_TAG, username);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.favorite) {
            users = getAllUsers();
            recipes = new ArrayList<>();
            for (UserInfo user : users) {
                if (user.getUserName().equals(username)) {
                    recipes = user.getRecipes();
                }
            }

            Intent intent = new Intent(this, FavoriteProperties.class);
            // Send recipe username, recipe name, using the recipe name find all his info
            for (Recipe recipe : recipes) {
                if (recipe.isFavorite()) {
                    intent.putExtra(Constants.RECIPE_NAME_TAG, recipe.getName());
                    intent.putExtra(Constants.RECIPE_INSTRUCTIONS_TAG, recipe.getInstructions());
                    intent.putExtra(Constants.RECIPE_DISH_TYPE_TAG, recipe.getDishType());
                    intent.putExtra(Constants.RECIPE_IS_VEGAN_TAG, recipe.isVegan());
                    startActivity(intent);
                }
            }
            Toast.makeText(this, "No favorite recipe", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}