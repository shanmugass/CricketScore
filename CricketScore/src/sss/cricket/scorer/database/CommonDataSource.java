package sss.cricket.scorer.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class CommonDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;

	public static final String TABLE_MATCH_TYPE = "CricketMatchType";
	public static final String COLUMN_MATCH_TYPE_ID = "MatchTypeId";

	public static final String COLUMN_Match_Type_Name = "MatchTypeName";
	public static final String COLUMN_has_Limited_Overs = "hasLimitedOvers";
	public static final String COLUMN_has_Two_Innigs = "hasTwoInnigs";

	public static final String[] ALL_COLUMNS = { COLUMN_MATCH_TYPE_ID,
			COLUMN_Match_Type_Name, COLUMN_has_Limited_Overs,
			COLUMN_has_Two_Innigs };

	public CommonDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void setResources(Resources resources) {
		dbHelper.setResources(resources);
	}

	private void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public static String getCurrentDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd hh:mm:ss");
		Calendar cal = Calendar.getInstance();

		return dateFormat.format(cal.getTime());
	}

	public void createDefaultEntries() {
		open();
		Cursor cursor = database.query(TABLE_MATCH_TYPE,
				new String[] { COLUMN_MATCH_TYPE_ID }, null, null, null, null,
				null);

		if (cursor.getCount() == 0) {

			ContentValues matchType1 = new ContentValues();
			matchType1.put(COLUMN_Match_Type_Name, "Limited Overs");
			matchType1.put(COLUMN_has_Limited_Overs, 1);
			matchType1.put(COLUMN_has_Two_Innigs, 0);

			database.insert(TABLE_MATCH_TYPE, null, matchType1);

			ContentValues matchType2 = new ContentValues();
			matchType2.put(COLUMN_Match_Type_Name,
					"Limited Overs - Two Innings");
			matchType2.put(COLUMN_has_Limited_Overs, 1);
			matchType2.put(COLUMN_has_Two_Innigs, 1);

			database.insert(TABLE_MATCH_TYPE, null, matchType2);

			ContentValues matchType3 = new ContentValues();
			matchType3.put(COLUMN_Match_Type_Name, "Test Match");
			matchType3.put(COLUMN_has_Limited_Overs, 0);
			matchType3.put(COLUMN_has_Two_Innigs, 1);

			database.insert(TABLE_MATCH_TYPE, null, matchType3);

		}
		cursor.close();
		close();
	}

	public MatchType[] getAllMatchTypesInArray() {
		open();
		List<MatchType> teams = new ArrayList<MatchType>();

		Cursor cursor = database.query(TABLE_MATCH_TYPE, ALL_COLUMNS, null,
				null, null, null, null);

		cursor.moveToFirst();

		int count = 0;
		while (!cursor.isAfterLast()) {
			MatchType team = getMatchType(cursor);
			teams.add(team);
			count++;
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		close();
		return teams.toArray(new MatchType[count]);
	}

	public MatchType getMatchTypeById(long matchTypeId) {
		open();

		Cursor cursor = database.query(TABLE_MATCH_TYPE, ALL_COLUMNS,
				COLUMN_MATCH_TYPE_ID + "=" + matchTypeId, null, null, null,
				null);

		cursor.moveToFirst();
		MatchType matchType = null;

		while (!cursor.isAfterLast()) {
			matchType = getMatchType(cursor);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		close();
		return matchType;
	}

	private MatchType getMatchType(Cursor cursor) {
		MatchType matchType = new MatchType();

		matchType.MatchTypeId = cursor.getLong(0);
		matchType.MatchTypeName = cursor.getString(1);
		matchType.IsLimitedOvers = cursor.getLong(2) > 0;
		matchType.IsTwoInnigs = cursor.getLong(3) > 0;

		return matchType;
	}

	private void close() {
		dbHelper.close();
	}

}