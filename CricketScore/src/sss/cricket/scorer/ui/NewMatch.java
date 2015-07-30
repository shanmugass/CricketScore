package sss.cricket.scorer.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import sss.cricket.scorer.database.CommonDataSource;
import sss.cricket.scorer.database.CricketMatch;
import sss.cricket.scorer.database.CricketMatchDataSource;
import sss.cricket.scorer.database.CricketTeam;
import sss.cricket.scorer.database.CricketTeamDataSource;
import sss.cricket.scorer.database.MatchType;

import sss.cricket.scorer.ui.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

public class NewMatch extends Activity implements OnSeekBarChangeListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_match);

		FillTeams();

		FillMatchType();

		SeekBar seekbar = (SeekBar) findViewById(R.id.seekBar1);
		seekbar.setOnSeekBarChangeListener(this);

		setOvers(seekbar.getProgress());

		final EditText et_matchName = (EditText) findViewById(R.id.matchName);

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
		Calendar cal = Calendar.getInstance();
		et_matchName.setText(dateFormat.format(cal.getTime()));
	}

	MatchType selectedMatchType;

	private void FillMatchType() {
		Spinner team1 = (Spinner) findViewById(R.id.spinner_match_type);

		CommonDataSource database = new CommonDataSource(this);

		final MatchType[] teams = database.getAllMatchTypesInArray();

		ArrayAdapter<MatchType> adapter = new ArrayAdapter<MatchType>(this,
				android.R.layout.simple_spinner_item, teams);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		team1.setAdapter(adapter);

		team1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				MatchType d = teams[position];

				selectedMatchType = d;
			}

			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}

	CricketTeam selectedTeam1 = new CricketTeam();
	CricketTeam selectedTeam2 = new CricketTeam();

	private void FillTeams() {
		Spinner team1 = (Spinner) findViewById(R.id.spinner_team1);
		Spinner team2 = (Spinner) findViewById(R.id.spinner_team2);

		CricketTeamDataSource database = new CricketTeamDataSource(this);

		final CricketTeam[] teams = database.GetAllTeamsInArray(false);

		ArrayAdapter<CricketTeam> adapter = new ArrayAdapter<CricketTeam>(this,
				android.R.layout.simple_spinner_item, teams);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		team1.setAdapter(adapter);
		team2.setAdapter(adapter);

		team1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				CricketTeam d = teams[position];
				selectedTeam1 = d;
				RadioButton rb_team1 = (RadioButton) findViewById(R.id.rb_toss_won_1);
				rb_team1.setText(d.TeamName);
			}

			public void onNothingSelected(AdapterView<?> parent) {
				selectedTeam1 = new CricketTeam();
				RadioButton rb_team1 = (RadioButton) findViewById(R.id.rb_toss_won_1);
				rb_team1.setText("");
			}
		});

		team2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				CricketTeam d = teams[position];
				selectedTeam2 = d;
				RadioButton rb_team2 = (RadioButton) findViewById(R.id.rb_toss_won_2);
				rb_team2.setText(d.TeamName);
			}

			public void onNothingSelected(AdapterView<?> parent) {
				selectedTeam2 = new CricketTeam();
				RadioButton rb_team2 = (RadioButton) findViewById(R.id.rb_toss_won_2);
				rb_team2.setText("");
			}
		});

		if (team1.getCount() > 1) {
			team1.setSelection(0);
			team2.setSelection(1);

			RadioButton rb_team1 = (RadioButton) findViewById(R.id.rb_toss_won_1);
			rb_team1.setText(team1.getSelectedItem().toString());

			RadioButton rb_team2 = (RadioButton) findViewById(R.id.rb_toss_won_2);

			rb_team2.setText(team2.getSelectedItem().toString());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.new_match, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.exit:
			this.finish();
			return true;
		default:
			return false;
		}
	}

	public void createMatchClick(View v) {

		CricketMatch match = new CricketMatch();

		match.setPlayedOn(CommonDataSource.getCurrentDate());

		final EditText et_matchName = (EditText) findViewById(R.id.matchName);
		final RadioButton tossWonByTeam1 = (RadioButton) findViewById(R.id.rb_toss_won_1);
		final RadioButton tossWonByTeam2 = (RadioButton) findViewById(R.id.rb_toss_won_2);

		final RadioButton electedToBat = (RadioButton) findViewById(R.id.rb_elected_to_bat);
		final RadioButton electedToBowl = (RadioButton) findViewById(R.id.rb_elected_to_bowl);

		match.setMatchName(et_matchName.getText().toString());
		match.setTeam1(selectedTeam1.TeamId);
		match.setTeam2(selectedTeam2.TeamId);

		if (tossWonByTeam1.isChecked()) {
			match.setTossWonBy(selectedTeam1.TeamId);

			if (electedToBat.isChecked()) {
				match.setBattingTeam(selectedTeam1.TeamId);
			} else if (electedToBowl.isChecked()) {
				match.setBattingTeam(selectedTeam2.TeamId);
			}

		} else if (tossWonByTeam2.isChecked()) {
			match.setTossWonBy(selectedTeam2.TeamId);

			if (electedToBat.isChecked()) {
				match.setBattingTeam(selectedTeam2.TeamId);
			} else if (electedToBowl.isChecked()) {
				match.setBattingTeam(selectedTeam1.TeamId);
			}
		}

		if (selectedMatchType.IsLimitedOvers) {
			SeekBar seekbar = (SeekBar) findViewById(R.id.seekBar1);
			match.setOvers(seekbar.getProgress());
		} else {
			match.setOvers(-1);
		}

		match.setMatchTypeId(selectedMatchType.MatchTypeId);

		if (match.getMatchName().length() > 3 && match.getTeam1() > 0
				&& match.getTeam2() > 0 && match.getTossWonBy() > 0
				&& match.getBattingTeam() > 0 && match.getMatchTypeId() > 0
				&& (match.getOvers() == -1 || match.getOvers() > 1)) {

			match.setFirstInnningsBattingTeamId(match.getBattingTeam());

			CricketMatchDataSource db = new CricketMatchDataSource(this);
			db.createMatch(match);

			if (match.getMatchId() > 0) {

				match.setMatchType(selectedMatchType);
				ScoreSheetDataStore.scoreSheetLoaded = false;
				ScoreSheetDataStore.context = this;
				ScoreSheetDataStore.Team1 = selectedTeam1;
				ScoreSheetDataStore.Team2 = selectedTeam2;
				ScoreSheetDataStore.Match = match;
				ScoreSheetDataStore.createNewInnings(match.getBattingTeam(),
						this);

				Intent scoreSheet = new Intent(this, MatchScoreSheet.class);
				startActivity(scoreSheet);
				this.finish();
			}
		} else {

			String validationMessage = "";

			if (match.getMatchName().length() < 4) {
				validationMessage += "Match name should contain atleast 4 characters.";
			}

			if (match.getTeam1() < 1) {
				validationMessage += "<Br>Please select team 1 to start the match.";
			}

			if (match.getTeam2() > 2) {
				validationMessage += "<Br>Please select team 2 to start the match.";
			}

			if (validationMessage == "") {
				if (match.getTossWonBy() < 1) {
					validationMessage += "<BR>Please select the toss detail.";
				}

				if (match.getBattingTeam() < 1) {
					validationMessage += "<BR>Please select batting team.";
				}

				if (match.getOvers() != -1 && match.getOvers() < 2) {

					validationMessage += "<BR>Over's should be greater than or equal to 2.";
				}

			}

			showCustomDialog(DialogWindowType.MessageDialog, validationMessage,
					"Input Error");
		}

	}

	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {

		setOvers(progress);
	}

	private void setOvers(int progress) {
		TextView tv_no_of_overs = (TextView) findViewById(R.id.tv_no_of_overs);

		tv_no_of_overs.setText(getResources().getString(
				R.string.new_match_no_of_overs)
				+ " - " + progress);
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

	@SuppressWarnings("deprecation")
	private void showCustomDialog(int id, String message, String title) {
		htmlMessage = message;
		dialogTitle = title;
		removeDialog(id);
		showDialog(id);
	}

	public void onStartTrackingTouch(SeekBar arg0) {

	}

	public void onStopTrackingTouch(SeekBar seekBar) {

	}

}
