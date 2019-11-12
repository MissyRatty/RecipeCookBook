package com.example.ratty.recipecookbook;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ViewRecipeItem extends AppCompatActivity {
    //get the ID of current Record
    String myId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe_item);

        //get Id, title and description from the MainActivity
        Bundle myData = getIntent().getExtras();
        String Id = myData.getString("ID");
        myId = Id;
        String Title = myData.getString("Title");
        String Desc = myData.getString("Desc");

        //get the id,title and description textboxes and label
        EditText myIDTxtbx = (EditText)findViewById(R.id.vwIDTxtbx);
        EditText myTitleTxtbx = (EditText)findViewById(R.id.vwTitleTxtbx);
        EditText myDescTxtbx = (EditText)findViewById(R.id.vwDescrTxtbx);
        TextView myTit = (TextView)findViewById(R.id.ViewTxVw);
        myTit.setText(myTit.getText().toString() + Id);

        //set the values from the bundle to the id,title and description textboxes
        myIDTxtbx.setText(Id);
        myTitleTxtbx.setText(Title);
        myDescTxtbx.setText(Desc);
    }

    //call this to open the Edit Recipe Activity
    public void openEditRecipe(View v)
    {
        //create intent instance
        Intent myIntent = new Intent(this, EditRecipe.class);

        //get the current values from the textboxes
        EditText myIDTxtbx = (EditText)findViewById(R.id.vwIDTxtbx);
        EditText myTitleTxtbx = (EditText)findViewById(R.id.vwTitleTxtbx);
        EditText myDescTxtbx = (EditText)findViewById(R.id.vwDescrTxtbx);

        //pass the values into the intent
        myIntent.putExtra("ID", myIDTxtbx.getText().toString());
        myIntent.putExtra("Title", myTitleTxtbx.getText().toString());
        myIntent.putExtra("Desc", myDescTxtbx.getText().toString());

        //start the Edit Recipe Activity
        //and pass the intent to it
        startActivity(myIntent);
    }

    //call this method to delete the recipe
    //it calls the MyAlert method
    //to show a dialog for the user to decide whether to delete or not
    public void delRecipe(View v)
    {
      MyAlert();
    }

    //this method passes the current ID to the contentProvider
    //calls the delete method and adds the Where part of the query
    //with the ID
    public void Deletion()
    {
        EditText myIDTxtbx = (EditText)findViewById(R.id.vwIDTxtbx);
        myId = myIDTxtbx.getText().toString();

        //get current ID
        String selection = "_ID = " + myId;

        //get number of records deleted
        int rowsDeleted = getContentResolver().delete(MyProviderContract.MY_RECIPE_URI,selection,null);

        //if > 0
        //then alert user that data was deleted
        if(rowsDeleted > 0)
        {
            Toast.makeText(getApplicationContext(), "Delete successful", Toast.LENGTH_SHORT).show();
            Intent myInt = new Intent(this,MainActivity.class);
            startActivity(myInt);
        }

        //if = 0
        //then no data was deleted
        if(rowsDeleted == 0)
        {
            Toast.makeText(getApplicationContext(), "Delete Error.. Please check data", Toast.LENGTH_SHORT).show();
        }


    }

    //prompt user if data is to be deleted
    //if yes, delete
    //if no, do not delete
    public void MyAlert()
    {
        AlertDialog myDialog = new AlertDialog.Builder(this)
                .setTitle("Delete Recipe: " + myId)
                .setMessage("Are you sure you want to delete ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Deletion();
                        dialog.dismiss();
                    }
                })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();

        myDialog.show();

    }
}


//References
//1.AlertDialog
//https://developer.android.com/reference/android/app/AlertDialog.html