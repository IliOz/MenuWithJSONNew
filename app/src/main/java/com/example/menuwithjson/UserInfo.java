package com.example.menuwithjson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserInfo {
    private String userName;
    private String password;
    private ArrayList<Recipe> recipes;

    public UserInfo(String name, String pass, ArrayList<Recipe> recipes){
        this.userName = name;
        this.password = pass;
        if (recipes == null)
            this.recipes = new ArrayList<Recipe>();
        else
            this.recipes = recipes;
    }

    public void addRecipe(Recipe recipe){
        if (this.recipes != null)
            this.recipes.add(recipe);
    }

    public ArrayList<Recipe> getRecipes(){
        return this.recipes;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setRecipes(ArrayList<Recipe> recipes) {
        this.recipes = recipes;
    }


    public JSONObject toJSON() {
        try {
            JSONObject json = new JSONObject();
            json.put(Constants.USERNAME_TAG, this.userName);
            json.put(Constants.PASSWORD_TAG, this.password);

            JSONArray recipesArray = new JSONArray();
            for (Recipe recipe : this.recipes) {
                recipesArray.put(recipe.toJSON());
            }
            json.put(Constants.RECIPE_TAG, recipesArray);

            return json;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        if (recipes != null) {
            String s = "";
            for (Recipe recipe : recipes) {
                s += recipe.toString() + " ";
            }
            return "UserInfo{" +
                    "userName='" + this.userName + '\'' +
                    ", password='" + this.password + '\'' +
                    ", recipes=" + s +
                    '}';
        }
        return "UserInfo{" +
                "userName='" + this.userName + '\'' +
                ", password='" + this.password + '\'' +
                ", recipes= null" +
                '}';
    }
}
