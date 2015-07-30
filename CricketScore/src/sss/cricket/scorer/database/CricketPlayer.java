package sss.cricket.scorer.database;

import sss.cricket.scorer.ui.ScoreSheetDataStore;

public class CricketPlayer {

	public static final String TABLE_NAME = "CricketPlayer";

	public static final String ASSOCIATION_TABLE_NAME = "CricketTeamAssociation";

	public static final String COLUMN_ID = "PlayerId";
	public static final String COLUMN_PLAYER_NAME = "PlayerName";

	public static final String[] ALL_COLUMNS = { COLUMN_ID, COLUMN_PLAYER_NAME };

	public static final String COLUMN_TEAM_ID = "TeamId";

	public long PlayerId;
	public String PlayerName;

	public long TeamId;

	int battingSkill;

	int bowlingSkill;

	public long getPlayerId() {
		return PlayerId;
	}

	public void setPlayerId(long playerId) {
		PlayerId = playerId;
	}

	public String getPlayerName() {
		return PlayerName;
	}

	public void setPlayerName(String playerName) {
		PlayerName = playerName;
	}

	public long getTeamId() {
		return TeamId;
	}

	public void setTeamId(long teamId) {
		TeamId = teamId;
	}

	public int getBattingSkill() {
		return battingSkill;
	}

	public void setBattingSkill(int battingSkill) {
		this.battingSkill = battingSkill;
	}

	public int getBowlingSkill() {
		return bowlingSkill;
	}

	public void setBowlingSkill(int bowlingSkill) {
		this.bowlingSkill = bowlingSkill;
	}

	public WicketFall getWicketFallDetails() {
		if (batsmanScore == null)
			return null;
		else {
			return batsmanScore.getWicketFallDetails();
		}
	}

	public void setWicketFallDetails(WicketFall wicketFallDetails) {

		if (batsmanScore != null) {
			batsmanScore.setWicketFallDetails(wicketFallDetails);
		}
	}

	public String getSpinnerText() {
		return PlayerName;
	}

	public String getValue() {
		return String.valueOf(PlayerId);
	}

	public String toString() {
		return PlayerName;
	}

	private BatsmanScore batsmanScore;

	public BatsmanScore getBatsmanScore() {
		return batsmanScore;
	}

	public void setBatsmanScore(BatsmanScore score) {
		batsmanScore = score;
	}

	public void setBowlerScore(BowlerScore score) {
		bowlerScore = score;
	}

	public void initializeBattingScore() {
		if (batsmanScore == null) {
			batsmanScore = new BatsmanScore();
		}
		if (batsmanScore.getBallsPlayed() == 0) {
			ScoreSheetDataStore.setBattingOrder(PlayerId);
		}
	}

	public void addBatsmanScrore(int scoreToAdd) {
		batsmanScore.addScroreToPlayer(scoreToAdd);
	}

	public void revertBackBatsmanScore(int runsToRevertBack) {
		batsmanScore.revertBackScore(runsToRevertBack);
	}

	public String getBatsmanStats() {
		return batsmanScore.getScoreBoardLabel(PlayerName);
	}

	private BowlerScore bowlerScore;

	public void initializeBowlerScore() {
		if (bowlerScore == null)
			bowlerScore = new BowlerScore();
	}

	public void addBowlerScrore(int runs, Boolean isWicket, Boolean isNoBall,
			Boolean isWide) {
		bowlerScore.addScore(runs, isWicket, isNoBall, isWide);
	}

	public void revertBowlerScore(int runs, Boolean isWicket, Boolean isNoBall,
			Boolean isWide) {
		bowlerScore.revertScore(runs, isWicket, isNoBall, isWide);
	}

	public String getBowlerStats() {
		return bowlerScore.getBowlerStats(PlayerName);
	}

	public void clearStats() {
		batsmanScore = null;
		bowlerScore = null;
	}

	public Boolean isPlaying() {

		return (batsmanScore == null || batsmanScore.getWicketFallDetails() == null);

	}

}
