package sss.cricket.scorer.database;

import java.util.ArrayList;
import java.util.List;

import sss.cricket.scorer.ui.FixedQueue;
import sss.cricket.scorer.ui.ScoreSheetDataStore;

public class CricketInnings {

	public static final String COLUMN_INNINGSID = "inningsId";
	public static final String COLUMN_MATCHID = "matchId";
	public static final String COLUMN_ALLOCATEDOVERS = "allocatedOvers";
	public static final String COLUMN_INNINGSNO = "inningsNo";
	public static final String COLUMN_STARTEDON = "startedOn";
	public static final String COLUMN_COMPLETEDON = "completedOn";
	public static final String COLUMN_BATTINGTEAM = "battingTeam";
	public static final String COLUMN_RUNS = "runs";
	public static final String COLUMN_BOWLEDOVERS = "bowledOvers";
	public static final String COLUMN_BALLS = "Balls";
	public static final String COLUMN_EXTRAS = "extras";
	public static final String COLUMN_WICKETS = "wickets";

	public static final String TABLE_NAME = "CricketInnings";

	public static final String[] ALL_COLUMNS = { COLUMN_INNINGSID,
			COLUMN_MATCHID, COLUMN_ALLOCATEDOVERS, COLUMN_INNINGSNO,
			COLUMN_STARTEDON, COLUMN_COMPLETEDON, COLUMN_BATTINGTEAM,
			COLUMN_RUNS, COLUMN_BOWLEDOVERS, COLUMN_EXTRAS, COLUMN_WICKETS };

	long inningsId;
	int matchId;
	int allocatedOvers;
	int inningsNo;
	String startedOn;
	String completedOn;
	long battingTeam;
	int totalRuns;
	int bowledOvers;
	int balls;
	int extras;
	int wickets;

	FixedQueue<ScoreHistory> runQueue = new FixedQueue<ScoreHistory>(13);

	public String getRunRate(Boolean includefullName) {
		float totalOvers = (float) bowledOvers + balls / 6.0f;

		if (totalOvers == 0) {
			return includefullName ? "Run Rate: 0" : "RR: 0";
		}

		return (includefullName ? "Run Rate: " : "RR: ")
				+ String.format("%.1f", (float) totalRuns / totalOvers);
	}

	public void setBattingOrder(long batsmanId) {

		CricketInningsDataSource database = new CricketInningsDataSource(
				ScoreSheetDataStore.context);
		database.setBatsmanOrder(batsmanId, inningsId, bowledOvers, balls);

	}

	public int getRemainingBalls() {

		int ballsRemaining = 0;

		ballsRemaining = (allocatedOvers - bowledOvers) * 6;
		ballsRemaining = ballsRemaining - balls;

		return ballsRemaining;
	}

