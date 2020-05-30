package com.example.recipeapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.recipeapp.model.Recipes
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso


class RecipeActivity : AppCompatActivity() {

    lateinit var foodimage: ImageView
    lateinit var foodtext: TextView
    lateinit var foodcategory: TextView
    lateinit var foodingredients: TextView
    lateinit var foodsteps: TextView
    lateinit var toolbar: Toolbar
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference
    lateinit var firebaseStorage: FirebaseStorage
    lateinit var storageReference: StorageReference
    lateinit var recipes: Recipes
    var key: String = ""
    var imageUrl: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recipes_detail)

        //toolbar
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //actionbar
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = "Recipes"

        recipes = Recipes()

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("Recipes")

        firebaseStorage = FirebaseStorage.getInstance()
        // storageReference = firebaseStorage.getReferenceFromUrl(imageUrl)



        foodimage = findViewById(R.id.recipesimage)
        foodtext = findViewById(R.id.recipestitle)
        foodcategory = findViewById(R.id.recipescategory)
        foodingredients = findViewById(R.id.recipesingredients)
        foodsteps = findViewById(R.id.recipessteps)

        val bundle: Bundle? = intent.extras

        if (bundle != null) {
            val Icon: String? = intent.getStringExtra("Icon")

            Picasso.get().load(Icon).into(foodimage)

            key = bundle.getString("KeyValue").toString()
            imageUrl = bundle.getString("Icon").toString()

            foodtext.setText(bundle.getString("Name"))
            foodcategory.setText(bundle.getString("Category"))
            foodingredients.setText(bundle.getString("Ingredients"))
            foodsteps.setText(bundle.getString("Steps"))
        }


        /*databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                *//*for (data in dataSnapshot.children) {
                    *//**//*if (data.key == "Ingredients") {
                        val orderNumber = data.value.toString()
                       Log.d("Specific Node Value", orderNumber)
                    }*//**//*

                    val value = data.child("Ingredients").getValue(Recipes::class.java)
                    foodingredients.text = value.toString()
                }*//*

               *//* for (uniquekey in dataSnapshot.children) {

                    for (ingredientsdata in uniquekey.child("Ingredients").children) {

                       *//**//* val value = data.child("Ingredients").getValue(String::class.java)*//**//*

                        val key = uniquekey.key
                        val value = ingredientsdata.getValue(String::class.java)
                        foodingredients.setText(value)
                    }
                }*//*

                for (data in dataSnapshot.children){

                    val recipeid = data.key
                    val value = data.child("Ingredients").getValue(String::class.java)

                    foodingredients.setText(value)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })*/

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.recipesaction, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //menu item selected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.delete -> {
              //  deleteRecipe()
                return true
            }
            R.id.update -> {
               //  updateRecipe()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun deleteRecipe() {
        databaseReference = firebaseDatabase.getReference("Recipes")

        storageReference = firebaseStorage.getReferenceFromUrl(imageUrl)

       // databaseReference.child(key).removeValue()
        storageReference.delete().addOnSuccessListener {
            fun onSuccess (void: Void){
            databaseReference.child(key).removeValue()
                Toast.makeText(this, "Recipe Deleted", Toast.LENGTH_SHORT).show()
                val intent = Intent (this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        }

    fun updateRecipe() {

        val intent = Intent(this, UpdateActivity::class.java)

        intent.putExtra("Icon", recipes.icon)
        intent.putExtra("Name", recipes.name)
        intent.putExtra("Category", recipes.category)
        intent.putExtra("Ingredients", recipes.ingredients)
        intent.putExtra("Steps", recipes.steps)
        intent.putExtra("KeyValue", recipes.id)
        startActivity(intent)
    }


}







                /*//   firebaseStorage.getReference(imageUrl).delete().addOnSuccessListener {
                       databaseReference.child(key).removeValue()

                       Toast.makeText(this, "Recipe Deleted", Toast.LENGTH_SHORT).show()

                       val intent = Intent(applicationContext, MainActivity::class.java)
                       startActivity(intent)
                       finish()*/


        /*  databaseReference.addListenerForSingleValueEvent (object : ValueEventListener {
            override fun onDataChange (dataSnapshot: DataSnapshot){
                if (dataSnapshot != null && dataSnapshot.getValue() !=null){
                    val key: String ? = dataSnapshot.key
                }

                databaseReference.child()
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })*/
        /* databaseReference.removeValue()
        Toast.makeText(this, "Recipe Deleted", Toast*/
