package com.example.ratty.recipecookbook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Ratty on 12/2/2016.
 */

//DB helper class
public class DBHelper extends SQLiteOpenHelper {

    //constructor
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory cursorFactory, int version)
    {
        super(context,"RecipeDB",null,8);
        Log.d("DBHelper", "DBHelper Constructor");
    }

    //called when the db class is first instantiated
    //Exec your Sql query here(Create DB here)
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Recipe table
        String my_query = "CREATE TABLE myrecipe(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "title VARCHAR(128),"+
                "description VARCHAR(500),"+
                "imgdir VARCHAR(200)"+
                ");";

        db.execSQL(my_query);

    }

    //called to make changes to DB when the version number(1) changes
    //helps to drop and recreate table if needed
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String db_query = "DROP TABLE IF EXISTS myrecipe;";
        db.execSQL(db_query);
        onCreate(db);
    }
}
