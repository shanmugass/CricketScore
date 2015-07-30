package sss.cricket.scorer.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class CricketMatchDataSource {

	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;

	public CricketMatchDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	private void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	private void close() {
		dbHelper.close();
	}

	public void deleteMatch(long matchId) {

		open();

		String query = String
				.format("Delete FROM CricketWicketHistory where InningsId IN (Select InningsId FROM CricketInnings WHERE MatchId=%s)",
						String.valueOf(matchId));

		database.execSQL(query);

		query = String
				.format("Delete FROM CricketInningsHistoryDetail where InningsId IN (Select InningsId FROM CricketInnings WHERE MatchId=%s)",
						String.valueOf(matchId));

		database.execSQL(query);

		query = String
				.format("Delete FROM CricketInningsHistory where InningsId IN (Select InningsId FROM CricketInnings WHERE MatchId=%s)",
						String.valueOf(matchId));

		database.execSQL(query);

		query = String
				.format("Delete FROM StatsBatsman where InningsId IN (Select InningsId FROM CricketInnings WHERE MatchId=%s)",
						String.valueOf(matchId));

		database.execSQL(query);

		query = String
				.format("Delete FROM StatsBowler where InningsId IN (Select InningsId FROM CricketInnings WHERE MatchId=%s)",
						String.valueOf(matchId));

		database.execSQL(query);

		query = String.format("DELETE FROM CricketInnings WHERE MatchId=%s",
				String.valueOf(matchId));

		database.execSQL(query);

		query = String.format("DELETE FROM CricketMatch WHERE MatchId=%s",
				String.valueOf(matchId));

		database.execSQL(query);

		close();

	}

	public void createMatch(CricketMatch match) {

		open();

		ContentValues values = new ContentValues();
		values.put(CricketMatch.COLUMN_MatchName, match.MatchName);
		values.put(CricketMatch.COLUMN_PlayedOn, match.PlayedOn);
		values.put(CricketMatch.COLUMN_Team1, match.Team1);
		values.put(CricketMatch.COLUMN_Team2, match.Team2);
		values.put(CricketMatch.COLUMN_TossWonBy, match.TossWonBy);
		values.put(CricketMatch.COLUMN_BattingTeam, match.BattingTeam);
		values.put(CricketMatch.COLUMN_MatchType, match.MatchTypeId);
		values.put(CricketMatch.COLUMN_Overs, match.Overs);
		

		long insertId = database.insert(CricketMatch.TABLE_NAME, null, values);

		match.MatchId = (int) insertId;

		close();
	}

	public void completeMatch(CricketMatch match) {

		open();

		ContentValues values = new ContentValues();
		if (match.WonBy > 0)
			values.put(CricketMatch.COLUMN_WonBy, match.WonBy);

		values.put(CricketMatch.COLUMN_MatchResult, match.MatchResult);
		values.put(CricketMatch.COLUMN_MatchDescription, match.MatchDescription);

		database.update(CricketMatch.TABLE_NAME, values,
				"MatchId=" + match.getMatchId(), null);

		close();
	}

	public CricketMatch[] getAllMatches() {

		open();
		List<CricketMatch> matches = new ArrayList<CricketMatch>();

		Cursor cursor = database.query(CricketMatch.TABLE_NAME,
				CricketMatch.ALL_COLUMNS, null, null, null, null, null);

		int count = 0;
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			count++;
			CricketMatch match = cursorToCricketMatch(cursor);
			matches.add(match);
			cursor.moveToNext();
		} // Make sure to close the cursor cursor.close();

		cursor.close();
		close();

		return matches.toArray(new CricketMatch[count]);
	}

	private CricketMatch cursorToCricketMatch(Cursor cursor) {
		CricketMatch newmatch = new CricketMatch();
		newmatch.MatchId = cursor.getInt(0);
		newmatch.MatchName = cursor.getString(1);
		newmatch.PlayedOn = cursor.getString(2);
		newmatch.Team1 = cursor.getInt(3);
		newmatch.Team2 = cursor.getInt(4);
		newmatch.TossWonBy = cursor.getInt(5);
		newmatch.BattingTeam = cursor.getInt(6);
		newmatch.setFirstInnningsBattingTeamId(newmatch.getBattingTeam());
		newmatch.MatchTypeId = cursor.getInt(7);
		newmatch.Overs = cursor.getInt(8);
		newmatch.WonBy = cursor.getInt(9);
		newmatch.MatchResult = cursor.getInt(10);
		newmatch.MatchDescription = cursor.getString(11);
		newmatch.setMatchType(getMatchTypeById(newmatch.MatchTypeId));

		return newmatch;
	}

	private MatchType getMatchTypeById(long matchTypeId) {

		Cursor cursor = database.query(CommonDataSource.TABLE_MATCH_TYPE,
				CommonDataSource.ALL_COLUMNS,
				CommonDataSource.COLUMN_MATCH_TYPE_ID + "=" + matchTypeId,
				null, null, null, null);

		cursor.moveToFirst();
		MatchType matchType = null;

		while (!cursor.isAfterLast()) {
			matchType = getMatchType(cursor);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return matchType;
	}

	private MatchType getMatchType(Cursor cursor) {
		MatchType matchType = new MatchType();

		matchType.MatchTypeId = cursor.getLong(0);
		matchType.MatchTypeName = cursor.getString(1);
		matchType.IsLimitedOvers = cursor.getInt(2) > 0;
		matchType.IsTwoInnigs = cursor.getInt(3) > 0;

		return matchType;
	}

}
