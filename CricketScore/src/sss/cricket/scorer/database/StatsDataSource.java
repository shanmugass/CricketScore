package sss.cricket.scorer.database;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class StatsDataSource {
	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;

	public StatsDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	private void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	private void close() {
		dbHelper.close();
	}

	public List<StatsBatsman> getBatsmanStats(int top, int orderBy) {
		List<StatsBatsman> batsmans = new ArrayList<StatsBatsman>();

		open();
		String query = String
				.format("select playerId, (select p.PlayerName FROM CricketPlayer p WHERE p.PlayerId= s.playerId) Name,  count(inningsid) Innings, sum(runs) Runs, sum(balls) Balls, sum(runs) *1.0/ count(IsOut) *1.0, count(CASE WHEN runs>24 AND runs<50 THEN 1 ELSE NULL END), count(CASE WHEN runs>49 THEN 1 ELSE NULL END),  max(runs) Highest, sum(fours) Fours, sum(sixes) Sixes, (sum(runs) * 1.0 /sum(balls)) * 100 StrikeRate  from statsBatsman s group by playerId order by %s desc LIMIT %s",
						String.valueOf(orderBy), String.valueOf(top));

		Cursor cursor = database.rawQuery(query, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {

			StatsBatsman bat = new StatsBatsman();

			bat.setPlayerId(cursor.getLong(0));
			bat.setPlayerName(cursor.getString(1));
			bat.setInnings(cursor.getInt(2));
			bat.setRuns(cursor.getInt(3));
			bat.setBalls(cursor.getInt(4));
			bat.setAverage(cursor.getDouble(5));
			bat.setTwentyFivePlus(cursor.getInt(6));
			bat.setFiftyPlus(cursor.getInt(7));
			bat.setHighest(cursor.getInt(8));
			bat.setFours(cursor.getInt(9));
			bat.setSixes(cursor.getInt(10));
			bat.setStrikeRate(cursor.getDouble(11));

			batsmans.add(bat);

			cursor.moveToNext();
		}

		cursor.close();

		close();
		return batsmans;
	}

	public List<StatsBowler> getBowlerStats(int top, int orderBy,
			String sortDirection) {
		List<StatsBowler> bowlers = new ArrayList<StatsBowler>();

		open();
		String query = String
				.format("select playerId, (select p.PlayerName FROM CricketPlayer p WHERE p.PlayerId= s.playerId) Name, count(inningsid), sum(wickets), Count(CASE WHEN Wickets>2 and Wickets<5 THEN 1 ELSE NULL END),Count(CASE WHEN Wickets>4 THEN 1 ELSE NULL END),sum(runs),sum(balls), sum(wides), sum(noballs), (sum(runs) *1.0 / sum(balls))*6.0 ER, sum(balls) *1.0 / sum(wickets) SR  from StatsBowler s Group by playerId Having sum(wickets)>0 order by %s %s LIMIT %s",
						String.valueOf(orderBy), sortDirection,
						String.valueOf(top));

		Cursor cursor = database.rawQuery(query, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {

			StatsBowler bowler = new StatsBowler();
			bowler.setPlayerId(cursor.getLong(0));
			bowler.setPlayerName(cursor.getString(1));
			bowler.setInnings(cursor.getInt(2));
			bowler.setWickets(cursor.getInt(3));

			bowler.setThreePlusWickets(cursor.getInt(4));
			bowler.setFivePlusWickets(cursor.getInt(5));

			bowler.setRuns(cursor.getInt(6));

			bowler.setBalls(cursor.getInt(7));
			bowler.setWides(cursor.getInt(8));
			bowler.setNoBalls(cursor.getInt(9));
			bowler.setEconomyRate(cursor.getDouble(10));
			bowler.setStrikeRate(cursor.getDouble(11));

			bowlers.add(bowler);

			cursor.moveToNext();
		}

		cursor.close();

		close();
		return bowlers;
	}

	public void SaveScoreToStatsDB(long inningsId,
			List<BatsmanScore> batsmanScore, List<BowlerScore> bowlerScores) {
		if (batsmanScore == null || bowlerScores == null)
			return;

		open();
		String query = String.format(
				"Delete FROM StatsBatsman where InningsId =%s",
				String.valueOf(inningsId));

		database.execSQL(query);

		query = String.format("Delete FROM StatsBowler where InningsId =%s",
				String.valueOf(inningsId));

		database.execSQL(query);

		int pos = 0;

		for (BatsmanScore score : batsmanScore) {
			if (score.getBallsPlayed() < 1
					&& score.getWicketFallDetails() == null)
				continue;

			pos++;

			ContentValues values = new ContentValues();
			values.put("InningsId", inningsId);
			values.put("BattingPosition", pos);
			values.put("PlayerId", score.getPlayerId());
			values.put("Balls", score.getBallsPlayed());
			values.put("Runs", score.getScoredRuns());
			values.put("Fours", score.getFours());
			values.put("Sixes", score.getSixes());

			if (score.getWicketFallDetails() != null) {
				values.put("IsOut", 1);
			}

			database.insert("StatsBatsman", null, values);

		}

		for (BowlerScore score : bowlerScores) {
			if (score.getBallsBowled() < 1)
				continue;

			ContentValues values = new ContentValues();
			values.put("InningsId", inningsId);
			values.put("PlayerId", score.getBowlerId());
			values.put("Balls", score.getBallsBowled());
			values.put("Runs", score.getRunsGiven());
			values.put("Wickets", score.getWickets());
			values.put("NoBalls", score.getNoBalls());
			values.put("Wides", score.getWides());
			database.insert("StatsBowler", null, values);
		}
		close();
	}
}
