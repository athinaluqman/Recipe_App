package com.example.recipeapp

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.viewholder.RecipesViewHolder
import com.example.recipeapp.model.Recipes
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*


class MainActivity : AppCompatActivity(){

    lateinit var recyclerView: RecyclerView
    lateinit var toolbar: Toolbar
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference
    lateinit var addButton: FloatingActionButton
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var firebaseRecyclerAdapter: FirebaseRecyclerAdapter<Recipes, RecipesViewHolder>
    lateinit var query: Query

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //toolbar
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //actionbar
        val actionBar = supportActionBar
        actionBar?.title = "Recipe App"

        //button
        addButton = findViewById(R.id.addButton)
        addButton.setOnClickListener{
            val intent = Intent(this@MainActivity, AddActivity::class.java)
            startActivity(intent)
        }

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("Recipes")

        //recyclerview
        recyclerView = findViewById(R.id.recyclerviewrecipes)
        // recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val newoptions = FirebaseRecyclerOptions.Builder<Recipes>()
            .setQuery(databaseReference, Recipes::class.java)
            .build()

        firebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<Recipes, RecipesViewHolder>(newoptions) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipesViewHolder {
                return RecipesViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_listrecipes, parent, false)
                )
            }

            override fun onBindViewHolder(p0: RecipesViewHolder, p1: Int, p2: Recipes) {
                p0.bind(p2)

                p0.cardView.setOnClickListener {

                    val intent = Intent(this@MainActivity, RecipeActivity::class.java)

                    intent.putExtra("Icon", p2.icon)
                    intent.putExtra("Name", p2.name)
                    intent.putExtra("Category", p2.category)
                    intent.putExtra("Ingredients", p2.ingredients)
                    intent.putExtra("Steps", p2.steps)
                    intent.putExtra("KeyValue", p2.id)
                    startActivity(intent)
                }
            }

        }

        recyclerView.adapter = firebaseRecyclerAdapter
        firebaseRecyclerAdapter.startListening()

        //set spinner
        val recipetypes = resources.getStringArray(R.array.recipe_types)

        val spinner = findViewById<Spinner>(R.id.spinner)

        if (spinner != null) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, recipetypes)
            spinner.adapter = adapter

            adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    Toast.makeText(
                        this@MainActivity,
                        "Selected Category:" + recipetypes[position],
                        Toast.LENGTH_SHORT
                    ).show()

                    val spinnerCategory: String = parent?.getItemAtPosition(position).toString()

                    //spinner options when selected
                    if (spinnerCategory.equals("Asian Cuisine")) {
                        query = databaseReference.orderByChild("category").equalTo("Asian Cuisine")
                    } else if (spinnerCategory.equals("Western Cuisine")) {
                        query = databaseReference.orderByChild("category").equalTo("Western Cuisine")
                    } else if (spinnerCategory.equals("Indian Cuisine")) {
                        query = databaseReference.orderByChild("category").equalTo("Indian Cuisine")
                    } else if (spinnerCategory.equals("Italian Cuisine")) {
                        query = databaseReference.orderByChild("category").equalTo("Italian Cuisine")
                    } else if (spinnerCategory.equals("Vegan Cuisine")) {
                        query = databaseReference.orderByChild("category").equalTo("Vegan Cuisine")
                    }else if (spinnerCategory.equals("All")) {
                        query = databaseReference
                    }

                    filteredData(query)

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }
        }

    }

    //create new query for spinner
    fun filteredData(category: Query){

        val newoptions = FirebaseRecyclerOptions.Builder<Recipes>()
            .setQuery(category, Recipes::class.java)
            .build()

        val newRecyclerAdapter = object : FirebaseRecyclerAdapter<Recipes, RecipesViewHolder>(newoptions) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipesViewHolder {
                return RecipesViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_listrecipes, parent, false)
                )
            }

            override fun onBindViewHolder(p0: RecipesViewHolder, p1: Int, p2: Recipes) {
                p0.bind(p2)

                p0.cardView.setOnClickListener {

                    val intent = Intent(this@MainActivity, RecipeActivity::class.java)

                    intent.putExtra("Icon", p2.icon)
                    intent.putExtra("Name", p2.name)
                    intent.putExtra("Category", p2.category)
                    intent.putExtra("Ingredients", p2.ingredients)
                    intent.putExtra("Steps", p2.steps)
                    intent.putExtra("KeyValue", p2.id)
                    startActivity(intent)
                }
            }

        }

        recyclerView.adapter = newRecyclerAdapter
        newRecyclerAdapter.startListening()

    }



    //menu inflater
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.mainmenu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //menu item selected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.exit -> {
                finishAffinity()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        //firebaseRecyclerAdapter.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        firebaseRecyclerAdapter.stopListening()
    }


}

