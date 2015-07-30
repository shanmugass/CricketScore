package sss.cricket.scorer.database;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import sss.cricket.scorer.ui.R;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_NEW_MATCH = "Match";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_MATCH_NAME = "matchName";
	public static final String COLUMN_OVERS = "overs";

	private static final String DATABASE_NAME = "cricketscorer.db";
	private static final int DATABASE_VERSION = 1;

	Resources resources;

	public void setResources(Resources resources) {
		this.resources = resources;
	}

	public MySQLiteHelper(Context context) {
		super(context, Environment.getExternalStorageDirectory()
				+ "/CricketScorer/" + DATABASE_NAME, null, DATABASE_VERSION);

	}

	private String getSqlRawFile() {
		if (resources == null)
			return "";

		InputStream inputStream = resources.openRawResource(R.raw.startup_sql);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		int i;
		try {
			i = inputStream.read();
			while (i != -1) {
				byteArrayOutputStream.write(i);
				i = inputStream.read();
			}
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return byteArrayOutputStream.toString();
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		createTables(database);
	}

	private void createTables(SQLiteDatabase database) {

		String sql = getSqlRawFile();

		String[] tableQueries = sql.split(";");

		for (String query : tableQueries) {
			database.execSQL(query.trim() + ";");
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		// db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEW_MATCH);
		onCreate(db);
	}

}
