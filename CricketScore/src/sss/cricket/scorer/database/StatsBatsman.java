package sss.cricket.scorer.database;

public class StatsBatsman {

	int runs;
	int balls;
	int highest;
	int innings;
	double strikeRate;
	int fours;
	int sixes;
	long playerId;
	String playerName;
	
	double average;
	
	int twentyFivePlus;
	
	int fiftyPlus;

	public double getAverage() {
		return average;
	}

	public void setAverage(double average) {
		this.average = average;
	}

	public int getTwentyFivePlus() {
		return twentyFivePlus;
	}

	public void setTwentyFivePlus(int twentyFivePlus) {
		this.twentyFivePlus = twentyFivePlus;
	}

	public int getFiftyPlus() {
		return fiftyPlus;
	}

	public void setFiftyPlus(int fiftyPlus) {
		this.fiftyPlus = fiftyPlus;
	}

	public int getRuns() {
		return runs;
	}

	public void setRuns(int runs) {
		this.runs = runs;
	}

	public int getBalls() {
		return balls;
	}

	public void setBalls(int balls) {
		this.balls = balls;
	}

	public int getHighest() {
		return highest;
	}

	public void setHighest(int highest) {
		this.highest = highest;
	}

	public int getInnings() {
		return innings;
	}

	public void setInnings(int innings) {
		this.innings = innings;
	}

	public double getStrikeRate() {
		return strikeRate;
	}

	public void setStrikeRate(double strikeRate) {
		this.strikeRate = strikeRate;
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

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

}
