package com.san.emenu.chowmein.localdb;

import java.io.File;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.util.Log;

public class SQLiteDBHandler {
	private static final String TAG = "SQLiteDBHandler";

	public static final String DATABASE_FILE_PATH = Environment
			.getExternalStorageDirectory() + "/sanemenuch";
	public static final String DATABASE_NAME = "emenu.db";

	public static final String CATEGORY = "category";
	public static final String SUBCATEGORY = "subcategory";
	public static final String ITEMS = "items";
	public static final String INGREDIENTS = "ingredients";
	public static final String SERVICE = "service";
	public static final String HALLS = "hallimages";

	private static final String CATEGORY_CREATE = "create table "
			+ CATEGORY + " (CAT_ID INTEGER, CAT_NAME TEXT, SECTION_ID TEXT);";
	private static final String SUBCATEGORY_CREATE = "create table "
			+ SUBCATEGORY
			+ " (RSCAT_ID INTEGER, RSCAT_NAME TEXT,RC_ID TEXT, R_STATUS TEXT, STAT TEXT);";

	private static final String ITEMS_CREATE = "create table " + ITEMS
			+ " (RITEM_ID INTEGER,RITEM_NAME TEXT,RITEM_DESC TEXT, RITEM_PRETIME TEXT, RITEM_PRICE TEXT, RITEM_CODE TEXT, RSCAT_ID TEXT, RRATING TEXT, R_STATUS TEXT, STAT TEXT);";
	private static final String INGREDIENTS_CREATE = "create table "
			+ INGREDIENTS
			+ " (ING_ID NUMERIC, ING_NAME TEXT);";
	/*private static final String SERVICE_CREATE = "create table " + SERVICE
			+ " (service_id TEXT, service_name TEXT, service_IMG BLOB);";*/


	private SQLiteDatabase database;

	public SQLiteDBHandler() {
		// super();

		try {

			// database = SQLiteDatabase.openDatabase(DATABASE_FILE_PATH+
			// File.separator + DATABASE_NAME,
			// null,SQLiteDatabase.OPEN_READWRITE);
			database = SQLiteDatabase.openOrCreateDatabase(DATABASE_FILE_PATH
					+ File.separator + DATABASE_NAME, null);
			database = SQLiteDatabase.openDatabase(DATABASE_FILE_PATH
					+ File.separator + DATABASE_NAME, null,
					SQLiteDatabase.OPEN_READWRITE);
			createTables();
		} catch (SQLiteException ex) {
			Log.e(TAG, "error--" + ex.getMessage(), ex);
			// error means tables does not exits
			// database =
			// SQLiteDatabase.openOrCreateDatabase(DATABASE_FILE_PATH+
			// File.separator + DATABASE_NAME, null);
			createTables();
		} finally {
			// DBUtil.safeCloseDataBase(database);
			// database.close();
			DbcusrsorUtil.closeDataBase(database);
		}
	}

	private void createTables() {
		database.execSQL(CATEGORY_CREATE);
		database.execSQL(SUBCATEGORY_CREATE);
		database.execSQL(ITEMS_CREATE);
		database.execSQL(INGREDIENTS_CREATE);
		//database.execSQL(SERVICE_CREATE);
	}

	public void close() {
		// DBUtil.safeCloseDataBase(database);
		// database.close();
		DbcusrsorUtil.closeDataBase(database);
	}

	public SQLiteDatabase getReadableDatabase() {
		database = SQLiteDatabase.openDatabase(DATABASE_FILE_PATH
				+ File.separator + DATABASE_NAME, null,
				SQLiteDatabase.OPEN_READONLY);
		return database;
	}

	public SQLiteDatabase getWritableDatabase() {
		database = SQLiteDatabase.openDatabase(DATABASE_FILE_PATH
				+ File.separator + DATABASE_NAME, null,
				SQLiteDatabase.OPEN_READWRITE);
		return database;
	}

}
