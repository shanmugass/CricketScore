package sss.cricket.scorer.database;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class CricketInningsDataSource {

	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;

	public CricketInningsDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	private void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	private void close() {
		dbHelper.close();
	}

	public List<CricketInnings> getAllInningsByMatch(long matchId) {
		List<CricketInnings> cricketInnings = new ArrayList<CricketInnings>();

		open();

		Cursor cursor = database.query(CricketInnings.TABLE_NAME,
				CricketInnings.ALL_COLUMNS, "matchId=" + matchId, null, null,
				null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			CricketInnings innings = getInnings(cursor);
			cricketInnings.add(innings);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		close();
		return cricketInnings;
	}

	public CricketInnings getInnings(Cursor cursor) {
		CricketInnings innings = new CricketInnings();

		innings.setInningsId(cursor.getLong(0));
		innings.setMatchId(cursor.getInt(1));
		innings.setAllocatedOvers(cursor.getInt(2));
		innings.setInningsNo(cursor.getInt(3));
		innings.setStartedOn(cursor.getString(4));
		innings.setCompletedOn(cursor.getString(5));
		innings.setBattingTeam(cursor.getInt(6));
		innings.setTotalRuns(cursor.getInt(7));

		innings.setBowledOvers(cursor.getInt(8));

		String oversBowled = cursor.getString(8);

		int pos = oversBowled.indexOf('.');
		if (pos < 0) {
			innings.setBalls(0);
		} else {
			int balls = Integer.valueOf(oversBowled.substring(pos + 1));
			innings.setBalls(balls);
		}

		innings.setExtras(cursor.getInt(9));
		innings.setWickets(cursor.getInt(10));

		return innings;
	}

	public void createInnings(CricketInnings innigs) {

		open();

		ContentValues values = new ContentValues();
		values.put(CricketInnings.COLUMN_BATTINGTEAM, innigs.getBattingTeam());
		values.put(CricketInnings.COLUMN_ALLOCATEDOVERS,
				innigs.getAllocatedOvers());
		values.put("MatchId", innigs.getMatchId());
		values.put(CricketInnings.COLUMN_STARTEDON, innigs.getStartedOn());
		values.put(CricketInnings.COLUMN_INNINGSNO, innigs.getInningsNo());

		values.put(CricketInnings.COLUMN_RUNS, 0);
		values.put(CricketInnings.COLUMN_BOWLEDOVERS, 0);
		values.put(CricketInnings.COLUMN_WICKETS, 0);
		values.put(CricketInnings.COLUMN_EXTRAS, 0);

		long insertId = database
				.insert(CricketInnings.TABLE_NAME, null, values);

		innigs.setInningsId(insertId);

		close();
	}

	public void updateInningsAndScoreDetails(ScoreHistory score) {

		open();

		ContentValues values = new ContentValues();

		values.put(CricketInnings.COLUMN_RUNS, score.getTeamScore());
		values.put(CricketInnings.COLUMN_BOWLEDOVERS, score.getOver() + "."
				+ score.getBall());
		values.put(CricketInnings.COLUMN_WICKETS, score.getWickets());
		values.put(CricketInnings.COLUMN_EXTRAS, score.getTotalExtras());
		values.put(CricketInnings.COLUMN_COMPLETEDON,
				CommonDataSource.getCurrentDate());

		database.update(CricketInnings.TABLE_NAME, values,
				"InningsId=" + score.getInningsId(), null);

		saveBallDetailsToDB(score);

		close();
	}

	public void updateInningsScore(long inningsId, int over, int ball,
			int score, int wickets, int extras) {

		open();

		ContentValues values = new ContentValues();

		values.put(CricketInnings.COLUMN_RUNS, score);
		values.put(CricketInnings.COLUMN_BOWLEDOVERS, over + "." + ball);
		values.put(CricketInnings.COLUMN_WICKETS, wickets);
		values.put(CricketInnings.COLUMN_EXTRAS, extras);
		values.put(CricketInnings.COLUMN_COMPLETEDON,
				CommonDataSource.getCurrentDate());

		database.update(CricketInnings.TABLE_NAME, values, "InningsId="
				+ inningsId, null);

		close();
	}

	public void deleteHistory(ScoreHistory score) {

		open();

		long historyId = score.getCricketInningsHistoryId();

		if (score.getIsBye() || score.getIsWide() || score.getIsNoBall()
				|| score.getIsLegBye()) {
			database.delete("CricketInningsHistoryDetail", "InningsHistoryId="
					+ historyId, null);
		}

		if (score.getIsWicket()) {
			database.delete("CricketWicketHistory", "InningsHistoryId="
					+ historyId, null);
		}

		database.delete("CricketInningsHistory", "InningsHistoryId="
				+ historyId, null);

		close();

	}

	public void setBatsmanOrder(long batsmanId, long inningsId, int overs,
			int balls) {
		open();

		database.delete("CricketInningsHistory", "InningsId=" + inningsId
				+ " and batsmanid=" + batsmanId + " and BowlerId IS NULL", null);
		
		ScoreHistory history = new ScoreHistory();
		history.setBatsmanId(batsmanId);
		history.setOver(overs);
		history.setBall(balls);
		history.setInningsId(inningsId);

		saveBallDetailsToDB(history);
		close();
	}

	private void saveBallDetailsToDB(ScoreHistory score) {

		long inningsId = score.getInningsId();
		ContentValues values = new ContentValues();
		values.put("InningsId", inningsId);

		if (score.getIsWide() || score.getIsNoBall()) {
			score.increaseBall();
		}

		values.put("over", score.over);
		values.put("ball", score.getBall());

		if (score.getBowlerId() > 0)
			values.put("BowlerId", score.getBowlerId());
		values.put("BatsmanId", score.getBatsmanId());

		values.put("TotalRuns", score.getTeamScore());
		values.put("Wickets", score.getWickets());

		if (score.getIsWide() || score.getIsLegBye() || score.getIsBye()) {
			values.put("runs", 0);
		} else {
			values.put("runs", score.getRunScored());
		}

		long insertId = database.insert("CricketInningsHistory", null, values);

		score.setCricketInningsHistoryId(insertId);

		if (score.getIsWicket()) {
			saveWicketFallDetailsToDatabase(insertId, inningsId,
					score.getWicketfallDetails());
		}

		if (score.getIsNoBall()) {
			saveExtrasDetailsToDB(insertId, inningsId, 1, 1);
		}

		if (score.getIsWide()) {
			saveExtrasDetailsToDB(insertId, inningsId, 2,
					1 + score.getRunScored());
		}

		if (score.getIsLegBye()) {
			saveExtrasDetailsToDB(insertId, inningsId, 3, score.getRunScored());

		}

		if (score.getIsLegBye()) {
			saveExtrasDetailsToDB(insertId, inningsId, 4, score.getRunScored());
		}

	}

	private void saveWicketFallDetailsToDatabase(long inningsHistoryId,
			long inningsId, WicketFall wicketfall) {

		if (wicketfall == null)
			return;

		ContentValues values = new ContentValues();
		values.put("InningsHistoryId", inningsHistoryId);
		values.put("inningsId", inningsId);
		values.put("OutType", wicketfall.getWicketType());

		if (wicketfall.getBowlerId() > 0) {
			values.put("BowlerId", wicketfall.getBowlerId());
		}
		values.put("batsmanId", wicketfall.getBatsmanId());

		if (wicketfall.getFielderId() > 0) {
			values.put("FielderId", wicketfall.getFielderId());
		}

		database.insert("CricketWicketHistory", null, values);
	}

	public List<BatsmanScore> getBatsmanruns(long inningsId) {
		String query = String
				.format("select h.batsmanId, sum(h.runs) runs, count(h.BowlerId) balls from CricketInningsHistory h Where h.Inningsid=%s AND h.InningsHistoryId NOT IN (Select InningsHistoryId FROM CricketInningsHistoryDetail hd where hd.inningsId=%s and hd.ballType IN (2)) group by h.batsmanid order by min(inningsHistoryId)",
						String.valueOf(inningsId), String.valueOf(inningsId));

		open();
		List<BatsmanScore> allScores = new ArrayList<BatsmanScore>();

		Cursor cursor = database.rawQuery(query, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			BatsmanScore score = getBatsmanScore(cursor, inningsId);
			allScores.add(score);
			cursor.moveToNext();
		}

		cursor.close();
		fillBoundries(allScores, inningsId);

		close();
		return allScores;
	}

	private BatsmanScore getBatsmanScore(Cursor cursor, long inningsId) {
		BatsmanScore score = new BatsmanScore();

		long batsmanId = cursor.getLong(0);

		score.setPlayerId(batsmanId);
		score.scoredRuns = cursor.getInt(1);
		score.ballsPlayed = cursor.getInt(2);

		score.setWicketFallDetails(getWicketFallDetails(batsmanId, inningsId));
		return score;
	}

	private void fillBoundries(List<BatsmanScore> allScores, long inningsId) {

		String query = String
				.format("select batsmanid, runs, count(runs) from CricketInningsHistory where InningsId=%s and runs IN (4,6) group by batsmanid, runs",
						String.valueOf(inningsId));

		Cursor cursor = database.rawQuery(query, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {

			long bowlerId = cursor.getLong(0);
			int boundryType = cursor.getInt(1);
			int totalBoundries = cursor.getInt(2);

			for (BatsmanScore score : allScores) {
				if (score.getPlayerId() == bowlerId) {

					if (boundryType == 4) {
						score.setFours(totalBoundries);
					} else {
						score.setSixes(totalBoundries);
					}
					break;
				}
			}

			cursor.moveToNext();
		}

		cursor.close();

	}

	private WicketFall getWicketFallDetails(long batsmanId, long inningsId) {
		WicketFall wicketfallDetails = null;

		String query = String
				.format("select bowlerId, fielderId, outtype from CricketWicketHistory h Where h.Inningsid=%s AND h.batsmanid=%s",
						String.valueOf(inningsId), String.valueOf(batsmanId));

		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();

			wicketfallDetails = new WicketFall();
			wicketfallDetails.setBatsmanId(batsmanId);
			wicketfallDetails.setInningsId(inningsId);

			wicketfallDetails.setBowlerId(cursor.getLong(0));
			wicketfallDetails.setFielderId(cursor.getLong(1));
			wicketfallDetails.setWicketType(cursor.getInt(2));
		}
		cursor.close();

		return wicketfallDetails;
	}

	public List<BowlerScore> getBowlerRuns(long inningsId) {
		String query = String
				.format("select h.bowlerId, sum(h.runs) runs, count(h.runs) balls from CricketInningsHistory h Where h.Inningsid=%s and h.bowlerId IS NOT NULL group by h.bowlerId order by min(inningsHistoryId)",
						String.valueOf(inningsId), String.valueOf(inningsId));

		open();
		List<BowlerScore> allScores = new ArrayList<BowlerScore>();

		Cursor cursor = database.rawQuery(query, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			BowlerScore score = getBowlerScore(cursor, inningsId);
			allScores.add(score);
			cursor.moveToNext();
		}

		cursor.close();

		fillExtras(allScores, inningsId);
		fillWickets(allScores, inningsId);

		close();
		return allScores;
	}

	private BowlerScore getBowlerScore(Cursor cursor, long inningsId) {
		BowlerScore score = new BowlerScore();

		long bowlerId = cursor.getLong(0);

		score.setBowlerId(bowlerId);
		score.setRunsGiven(cursor.getInt(1));
		score.setBallsBowled(cursor.getInt(2));

		return score;
	}

	private void fillExtras(List<BowlerScore> allScore, long inningsId) {

		String query = String
				.format("select h.bowlerId, hd.ballType, sum(hd.extras), count(hd.ballType) from CricketInningsHistoryDetail hd INNER JOIN CricketInningsHistory h ON hd.InningsHistoryId=h.InningsHistoryId AND hd.inningsId=%s AND hd.ballType IN (1,2) group by h.bowlerId, hd.balltype",
						String.valueOf(inningsId));

		Cursor cursor = database.rawQuery(query, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {

			long bowlerId = cursor.getLong(0);
			int ballType = cursor.getInt(1);
			int totalExtras = cursor.getInt(2);
			int totalBalls = cursor.getInt(2);

			for (BowlerScore score : allScore) {
				if (score.getBowlerId() == bowlerId) {

					score.detectBalls(totalBalls);

					if (ballType == 1) // No Ball
					{
						score.setNoBalls(totalExtras);
					} else // Wide
					{
						score.setWides(totalExtras);
					}

					score.addRunsGiven(totalExtras);
					break;
				}
			}

			cursor.moveToNext();
		}

		cursor.close();

	}

	private void fillWickets(List<BowlerScore> allScore, long inningsId) {

		String query = String
				.format("select bowlerId, count(bowlerId) from CricketWicketHistory where InningsId=%s AND OutType<>3  Group by bowlerid",
						String.valueOf(inningsId));

		Cursor cursor = database.rawQuery(query, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {

			long bowlerId = cursor.getLong(0);
			int wickets = cursor.getInt(1);

			for (BowlerScore score : allScore) {
				if (score.getBowlerId() == bowlerId) {
					score.setWickets(wickets);
					break;
				}
			}

			cursor.moveToNext();
		}

		cursor.close();

	}

	private void saveExtrasDetailsToDB(long inningsHistoryId, long inningsId,
			int ballType, int totExtras) {
		ContentValues values = new ContentValues();
		values.put("InningsHistoryId", inningsHistoryId);
		values.put("InningsId", inningsId);
		values.put("BallType", ballType);
		values.put("Extras", totExtras);

		database.insert("CricketInningsHistoryDetail", null, values);
	}

	public void UpdateAllocatedOvers(long inningsid, int changedOvers) {
		open();

		ContentValues values = new ContentValues();

		values.put(CricketInnings.COLUMN_ALLOCATEDOVERS, changedOvers);

		database.update(CricketInnings.TABLE_NAME, values, "InningsId="
				+ inningsid, null);
		close();

	}

}
