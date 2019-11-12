package com.example.ratty.recipecookbook;

import android.net.Uri;
/**
 * Created by Ratty on 12/2/2016.
 */

public class MyProviderContract {
    //app responsible for this DATA
    public static final String AUTHORITY = "com.example.ratty.recipecookbook.MyContentProvider";

    //URI
    //tables
    public static final Uri MY_RECIPE_URI = Uri.parse("content://"+AUTHORITY+ "/myrecipe");

    //Col Names
    public static final String _ID = "_id";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String IMGDIR = "imgdir";

    //Meta data types
    public static final String CONTENT_TYPE_SINGLE = "vnd.android.cursor.item/MyContentProvider.data.text";
    public static final String CONTENT_TYPE_MULTIPLE = "vnd.android.cursor.dir/MyContentProvider.data.text";
}
