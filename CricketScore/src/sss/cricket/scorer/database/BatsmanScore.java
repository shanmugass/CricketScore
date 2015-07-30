package sss.cricket.scorer.database;

import sss.cricket.scorer.ui.ScoreSheetDataStore;

public class BatsmanScore {

	public int getScoredRuns() {
		return scoredRuns;
	}

	public int getBallsPlayed() {
		return ballsPlayed;
	}

	int scoredRuns = 0;
	int ballsPlayed = 0;

	int fours;
	int sixes;

	WicketFall wicketFallDetails;

	public WicketFall getWicketFallDetails() {
		return wicketFallDetails;
	}

	public int getFours() {
		return fours;
	}

	public void setFours(int fours) {
		this.fours = fours;
	}

	public int getSixes() {
		return sixes;
	}

	public void setSixes(int sixes) {
		this.sixes = sixes;
	}

	public void setWicketFallDetails(WicketFall wicketFallDetails) {
		this.wicketFallDetails = wicketFallDetails;
	}

	long playerId;

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public String getPlayerName() {
		return ScoreSheetDataStore.getPlayerName(playerId);
	}

	public void setScoredRuns(int scoredRuns) {
		this.scoredRuns = scoredRuns;
	}

	public void setBallsPlayed(int ballsPlayed) {
		this.ballsPlayed = ballsPlayed;
	}

	public void addScroreToPlayer(int runsToAdd) {

		ballsPlayed++;
		scoredRuns += runsToAdd;
	}

	public void revertBackScore(int runsToDetect) {
		ballsPlayed--;
		scoredRuns -= runsToDetect;

	}

	public String getStrikeRate() {
		if (ballsPlayed == 0)
			return "0";

		return String.format("%.1f", (float) scoredRuns / (float) ballsPlayed
				* 100.00);

	}

	public String getScoreBoardLabel(String playerName) {

		String returnValue = "";

		return returnValue + playerName + "  " + scoredRuns + "(" + ballsPlayed
				+ ")";

	}

	public String getWicketDetail() {
		if (wicketFallDetails == null || wicketFallDetails.getWicketType() < 1)
			return "not out";

		return wicketFallDetails.getWicketDetail();
	}
}
