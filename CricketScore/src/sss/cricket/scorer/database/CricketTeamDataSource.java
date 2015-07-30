package sss.cricket.scorer.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class CricketTeamDataSource {
	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;

	public CricketTeamDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	private void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	private void close() {
		dbHelper.close();
	}

	public void createTeam(CricketTeam team) {

		open();

		ContentValues values = new ContentValues();
		values.put(CricketTeam.COLUMN_TEAM_NAME, team.TeamName);

		long insertId = database.insert(CricketTeam.TABLE_NAME, null, values);

		team.TeamId = insertId;

		close();
	}

	public List<CricketTeam> GetAllTeams() {
		open();
		List<CricketTeam> teams = new ArrayList<CricketTeam>();

		Cursor cursor = database.query(CricketTeam.TABLE_NAME,
				CricketTeam.ALL_COLUMNS, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			CricketTeam team = GetTeam(cursor);
			teams.add(team);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		close();
		return teams;
	}

	public CricketTeam[] GetAllTeamsInArray(Boolean includeEmpty) {
		open();
		List<CricketTeam> teams = new ArrayList<CricketTeam>();

		if (includeEmpty) {

			CricketTeam team = new CricketTeam();
			team.TeamName = "";
			team.TeamId = 0;

			teams.add(team);

		}

		Cursor cursor = database.query(CricketTeam.TABLE_NAME,
				CricketTeam.ALL_COLUMNS, null, null, null, null, null);

		cursor.moveToFirst();

		int count = 0;
		while (!cursor.isAfterLast()) {
			CricketTeam team = new CricketTeam();
			fillTeam(team, cursor);
			teams.add(team);
			count++;
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		close();
		return teams.toArray(new CricketTeam[count]);
	}

	public void fillTeam(CricketTeam cricketTeam, long teamId) {
		open();

		Cursor cursor = database.query(CricketTeam.TABLE_NAME,
				CricketTeam.ALL_COLUMNS, "teamid=" + teamId, null, null, null,
				null);

		cursor.moveToFirst();

		fillTeam(cricketTeam, cursor);
		cursor.close();
		close();
	}

	private void fillTeam(CricketTeam cricketTeam, Cursor cursor) {
		cricketTeam.TeamId = cursor.getLong(0);
		cricketTeam.TeamName = cursor.getString(1);
	}

	private CricketTeam GetTeam(Cursor cursor) {
		CricketTeam team = new CricketTeam();
		team.TeamId = cursor.getLong(0);
		team.TeamName = cursor.getString(1);
		return team;
	}
}
