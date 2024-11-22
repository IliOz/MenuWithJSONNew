package com.example.menuwithjson;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends ArrayAdapter<Recipe> {
    private ArrayList<Recipe> recipes;


    public CustomAdapter(@NonNull Context context, @NonNull ArrayList<Recipe> objects) {
        super(context, 0, objects);
        this.recipes = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.custom_adapter, null);
        }

        Recipe recipe = recipes.get(position);
        TextView nameTextView = convertView.findViewById(R.id.nameEditText);
        nameTextView.setText(recipe.getName());

        CheckBox favoriteCheckBox = convertView.findViewById(R.id.favoriteCheckBox);

        Button delete = convertView.findViewById(R.id.delete);

        favoriteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean flag = false;
                for (int index = 0; index < recipes.size(); index++) {
                    if (recipes.get(index).isFavorite() && recipes.get(index) != recipe) {
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    Toast.makeText(getContext(), "You can't have more than one favorite recipe", Toast.LENGTH_SHORT).show();
                    favoriteCheckBox.setChecked(false);
                }
                else
                    recipe.setFavorite(isChecked);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Delete");
                builder.setMessage("Are you sure you want to delete this recipe?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        recipes.remove(recipe);
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), "Recipe not deleted", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        return convertView;
    }
}
