package com.example.younghyun.bnuts.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.younghyun.bnuts.data.DateInformation;


/**
 * Created by younghyun on 15. 1. 21..
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final int DBVERSION = 1;
    private static final String DBNAME = "profile.db";

    public DBHelper(Context context) {
        super(context, DBNAME, null, DBVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NEW_TABLE = "CREATE TABLE " + DateInformation.TABLE + "("
                + "P_KEY INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DateInformation.DATE + " VARCHAR(10) UNIQUE,"
                + DateInformation.MENSES + " TINYINT DEFAULT 0,"
                + DateInformation.BODYTEMP+ " DOUBLE DEFAULT 36.5,"
                + DateInformation.SEXUAL + " TINYINT DEFAULT 0,"
                + DateInformation.MEDICINE + " TINYINT DEFAULT 0,"
                + DateInformation.HOSP + " TINYINT DEFAULT 0,"
                + DateInformation.WEIGHT + " DOUBLE);";
        System.err.println(CREATE_NEW_TABLE); //for debug
        db.execSQL(CREATE_NEW_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed, all data will be gone!!!
        db.execSQL("DROP TABLE IF EXISTS " + DateInformation.TABLE);
        // Create tables again
        onCreate(db);

    }
}
