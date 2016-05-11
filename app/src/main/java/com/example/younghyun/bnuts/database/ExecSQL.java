package com.example.younghyun.bnuts.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.younghyun.bnuts.data.DateInformation;

import java.util.ArrayList;

/**
 * Created by younghyun on 2015. 11. 9..
 */
public class ExecSQL {
    private DBHelper dbHelper;

    public ExecSQL(Context context)
    {
        dbHelper = new DBHelper(context);
    }

    public boolean insert(DateInformation dateInformation) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DateInformation.DATE, dateInformation.getDate());
        values.put(DateInformation.MENSES, dateInformation.getMenses());
        values.put(DateInformation.BODYTEMP, dateInformation.getBodytemp());
        values.put(DateInformation.SEXUAL, dateInformation.getSexual());
        values.put(DateInformation.HOSP, dateInformation.getHosp());
        values.put(dateInformation.WEIGHT, dateInformation.getWeight());

        long result = db.insert(DateInformation.TABLE, null, values);
        db.close();
        if(result==-1) return false;
        else return true;
    }

    public boolean insert(String _date, double _bodytemp) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DateInformation.DATE, _date);
        values.put(DateInformation.BODYTEMP, _bodytemp);

        long result = db.insert(DateInformation.TABLE, null, values);
        db.close();
        if(result==-1) return false;
        else return true;
    }

    public boolean update(DateInformation dateInformation) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DateInformation.DATE, dateInformation.getDate());
        values.put(DateInformation.MENSES, dateInformation.getMenses());
        values.put(DateInformation.BODYTEMP, dateInformation.getBodytemp());
        values.put(DateInformation.SEXUAL, dateInformation.getSexual());
        values.put(DateInformation.HOSP, dateInformation.getHosp());
        values.put(dateInformation.WEIGHT, dateInformation.getWeight());

        long result = db.update(DateInformation.TABLE, values, "date=?", new String[]{dateInformation.getDate()});
        db.close();
        if(result==-1) return false;
        else return true;
    }

    public boolean update(String _date, double _bodytemp) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DateInformation.DATE, _date);
        values.put(DateInformation.BODYTEMP, _bodytemp);

        long result = db.update(DateInformation.TABLE, values, "date=?", new String[]{_date});
        db.close();
        if(result==-1) return false;
        else return true;
    }

    public ArrayList selectOnedayData(String date) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String queryStr = "SELECT * FROM "
                + DateInformation.TABLE
                + " WHERE DATE = '" + date + "'" ;

        ArrayList<DateInformation> arrayList = new ArrayList();

        Cursor cursor = db.rawQuery(queryStr, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            String _date = cursor.getString(cursor.getColumnIndex(DateInformation.DATE));
            int _menses = cursor.getInt(cursor.getColumnIndex(DateInformation.MENSES));
            double _temp = cursor.getDouble(cursor.getColumnIndex(DateInformation.BODYTEMP));
            int _sexual = cursor.getInt(cursor.getColumnIndex(DateInformation.SEXUAL));
            int _med = cursor.getInt(cursor.getColumnIndex(DateInformation.MEDICINE));
            int _hosp = cursor.getInt(cursor.getColumnIndex(DateInformation.HOSP));
            double _weight = cursor.getDouble(cursor.getColumnIndex(DateInformation.WEIGHT));
            arrayList.add(new DateInformation(_date, _menses, _temp, _sexual, _med, _hosp, _weight));
            cursor.moveToNext();
        }
        cursor.close();

        return arrayList;
    }

    public Double[] selectOnemonthTempData(int month) {
        String _month;
        if(month<10) _month = "0" + month;
        else _month = "" + month;
        System.err.println("month is "+_month);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String queryStr = "SELECT * FROM "
                + DateInformation.TABLE
                + " WHERE " + DateInformation.DATE
                + " LIKE '%/" + _month + "/%'"
                + " ORDER BY P_KEY ASC";
        System.err.println("sql query is "+queryStr);
        ArrayList<Double> arrayList = new ArrayList();

        Cursor cursor = db.rawQuery(queryStr, null);
        if (cursor.moveToFirst()) {
            do {
                double _temp = cursor.getDouble(cursor.getColumnIndex(DateInformation.BODYTEMP));
                arrayList.add(new Double(_temp));
            } while (cursor.moveToNext());
        }

        return arrayList.toArray(new Double[arrayList.size()]);
    }
}
