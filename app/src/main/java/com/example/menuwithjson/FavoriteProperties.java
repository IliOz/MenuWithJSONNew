package com.example.menuwithjson;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class FavoriteProperties extends AppCompatActivity {
    TextView favoriteRecipeTitle, favoriteRecipeName, favoriteDishType,
            favoriteIsVegan, favoriteRecipeInstructions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_propreties);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initialize_views();
        Intent intent = getIntent();
        if (intent != null) {
            String recipeName = intent.getStringExtra(Constants.RECIPE_NAME_TAG);
            String recipeInstructions = intent.getStringExtra(Constants.RECIPE_INSTRUCTIONS_TAG);
            String recipeDishType = intent.getStringExtra(Constants.RECIPE_DISH_TYPE_TAG);
            boolean isVegan = intent.getBooleanExtra(Constants.RECIPE_IS_VEGAN_TAG, false);

            favoriteRecipeName.setText("Name: " + recipeName);
            favoriteDishType.setText("Dish Type: " + recipeDishType);
            favoriteIsVegan.setText("Is Vegan: " + isVegan);
            favoriteRecipeInstructions.setText("Instructions: " + recipeInstructions);
        }
    }

    public void initialize_views(){
        favoriteRecipeTitle = findViewById(R.id.favorite_recipe_title);
        favoriteRecipeName = findViewById(R.id.favorite_recipe_name);
        favoriteDishType = findViewById(R.id.favorite_dish_type);
        favoriteIsVegan = findViewById(R.id.favorite_is_vegan);
        favoriteRecipeInstructions = findViewById(R.id.favorite_recipe_instructions);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem log_out = menu.findItem(R.id.log_out);
        log_out.setTitle("Log out");

        MenuItem create_recipe = menu.findItem(R.id.create_recipe);
        create_recipe.setVisible(false);

        MenuItem favorite = menu.findItem(R.id.favorite);
        favorite.setTitle("Go back");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.log_out) {
            Intent intent = new Intent(FavoriteProperties.this, LogIn.class);
            intent.putExtra(Constants.SHARED_PREF_NAME, Constants.SHARED_PREF_NAME);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.favorite) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}