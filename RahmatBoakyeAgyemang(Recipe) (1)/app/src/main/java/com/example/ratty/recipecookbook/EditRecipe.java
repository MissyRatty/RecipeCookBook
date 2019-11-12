package com.example.ratty.recipecookbook;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditRecipe extends AppCompatActivity {

    //variable for getting ID of current Record
    String myID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);

        //get Data passed into Intent from ViewRecipe Activity
        //get ID, Title and Description
        Bundle myData = getIntent().getExtras();
        String Id = myData.getString("ID");
        myID = myData.getString("ID");
        String Title = myData.getString("Title");
        String Desc = myData.getString("Desc");

        //get the id, title and description label and textboxes
        TextView myIDTxtbx = (TextView) findViewById(R.id.editTxVw);
        EditText myTitleTxtbx = (EditText)findViewById(R.id.editTitleTxtbx);
        EditText myDescTxtbx = (EditText)findViewById(R.id.editDescrTxtbx);

        //set the values from the intent into id,title and description views
        //for user viewing
        myIDTxtbx.setText("Editing Recipe Number: " + Id);
        myTitleTxtbx.setText(Title);
        myDescTxtbx.setText(Desc);
    }

    //when user clicks update button
    public void updateRecipe(View v)
    {
        //get title and description textboxes
        EditText myTitleTxtbx = (EditText)findViewById(R.id.editTitleTxtbx);
        EditText myDescTxtbx = (EditText)findViewById(R.id.editDescrTxtbx);

        //variable to check for number of updated records from ContentProvider Update method
        int recordsUpdated = 0;

        //convert to string
        String tit = myTitleTxtbx.getText().toString();
        String descr = myDescTxtbx.getText().toString();

        //check if data is not null or empty before updating
        if(tit != null && descr != null)
        {
            if(tit != "" && descr != "")
            {
                //pass new values into the contentValues
                //map contentValues to the ProviderContract class fields
                ContentValues newValues;
                newValues = new ContentValues();
                newValues.put(MyProviderContract.TITLE,tit);
                newValues.put(MyProviderContract.DESCRIPTION,descr);

                //pass the where part of the query
                //add the current record ID to the query
                String selection = "_ID = " + myID;

                //number of records updated returned as Int
                recordsUpdated = getContentResolver().update(MyProviderContract.MY_RECIPE_URI,newValues,selection,null);

                //if number of records count returned is not 0
                //then the record was updated
                //prompt user
                if(recordsUpdated != 0)
                {
                    Toast.makeText(this, "Data updated successfully", Toast.LENGTH_SHORT).show();
                }

                //go back to MainActivity
                Intent myInt = new Intent(this, MainActivity.class);
                startActivity(myInt);

            }
        }

        //if number of records count returned is o
        //then no data was updated
        //prompt user
        if(recordsUpdated == 0)
        {
            Toast.makeText(this, "Update Error, Please check your entries", Toast.LENGTH_SHORT).show();
        }
    }

    //user clicks cancel
    //end this current activity
    public void editCancelRecipe(View v)
    {
      finish();
    }

}


//References:
//Content Providers
//1.https://developer.android.com/reference/android/content/ContentProvider.html