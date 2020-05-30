package com.example.recipeapp.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.model.Recipes
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_listrecipes.view.*


class RecipesViewHolder(itemView: View, var recipes: Recipes? = null) : RecyclerView.ViewHolder(itemView) {

    val Icon = itemView.recipe_image
    val Name = itemView.recipe_name
    val Category = itemView.recipe_category
    val Ingredients = itemView.recipe_ingredients
    val Steps = itemView.recipe_steps
    val cardView = itemView.cardview

    fun bind(recipes: Recipes) {
        // icon.setImageResource(recipes.Icon)
        with(recipes) {
            /*val Name = itemView.recipe_name
        val Icon = itemView.recipe_image*/

            Name.text = recipes.name
            Category.text = recipes.category
            Ingredients.text = recipes.ingredients
            Steps.text = recipes.steps
            Picasso.get().load(recipes.icon).into(Icon)


        }

    }


}

