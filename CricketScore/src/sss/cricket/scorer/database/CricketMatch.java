package sss.cricket.scorer.database;

import java.util.List;

import sss.cricket.scorer.ui.ScoreSheetDataStore;

import android.content.Context;

public class CricketMatch {

	public static final String TABLE_NAME = "CricketMatch";

	public static final String COLUMN_MatchId = "MatchId";
	public static final String COLUMN_MatchName = "MatchName";
	public static final String COLUMN_PlayedOn = "PlayedOn";
	public static final String COLUMN_Team1 = "Team1";
	public static final String COLUMN_Team2 = "Team2";
	public static final String COLUMN_TossWonBy = "TossWonBy";
	public static final String COLUMN_BattingTeam = "BattingTeam";
	public static final String COLUMN_MatchType = "MatchType";
	public static final String COLUMN_Overs = "Overs";
	public static final String COLUMN_WonBy = "WonBy";
	public static final String COLUMN_MatchResult = "MatchResult";
	public static final String COLUMN_MatchDescription = "MatchDescription";

	public static final String[] ALL_COLUMNS = { COLUMN_MatchId,
			COLUMN_MatchName, COLUMN_PlayedOn, COLUMN_Team1, COLUMN_Team2,
			COLUMN_TossWonBy, COLUMN_BattingTeam, COLUMN_MatchType,
			COLUMN_Overs, COLUMN_WonBy, COLUMN_MatchResult,
			COLUMN_MatchDescription };

	int MatchId;
	String MatchName;
	String PlayedOn;
	long Team1;
	long Team2;
	long TossWonBy;

	long firstInnningsBattingTeamId;

	MatchType matchType;

	Boolean isMatchCompleted;

	public Boolean getIsMatchCompleted() {
		return isMatchCompleted;
	}

	public void setIsMatchCompleted(Boolean isMatchCompleted) {
		this.isMatchCompleted = isMatchCompleted;
	}

	public CricketMatch() {
		MatchDescription = "";
	}

	public MatchType getMatchType() {
		return matchType;
	}

	public void setMatchType(MatchType matchType) {
		this.matchType = matchType;
	}

	public long getFirstInnningsBattingTeamId() {
		return firstInnningsBattingTeamId;
	}

	public void setFirstInnningsBattingTeamId(long firstInnningsBattingTeamId) {
		this.firstInnningsBattingTeamId = firstInnningsBattingTeamId;
	}

	public String getSpinnerText() {
		return MatchId + ".  " + MatchName;
	}

	public String getValue() {
		return String.valueOf(MatchId);
	}

	public String toString() {
		return MatchId + ".  " + MatchName;
	}

	CricketInnings[] allInnings = new CricketInnings[4];
	int inningsNo = -1;

	public CricketInnings getInnings(int index) {
		return allInnings[index];
	}

	public int getCurrentInningsId() {

		return inningsNo + 1;
	}

	public CricketInnings getCurrentInnings() {
		BattingTeam = allInnings[inningsNo].getBattingTeam();
		return allInnings[inningsNo];

	}

	public void createInnings(long battingTeamId) {
		inningsNo++;

		int allocatedOvers = 0;
		if (inningsNo > 0) {
			allocatedOvers = allInnings[inningsNo - 1].allocatedOvers;
		} else {
			allocatedOvers = Overs;
		}

		BattingTeam = battingTeamId;
		CricketInnings cricketInnigs = new CricketInnings();
		cricketInnigs.setBattingTeam(battingTeamId);
		cricketInnigs.setAllocatedOvers(allocatedOvers);
		cricketInnigs.setInningsNo(inningsNo);
		cricketInnigs.setMatchId(MatchId);
		cricketInnigs.setInningsNo(inningsNo);
		CricketInningsDataSource db = new CricketInningsDataSource(
				ScoreSheetDataStore.context);

		db.createInnings(cricketInnigs);

		allInnings[inningsNo] = cricketInnigs;
	}

