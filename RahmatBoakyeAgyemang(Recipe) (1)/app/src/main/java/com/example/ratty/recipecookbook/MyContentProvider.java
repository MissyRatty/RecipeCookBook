package com.example.ratty.recipecookbook;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.Objects;

/**
 * Created by Ratty on 12/2/2016.
 */

public class MyContentProvider extends ContentProvider {

    private DBHelper dbHelper = null;

    //use this to map the Provider Contract Class to db Implementation
    //create a UriMatcher to match specific Uri
    //paths to tables in the database.
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        //recipe URI Matching here
        uriMatcher.addURI(MyProviderContract.AUTHORITY, "myrecipe", 1);
        uriMatcher.addURI(MyProviderContract.AUTHORITY, "myrecipe/#", 2);
    };

    //called when this contentProvider is instantiated by any component
    //it returns a handle to the underlying DB
    @Override
    public boolean onCreate() {
        Log.d("ContentProvider:", "contentProvider OnCreate");
        this.dbHelper = new DBHelper(this.getContext(),"RecipeDB",null,8);
        return true;
    }


    //query dB for data
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch(uriMatcher.match(uri))
        {
            //one Item
            case 2:
                selection = "_ID = " + uri.getLastPathSegment();
                //whole recipe table
            case 1:
                return db.query("myrecipe", projection, selection, selectionArgs, null, null, sortOrder);
            default:
                return null;
        }
    }



    //checkc Uri type
    //if file or folder(dir)
    @Override
    public String getType(Uri uri) {
        String contentType;
        //check if last file exists
        if(uri.getLastPathSegment() == null)
        {
            //if not
            //return the whole folder
            contentType = "vnd.android.cursor.dir/MyContentProvider.data.text";
        }
        else
        {
            //else return the single item
            contentType = "vnd.android.cursor.item/MyProvider.data.text";
        }

        return contentType;
    }


    //Use URI matcher here to insert into
    //the database and to decide which table to use:
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String TableName ;
        Uri newRecord = null;
        long newID = 0;

        try
        {
            //get table name from the uri passed
            TableName = uriMatcher.match(uri) == 1 ? "myrecipe" : "myrecipe";


            //adds the item to the database, and returns a
            //new Uri to identify the newly insert record, or the form
            //content://authority/path/new_row_id:
            newID = db.insert(TableName,null,values);


            if (newID != 0)
            {
                //returns new Added item as content://authority/path/new_row_id:
                newRecord = ContentUris.withAppendedId(uri,newID);

                //Toast.makeText(getContext(), "New Data Added:-> "+ newRecord.toString(), Toast.LENGTH_SHORT).show();

                //The notifyChange call will notify any ContentObservers that have registered
                //listeners that match this particular Uri.
                getContext().getContentResolver().notifyChange(newRecord, null);
            }

        }
        catch(Exception ex)
        {
            Toast.makeText(getContext(), "Insert Error:-> " + ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }


        return newRecord;
    }

    //deletion
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String TableName;
        //get number of records deleted
        int numOfRowsDel = 0;

        try {
            //get table name from the uri passed
            TableName = uriMatcher.match(uri) == 1 ? "myrecipe" : "myrecipe";

            //return the number of records deleted
            numOfRowsDel =  db.delete(TableName,selection,selectionArgs);
            if(numOfRowsDel > 0)
            {
                //Toast.makeText(getContext(), "Data deleted:-> "+ selection.toString(), Toast.LENGTH_SHORT).show();
            }


        }
        catch (Exception ex)
        {
            Toast.makeText(getContext(), "Delete Error :-> "+ ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }

            return numOfRowsDel;


    }


    //updating
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String TableName;
        //number of records updated
        int numOfRowsUpdated = 0;

        try
        {
            //get table name from the uri passed
            TableName = uriMatcher.match(uri) == 1 ? "myrecipe" : "myrecipe";

            //number of records returned after update is done
            numOfRowsUpdated = db.update(TableName,values,selection,selectionArgs);

            //for debugging
            if(numOfRowsUpdated > 0)
            {
                //Toast.makeText(getContext(), "Data Updated to :-> "+ values.toString(), Toast.LENGTH_SHORT).show();
            }


        }
        catch(Exception ex)
        {
            Toast.makeText(getContext(), "Update Error :-> "+ ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
        finally
        {
            return  numOfRowsUpdated;
        }
    }
}


//References
//1.Content Provider
//https://developer.android.com/reference/android/content/ContentProvider.html
