package sss.cricket.scorer.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CricketPlayerDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;

	public CricketPlayerDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	private void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	private void close() {
		dbHelper.close();
	}

	public void createPlayer(CricketPlayer player) {

		open();

		ContentValues values = new ContentValues();
		values.put(CricketPlayer.COLUMN_PLAYER_NAME, player.PlayerName);
		values.put("BattingSkill", player.getBattingSkill());
		values.put("BowlingSkill", player.getBattingSkill());

		long insertId = database.insert(CricketPlayer.TABLE_NAME, null, values);

		player.PlayerId = insertId;

		close();
	}

	public void updatePlayer(CricketPlayer player) {

		open();

		ContentValues values = new ContentValues();
		values.put(CricketPlayer.COLUMN_PLAYER_NAME, player.PlayerName);
		values.put("BattingSkill", player.getBattingSkill());
		values.put("BowlingSkill", player.getBattingSkill());

		database.update(CricketPlayer.TABLE_NAME, values, "PlayerId="
				+ player.PlayerId, null);

		close();
	}

	public void createPlayers(List<CricketPlayer> players) {

		open();

		for (CricketPlayer player : players) {

			String playerDetails = player.PlayerName;

			String[] details = playerDetails.split("-");

			int battingSkill = Integer.valueOf(details[0]);
			int bowlingSkill = Integer.valueOf(details[1]);
			String playerName = details[2];

			player.PlayerName = playerName;

			ContentValues values = new ContentValues();
			values.put(CricketPlayer.COLUMN_PLAYER_NAME, playerName);
			values.put("BattingSkill", battingSkill);
			values.put("BowlingSkill", bowlingSkill);

			long insertId = database.insert(CricketPlayer.TABLE_NAME, null,
					values);

			Log.i("Player Created", playerName + "-" + battingSkill + "-"
					+ bowlingSkill);

			player.PlayerId = insertId;

			if (player.TeamId > 0) {
				ContentValues assoc_values = new ContentValues();
				assoc_values.put(CricketPlayer.COLUMN_ID, player.PlayerId);
				assoc_values.put(CricketPlayer.COLUMN_TEAM_ID, player.TeamId);

				database.insert(CricketPlayer.ASSOCIATION_TABLE_NAME, null,
						assoc_values);
			}
		}
		close();
	}

	public void addAssociation(long playerId, long teamId) {

		open();

		ContentValues values = new ContentValues();
		values.put(CricketPlayer.COLUMN_ID, playerId);
		values.put(CricketPlayer.COLUMN_TEAM_ID, teamId);

		database.insert(CricketPlayer.ASSOCIATION_TABLE_NAME, null, values);

		close();
	}

	public List<CricketPlayer> getPlayers(long teamId, String orderBy) {

		open();
		List<CricketPlayer> matches = new ArrayList<CricketPlayer>();

		String query = "Select p.playerId, p.playerName, p.battingSkill, p.bowlingSkill from CricketPlayer p JOIN CricketTeamAssociation A ON A.playerid=p.PlayerId Where TeamId="
				+ teamId + " Order By p." + orderBy;

		Cursor cursor = database.rawQuery(query, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			CricketPlayer match = cursorToCricketPlayer(cursor);
			matches.add(match);
			cursor.moveToNext();
		} // Make sure to close the cursor cursor.close();

		cursor.close();
		close();
		return matches;
	}

	private CricketPlayer cursorToCricketPlayer(Cursor cursor) {
		CricketPlayer newmatch = new CricketPlayer();
		newmatch.PlayerId = cursor.getLong(0);
		newmatch.PlayerName = cursor.getString(1);
		newmatch.setBattingSkill(cursor.getInt(2));
		newmatch.setBowlingSkill(cursor.getInt(3));
		return newmatch;
	}

}
