package sss.cricket.scorer.ui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import sss.cricket.scorer.database.StatsBatsman;
import sss.cricket.scorer.database.StatsBowler;
import sss.cricket.scorer.database.StatsDataSource;
import sss.cricket.scorer.ui.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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

public class StatsHome extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stats_home);
		fillBatsmanStats(4);
	}

	float textSize = 18f;

	private Boolean isBatsmanStats = true;
	String htmlMessage;
	String dialogTitle;
	String saveAsHtml;
	String sortDirection;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.stats_home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.saveAsHtml:
			saveAsHtml();
			String cricketScrorerPath = Environment
					.getExternalStorageDirectory() + "/CricketScorer";
			cricketScrorerPath = cricketScrorerPath + "/" + "Stats.html";
			showCustomDialog(DialogWindowType.MessageDialog,
					"Report Successfully saved at " + cricketScrorerPath,
					"Success");
			return true;
		case R.id.exit:
			this.finish();
			return true;
		default:
			return false;
		}
	}

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

	public void showStats(View v) {

		if (v.getId() == R.id.btn_batsmanStats) {
			isBatsmanStats = true;
			fillBatsmanStats(4);
		} else {
			isBatsmanStats = false;
			sortDirection = "Desc";
			fillBowlerStats(4);
		}

	}

	public void saveAsHtml() {

		saveAsHtml = "<div class='inningsHeader'>Batsman Statistics</div>";
		StatsDataSource database = new StatsDataSource(this);

		saveAsHtml += "<Table id='batsmanTable' class='tablesorter'>";
		populateHTMLLabels("Runs", "Balls", "Avg", "25+", "50+", "Highest",
				"4's", "6's", "S/R");

		List<StatsBatsman> batsmanStats = database.getBatsmanStats(30, 4);

		generateHtmlBatsmanReport(batsmanStats);

		saveAsHtml += "</Table>";

		saveAsHtml += "<div class='inningsHeader'>Bowler Statistics</div>";
		saveAsHtml += "<Table id='bowlerTable' class='tablesorter'>";
		populateHTMLLabels("Wickets", "3+", "5+", "Runs", "Overs", "Wides",
				"Nb", "E/R", "S/R");
		sortDirection = "Desc";
		List<StatsBowler> bowlerStats = database.getBowlerStats(20, 4,
				sortDirection);
		generateHtmlBowlerReport(bowlerStats);
		saveAsHtml += "</Table>";

		String template = readNewTxt();
		template = template.replace("++MatchContent++", saveAsHtml);
		writeHtmlToSDCard(template);
	}

	private String readNewTxt() {
		InputStream inputStream = getResources().openRawResource(
				R.raw.template_stats);
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

	private void writeHtmlToSDCard(String content) {
		try {
			String cricketScrorerPath = Environment
					.getExternalStorageDirectory() + "/CricketScorer";

			File resolveMeSDCard = new File(cricketScrorerPath + "/"
					+ "Stats.html");

			resolveMeSDCard.createNewFile();

			FileOutputStream fos = new FileOutputStream(resolveMeSDCard);

			fos.write(content.getBytes());
			fos.close();
		} catch (Exception ee) {

		}
	}

	private void populateHTMLLabels(String col1, String col2, String col3,
			String col4, String col5, String col6, String col7, String col8,
			String col9) {

		saveAsHtml += " <thead><tr class='tblHeader'>";
		saveAsHtml += "<th class='statsField name'>Name</th>";
		saveAsHtml += "<th class='statsField'>Innings</th>";
		saveAsHtml += "<th class='statsField'>" + col1 + "</th>";
		saveAsHtml += "<th class='statsField'>" + col2 + "</th>";
		saveAsHtml += "<th class='statsField'>" + col3 + "</th>";
		saveAsHtml += "<th class='statsField'>" + col4 + "</th>";
		saveAsHtml += "<th class='statsField'>" + col5 + "</th>";
		saveAsHtml += "<th class='statsField'>" + col6 + "</th>";
		saveAsHtml += "<th class='statsField'>" + col7 + "</th>";
		saveAsHtml += "<th class='statsField'>" + col8 + "</th>";
		if (col9 != "")
			saveAsHtml += "<th>" + col9 + "</th>";
		saveAsHtml += "</tr> </thead>";
	}

	private void generateHtmlBatsmanReport(List<StatsBatsman> batsmanStats) {
		Boolean isAlternavtiveRow = false;
		saveAsHtml += "<tbody>";
		for (StatsBatsman score : batsmanStats) {

			saveAsHtml += "<tr class='statsrow'>";
			saveAsHtml += "<td>" + score.getPlayerName() + "</td>";

			saveAsHtml += "<td>" + score.getInnings() + "</td>";

			saveAsHtml += "<td>" + score.getRuns() + "</td>";

			saveAsHtml += "<td>" + score.getBalls() + "</td>";

			saveAsHtml += "<td>" + String.format("%.1f", score.getAverage())
					+ "</td>";

			saveAsHtml += "<td>" + score.getTwentyFivePlus() + "</td>";

			saveAsHtml += "<td>" + score.getFiftyPlus() + "</td>";

			saveAsHtml += "<td>" + score.getHighest() + "</td>";

			saveAsHtml += "<td>" + score.getFours() + "</td>";

			saveAsHtml += "<td>" + score.getSixes() + "</td>";

			saveAsHtml += "<td>" + String.format("%.1f", score.getStrikeRate())
					+ "</td>";

			saveAsHtml += "</tr>";
			isAlternavtiveRow = !isAlternavtiveRow;

		}
		saveAsHtml += "</tbody>";
	}

	private void generateHtmlBowlerReport(List<StatsBowler> bowlerStats) {
		Boolean isAlternavtiveRow = false;
		saveAsHtml += "<tbody>";
		for (StatsBowler score : bowlerStats) {

			saveAsHtml += "<tr class='statsrow'>";
			saveAsHtml += "<td>" + score.getPlayerName() + "</td>";

			saveAsHtml += "<td>" + score.getInnings() + "</td>";

			saveAsHtml += "<td>" + score.getWickets() + "</td>";

			saveAsHtml += "<td>" + score.getThreePlusWickets() + "</td>";

			saveAsHtml += "<td>" + score.getFivePlusWickets() + "</td>";

			saveAsHtml += "<td>" + score.getRuns() + "</td>";

			saveAsHtml += "<td>" + score.getOvers() + "</td>";

			saveAsHtml += "<td>" + score.getWides() + "</td>";

			saveAsHtml += "<td>" + score.getNoBalls() + "</td>";

			saveAsHtml += "<td>"
					+ String.format("%.1f", score.getEconomyRate()) + "</td>";

			saveAsHtml += "<td>" + String.format("%.1f", score.getStrikeRate())
					+ "</td>";

			saveAsHtml += "</tr>";
			isAlternavtiveRow = !isAlternavtiveRow;

		}
		saveAsHtml += "</tbody>";
	}

	private void fillBatsmanStats(int sortColumn) {
		StatsDataSource database = new StatsDataSource(this);

		populateLabels("Runs", "Balls", "Avg", "25+", "50+", "Highest", "4's",
				"6's", "S/R");

		List<StatsBatsman> stats = database.getBatsmanStats(30, sortColumn);
		generateBatsmanReport(stats);
	}

	private void populateLabels(String col1, String col2, String col3,
			String col4, String col5, String col6, String col7, String col8,
			String col9) {

		final TextView tv_Runs = (TextView) findViewById(R.id.tv_Runs);
		tv_Runs.setText(col1);

		final TextView tv_Balls = (TextView) findViewById(R.id.tv_Balls);
		tv_Balls.setText(col2);

		final TextView tv_Avg = (TextView) findViewById(R.id.tv_Avg);
		tv_Avg.setText(col3);

		final TextView tv_twentyFivePlus = (TextView) findViewById(R.id.tv_twentyFivePlus);
		tv_twentyFivePlus.setText(col4);

		final TextView tv_fiftyPlus = (TextView) findViewById(R.id.tv_fiftyPlus);
		tv_fiftyPlus.setText(col5);

		final TextView tv_Higesht = (TextView) findViewById(R.id.tv_Higesht);
		tv_Higesht.setText(col6);

		final TextView tv_Fours = (TextView) findViewById(R.id.tv_Fours);
		tv_Fours.setText(col7);

		final TextView tv_Sixes = (TextView) findViewById(R.id.tv_Sixes);
		tv_Sixes.setText(col8);

		final TextView tv_SR = (TextView) findViewById(R.id.tv_SR);
		tv_SR.setText(col9);
	}

	private void fillBowlerStats(int sortColumn) {
		StatsDataSource database = new StatsDataSource(this);

		populateLabels("Wickets", "3+", "5+", "Runs", "Overs", "Wides", "Nb",
				"E/R", "S/R");

		List<StatsBowler> stats = database.getBowlerStats(20, sortColumn,
				sortDirection);
		generateBowlerReport(stats);
	}

	private void generateBatsmanReport(List<StatsBatsman> stats) {

		final LinearLayout layoutToadd = (LinearLayout) findViewById(R.id.ll_BatsmanStats);
		layoutToadd.removeAllViews();
		Boolean isAlternavtiveRow = false;

		for (StatsBatsman score : stats) {

			String playerName = score.getPlayerName();

			String strInnings = "" + score.getInnings();

			String strRuns = "" + score.getRuns();

			String strBalls = "" + score.getBalls();

			String strAvg = String.format("%.1f", score.getAverage());

			String str25plus = "" + score.getTwentyFivePlus();

			String str50Plus = "" + score.getFiftyPlus();

			String strHighest = "" + score.getHighest();

			String strFours = "" + score.getFours();

			String strSixes = "" + score.getSixes();

			String strSr = String.format("%.1f", score.getStrikeRate());

			addStatsRow(layoutToadd, playerName, strInnings, strRuns, strBalls,
					strAvg, str25plus, str50Plus, strHighest, strFours,
					strSixes, strSr, isAlternavtiveRow);

			isAlternavtiveRow = !isAlternavtiveRow;

		}

	}

	private void generateBowlerReport(List<StatsBowler> stats) {

		final LinearLayout layoutToadd = (LinearLayout) findViewById(R.id.ll_BatsmanStats);
		layoutToadd.removeAllViews();
		Boolean isAlternavtiveRow = false;
		for (StatsBowler score : stats) {

			String playerName = score.getPlayerName();

			String strInnings = "" + score.getInnings();

			String strWickets = "" + score.getWickets();

			String str3Plus = "" + score.getThreePlusWickets();

			String str5Plus = "" + score.getFivePlusWickets();

			String runs = "" + score.getRuns();

			String strOver = "" + score.getOvers();

			String strWides = "" + score.getWides();

			String strNoBall = "" + score.getNoBalls();

			String strEc = "" + String.format("%.1f", score.getEconomyRate());

			String strSr = String.format("%.1f", score.getStrikeRate());

			addStatsRow(layoutToadd, playerName, strInnings, strWickets,
					str3Plus, str5Plus, runs, strOver, strWides, strNoBall,
					strEc, strSr, isAlternavtiveRow);
			isAlternavtiveRow = !isAlternavtiveRow;

		}

	}

	public void addStatsRow(LinearLayout layoutToadd, String column1,
			String column2, String column3, String column4, String column5,
			String column6, String column7, String column8, String column9,
			String column10, String column11, Boolean isAlternavtiveRow) {

		LinearLayout playerRow = new LinearLayout(this);
		if (isAlternavtiveRow)
			playerRow.setBackgroundColor(Color.RED);

		playerRow.setLayoutParams(getParams(true));
		playerRow.setWeightSum(1.0f);
		playerRow.setPadding(3, 3, 3, 3);

		TextView playerName = new TextView(this);
		playerName.setLayoutParams(getParams(0.15f));
		playerName.setText(column1);
		playerName.setTextSize(textSize);
		playerRow.addView(playerName);

		TextView innings = new TextView(this);
		innings.setLayoutParams(getParams(0.085f));
		innings.setText(column2);
		innings.setTextSize(textSize);
		playerRow.addView(innings);

		TextView runs = new TextView(this);
		runs.setLayoutParams(getParams(0.085f));
		runs.setText(column3);
		runs.setTextSize(textSize);
		playerRow.addView(runs);

		TextView balls = new TextView(this);
		balls.setLayoutParams(getParams(0.085f));
		balls.setText(column4);
		balls.setTextSize(textSize);
		playerRow.addView(balls);

		TextView avg = new TextView(this);
		avg.setLayoutParams(getParams(0.085f));
		avg.setText(column5);
		avg.setTextSize(textSize);
		playerRow.addView(avg);

		TextView twentyFivePlus = new TextView(this);
		twentyFivePlus.setLayoutParams(getParams(0.085f));
		twentyFivePlus.setText(column6);
		twentyFivePlus.setTextSize(textSize);
		playerRow.addView(twentyFivePlus);

		TextView fiftyPlus = new TextView(this);
		fiftyPlus.setLayoutParams(getParams(0.085f));
		fiftyPlus.setText(column7);
		fiftyPlus.setTextSize(textSize);
		playerRow.addView(fiftyPlus);

		TextView higest = new TextView(this);
		higest.setLayoutParams(getParams(0.085f));
		higest.setText(column8);
		higest.setTextSize(textSize);
		playerRow.addView(higest);

		TextView fours = new TextView(this);
		fours.setLayoutParams(getParams(0.085f));
		fours.setText(column9);
		fours.setTextSize(textSize);
		playerRow.addView(fours);

		TextView sixes = new TextView(this);
		sixes.setLayoutParams(getParams(0.085f));
		sixes.setText(column10);
		sixes.setTextSize(textSize);
		playerRow.addView(sixes);

		TextView sr = new TextView(this);
		sr.setLayoutParams(getParams(0.085f));
		sr.setText(column11);
		sr.setTextSize(textSize);
		playerRow.addView(sr);

		layoutToadd.addView(playerRow);

	}

	public void sort_stats(View v) {

		int colNo = 4;

		switch (v.getId()) {
		case R.id.tv_Innings:
			colNo = 3;
			break;
		case R.id.tv_Runs:
			colNo = 4;
			break;
		case R.id.tv_Balls:
			colNo = 5;
			break;
		case R.id.tv_Avg:
			colNo = 6;
			break;
		case R.id.tv_twentyFivePlus:
			colNo = 7;
			break;
		case R.id.tv_fiftyPlus:
			colNo = 8;
			break;
		case R.id.tv_Higesht:
			colNo = 9;
			break;
		case R.id.tv_Fours:
			colNo = 10;
			break;
		case R.id.tv_Sixes:
			colNo = 11;
			break;
		case R.id.tv_SR:
			colNo = 12;
			break;
		}

		if (isBatsmanStats) {
			fillBatsmanStats(colNo);
		} else {

			if (colNo > 10) {
				sortDirection = "ASC";
			} else {
				sortDirection = "DESC";
			}

			fillBowlerStats(colNo);
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

}
