package sss.cricket.scorer.ui;

import sss.cricket.scorer.database.CricketPlayer;
import sss.cricket.scorer.database.CricketTeam;
import sss.cricket.scorer.database.CricketTeamDataSource;

import sss.cricket.scorer.ui.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class ViewTeam extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_team);

		populateTeam();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (selectedTeam1 != null && selectedTeam1.TeamId > 0) {
			populatePlayers();
		}
	}

	float textSize = 18f;
	CricketTeam selectedTeam1;

	private void populateTeam() {

		final LinearLayout ll_viewTeam = (LinearLayout) findViewById(R.id.ll_viewTeam);

		ll_viewTeam.setVisibility(View.INVISIBLE);
		Spinner team1 = (Spinner) findViewById(R.id.spinner_team1);

		CricketTeamDataSource database = new CricketTeamDataSource(this);

		final CricketTeam[] teams = database.GetAllTeamsInArray(true);

		final EditText et_Team = (EditText) findViewById(R.id.et_teamName);

		ArrayAdapter<CricketTeam> adapter = new ArrayAdapter<CricketTeam>(this,
				android.R.layout.simple_spinner_item, teams);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		team1.setAdapter(adapter);

		team1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				CricketTeam d = teams[position];
				selectedTeam1 = d;
				et_Team.setText(d.TeamName);
				if (selectedTeam1.TeamId > 0) {
					ll_viewTeam.setVisibility(View.VISIBLE);
					populatePlayers();
				} else
					ll_viewTeam.setVisibility(View.INVISIBLE);
			}

			public void onNothingSelected(AdapterView<?> parent) {
				selectedTeam1 = new CricketTeam();
				et_Team.setText("");
				ll_viewTeam.setVisibility(View.INVISIBLE);
			}
		});

	}

	protected void populatePlayers() {

		if (selectedTeam1.Players == null || selectedTeam1.Players.isEmpty())
			selectedTeam1.fillPlayers(this);

		final LinearLayout ll_players = (LinearLayout) findViewById(R.id.ll_teamPlayers);
		ll_players.removeAllViews();
		
		Boolean isAlternativePlayer=false;
		
		for (CricketPlayer player : selectedTeam1.Players) {
			TextView playerName = new TextView(this);
			playerName.setLayoutParams(getParams());
			playerName.setText(player.PlayerName);
			playerName.setClickable(true);
			playerName.setId((int) player.PlayerId);
			playerName.setTextSize(textSize);
			
			isAlternativePlayer= !isAlternativePlayer;
			
			if(isAlternativePlayer)
			{
				playerName.setBackgroundColor(Color.RED);
				playerName.setTextColor(Color.BLACK);
			}

			playerName.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {

					int playerId = v.getId();

					CricketPlayer playerToView = null;

					for (CricketPlayer teamPlayer : selectedTeam1.Players) {
						if (teamPlayer.PlayerId == playerId) {
							playerToView = teamPlayer;
							break;
						}
					}

					if (playerToView != null) {
						Intent viewTeamMember1 = new Intent(v.getContext(),
								ViewTeamMember.class);
						ScoreSheetDataStore.PlayerToView = playerToView;
						startActivity(viewTeamMember1);
					}

				}
			});
			ll_players.addView(playerName);
		}
	}

	private LayoutParams getParams() {
		LayoutParams lparams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);

		lparams.setMargins(0, 0, 0, 5);
		return lparams;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.view_team, menu);
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

	@SuppressWarnings({ "deprecation" })
	private void showCustomDialog(int id, String message, String title) {
		htmlMessage = message;
		dialogTitle = title;
		removeDialog(id);
		showDialog(id);
	}

	public void viewTeamClick(View v) {

		if (selectedTeam1 != null && selectedTeam1.TeamId > 0) {

		} else {
			showCustomDialog(DialogWindowType.MessageDialog,
					"Please select the team", "Input Error");
		}

	}

}
