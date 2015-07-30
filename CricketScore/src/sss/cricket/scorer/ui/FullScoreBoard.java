package sss.cricket.scorer.ui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import sss.cricket.scorer.database.BatsmanScore;
import sss.cricket.scorer.database.BowlerScore;
import sss.cricket.scorer.database.CricketInnings;
import sss.cricket.scorer.database.CricketInningsDataSource;
import sss.cricket.scorer.database.CricketMatch;
import sss.cricket.scorer.database.CricketTeam;
import sss.cricket.scorer.database.StatsDataSource;
import sss.cricket.scorer.ui.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class FullScoreBoard extends Activity {

	CricketMatch match;
	CricketTeam team1;
	CricketTeam team2;

	List<BowlerScore> bowlerScores;
	List<BatsmanScore> batsmanScores;
	float textSize = 18f;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.full_score_board);
		match = ScoreSheetDataStore.Match;
		team1 = ScoreSheetDataStore.Team1;
		team2 = ScoreSheetDataStore.Team2;

		if (match != null && team1 != null && team2 != null) {

			populateTeamDetails();
			populateInningsDetails();

			if (ScoreSheetDataStore.scoreSheetLoaded == false) {
				ScoreSheetDataStore.scoreSheetLoaded = true;

				ScoreSheetDataStore.getBattingTeam().fillBattingScores(
						batsmanScores);
				ScoreSheetDataStore.getBowlingTeam().fillBowlerScores(
						bowlerScores);
			}
		}
	}

	String htmlMessage;
	String dialogTitle;

	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {

		case DialogWindowType.MessageDialog: // Populate Bats man
		default:
			LayoutInflater factory = LayoutInflater.from(this);
			final View messageWindow = factory.inflate(R.layout.dialog_message,
					null);

			final TextView tv_messageToDiplay = (TextView) messageWindow
					.findViewById(R.id.tv_messageToDisplay);

			tv_messageToDiplay.setText(Html.fromHtml(htmlMessage));

			return new AlertDialog.Builder(this)
					.setIcon(1)
					.setTitle(dialogTitle)
					.setView(messageWindow)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

								}

							}).create();
		}
	}

	@SuppressWarnings({ "deprecation" })
	private void showCustomDialog(int id, String message, String title) {
		htmlMessage = message;
		dialogTitle = title;
		removeDialog(id);
		showDialog(id);
	}

	private void saveAsHtml(String content) {
		try {
			String cricketScrorerPath = Environment
					.getExternalStorageDirectory() + "/CricketScorer";

			cricketScrorerPath = cricketScrorerPath + "/"
					+ match.getMatchName().replace(" ", "-") + ".html";

			File resolveMeSDCard = new File(cricketScrorerPath);

			resolveMeSDCard.createNewFile();

			FileOutputStream fos = new FileOutputStream(resolveMeSDCard);

			fos.write(content.getBytes());
			fos.close();

			showCustomDialog(DialogWindowType.MessageDialog,
					"Report Successfully saved at " + cricketScrorerPath,
					"Success");
		} catch (Exception ee) {

		}
	}

	String htmlScoreCard = "";

	private void appendHtml(String html) {
		htmlScoreCard += html;
	}

	private void populateTeamDetails() {

		final TextView teams = (TextView) findViewById(R.id.team_detail);

		String teamNames = team1.TeamName + " Vs " + team2.TeamName;

		teams.setText(teamNames);
		appendHtml("<div class='alignCenter'>");

		appendHtml("<h1>" + teamNames + "</h1>");

		String matchDesciption = "Match - " + match.getMatchName()
				+ ", Played on " + match.getPlayedOn();

		final TextView matchDesc = (TextView) findViewById(R.id.match_detail);
		matchDesc.setText(matchDesciption);

		appendHtml("<h4>" + matchDesciption + "</h4>");

		final TextView toss = (TextView) findViewById(R.id.toss_detail);

		String tossWonBy = "";
		String electedTo = "";

		if (match.getTossWonBy() == team1.TeamId) {
			tossWonBy = team1.TeamName;
			if (match.getFirstInnningsBattingTeamId() == team1.TeamId) {
				electedTo = "Bat";
			} else {
				electedTo = "Bowl";
			}
		} else {
			tossWonBy = team2.TeamName;
			if (match.getFirstInnningsBattingTeamId() == team2.TeamId) {
				electedTo = "Bat";
			} else {
				electedTo = "Bowl";
			}
		}

		String tossDetail = tossWonBy + " won the toss & elected to "
				+ electedTo + ".";
		toss.setText(tossDetail);
		appendHtml("<h4>" + tossDetail + "</h4>");

		String strMatchSummary = "";
		final TextView matchsummary = (TextView) findViewById(R.id.match_summary);
		strMatchSummary = match.getMatchDescription();
		matchsummary.setText(strMatchSummary);
		appendHtml("<h1 class='greenColor'>" + strMatchSummary + "</h1>");
		appendHtml("</div>");
	}

	private void populateInningsDetails() {

		final LinearLayout scoreBoard = (LinearLayout) findViewById(R.id.ll_score_board);

		scoreBoard.removeAllViews();

		for (int innings = 0; innings < 4; innings++) {

			CricketInnings currentInnings = match.getInnings(innings);
			if (currentInnings != null) {

				LinearLayout layout = new LinearLayout(this);
				layout.setBackgroundColor(Color.RED);
				layout.setLayoutParams(getParams(false));
				layout.setOrientation(LinearLayout.VERTICAL);
				

				TextView inningsHeader = new TextView(this);
				inningsHeader.setLayoutParams(getParams(false));

				String headerText = "";
				if (currentInnings.getBattingTeam() == team1.TeamId) {
					headerText = team1.TeamName;
				} else {
					headerText = team2.TeamName;
				}

				headerText += ": " + currentInnings.getTotalRuns() + "/"
						+ currentInnings.getWickets();

				if (currentInnings.getBalls() == 6) {
					headerText += " (" + (currentInnings.getBowledOvers() + 1)
							+ ")";
				} else {
					headerText += " (" + currentInnings.getBowledOvers() + "."
							+ currentInnings.getBalls() + ")";
				}

				headerText += ", " + currentInnings.getRunRate(false);

				headerText += ", Extras: " + currentInnings.getExtras();

				long duration = 0;

				try {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
					Date started = df.parse(currentInnings.getStartedOn());
					Date completed = df.parse(currentInnings.getCompletedOn());

					duration = ((completed.getTime() / 60000) - (started
							.getTime() / 60000));

				} catch (Exception ee) {

				}

				if (duration > 0 && duration < 500) {
					headerText += ", Duration: " + duration + " Min";
				}

				inningsHeader.setText(headerText);
				inningsHeader.setTextSize(textSize);

				layout.addView(inningsHeader);

				appendHtml("<div class='inningsHeader'>" + headerText
						+ "</div>");

				scoreBoard.addView(layout);

				long inningsId = currentInnings.getInningsId();
				appendHtml("<Table class='tblScoreCard'>");
				getBatsmanHeader(scoreBoard);
				getBatsmanScores(scoreBoard, inningsId);
				appendHtml("</Table>");

				appendHtml("<Table class='tblScoreCard'>");
				getBowlerHeader(scoreBoard);
				getBowlerScores(scoreBoard, inningsId);
				appendHtml("</Table>");

				StatsDataSource statsDb = new StatsDataSource(
						ScoreSheetDataStore.context);

				statsDb.SaveScoreToStatsDB(inningsId, batsmanScores,
						bowlerScores);

			}

		}

	}

	private void getBatsmanHeader(LinearLayout layoutToadd) {

		LinearLayout playerRow = new LinearLayout(this);
		playerRow.setLayoutParams(getParams(true));
		playerRow.setWeightSum(1.0f);
		playerRow.setBackgroundColor(Color.LTGRAY);
		playerRow.setPadding(3, 3, 3, 3);

		appendHtml("<tr class='tblHeader'>");
		TextView playerName = new TextView(this);
		playerName.setLayoutParams(getParams(0.6f));
		playerName.setText("Batting");
		playerName.setTextColor(Color.BLACK);
		playerName.setTextSize(textSize);
		playerRow.addView(playerName);

		appendHtml("<th colspan='2'>Batting</th>");

		TextView runs = new TextView(this);
		runs.setLayoutParams(getParams(0.08f));
		runs.setText("R");
		runs.setTextColor(Color.BLACK);
		runs.setTextSize(textSize);
		playerRow.addView(runs);
		appendHtml("<th  class='statsField'>R</th>");

		TextView balls = new TextView(this);
		balls.setLayoutParams(getParams(0.08f));
		balls.setText("B");
		balls.setTextColor(Color.BLACK);
		balls.setTextSize(textSize);
		playerRow.addView(balls);
		appendHtml("<th  class='statsField'>B</th>");

		TextView fours = new TextView(this);
		fours.setLayoutParams(getParams(0.08f));
		fours.setText("4's");
		fours.setTextColor(Color.BLACK);
		fours.setTextSize(textSize);
		playerRow.addView(fours);
		appendHtml("<th class='statsField'>4's</th>");

		TextView sixes = new TextView(this);
		sixes.setLayoutParams(getParams(0.08f));
		sixes.setText("6's");
		sixes.setTextColor(Color.BLACK);
		sixes.setTextSize(textSize);
		playerRow.addView(sixes);
		appendHtml("<th class='statsField'>6's</th>");

		TextView sr = new TextView(this);
		sr.setLayoutParams(getParams(0.08f));
		sr.setText("S/R");
		sr.setTextColor(Color.BLACK);
		sr.setTextSize(textSize);
		playerRow.addView(sr);
		appendHtml("<th class='statsField'>S/R</th>");

		appendHtml("</tr>");
		layoutToadd.addView(playerRow);
	}

	private void getBatsmanScores(LinearLayout layoutToadd, long inningsId) {

		CricketInningsDataSource database = new CricketInningsDataSource(this);

		List<BatsmanScore> allScores = database.getBatsmanruns(inningsId);

		if (ScoreSheetDataStore.scoreSheetLoaded == false) {
			batsmanScores = allScores;
		}

		for (BatsmanScore score : allScores) {

			if (score.getBallsPlayed() == 0
					&& score.getWicketFallDetails() == null)
				continue;

			appendHtml("<tr class='statsRow'>");

			LinearLayout playerRow = new LinearLayout(this);
			playerRow.setLayoutParams(getParams(true));
			playerRow.setWeightSum(1.0f);
			playerRow.setPadding(3, 3, 3, 3);

			TextView playerName = new TextView(this);
			playerName.setLayoutParams(getParams(0.22f));
			playerName.setText(score.getPlayerName());
			playerName.setTextSize(textSize);
			playerRow.addView(playerName);
			
			appendHtml("<td> " + score.getPlayerName() + "</td>");

			String strWicketDetail = score.getWicketDetail();
			TextView wicket = new TextView(this);
			wicket.setLayoutParams(getParams(0.38f));
			wicket.setText(strWicketDetail);
			wicket.setTextSize(textSize);
			playerRow.addView(wicket);
			appendHtml("<td> " + strWicketDetail + "</td>");

			String strRuns = "" + score.getScoredRuns();
			TextView runs = new TextView(this);
			runs.setLayoutParams(getParams(0.08f));
			runs.setText(strRuns);
			runs.setTextSize(textSize);
			playerRow.addView(runs);
			appendHtml("<td> " + strRuns + "</td>");

			String strBalls = "" + score.getBallsPlayed();
			TextView balls = new TextView(this);
			balls.setLayoutParams(getParams(0.08f));
			balls.setText(strBalls);
			balls.setTextSize(textSize);
			playerRow.addView(balls);
			appendHtml("<td> " + strBalls + "</td>");

			String strFours = "" + score.getFours();
			TextView fours = new TextView(this);
			fours.setLayoutParams(getParams(0.08f));
			fours.setText(strFours);
			fours.setTextSize(textSize);
			playerRow.addView(fours);
			appendHtml("<td> " + strFours + "</td>");

			String strSixes = "" + score.getSixes();
			TextView sixes = new TextView(this);
			sixes.setLayoutParams(getParams(0.08f));
			sixes.setText(strSixes);
			sixes.setTextSize(textSize);
			playerRow.addView(sixes);
			appendHtml("<td> " + strSixes + "</td>");

			String strSr = score.getStrikeRate();
			TextView sr = new TextView(this);
			sr.setLayoutParams(getParams(0.8f));
			sr.setText(strSr);
			sr.setTextSize(textSize);
			playerRow.addView(sr);
			appendHtml("<td> " + strSr + "</td>");

			layoutToadd.addView(playerRow);
			appendHtml("</tr>");

		}
	}

	private void getBowlerHeader(LinearLayout layoutToadd) {

		LinearLayout playerRow = new LinearLayout(this);
		playerRow.setLayoutParams(getParams(true));
		playerRow.setWeightSum(1.0f);
		playerRow.setBackgroundColor(Color.LTGRAY);
		playerRow.setPadding(3, 3, 3, 3);

		appendHtml("<tr class='tblHeader'>");

		TextView playerName = new TextView(this);
		playerName.setLayoutParams(getParams(0.52f));
		playerName.setText("Bowling");
		playerName.setTextColor(Color.BLACK);
		playerName.setTextSize(textSize);
		playerRow.addView(playerName);
		appendHtml("<th>Bowling</th>");

		TextView runs = new TextView(this);
		runs.setLayoutParams(getParams(0.08f));
		runs.setText("O");
		runs.setTextColor(Color.BLACK);
		runs.setTextSize(textSize);
		playerRow.addView(runs);
		appendHtml("<th class='statsField'>O</th>");

		TextView balls = new TextView(this);
		balls.setLayoutParams(getParams(0.08f));
		balls.setText("R");
		balls.setTextColor(Color.BLACK);
		balls.setTextSize(textSize);
		playerRow.addView(balls);
		appendHtml("<th class='statsField'>R</th>");

		TextView wk = new TextView(this);
		wk.setLayoutParams(getParams(0.08f));
		wk.setText("W");
		wk.setTextColor(Color.BLACK);
		wk.setTextSize(textSize);
		playerRow.addView(wk);
		appendHtml("<th class='statsField'>W</th>");

		TextView nb = new TextView(this);
		nb.setLayoutParams(getParams(0.08f));
		nb.setText("Nb");
		nb.setTextColor(Color.BLACK);
		nb.setTextSize(textSize);
		playerRow.addView(nb);
		appendHtml("<th class='statsField'>Nb</th>");

		TextView wd = new TextView(this);
		wd.setLayoutParams(getParams(0.08f));
		wd.setText("Wd");
		wd.setTextColor(Color.BLACK);
		wd.setTextSize(textSize);
		playerRow.addView(wd);
		appendHtml("<th class='statsField'>Wd</th>");

		TextView er = new TextView(this);
		er.setLayoutParams(getParams(0.08f));
		er.setText("E/R");
		er.setTextColor(Color.BLACK);
		er.setTextSize(textSize);
		playerRow.addView(er);
		appendHtml("<th class='statsField'>E/R</th>");

		appendHtml("</tr>");

		layoutToadd.addView(playerRow);
	}

	private void getBowlerScores(LinearLayout layoutToadd, long inningsId) {

		CricketInningsDataSource database = new CricketInningsDataSource(this);

		List<BowlerScore> allScores = database.getBowlerRuns(inningsId);

		if (ScoreSheetDataStore.scoreSheetLoaded == false) {
			bowlerScores = allScores;
		}

		for (BowlerScore score : allScores) {

			appendHtml("<tr class='statsRow'>");
			LinearLayout playerRow = new LinearLayout(this);
			playerRow.setLayoutParams(getParams(true));
			playerRow.setWeightSum(1.0f);
			playerRow.setPadding(3, 3, 3, 3);

			TextView playerName = new TextView(this);
			playerName.setLayoutParams(getParams(0.52f));
			playerName.setText(score.getPlayerName());
			playerName.setTextSize(textSize);
			playerRow.addView(playerName);

			appendHtml("<td> " + score.getPlayerName() + "</td>");

			String strOver = "" + score.getOvers();
			TextView balls = new TextView(this);
			balls.setLayoutParams(getParams(0.08f));
			balls.setText(strOver);
			balls.setTextSize(textSize);
			playerRow.addView(balls);
			appendHtml("<td> " + strOver + "</td>");

			String strRuns = "" + score.getRunsGiven();
			TextView runs = new TextView(this);
			runs.setLayoutParams(getParams(0.08f));
			runs.setText(strRuns);
			runs.setTextSize(textSize);
			playerRow.addView(runs);
			appendHtml("<td> " + strRuns + "</td>");

			String strWickets = "" + score.getWickets();
			TextView wicket = new TextView(this);
			wicket.setLayoutParams(getParams(0.08f));
			wicket.setText(strWickets);
			wicket.setTextSize(textSize);
			playerRow.addView(wicket);
			appendHtml("<td> " + strWickets + "</td>");

			String strnb = "" + score.getNoBalls();
			TextView nb = new TextView(this);
			nb.setLayoutParams(getParams(0.08f));
			nb.setText(strnb);
			nb.setTextSize(textSize);
			playerRow.addView(nb);
			appendHtml("<td> " + strnb + "</td>");

			String strwd = "" + score.getWides();
			TextView wd = new TextView(this);
			wd.setLayoutParams(getParams(0.08f));
			wd.setText(strwd);
			wd.setTextSize(textSize);
			playerRow.addView(wd);
			appendHtml("<td> " + strwd + "</td>");

			String strEr = score.getEconomyRate();
			TextView er = new TextView(this);
			er.setLayoutParams(getParams(0.08f));
			er.setText(strEr);
			er.setTextSize(textSize);
			playerRow.addView(er);
			appendHtml("<td> " + strEr + "</td>");
			layoutToadd.addView(playerRow);

			appendHtml("</tr>");
		}
	}

	private LayoutParams getParams(Boolean includeMargins) {
		LayoutParams lparams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		return lparams;

	}

	private LayoutParams getParams(float weight) {
		LayoutParams lparams = new LayoutParams(0, LayoutParams.WRAP_CONTENT,
				weight);

		return lparams;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.full_score_board, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.viewscorer:
			Intent scoreSheet = new Intent(this, MatchScoreSheet.class);
			scoreSheet.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(scoreSheet);
			this.finish();
			return true;
		case R.id.saveAsHtml:
			String template = readNewTxt();
			template = template.replace("++MatchContent++", htmlScoreCard);
			saveAsHtml(template);
			return true;
		case R.id.exit:
			this.finish();
			return true;
		default:
			return false;
		}
	}

	private String readNewTxt() {
		InputStream inputStream = getResources()
				.openRawResource(R.raw.template);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		int i;
		try {
			i = inputStream.read();
			while (i != -1) {
				byteArrayOutputStream.write(i);
				i = inputStream.read();
			}
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return byteArrayOutputStream.toString();
	}
}