	public String getFirstInningsScore(long teamId) {
		int i = 0;
		for (i = 0; i < 2; i++) {
			if (allInnings[i] != null
					&& allInnings[i].getBattingTeam() == teamId) {
				return getScore(allInnings[i]);
			}
		}

		return "0/0";
	}

	public String getSecondInningsScore(long teamId) {
		int i = 2;
		for (i = 2; i < 4; i++) {
			if (allInnings[i] != null
					&& allInnings[i].getBattingTeam() == teamId) {
				return getScore(allInnings[i]);
			}
		}

		return "0/0";
	}

	public String getScore(CricketInnings innings) {
		String score = "";

		score += innings.getTotalRuns() + "/" + innings.getWickets() + " ("
				+ innings.getBowledOvers() + "." + innings.getBalls() + " ovr)";

		return score;
	}

	public long getMatchId() {
		return MatchId;
	}

	public void setMatchId(int matchId) {
		MatchId = matchId;
	}

	public String getMatchName() {
		return MatchName;
	}

	public void setMatchName(String matchName) {
		MatchName = matchName;
	}

	public String getPlayedOn() {
		return PlayedOn;
	}

	public void setPlayedOn(String playedOn) {
		PlayedOn = playedOn;
	}

	public long getTeam1() {
		return Team1;
	}

	public void setTeam1(long team1) {
		Team1 = team1;
	}

	public long getTeam2() {
		return Team2;
	}

	public void setTeam2(long team2) {
		Team2 = team2;
	}

	public long getTossWonBy() {
		return TossWonBy;
	}

	public int getTarget() {
		int score = 0;
		for (int i = 0; i <= inningsNo; i++) {

			CricketInnings innings = allInnings[i];

			if (innings == null)
				continue;

			if (innings.battingTeam == BattingTeam) {
				score += innings.getTotalRuns();
			} else {
				score -= innings.getTotalRuns();
			}
		}

		return score;
	}

	public void deleteMatch(Context context) {

		CricketMatchDataSource database = new CricketMatchDataSource(context);
		database.deleteMatch(MatchId);

	}

	public String getMatchSummary() {

		isMatchCompleted = false;
		int remainingBalls = getCurrentInnings().getRemainingBalls();
		isMatchCompleted = (remainingBalls < 1);
		// TODO add required run rate and with all Match types
		String returnValue = "";
		CricketInnings currentInnings = getCurrentInnings();
		returnValue += currentInnings.getRunRate(true) + ", Extras: "
				+ currentInnings.getExtras()
				+ currentInnings.getCurrentOverScore();

		int currentInningsId = getCurrentInningsId();

		if (currentInningsId == 1)
			return returnValue;

		returnValue += "<br/>";

		int firstInningsScore = allInnings[0].getTotalRuns();

		if (matchType.IsTwoInnigs) {

			if (currentInningsId == 2) {

				// TODO move follow on target to match type
				double followOn = ((float) firstInningsScore * 70.00) / 100.00;

				int followOnTarget = (int) Math.ceil(followOn);
				int secondInningsScore = allInnings[1].getTotalRuns();

				if (followOnTarget > secondInningsScore) {
					if (secondInningsScore > 0) {
						returnValue += (followOnTarget - secondInningsScore)
								+ " runs to avoid follow-on from "
								+ remainingBalls + " Balls, F-Target: "
								+ followOnTarget;

						int target = getTarget();
						returnValue += ", Trail by: " + (target * -1);

					} else {
						returnValue += "Follow on Target: " + followOnTarget;
					}
				} else if (followOnTarget == secondInningsScore) {
					returnValue += "Follow on avoided";
				} else if (firstInningsScore < secondInningsScore) {

					returnValue += "Lead by "
							+ (secondInningsScore - firstInningsScore);
				} else if (secondInningsScore < firstInningsScore) {

					returnValue += "Trail by "
							+ (firstInningsScore - secondInningsScore);
				} else {
					returnValue += "Scores are level";
				}
			} else if (currentInningsId == 3) {

				// TODO add logic for complete the match on third innings
				int target = getTarget();

				if (target < 0) {
					returnValue += "Trail by " + (target * -1);
				} else if (target == 0) {
					returnValue += "Scores are level ";
				} else {
					returnValue += "Lead by " + target;
				}

			} else {

				int target = getTarget();

				if (target < 0) {
					returnValue += ((target * -1) + 1) + " Runs to Win from "
							+ remainingBalls + " balls";
				} else if (target == 0) {
					returnValue += "Scores are level ";
				} else {
					if (Team1 == allInnings[3].battingTeam) {

						returnValue += ScoreSheetDataStore.Team1.TeamName
								+ " Won the match";
					} else {
						returnValue += ScoreSheetDataStore.Team2.TeamName
								+ " Won the match";
					}
				}

			}
		} else {

			int secondInningsScore = allInnings[1].getTotalRuns();

			if (secondInningsScore < firstInningsScore) {
				returnValue += "Target: " + (firstInningsScore + 1);
			}

			if (secondInningsScore > 0) {
				if (firstInningsScore > secondInningsScore) {
					returnValue += ", "
							+ (firstInningsScore + 1 - secondInningsScore)
							+ " Runs to win from " + remainingBalls + "Balls";
				} else if (firstInningsScore == secondInningsScore) {
					returnValue += "Score are level";
				} else {
					isMatchCompleted = true;
					if (Team1 == allInnings[1].battingTeam) {

						returnValue += ScoreSheetDataStore.Team1.TeamName
								+ " Won the match";
					} else {
						returnValue += ScoreSheetDataStore.Team2.TeamName
								+ " Won the match";
					}
				}
			}

		}

		return returnValue;

	}

