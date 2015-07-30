package sss.cricket.scorer.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sss.cricket.scorer.database.BallByBall;
import sss.cricket.scorer.database.CricketInnings;
import sss.cricket.scorer.database.CricketMatch;
import sss.cricket.scorer.database.CricketPlayer;
import sss.cricket.scorer.database.CricketTeam;
import sss.cricket.scorer.database.WicketFall;

import android.content.Context;

public class ScoreSheetDataStore {

	public static CricketTeam Team1;
	public static CricketTeam Team2;
	public static CricketMatch Match;

	public static CricketPlayer PlayerToView;

	public static Context context;

	public static CricketTeam getBattingTeam() {
		if (Team1.TeamId == Match.getBattingTeam()) {
			return Team1;
		} else {
			return Team2;
		}
	}

	public static Boolean scoreSheetLoaded;

	public static CricketTeam getBowlingTeam() {
		if (Team1.TeamId == Match.getBattingTeam()) {
			return Team2;
		} else {
			return Team1;
		}
	}

	public static Boolean isOversCompleted() {
		return currentInnings.getAllocatedOvers() == currentInnings
				.getBowledOvers();
	}

	public static CricketInnings currentInnings;

	static Map<String, String> playerDictionary = new HashMap<String, String>();

	public static String getPlayerName(long playerId) {

		String strPlayerId = String.valueOf(playerId);
		if (playerDictionary.containsKey(strPlayerId)) {
			return playerDictionary.get(strPlayerId);
		}

		return "";
	}

	public static void createNewInnings(long batttingTeamId, Context context) {

		fillPlayers(batttingTeamId, context);

		fillPlayerDictionary();

		Batsman1 = Batsman2 = null;
		Bowler = null;
		Match.createInnings(batttingTeamId);
		currentInnings = Match.getCurrentInnings();
	}

	public static void fillPlayers(long batttingTeamId, Context context) {
		if (Team1 == null && Team2 == null)
			return;
		if (batttingTeamId == Team1.TeamId) {
			Team1.fillPlayersByBattingOrder(context);
			Team2.fillPlayersByBowlingOrder(context);
		} else {
			Team2.fillPlayersByBattingOrder(context);
			Team1.fillPlayersByBowlingOrder(context);
		}
	}

	public static void fillPlayerDictionary() {

		if (Team1 == null || Team2 == null)
			return;

		for (CricketPlayer player : Team1.Players) {
			if (!playerDictionary.containsKey(player.PlayerId)) {
				playerDictionary.put(String.valueOf(player.PlayerId),
						player.PlayerName);
			}
		}

		for (CricketPlayer player : Team2.Players) {
			if (!playerDictionary.containsKey(player.PlayerId)) {
				playerDictionary.put(String.valueOf(player.PlayerId),
						player.PlayerName);
			}
		}
	}

	public static Boolean hasStartedSecondInnings() {

		return Match.getCurrentInningsId() > 2;

	}

	public static void setBattingOrder(long batsmanId) {
		currentInnings.setBattingOrder(batsmanId);
	}

	public static void setCurrentInnings() {
		currentInnings = Match.getCurrentInnings();
	}

	public static String getFirstInningsScore(long teamId) {
		return Match.getFirstInningsScore(teamId);
	}

	public static String getSecondInningsScore(long teamId) {
		return Match.getSecondInningsScore(teamId);
	}

	public static int getCurrentInningsId() {
		return Match.getCurrentInningsId();
	}

	public static Boolean recordScore(int runsScored, Boolean isWide,
			Boolean isNoBall, Boolean isBye, Boolean isWicket,
			Boolean isLegBye, WicketFall wicketfall) {
		return currentInnings.recordScore(OnStrikeBatsmanId,
				getNonStikeBatsman().PlayerId, Bowler.PlayerId, runsScored,
				isWide, isNoBall, isBye, isWicket, isLegBye, wicketfall);
	}

	public static String getPlayingTeamScore() {
		return currentInnings.getScoreText();
	}

	public static List<BallByBall> getLastTenBallDetails() {
		return currentInnings.getLastTenBallDetails();
	}

	public static CricketPlayer Batsman1;
	public static CricketPlayer Batsman2;

	public static long OnStrikeBatsmanId;

	public static String getMatchStats() {

		return Match.getMatchSummary();

	}

	public static void removeLastRecordedAction() {
		currentInnings.revertLastRecordAction();
	}

