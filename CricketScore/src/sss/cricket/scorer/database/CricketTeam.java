package sss.cricket.scorer.database;

import java.util.List;

import android.content.Context;

public class CricketTeam {

	public static final String TABLE_NAME = "CricketTeam";

	public static final String COLUMN_ID = "TeamId";
	public static final String COLUMN_TEAM_NAME = "TeamName";

	public static final String[] ALL_COLUMNS = { COLUMN_ID, COLUMN_TEAM_NAME };

	public long TeamId;

	public String TeamName;

	public List<CricketPlayer> Players;

	public void Select(long teamId, Context context) {

		CricketTeamDataSource database = new CricketTeamDataSource(context);

		database.fillTeam(this, teamId);

	}

	public void fillPlayers(Context context) {
		CricketPlayerDataSource playerDatabase = new CricketPlayerDataSource(
				context);

		Players = playerDatabase.getPlayers(TeamId, "PlayerName");
	}

	public void fillPlayersByBattingOrder(Context context) {
		CricketPlayerDataSource playerDatabase = new CricketPlayerDataSource(
				context);

		Players = playerDatabase.getPlayers(TeamId, "BattingSkill DESC ");
	}

	public void fillPlayersByBowlingOrder(Context context) {
		CricketPlayerDataSource playerDatabase = new CricketPlayerDataSource(
				context);

		Players = playerDatabase.getPlayers(TeamId, "BowlingSkill DESC");
	}

	public CricketPlayer[] getPlayersInArray() {
		return Players.toArray(new CricketPlayer[Players.size()]);
	}

	public String getSpinnerText() {
		return TeamName;
	}

	public String getValue() {
		return String.valueOf(TeamId);
	}

	public String toString() {
		return TeamName;
	}

	public void fillBattingScores(List<BatsmanScore> allScores) {

		if (Players == null)
			return;

		for (BatsmanScore score : allScores) {
			long batsmanId = score.getPlayerId();
			for (CricketPlayer player : Players) {

				if (player.PlayerId == batsmanId) {
					player.setBatsmanScore(score);
					break;
				}

			}

		}

	}

	public void fillBowlerScores(List<BowlerScore> allScores) {

		if (Players == null)
			return;

		for (BowlerScore score : allScores) {
			long bowlerId = score.getBowlerId();
			for (CricketPlayer player : Players) {

				if (player.PlayerId == bowlerId) {
					player.setBowlerScore(score);
					break;
				}

			}

		}
	}
}