	public void setTossWonBy(long tossWonBy) {
		TossWonBy = tossWonBy;
	}

	public long getBattingTeam() {
		return BattingTeam;
	}

	public void setBattingTeam(long battingTeam) {
		BattingTeam = battingTeam;
	}

	public long getMatchTypeId() {
		return MatchTypeId;
	}

	public void setMatchTypeId(long matchType) {
		MatchTypeId = matchType;
	}

	public int getOvers() {
		return Overs;
	}

	public void setOvers(int overs) {
		Overs = overs;
	}

	public long getWonBy() {
		return WonBy;
	}

	public void setWonBy(long wonBy) {
		WonBy = wonBy;
	}

	public long getMatchResult() {
		return MatchResult;
	}

	public void setMatchResult(long matchResult) {
		MatchResult = matchResult;
	}

	public String getMatchDescription() {
		return MatchDescription;
	}

	public void setMatchDescription(String matchDescription) {
		MatchDescription = matchDescription;
	}

	public void selectAllInningsByMatch(Context context) {
		inningsNo = 0;
		allInnings = new CricketInnings[4];

		CricketInningsDataSource database = new CricketInningsDataSource(
				context);

		List<CricketInnings> allInningsList = database
				.getAllInningsByMatch(MatchId);

		for (CricketInnings innings : allInningsList) {
			allInnings[inningsNo] = innings;
			ScoreSheetDataStore.currentInnings = innings;

			if (innings.getBalls() >= 6) {
				innings.setBalls(0);
				innings.setBowledOvers(innings.getBowledOvers() + 1);
			}

			inningsNo++;
		}

		if (inningsNo > 0) {
			inningsNo--;
		}

	}

	long BattingTeam;
	long MatchTypeId;
	int Overs;
	long WonBy;
	long MatchResult;
	String MatchDescription;

	public long getWinningTeam() {

		int team1Score = 0;
		int team2Score = 0;

		int innings = 0;

		for (innings = 0; innings < 4; innings++) {

			CricketInnings currentInnings = allInnings[innings];
			if (currentInnings == null)
				break;
			if (currentInnings.getBattingTeam() == Team1) {
				team1Score += currentInnings.getTotalRuns();
			} else {
				team2Score += currentInnings.getTotalRuns();
			}
		}

		if (team2Score < team1Score) {
			return Team1;
		} else {
			return Team2;
		}
	}

}