	public static Boolean hasRecordedActions() {
		return currentInnings.hasRecordedAction();
	}

	public static void checkOnStikeBatsman(long newBatsmanId) {
		// Onstrike batsman is valid player
		if (OnStrikeBatsmanId == Batsman1.PlayerId
				|| OnStrikeBatsmanId == Batsman2.PlayerId)
			return;

		OnStrikeBatsmanId = newBatsmanId;

	}

	public static void setCurrentBatsmans(long onStrikeBatmanId,
			long nonStrikeBatsManId) {

		CricketTeam battingTeam = getBattingTeam();

		if (Batsman1.PlayerId != onStrikeBatmanId
				&& Batsman2.PlayerId != onStrikeBatmanId) {

			if (Batsman1.PlayerId == nonStrikeBatsManId) {
				Batsman2 = findPlayerFromTeam(battingTeam, onStrikeBatmanId);
			} else {
				Batsman1 = findPlayerFromTeam(battingTeam, onStrikeBatmanId);
			}

		}

		OnStrikeBatsmanId = onStrikeBatmanId;

		if (Batsman1.PlayerId != nonStrikeBatsManId
				&& Batsman2.PlayerId != nonStrikeBatsManId) {

			if (Batsman1.PlayerId == onStrikeBatmanId) {
				Batsman2 = findPlayerFromTeam(battingTeam, nonStrikeBatsManId);
			} else {
				Batsman1 = findPlayerFromTeam(battingTeam, nonStrikeBatsManId);
			}
		}

		Batsman1.setWicketFallDetails(null);
		Batsman2.setWicketFallDetails(null);
	}

	static CricketPlayer findPlayerFromTeam(CricketTeam team, long playerId) {
		for (CricketPlayer player : team.Players) {
			if (player.PlayerId == playerId) {
				return player;
			}
		}

		return null;
	}

	public static void setCurrentBowler(long bowlerId) {
		if (Bowler.PlayerId != bowlerId) {

			Bowler = findPlayerFromTeam(getBowlingTeam(), bowlerId);

		}
	}

	public static CricketPlayer getOnStikeBatsman() {

		if (Batsman1.PlayerId == OnStrikeBatsmanId) {
			return Batsman1;
		} else {
			return Batsman2;
		}

	}

	public static CricketPlayer getNonStikeBatsman() {

		if (Batsman1.PlayerId == OnStrikeBatsmanId) {
			return Batsman2;
		} else {
			return Batsman1;
		}

	}

	public static CricketPlayer Bowler;

	public static CricketPlayer getBowler() {
		return Bowler;
	}

	public static void switchBatsman() {		
		if (Batsman1.PlayerId == OnStrikeBatsmanId) {
			OnStrikeBatsmanId = Batsman2.PlayerId;
		} else {
			OnStrikeBatsmanId = Batsman1.PlayerId;
		}
		
		Batsman1.initializeBattingScore();
		Batsman2.initializeBattingScore();
	}

	public static CricketPlayer[] getBatsmans() {

		List<CricketPlayer> players = new ArrayList<CricketPlayer>();

		CricketTeam battingTeam = getBattingTeam();

		for (CricketPlayer player : battingTeam.Players) {
			if (player.getWicketFallDetails() != null)
				continue;

			if (Batsman1 == player)
				continue;

			if (Batsman2 == player)
				continue;

			players.add(player);
		}

		if (Batsman1 != null && Batsman1.isPlaying()) {

			players.add(Batsman1);
		}

		if (Batsman2 != null && Batsman2.isPlaying()) {

			players.add(Batsman2);
		}

		return players.toArray(new CricketPlayer[players.size()]);

	}

	public static CricketPlayer[] getBowlers(Boolean includeEmpty) {

		List<CricketPlayer> players = new ArrayList<CricketPlayer>();

		CricketTeam bowlingTeam = getBowlingTeam();

		if (includeEmpty) {
			CricketPlayer dummyPlayer = new CricketPlayer();
			dummyPlayer.PlayerId = 0;
			dummyPlayer.PlayerName = "";
			players.add(dummyPlayer);
		}

		for (CricketPlayer player : bowlingTeam.Players) {
			if (player.getWicketFallDetails() != null)
				continue;

			if (!includeEmpty && Bowler == player)
				continue;

			players.add(player);
		}

		return players.toArray(new CricketPlayer[players.size()]);

	}
}
