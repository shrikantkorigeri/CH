package com.san.emenu.chowmein.localdb;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DbcusrsorUtil {
		
	public static void closeCursor(Cursor cursor)
	{
		if (cursor != null)
		{
			cursor.close();
		}
	}

	public static void closeDataBase(SQLiteDatabase database)
	{
		if (database != null)
		{
			database.close();
		}
	}
}
