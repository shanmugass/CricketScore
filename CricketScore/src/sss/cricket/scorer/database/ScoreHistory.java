package sss.cricket.scorer.database;

public class ScoreHistory {

	long inningsId;

	int teamScore;
	long wickets;
	long batsmanId;
	long nonStrikeBatsmanId;

	public ScoreHistory() {

		isWicket = isLegBye = isBye = isWide = isNoBall = false;
		over = ball = 0;
		batsmanId = bowlerId = inningsId = 0;
	}

	long cricketInningsHistoryId;

	public long getCricketInningsHistoryId() {
		return cricketInningsHistoryId;
	}

	public void setCricketInningsHistoryId(long cricketInningsHistoryId) {
		this.cricketInningsHistoryId = cricketInningsHistoryId;
	}

	public long getInningsId() {
		return inningsId;
	}

	public void setInningsId(long inningsId) {
		this.inningsId = inningsId;
	}

	public int getTeamScore() {
		return teamScore;
	}

	public void setTeamScore(int teamScore) {
		this.teamScore = teamScore;
	}

	public long getWickets() {
		return wickets;
	}

	public void setWickets(long wickets) {
		this.wickets = wickets;
	}

	public long getBatsmanId() {
		return batsmanId;
	}

	public void setBatsmanId(long batsmanId) {
		this.batsmanId = batsmanId;
	}

	public long getBowlerId() {
		return bowlerId;
	}

	public void setBowlerId(long bowlerId) {
		this.bowlerId = bowlerId;
	}

	public int getOver() {
		return over;
	}

	public void setOver(int over) {
		this.over = over;
	}

	public int getBall() {
		return ball;
	}

	public void setBall(int ball) {
		this.ball = ball;
	}

	public long getNonStrikeBatsmanId() {
		return nonStrikeBatsmanId;
	}

	public void setNonStrikeBatsmanId(long nonStrikeBatsmanId) {
		this.nonStrikeBatsmanId = nonStrikeBatsmanId;
	}

	long bowlerId;
	int over;
	int ball;

	int runScored;

	public int getRunScored() {
		return runScored;
	}

	int extras;

	public int getExtras() {
		return extras;
	}

	public void setExtras(int extras) {
		this.extras = extras;
	}

	public void setRunScored(int runScored) {
		this.runScored = runScored;
	}

	public String getOverLabel() {
		return over + "." + ball;
	}

	public String getBallDetail() {

		String returnValue = "";

		if (isWicket) {
			returnValue += "Wk";
		}

		if (isWide) {
			if (returnValue != "")
				returnValue += "+";

			returnValue += "Wd";
		}

		if (isNoBall) {
			if (returnValue != "")
				returnValue += "+";

			returnValue += "Nb";
		}
		if (isBye) {
			if (returnValue != "")
				returnValue += "+";

			returnValue = "B";
		}
		if (isLegBye) {
			if (returnValue != "")
				returnValue += "+";

			returnValue = "Lb";
		}

		if (returnValue != "") {
			if (runScored > 0) {
				returnValue += "+";

				returnValue += "" + runScored;
			}
		} else {
			returnValue += "" + runScored;
		}

		return returnValue;

	}

	Boolean isWicket;
	Boolean isNoBall;
	Boolean isWide;
	Boolean isBye;

	public Boolean getIsWicket() {
		return isWicket;
	}

	public void setIsWicket(Boolean isWicket) {
		this.isWicket = isWicket;
	}

	public Boolean getIsNoBall() {
		return isNoBall;
	}

	public void setIsNoBall(Boolean isNoBall) {
		this.isNoBall = isNoBall;
	}

	public Boolean getIsWide() {
		return isWide;
	}

	public void setIsWide(Boolean isWide) {
		this.isWide = isWide;
	}

	public Boolean getIsBye() {
		return isBye;
	}

	public void setIsBye(Boolean isBye) {
		this.isBye = isBye;
	}

	public Boolean getIsLegBye() {
		return isLegBye;
	}

	public void setIsLegBye(Boolean isLegBye) {
		this.isLegBye = isLegBye;
	}

	Boolean isLegBye;

	WicketFall wicketfallDetails;

	public WicketFall getWicketfallDetails() {
		return wicketfallDetails;
	}

	public void setWicketfallDetails(WicketFall wicketfallDetails) {
		this.wicketfallDetails = wicketfallDetails;
	}

	int totalExtras;

	public int getTotalExtras() {
		return totalExtras;
	}

	public void setTotalExtras(int totalExtras) {
		this.totalExtras = totalExtras;
	}

	public void increaseBall() {

		if (ball == 6) {
			ball = 1;
			over++;
		} else {
			ball++;
		}
	}
	
	public void decreaseBall() {

		if (ball == 0) {
			ball = 6;
			over--;
		} else {
			ball--;
		}
	}

}