	public Boolean recordScore(long batsmanId, long nonStrikeBatsmanId,
			long bowlerId, int runsScored, Boolean isWide, Boolean isNoBall,
			Boolean isBye, Boolean isWicket, Boolean isLegBye,
			WicketFall wicketfall) {

		if (runQueue.list.size() == 0) {
			addEmptyHistory();
		}

		Boolean changeBowler = false;

		totalRuns += runsScored;

		ScoreHistory history = new ScoreHistory();
		history.setBatsmanId(batsmanId);
		history.setNonStrikeBatsmanId(nonStrikeBatsmanId);
		history.setBowlerId(bowlerId);

		if (!isWide && !isBye & !isLegBye) {

			ScoreSheetDataStore.getOnStikeBatsman()
					.addBatsmanScrore(runsScored);
		}

		if (runsScored % 2 == 1) {
			ScoreSheetDataStore.switchBatsman();
		}

		Boolean isBowlerWicket = false;

		// checking is bowler wicket or not, excluding Run out
		if (isWicket && wicketfall != null) {
			isBowlerWicket = (wicketfall.getWicketType() != 3);
		}

		if (isBye || isLegBye) {
			ScoreSheetDataStore.getBowler().addBowlerScrore(0,
					(isWicket && isBowlerWicket), isNoBall, isWide);
		} else {
			ScoreSheetDataStore.getBowler().addBowlerScrore(runsScored,
					(isWicket && isBowlerWicket), isNoBall, isWide);
		}

		if (!isWide && !isNoBall) {

			// Over Change
			if (balls >= 5) {

				bowledOvers++;
				ScoreSheetDataStore.switchBatsman();
				balls = 0;
				changeBowler = true;

			} else {
				balls++;
			}
		} else {

			extras++;
			if (isLegBye || isBye) {
				extras += runsScored;
			}
			totalRuns++;
		}

		// New Wicket
		if (isWicket) {
			wickets++;
		}

		history.setInningsId(inningsId);
		history.setTeamScore(totalRuns);
		if (balls == 0 && bowledOvers != 0) {
			history.setBall(6);
			history.setOver(bowledOvers - 1);
		} else {
			history.setBall(balls);
			history.setOver(bowledOvers);
		}
		history.setWickets(wickets);
		history.setRunScored(runsScored);
		history.setTotalExtras(extras);
		history.setIsBye(isBye);
		history.setIsLegBye(isLegBye);
		history.setIsNoBall(isNoBall);
		history.setIsWicket(isWicket);
		history.setIsWide(isWide);
		history.setWicketfallDetails(wicketfall);

		runQueue.add(history);

		saveToDatabase(history);

		return changeBowler;
	}

	private void saveToDatabase(ScoreHistory history) {
		CricketInningsDataSource database = new CricketInningsDataSource(
				ScoreSheetDataStore.context);

		database.updateInningsAndScoreDetails(history);
	}

	private void deleteFromDatabase(ScoreHistory history) {
		CricketInningsDataSource database = new CricketInningsDataSource(
				ScoreSheetDataStore.context);

		database.deleteHistory(history);
		database.updateInningsScore(inningsId, bowledOvers, balls, totalRuns,
				wickets, extras);
	}

	public String getScoreText() {
		return "" + totalRuns + "/" + wickets + " (" + "" + bowledOvers + "."
				+ balls + " ovr)";
	}

	public String getCurrentOverScore() {

		if (runQueue.list.size() < 1)
			return "";

		int lastBall = runQueue.list.size() - 1;

		int overNo = runQueue.list.get(lastBall).over;

		int runsToReturn = 0;
		for (ScoreHistory history : runQueue.list) {
			if (history.getOver() == overNo) {

				runsToReturn += history.getRunScored();

				if (history.getIsWide() || history.getIsNoBall()) {
					runsToReturn++;
				}
			}
		}

		return ", <u><i>" + (overNo + 1) + " Over: " + runsToReturn
				+ " Runs</i></u>";
	}

	public void revertLastRecordAction() {
		ScoreHistory scoreToReverse = runQueue.getLastBeforeAction();
		ScoreHistory revertPlayersScore = runQueue.getLastAction();

		if (scoreToReverse == null)
			return;

		if (scoreToReverse.getIsWide() || scoreToReverse.getIsNoBall()) {
			scoreToReverse.decreaseBall();
		}

		bowledOvers = scoreToReverse.getOver();
		balls = scoreToReverse.getBall();
		if (balls == 6) {
			balls = 0;
			bowledOvers++;
		}
		totalRuns = scoreToReverse.getTeamScore();
		extras = scoreToReverse.getTotalExtras();

		ScoreSheetDataStore.setCurrentBatsmans(
				revertPlayersScore.getBatsmanId(),
				revertPlayersScore.getNonStrikeBatsmanId());
		ScoreSheetDataStore.setCurrentBowler(revertPlayersScore.getBowlerId());

		int runsToDetect = revertPlayersScore.getRunScored();

		if (!revertPlayersScore.getIsWide()
				&& !revertPlayersScore.getIsLegBye()
				&& !revertPlayersScore.getIsBye()) {
			ScoreSheetDataStore.getOnStikeBatsman().revertBackBatsmanScore(
					runsToDetect);
		}

		ScoreSheetDataStore.getBowler().revertBowlerScore(runsToDetect,
				revertPlayersScore.getIsWicket(),
				revertPlayersScore.getIsNoBall(),
				revertPlayersScore.getIsWide());

		if (revertPlayersScore.getIsWicket()) {
			wickets--;
		}

		deleteFromDatabase(revertPlayersScore);

		runQueue.removeLast();
	}

