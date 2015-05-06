package com.remedeo.remedeoapp.common;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "db_remedeo";
	
	private static final int DB_VERSOIN = 3;
	
	private static DatabaseHandler dbHandler;
	
	private static final String CREATE_TABLE = "CREATE TABLE PATIENT_INFO(_ID INTEGER PRIMARY KEY AUTOINCREMENT,PATIENT_ID UNIQ TEXT,NAME TEXT,GENDER TEXT,AGE INTEGER,"
			+ "DOB TEXT,DOCTOR TEXT,HOSPITAL_NAME TEXT,COMMENT TEXT,ADDRESS TEXT)";
	
	private DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DB_VERSOIN);
	}
	
	public static DatabaseHandler getInstance(Context context) {
		if (dbHandler == null) {
			dbHandler = new DatabaseHandler(context);
		}
		return dbHandler;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		try {
			
			db.execSQL("ALTER TABLE PATIENT_INFO ADD COLUMN ADDRESS TEXT");
		}
		catch (Exception e) {
			
		}
	}
	
}
