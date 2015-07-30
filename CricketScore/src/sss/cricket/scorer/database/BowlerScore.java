package sss.cricket.scorer.database;

import sss.cricket.scorer.ui.ScoreSheetDataStore;

public class BowlerScore {

	int runsGiven = 0;
	int ballsBowled = 0;
	int wickets = 0;
	int wides = 0;
	int noBalls = 0;
	long bowlerId;

	

	public int getRunsGiven() {
		return runsGiven;
	}

	public void setRunsGiven(int runsGiven) {
		this.runsGiven = runsGiven;
	}

	public int getBallsBowled() {
		return ballsBowled;
	}

	public void setBallsBowled(int ballsBowled) {
		this.ballsBowled = ballsBowled;
	}

	public int getWickets() {
		return wickets;
	}

	public void setWickets(int wickets) {
		this.wickets = wickets;
	}

	public int getWides() {
		return wides;
	}

	public void setWides(int wides) {
		this.wides = wides;
	}

	public int getNoBalls() {
		return noBalls;
	}

	public void setNoBalls(int noBalls) {
		this.noBalls = noBalls;
	}

	public long getBowlerId() {
		return bowlerId;
	}

	public void setBowlerId(long bowlerId) {
		this.bowlerId = bowlerId;
	}

	public void addScore(int runs, Boolean isWicket, Boolean isNoBall,
			Boolean isWide) {
		runsGiven += runs;

		ballsBowled++;

		if (isWide) {
			runsGiven++;
			wides++;
			ballsBowled--;
		} else if (isNoBall) {
			runsGiven++;
			noBalls++;
			ballsBowled--;
		} else if (isWicket) {
			wickets++;
		}
	}

	public void revertScore(int runs, Boolean isWicket, Boolean isNoBall,
			Boolean isWide) {
		runsGiven -= runs;

		if (isWide || isNoBall) {
			runsGiven--;
		} else {
			ballsBowled--;
		}

		if (isWicket) {
			wickets--;
		}
	}

	public String getOvers() {
		String valueToReturn = "";

		valueToReturn += ballsBowled / 6;

		int balls = ballsBowled % 6;
		if (balls != 0) {
			valueToReturn += "." + balls;
		}

		return valueToReturn;
	}

	public String getEconomyRate() {
		String valueToReturn = String.format("%.1f",
				((float) runsGiven / (float) ballsBowled) * 6.0);

		return valueToReturn;
	}

	public String getPlayerName() {
		return ScoreSheetDataStore.getPlayerName(bowlerId);
	}

	public String getBowlerStats(String playerName) {
		String valueToReturn = playerName;

		valueToReturn += " " + ballsBowled / 6;

		int balls = ballsBowled % 6;
		if (balls != 0) {
			valueToReturn += "." + balls;
		}

		valueToReturn += " - " + runsGiven;

		valueToReturn += " - " + wickets;

		return valueToReturn;
	}

	public void detectBalls(int noOfballToDetect) {
		ballsBowled -= noOfballToDetect;
	}

	public void addRunsGiven(int runsToAdd) {
		runsGiven += runsToAdd;
	}

}