	public List<BallByBall> getLastTenBallDetails() {

		List<BallByBall> allBalls = new ArrayList<BallByBall>();

		Boolean isFirst = true;
		for (ScoreHistory run : runQueue.list) {
			if (isFirst) {
				isFirst = false;
				continue;
			}

			BallByBall ball = new BallByBall();

			ball.setOver(run.getOverLabel());
			ball.setBallDetails(run.getBallDetail());

			allBalls.add(ball);
		}

		return allBalls;
	}

	public CricketInnings() {

		startedOn = CommonDataSource.getCurrentDate();
		bowledOvers = 0;
		extras = 0;
		wickets = 0;

	}

	public void addEmptyHistory() {
		ScoreHistory history = new ScoreHistory();

		history.setBall(balls);
		history.setWickets(wickets);
		history.setTeamScore(totalRuns);
		history.setRunScored(0);
		history.setBatsmanId(ScoreSheetDataStore.OnStrikeBatsmanId);
		history.setBowlerId(ScoreSheetDataStore.Bowler.PlayerId);
		history.setNonStrikeBatsmanId(ScoreSheetDataStore.getNonStikeBatsman().PlayerId);
		history.setOver(bowledOvers);
		runQueue.add(history);
	}

	public Boolean hasRecordedAction() {
		return runQueue.list.size() > 1;
	}

	public long getInningsId() {
		return inningsId;
	}

	public void setInningsId(long inningsId) {
		this.inningsId = inningsId;
	}

	public int getMatchId() {
		return matchId;
	}

	public void setMatchId(int matchId) {
		this.matchId = matchId;
	}

	public int getAllocatedOvers() {
		return allocatedOvers;
	}

	public void setAllocatedOvers(int allocatedOvers) {
		if (this.allocatedOvers == allocatedOvers)
			return;

		this.allocatedOvers = allocatedOvers;

	}

	public void saveOvers() {
		if (inningsId > 0) {
			CricketInningsDataSource database = new CricketInningsDataSource(
					ScoreSheetDataStore.context);
			database.UpdateAllocatedOvers(inningsId, allocatedOvers);
		}
	}

	public int getInningsNo() {
		return inningsNo;
	}

	public void setInningsNo(int inningsNo) {
		this.inningsNo = inningsNo;
	}

	public String getStartedOn() {
		return startedOn;
	}

	public void setStartedOn(String startedOn) {
		this.startedOn = startedOn;
	}

	public String getCompletedOn() {
		return completedOn;
	}

	public void setCompletedOn(String completedOn) {
		this.completedOn = completedOn;
	}

	public long getBattingTeam() {
		return battingTeam;
	}

	public void setBattingTeam(long battingTeam) {
		this.battingTeam = battingTeam;
	}

	public int getTotalRuns() {
		return totalRuns;
	}

	public void setTotalRuns(int runs) {
		this.totalRuns = runs;
	}

	public int getBowledOvers() {
		return bowledOvers;
	}

	public int getBalls() {
		return balls;
	}

	public void setBalls(int balls) {
		this.balls = balls;
	}

	public void setBowledOvers(int bowledOvers) {
		this.bowledOvers = bowledOvers;
	}

	public int getExtras() {
		return extras;
	}

	public void setExtras(int extras) {
		this.extras = extras;
	}

	public int getWickets() {
		return wickets;
	}

	public void setWickets(int wickets) {
		this.wickets = wickets;
	}

}
