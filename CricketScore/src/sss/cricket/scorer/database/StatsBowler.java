package sss.cricket.scorer.database;

public class StatsBowler {

	int wickets;
	int wides;
	int noBalls;
	int innings;
	double economyRate;
	double strikeRate;
	int runs;

	public int getRuns() {
		return runs;
	}

	public void setRuns(int runs) {
		this.runs = runs;
	}

	int balls;

	int threePlusWickets;

	int fivePlusWickets;

	public int getThreePlusWickets() {
		return threePlusWickets;
	}

	public void setThreePlusWickets(int threePlusWickets) {
		this.threePlusWickets = threePlusWickets;
	}

	public int getFivePlusWickets() {
		return fivePlusWickets;
	}

	public void setFivePlusWickets(int fivePlusWickets) {
		this.fivePlusWickets = fivePlusWickets;
	}

	public String getPlayerName() {
		return playerName;
	}

	long playerId;
	String playerName;

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
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

	public int getInnings() {
		return innings;
	}

	public void setInnings(int innings) {
		this.innings = innings;
	}

	public double getEconomyRate() {
		return economyRate;
	}

	public void setEconomyRate(double economyRate) {
		this.economyRate = economyRate;
	}

	public double getStrikeRate() {
		return strikeRate;
	}

	public void setStrikeRate(double strikeRate) {
		this.strikeRate = strikeRate;
	}

	public int getBalls() {
		return balls;
	}

	public void setBalls(int balls) {
		this.balls = balls;
	}

	public String getOvers() {
		String valueToReturn = "";

		valueToReturn += balls / 6;

		int remBalls = balls % 6;
		if (remBalls != 0) {
			valueToReturn += "." + remBalls;
		}

		return valueToReturn;
	}

}
