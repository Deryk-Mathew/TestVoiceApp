package com.example.glub.testvoiceapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Glub on 24/02/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // DB name
    static final String dbName="mallDB";

    // Shops table
    static final String shopTable="shops";
    static final String shop_id="shop_id";
    static final String shop_name="shop_name";
    static final String shop_type="shop_type";
    static final String shop_x="shop_x";
    static final String shop_y="shop_y";

    // Items table
    static final String itemTable="items";
    static final String item_id="item_id";
    static final String item_name="item_name";
    static final String item_type="item_type";

    // Comp table
    static final String compTable="items";
    static final String comp_item_id="item_id";
    static final String comp_shop_id="shop_id";


    public DatabaseHelper(Context context) {
        super(context, dbName, null,34);
    }

    public Cursor executeTestQuery(String shop_name)
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur = db.query(shopTable, new String[]{shop_id, DatabaseHelper.shop_name, shop_type, shop_x, shop_y}, DatabaseHelper.shop_name +"=?", new String[]{shop_name},null, null, null, null);
        // Cursor cur=db.rawQuery("SELECT * FROM" +shopTable+ "WHERE" +this.shop_name+ "="+shop_name,new String [] {});
        return cur;
    }

    public Cursor executeTestQuery(String shop_name, String shop_type, String item_name, String item_type)
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("SELECT * FROM "+shopTable,new String [] {});
        return cur;
    }

    private void insertStuff(SQLiteDatabase db){
        //SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        // Shops
        cv.put(shop_id, 1);
        cv.put(shop_name, "Footlocker");
        cv.put(shop_type, "Shoe");
        cv.put(shop_x, 123);
        cv.put(shop_y, 456);
        db.insert(shopTable, shop_id, cv);

        cv.put(shop_id, 2);
        cv.put(shop_name, "Starbucks");
        cv.put(shop_type, "Coffee");
        cv.put(shop_x, 123);
        cv.put(shop_y, 456);
        db.insert(shopTable, shop_id, cv);

        // Items
        cv.put(item_id, 1);
        cv.put(item_name, "Shoe");
        db.insert(itemTable, item_id, cv);

        // Comp stuff
        cv.put(item_id, 1);
        cv.put(shop_id, 1);
        db.insert(compTable, comp_item_id, cv);

        //db.close();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //  Create shop table
        db.execSQL("CREATE TABLE IF NOT EXISTS "+shopTable+" ("+shop_id+ " INTEGER PRIMARY KEY , "+
                shop_name+ " TEXT, "+shop_type+" TEXT, "+shop_x+ " INTEGER NOT NULL, "+shop_y+" INTEGER NOT NULL)");

        // Create items table
        db.execSQL("CREATE TABLE IF NOT EXISTS "+itemTable+" ("+item_id+ " INTEGER PRIMARY KEY , "+
                item_name+ " TEXT, "+item_type+" TEXT)");

        // Create comp table
        db.execSQL("CREATE TABLE IF NOT EXISTS "+compTable+" ("+comp_item_id+ " INTEGER PRIMARY KEY , "+
                comp_shop_id+ " INTEGER PRIMARY KEY)");

        insertStuff(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+shopTable);
        db.execSQL("DROP TABLE IF EXISTS "+itemTable);
        db.execSQL("DROP TABLE IF EXISTS "+compTable);

        onCreate(db);
    }
}
