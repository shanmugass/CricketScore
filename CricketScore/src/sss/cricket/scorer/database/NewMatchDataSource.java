package sss.cricket.scorer.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class NewMatchDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
			MySQLiteHelper.COLUMN_MATCH_NAME, MySQLiteHelper.COLUMN_OVERS };

	public NewMatchDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public NewMatchBuiness createMatch(String matchName, int overs) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_MATCH_NAME, matchName);
		values.put(MySQLiteHelper.COLUMN_OVERS, overs);

		long insertId = database.insert(MySQLiteHelper.TABLE_NEW_MATCH, null,
				values);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_NEW_MATCH,
				allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		NewMatchBuiness newmatch = cursorToNewMatchBuiness(cursor);
		cursor.close();
		return newmatch;
	}

	public void deleteComment(NewMatchBuiness newmatch) {

		database.delete(MySQLiteHelper.TABLE_NEW_MATCH,
				MySQLiteHelper.COLUMN_ID + " = " + newmatch._Id, null);
	}

	public List<NewMatchBuiness> getAllComments() {
		List<NewMatchBuiness> matches = new ArrayList<NewMatchBuiness>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_NEW_MATCH,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			NewMatchBuiness match = cursorToNewMatchBuiness(cursor);
			matches.add(match);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return matches;
	}

	private NewMatchBuiness cursorToNewMatchBuiness(Cursor cursor) {
		NewMatchBuiness newmatch = new NewMatchBuiness();
		newmatch._Id = cursor.getLong(0);
		newmatch.MatchName = cursor.getString(1);
		newmatch.Overs = cursor.getInt(2);
		return newmatch;
	}
}
