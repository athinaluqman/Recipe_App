package com.example.recipeapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.recipeapp.model.Recipes
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
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
    private var downloadUri: Uri? = null
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

        newTitle = findViewById(R.id.newrecipe_name)
        newCategory = findViewById(R.id.newrecipe_category)
        newIngredients = findViewById(R.id.newrecipe_ingredients)
        newSteps = findViewById(R.id.newrecipe_steps)
        newImage = findViewById(R.id.newrecipe_image)


    }


    fun selectImage(view: View){
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

    fun getFileExtension(uri: Uri?): String ?{
        val cR = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri!!))
    }


    fun uploadImage(){

        imageReference = storageReference.child("uploads/" + imageUri!!.lastPathSegment.toString() + getFileExtension(imageUri).toString())

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Recipe Uploading....")
        progressDialog.show()

        //start upload
        uploadTask = imageReference.putFile(imageUri!!)

        //observe state change events
        uploadTask.addOnSuccessListener (object:OnSuccessListener <UploadTask.TaskSnapshot>{

            override fun onSuccess(taskSnapshot: UploadTask.TaskSnapshot) {

                Toast.makeText(this@AddActivity, "Upload Successful", Toast.LENGTH_SHORT).show()

                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }


                    //get download URL from Firebase Storage
                    imageReference.downloadUrl.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            downloadUri = task.getResult()
                            imageUrl = downloadUri.toString()
                            Picasso.get().load(imageUrl).into(newrecipe_image)
                            uploadRecipe()
                            progressDialog.dismiss()
                        }else{
                            Toast.makeText(this@AddActivity, "Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }

        }).addOnFailureListener(object:OnFailureListener{

            @NonNull
            override fun onFailure (exception: Exception) {

                progressDialog.dismiss()
                Toast.makeText(this@AddActivity, "Error", Toast.LENGTH_SHORT).show()
            }
        }).addOnProgressListener (object:OnProgressListener<UploadTask.TaskSnapshot>{
            override fun onProgress (taskSnapshot: UploadTask.TaskSnapshot){

                val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
                progressDialog.incrementProgressBy(progress.toInt())
            }
        })
    }

    fun buttonuploadRecipe(view: View){

        uploadImage()

    }

    fun uploadRecipe() {

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
            databaseReference.child(id).setValue(recipes).addOnCompleteListener(object: OnCompleteListener <Void> {

                override fun onComplete(task: Task<Void>) {

                    if (task.isSuccessful) {
                        Toast.makeText(this@AddActivity, "Recipe Uploaded", Toast.LENGTH_SHORT).show()

                        finish()
                    }
                }
            }).addOnFailureListener(object:OnFailureListener{
                override fun onFailure(exception: Exception){
                    Toast.makeText(this@AddActivity,"Failed", Toast.LENGTH_SHORT).show()
                }
            })
        }

    }
}
