package sss.cricket.scorer.ui;

import sss.cricket.scorer.database.CricketPlayer;
import sss.cricket.scorer.database.CricketPlayerDataSource;
import sss.cricket.scorer.database.CricketTeam;
import sss.cricket.scorer.database.CricketTeamDataSource;

import sss.cricket.scorer.ui.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

public class AddTeamMember extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_team_member);
		FillTeams();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.add_team_member, menu);
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

	long selectedTeamId = 0;
	long toTeamId = 0;
	long fromTeamId = 0;

	public void createTeamMemberClick(View v) {

		CricketPlayerDataSource database = new CricketPlayerDataSource(this);

		if (v.getId() == R.id.btn_create_new) {

			final EditText et_playerName = (EditText) findViewById(R.id.playerName);

			String playerName = et_playerName.getText().toString();

			if (selectedTeamId > 0 && playerName.length() > 2) {

				final SeekBar sb_battingSkill = (SeekBar) findViewById(R.id.battingSkill);
				final SeekBar sb_bowlingSkill = (SeekBar) findViewById(R.id.BowlingSkill);

				CricketPlayer player = new CricketPlayer();
				player.PlayerName = playerName;
				player.setBattingSkill(sb_battingSkill.getProgress());
				player.setBowlingSkill(sb_bowlingSkill.getProgress());

				database.createPlayer(player);

				if (player.PlayerId > 0) {
					Log.i("Player Created", playerName + " id "
							+ player.PlayerId);
					et_playerName.setText("");
					database.addAssociation(player.PlayerId, selectedTeamId);
					addTeamMemberToScoringSheet(player, selectedTeamId);

				}

			} else {
				String validationMessage = "";
				if (selectedTeamId < 1) {
					validationMessage += "Please select the team to add the player";
				}
				if (playerName.length() > 2) {
					if (validationMessage != "")
						validationMessage += "<br>";
					validationMessage += "Player name should contain atleast 3 characters";
				}
				showCustomDialog(DialogWindowType.MessageDialog,
						validationMessage, "Input Error");

			}
		} else if (v.getId() == R.id.btn_create_existing) {

			if (toTeam != null && fromTeam != null & toTeamId != fromTeamId
					&& selectedPlayer != null) {

				toTeam.fillPlayers(this);

				boolean playerNotFound = true;
				for (CricketPlayer player : toTeam.Players) {

					if (player.PlayerId == selectedPlayer.PlayerId) {
						playerNotFound = false;
						break;
					}
				}

				if (playerNotFound) {
					database.addAssociation(selectedPlayer.PlayerId, toTeamId);
					addTeamMemberToScoringSheet(selectedPlayer, toTeamId);
				} else {

					showCustomDialog(DialogWindowType.MessageDialog,
							"Selected player already exists in your team",
							"Input Error");
				}

				FillTeams();
			} else {

				String validationMessage = "";
				if (toTeam == null) {
					validationMessage += "Please select the team to add player.";
				}

				if (fromTeam == null) {
					validationMessage += "<BR>Please select the team to get the players.";
				}

				if (toTeamId == fromTeamId) {
					validationMessage += "<BR>From-Team and To-Team Should no be same.";
				}

				if (selectedPlayer != null) {
					validationMessage += "<BR>Select the player to add.";
				}

				showCustomDialog(DialogWindowType.MessageDialog,
						validationMessage, "Input Error");

			}

		}

	}

	public void addTeamMemberToScoringSheet(CricketPlayer player, long teamId) {

		if (ScoreSheetDataStore.Team1 != null
				&& ScoreSheetDataStore.Team1.TeamId == teamId) {

			ScoreSheetDataStore.Team1.Players.add(player);

		} else if (ScoreSheetDataStore.Team2 != null
				&& ScoreSheetDataStore.Team1.TeamId == teamId) {
			ScoreSheetDataStore.Team2.Players.add(player);
		}

	}

	private void fillPlayer() {

		if (fromTeam == null)
			return;
		Spinner team1 = (Spinner) findViewById(R.id.select_player_to_add);

		fromTeam.fillPlayers(this);

		final CricketPlayer[] players = fromTeam.getPlayersInArray();

		ArrayAdapter<CricketPlayer> adapter = new ArrayAdapter<CricketPlayer>(
				this, android.R.layout.simple_spinner_item, players);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		team1.setAdapter(adapter);

		team1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				CricketPlayer d = players[position];
				selectedPlayer = d;
			}

			public void onNothingSelected(AdapterView<?> parent) {
				selectedPlayer = null;
			}
		});

	}

	CricketTeam fromTeam;
	CricketTeam toTeam;
	CricketPlayer selectedPlayer;

	private void FillTeams() {
		Spinner team1 = (Spinner) findViewById(R.id.team1);
		Spinner sp_toTeam = (Spinner) findViewById(R.id.select_team1);
		Spinner sp_fromTeam = (Spinner) findViewById(R.id.select_player_from_team);

		CricketTeamDataSource database = new CricketTeamDataSource(this);

		final CricketTeam[] teams = database.GetAllTeamsInArray(false);

		ArrayAdapter<CricketTeam> adapter = new ArrayAdapter<CricketTeam>(this,
				android.R.layout.simple_spinner_item, teams);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		team1.setAdapter(adapter);

		team1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				CricketTeam d = teams[position];
				selectedTeamId = d.TeamId;
				Log.i("Team Selected", "Team Id" + selectedTeamId);
			}

			public void onNothingSelected(AdapterView<?> parent) {
				selectedTeamId = 0;
			}
		});

		final CricketTeam[] teamCollection2 = database.GetAllTeamsInArray(true);
		ArrayAdapter<CricketTeam> adapter1 = new ArrayAdapter<CricketTeam>(
				this, android.R.layout.simple_spinner_item, teamCollection2);

		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		sp_toTeam.setAdapter(adapter1);

		sp_toTeam
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						CricketTeam d = teamCollection2[position];
						toTeam = d;
						toTeamId = d.TeamId;

					}

					public void onNothingSelected(AdapterView<?> parent) {
						toTeamId = 0;
						toTeam = new CricketTeam();
					}
				});

		final CricketTeam[] teamCollection3 = database.GetAllTeamsInArray(true);

		ArrayAdapter<CricketTeam> adapter2 = new ArrayAdapter<CricketTeam>(
				this, android.R.layout.simple_spinner_item, teamCollection3);

		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		sp_fromTeam.setAdapter(adapter2);

		sp_fromTeam
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						CricketTeam d = teamCollection2[position];
						fromTeamId = d.TeamId;
						if (toTeamId != 0 & fromTeamId != 0
								&& fromTeamId != toTeamId) {
							fromTeam = d;
							fillPlayer();

						}
					}

					public void onNothingSelected(AdapterView<?> parent) {
						fromTeamId = 0;
						fromTeam = new CricketTeam();
					}
				});
	}
}
