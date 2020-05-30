package com.example.recipeapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.recipeapp.model.Recipes
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.add_recipe.*


class AddActivity : AppCompatActivity (){

    private val PICK_IMAGE_REQUEST = 1

    lateinit var toolbar: Toolbar
    lateinit var saveButton: Button
    lateinit var chooseimageButton: Button
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var firebaseStorage: FirebaseStorage
    lateinit var storageReference: StorageReference
    lateinit var imageReference: StorageReference
    lateinit var databaseReference: DatabaseReference
    lateinit var newImage: ImageView
    private var imageUri: Uri? = null
    private var downloadUrl: Uri? = null
    lateinit var taskUrl: Task<Uri>
    lateinit var uploadTask: UploadTask
    lateinit var newTitle: EditText
    lateinit var  newCategory: EditText
    lateinit var newIngredients: EditText
    lateinit var newSteps: EditText
    lateinit var ref: DatabaseReference
    lateinit var recipes: Recipes
    lateinit var imageUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_recipe)

        //toolbar
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //actionbar
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = "Add Recipe"

      //  newImage = findViewById(R.id.newrecipe_image)

        //Firebase database
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("Recipes")

        //Firebase storage
        firebaseStorage = FirebaseStorage.getInstance()
       storageReference = firebaseStorage.getReference("Recipes")
    //    databaseReference.child("Recipes").push().key

        //choosebutton
        chooseimageButton = findViewById(R.id.button_chooseimage)
        chooseimageButton.setOnClickListener{
            selectImage()
        }

        newTitle = findViewById(R.id.newrecipe_name)
        newCategory = findViewById(R.id.newrecipe_category)
        newIngredients = findViewById(R.id.newrecipe_ingredients)
        newSteps = findViewById(R.id.newrecipe_steps)
        newImage = findViewById(R.id.newrecipe_image)


        //savebutton
        saveButton = findViewById(R.id.saveButton)
        saveButton.setOnClickListener{

            val newTitle: String = ( newTitle.getText().toString().trim())
            val newCategory: String = newCategory.getText().toString().trim()
            val newIngredients: String = newIngredients.getText().toString().trim()
            val newSteps: String = newSteps.getText().toString().trim()
            var newImage = newImage.toString()

            uploadImage()

            val id: String ?= databaseReference.push().key
            var recipes = Recipes(id, newImage, newTitle, newCategory, newIngredients, newSteps)
            if (id != null) {
                databaseReference.child(id).setValue(recipes)
            }

            Toast.makeText(this,  "Recipe added", Toast.LENGTH_LONG).show()
            finish()
        }

    }


    fun selectImage(){
        val intent =  Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
            && data != null && data.data != null){

            Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show()

            imageUri = data.getData()

            Picasso.get().load(imageUri).into(newrecipe_image)

           //
        }
    }

    fun getFileExtension(uri: Uri?): String? {
        val cR = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri!!))
    }

    fun uploadImage(){

        if(imageUri!=null){

        //imageReference = storageReference.child("uploads/" + imageUri!!.lastPathSegment)
        imageReference = storageReference.child("uploads/"  + getFileExtension(imageUri))

        //start upload
        uploadTask = imageReference.putFile(imageUri!!)

        //observe state change events
        uploadTask.addOnProgressListener{

           fun onProgress (taskSnapshot: UploadTask.TaskSnapshot){

              //  val progress: Double = (100.0 * taskSnapshot.bytesTransferred)/taskSnapshot.totalByteCount
            }
        }.addOnFailureListener{

            @NonNull
            fun onFailure (exception: Exception) {

                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
        }.addOnSuccessListener {
            fun onSuccess(taskSnapshot: UploadTask.TaskSnapshot) {

                val newtitle: String = newTitle.text.toString().trim()
                val newcategory: String = newCategory.text.toString().trim()
                val newingredients: String = newIngredients.text.toString().trim()
                val newsteps: String = newSteps.text.toString().trim()

                Toast.makeText(this, "Upload Successful", Toast.LENGTH_LONG).show()

                taskUrl = taskSnapshot.storage.downloadUrl

                while (!taskUrl.isComplete)
                    downloadUrl = taskUrl.getResult()
                imageUrl = downloadUrl.toString()

                val uploadID: String = databaseReference.push().key.toString()

                var recipes = Recipes(uploadID, imageUrl, newtitle, newcategory, newingredients, newsteps).toString()

                databaseReference.child(uploadID).setValue(recipes)

                //uploadRecipe()


            }
        }
        }
    }

    /*fun buttonuploadRecipe(view: View){

        uploadImage()

    }*/

    /*fun uploadRecipe() {

        val id: String ?= databaseReference.push().key

        val recipes = Recipes(
            id,
            imageUrl,
            newTitle.text.toString().trim(),
            newCategory.text.toString().trim(),
            newIngredients.text.toString().trim(),
            newSteps.text.toString().trim()
        )

        if (id != null) {
            databaseReference.child(id).setValue(recipes).addOnCompleteListener {

                fun onComplete(task: Task<Void>) {

                    if (task.isSuccessful) {
                        Toast.makeText(this, "Recipe Uploaded", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }.addOnFailureListener{
                fun onFailure(exception: Exception){
                    Toast.makeText(this,"Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }*/
}