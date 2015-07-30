package sss.cricket.scorer.ui;

import java.util.ArrayList;
import java.util.List;

import sss.cricket.scorer.database.CricketPlayer;
import sss.cricket.scorer.database.CricketPlayerDataSource;
import sss.cricket.scorer.database.CricketTeam;
import sss.cricket.scorer.database.CricketTeamDataSource;

import sss.cricket.scorer.ui.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class AddTeam extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_team);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.add_team, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.viewTeam:
			Intent team = new Intent(this, ViewTeam.class);
			team.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(team);
		case R.id.exit:
			this.finish();
			return true;
		default:
			return false;
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

	@SuppressWarnings("deprecation")
	private void showCustomDialog(int id, String message, String title) {
		htmlMessage = message;
		dialogTitle = title;
		removeDialog(id);
		showDialog(id);
	}

	public void createTeamClick(View v) {
		final EditText et_teamName = (EditText) findViewById(R.id.teamName);
		final CheckBox cd_sampleTeam = (CheckBox) findViewById(R.id.chk_create_sample_team_members);

		String teamName = et_teamName.getText().toString();

		if (teamName != null && teamName.length() > 2) {

			CricketTeam team = new CricketTeam();
			team.TeamName = et_teamName.getText().toString();

			CricketTeamDataSource database = new CricketTeamDataSource(this);
			database.createTeam(team);

			Log.i("Team Created", team.TeamName + " id: " + team.TeamId);

			long teamId = team.TeamId;
			if (cd_sampleTeam.isChecked()) {
				List<CricketPlayer> players = new ArrayList<CricketPlayer>();

				String[] team1Players = { "10-5-Batsman1", "10-5-Batsman2",
						"10-5-Batsman3", "9-5-Batsman4", "9-5-Batsman5",
						"9-5-Batsman6", "7-7-AllRounder1", "7-7-AllRounder2",
						"7-7-AllRounder3", "7-7-AllRounder4",
						"7-7-AllRounder5", "5-8-Bowler1", "5-8-Bowler2",
						"5-8-Bowler3", "5-8-Bowler4", "5-8-Bowler5",
						"5-8-Bowler6" };

				for (String teamMember : team1Players) {
					CricketPlayer player = new CricketPlayer();
					player.PlayerName = teamMember;
					player.TeamId = teamId;
					players.add(player);
				}

				CricketPlayerDataSource db = new CricketPlayerDataSource(this);

				db.createPlayers(players);
			}

			showCustomDialog(DialogWindowType.MessageDialog, teamName + " Team "
					+ "Sucessfully created", "Success");
		} else {
			showCustomDialog(DialogWindowType.MessageDialog,
					"Team name should contain atleast 3 characters",
					"Input Error");
		}
	}
}
