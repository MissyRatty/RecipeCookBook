package com.example.ratty.recipecookbook;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class AddNewRecipe extends AppCompatActivity {


    //constant to check which activity is returning the result
    final static int ACTIVITY_IMG_PICKER_REQUEST_CODE = 1;
    Uri myImgDir = null;
    String picPath = null;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    //call this method to check if application has READ and WRITE permissions
    //if not Granted, prompt user to allow app to access photos
    public static void Granted_Permissions(Activity activity) {
        // Check if we have READ/WRITE permissions
        int perm4Write = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int perm4Read = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);



        //if permission is not granted
        //prompt user to give permission
        if (perm4Write != PackageManager.PERMISSION_GRANTED || perm4Read != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_recipe);
    }

    //call this method to Save recipe details into database using the contentProvider
    public void saveRecipe(View v) {
        //get the title and description entered by user
        //get the File path for the Image selected from the OnActivityResult Method
        final EditText myTitle = (EditText) findViewById(R.id.newTitleTxtbx);
        final EditText myDescription = (EditText) findViewById(R.id.newDescrTxtbx);
        final String myImgPath = picPath != null ? picPath : null;
        Uri newRec = null;

        Log.d("trying", "saveRecipe:" + myImgPath);

        String title = myTitle.getText().toString();
        String descr = myDescription.getText().toString();


        //pass data into the contentValues
        //map contentValues to ProviderContract class fields
        //Get the ContentProvider and call insert method for insertion of data
        //Check if title and description is not null
        //to prevent inserting null values into the DB
        if ((title != null && descr != null))
        {
            ContentValues newValues;
            newValues = new ContentValues();
            newValues.put(MyProviderContract.TITLE, title);
            newValues.put(MyProviderContract.DESCRIPTION, descr);
            newValues.put(MyProviderContract.IMGDIR, myImgPath);

            //the new URI for the data just inserted is returned
            newRec = getContentResolver().insert(MyProviderContract.MY_RECIPE_URI, newValues);

            //check if a URI is returned
            //if URI is returned then data was inserted
            //prompt user that Data is inserted successfully
            if (newRec != null) {
                Toast.makeText(this, "Data is saved successfully", Toast.LENGTH_SHORT).show();
            }

            Intent myInt = new Intent(this, MainActivity.class);
            startActivity(myInt);


        }

        //if no URI is returned
        //then no Data was saved or inserted
        if (newRec == null) {
            Toast.makeText(this, "Data Error: Please check your entries", Toast.LENGTH_SHORT).show();
        }

    }

    //end the current Activity
    public void cancelRecipe(View v) {
        finish();
    }

    //call this method to launch the Image Picker
    public void uploadPic(View v) {
        //Start the myPicker Activity
        //To Do This
        //Create an instance of the Intent class
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, ACTIVITY_IMG_PICKER_REQUEST_CODE);
    }


    //this is to handle the data (ContentUri) that is returned after an image is picked to this activity
    //Pass the selected File path to the SaveRecipe method for Insertion into DATABASE
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            //get the URI from the intent
            myImgDir  = data.getData();

            //get the ID of the item in this URI
            //convert to string
            //and use that as the fileName
            //outputStream variable to compress from bitmap to bytes
            Long myImgID = ContentUris.parseId(myImgDir);
            String newID = Long.toString(myImgID);
            OutputStream fOutputStream = null;

            try {
                //convert URI to bitmap
                //get the Pictures directory
                Bitmap bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(),myImgDir);
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

                //Check for Read and Write permissions
                //call Granted_Permissions
                Granted_Permissions(this);


                //check if the Path Exists
                if(!path.exists())
                {
                    path.mkdirs();
                }

                try
                {
                    //create a file in the Pictures directory and add filename
                    //filename here = the ID taken from the URI to the image(content:authority/path/ID)
                    //create new file if it doesn't exist
                    File pic = new File(path,newID);
                    if(!pic.exists())
                    {
                        pic.createNewFile();
                    }
                    //compress pic into Jpeg format
                    fOutputStream = new FileOutputStream(pic);
                    bmp.compress(Bitmap.CompressFormat.JPEG,60,fOutputStream);

                    //Flushes this output stream and forces any buffered output bytes to be written out.
                    fOutputStream.flush();
                    //Closes this output stream and releases any system resources associated with this stream.
                    fOutputStream.close();

                    //call the Content Provider(MediaStore)insert new file
                    MediaStore.Images.Media.insertImage(getContentResolver(), pic.getAbsolutePath(), pic.getName(), pic.getName());


                    //get the file path to be saved into the DB
                    picPath = pic.getAbsolutePath();
                    Toast.makeText(this, "Image Uploaded Successfully... ", Toast.LENGTH_SHORT).show();
                }
                catch (FileNotFoundException e) {
                    Toast.makeText(this, "Save Failed..", Toast.LENGTH_SHORT).show();
                    return;
                } catch (IOException e) {
                    Toast.makeText(this, "Save Failed..", Toast.LENGTH_SHORT).show();
                    return;
                }



            }
            catch (IOException e) {
                e.printStackTrace();
            }

        }

        }
    }


//References:
//FileOutputStream
//1.https://developer.android.com/reference/java/io/OutputStream.html
//File
//2.https://developer.android.com/reference/java/io/File.html
//Permissions
//3.https://developer.android.com/reference/android/Manifest.permission.html
//Read and Store data in External Storage
//4.https://developer.android.com/training/basics/data-storage/files.html