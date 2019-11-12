package com.example.ratty.recipecookbook;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PermissionInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    SimpleCursorAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String mySearchQuery = null;
        String[] mySearchArray = null;

        //refresh ListView with Data from DB
        queryContentProvider(mySearchQuery,mySearchArray);

        //set Item click Listener on Items in the ListView
        //open View Recipe Details on Item Click
        final ListView myLst = (ListView)findViewById(R.id.listView);
        myLst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final TextView myItemID = (TextView) view.findViewById(R.id.value1);
                final TextView myItemTitle = (TextView)view.findViewById(R.id.value2);
                final TextView myItemDesc = (TextView)view.findViewById(R.id.value3);

                String ID = myItemID.getText().toString();
                String Title = myItemTitle.getText().toString();
                String Desc = myItemDesc.getText().toString();

                Intent view_data_intent = new Intent(getApplicationContext(),ViewRecipeItem.class);
                view_data_intent.putExtra("ID",ID );
                view_data_intent.putExtra("Title", Title);
                view_data_intent.putExtra("Desc", Desc);
                startActivity(view_data_intent);
            }
        });

        //add on TextChanged listener to Search TextBox
        final EditText mySearch = (EditText)findViewById(R.id.searchTxtbx);
        mySearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("before", "beforeTextChanged:" + s + start + count + after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("before", "beforeTextChanged:" + s + start + count + before);

                  String searchVal = mySearch.getText().toString();
                  String selection = "TITLE like ?";
                  String[] selectionArgs = new String[]{"%"+ searchVal +"%"};
                  queryContentProvider(selection,selectionArgs);
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("before", "beforeTextChanged:" + s );
            }
        });
    }

    //when add recipe button is clicked
    //start the add new Recipe Activity
    public  void AddNewRecipe(View v)
    {
        Intent myIntent = new Intent(this,AddNewRecipe.class);
        startActivity(myIntent);
    }

    //bind data to ListView from DB
    public void queryContentProvider(String search, String[] searchArray) {

       ListView myLstVw = (ListView)findViewById(R.id.listView);

        //A "projection" defines the columns that will be returned for each row
        String[] projection = new String[] {
                MyProviderContract._ID,
                MyProviderContract.TITLE,
                MyProviderContract.DESCRIPTION,
                MyProviderContract.IMGDIR
        };


        //cols to show to user
        String colsToDisplay [] = new String[] {
                MyProviderContract._ID,
                MyProviderContract.TITLE,
                MyProviderContract.DESCRIPTION,
                MyProviderContract.IMGDIR
        };

        //int for IDs
        int [] colResIDs = new int[]
                {
                        R.id.value1,
                        R.id.value2,
                        R.id.value3,
                        R.id.img
                };


        //query DB using the ContentProvider for data
        //returns a cursor
        Cursor cursor = getContentResolver().query(MyProviderContract.MY_RECIPE_URI, projection, search, searchArray, null);


        //pass cursor(containing Data), myLayout to display data, columns to display, and Col Mappings to Adapter
        dataAdapter = new SimpleCursorAdapter(this,R.layout.db_item_layout,cursor,colsToDisplay,colResIDs,0);
        //since this data has file path to Images
        //I call the setViewBinder method of the simpleCursorAdapter
        //get the column for the image paths
        //convert to bitmap
        //and pass the bitmap to the imageview
        dataAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == R.id.img)
                {
                    // Get the String URI from the database.
                    String imgPath = cursor.getString(columnIndex);
                    Bitmap bmp = null;

                        //Resize Bitmap to avoid out of Memory errors
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 8;
                    try
                    {
                        //check if image path is null or not
                        //to prevent decodeFile method null pointer reference
                        if(imgPath != null)
                        {
                            //get image
                            bmp = BitmapFactory.decodeFile(imgPath,options);
                        }

                        //if path is null
                        if(imgPath == null)
                        {
                            //pass null for bitmap
                            bmp = null;
                        }

                        // Set the bitmap.
                        ImageView iconImageView = (ImageView) view;
                        iconImageView.setImageBitmap(bmp);
                    }

                    catch(Exception ex)
                    {
                     ex.printStackTrace();
                    }

                    return true;
                }

                else
                {
                    return  false;
                }

            }
        });

        myLstVw.setAdapter(dataAdapter);


        //show user how many records are available
        if(dataAdapter != null)
        {
            Toast.makeText(this, "showing Results for: " + dataAdapter.getCount() + " Recipes", Toast.LENGTH_SHORT).show();
        }

        else
            //prompt user no records are available
        if(dataAdapter.getCount() == 0)
            Toast.makeText(this, "No Recipes to show", Toast.LENGTH_SHORT).show();
    }
}