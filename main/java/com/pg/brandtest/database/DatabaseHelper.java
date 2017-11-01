package com.pg.brandtest.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.pg.brandtest.model.BrandModel;

import java.util.ArrayList;
import java.util.List;

/***
 * Created by Purva Gevaria
 * Class name : DatabaseHelper
 * Staracture : Database name : brandmanager
 *              Table name :
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "brandmanager";
    private static final String TABLE_BRAND = "brand";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DISCRIPTION = "description";
    private static final String CREATED_AT = "created_at";
    private static final String KEY_IS_SYNCED = "is_synced";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_BRAND = "CREATE TABLE " + TABLE_BRAND + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_DISCRIPTION + " TEXT" + "," +
                KEY_IS_SYNCED + " TEXT" + "," +
                CREATED_AT + " TEXT" + ")";
        Log.d(TAG, "Create table query :: " + CREATE_TABLE_BRAND);
        db.execSQL(CREATE_TABLE_BRAND);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BRAND);
        onCreate(db);
    }

    /**
     * will add single recoard
     * @param brandModel
     */
    public void addBrand(BrandModel brandModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, brandModel.getName());
        values.put(KEY_IS_SYNCED, brandModel.getIsSynced());
        values.put(KEY_DISCRIPTION, brandModel.getDescription());
        db.insert(TABLE_BRAND, null, values);
        db.close();
    }   //end of addBrand


    // Getting All Contacts
    public List<BrandModel> getAllBrand() {
        List<BrandModel> contactList = new ArrayList<BrandModel>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_BRAND;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                BrandModel contact = new BrandModel();
                contact.setId(cursor.getString(0));
                contact.setName(cursor.getString(1));
                contact.setDescription(cursor.getString(2));
                contact.setDescription(cursor.getString(3));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }   //end of getAllBrand


    // Getting All Contacts
    public Cursor getUnSyncedData() {
        List<BrandModel> contactList = new ArrayList<BrandModel>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_BRAND + " WHERE " + KEY_IS_SYNCED + "='false'";
        Log.d(TAG, "Get synced data query :: " + selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0)
            return cursor;
        else return null;

    }

    /**
     * this will truncate table
     */
    public void truncateTable() {
        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();
            db.delete(TABLE_BRAND, null, null);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
}