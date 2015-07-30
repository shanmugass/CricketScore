package sss.cricket.scorer.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sss.cricket.scorer.database.CommonDataSource;
import sss.cricket.scorer.database.CricketPlayer;
import sss.cricket.scorer.database.CricketPlayerDataSource;
import sss.cricket.scorer.database.CricketTeam;
import sss.cricket.scorer.database.CricketTeamDataSource;

import sss.cricket.scorer.ui.R;

import android.os.Bundle;
import android.os.Environment;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class HomeScreen extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_screen);

		// gets the activity's default ActionBar

		ActionBar actionBar = getActionBar();

		actionBar.show();
		actionBar.setDisplayHomeAsUpEnabled(true);

		String cricketScrorerPath = Environment.getExternalStorageDirectory()
				+ "/CricketScorer";
		File folder = new File(cricketScrorerPath);

		if (!folder.exists()) {
			folder.mkdir();
		}

		CommonDataSource db = new CommonDataSource(this);
		db.setResources(getResources());
		db.createDefaultEntries();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_home_screen, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		return OpenActivity(item.getItemId());
	}

	public void homeScreenOnClick(View v) {
		OpenActivity(v.getId());
	}

	private boolean OpenActivity(int id) {
		switch (id) {
		case R.id.new_match:
		case R.id.btn_new_match:
			Intent newMatch = new Intent(HomeScreen.this, NewMatch.class);
			startActivity(newMatch);
			return true;
		case R.id.view_match:
		case R.id.btn_view_match:
			Intent viewMatch = new Intent(HomeScreen.this, ViewMatch.class);
			startActivity(viewMatch);
			return true;
		case R.id.add_team:
		case R.id.btn_add_team:
			Intent addTeam = new Intent(HomeScreen.this, AddTeam.class);
			startActivity(addTeam);
			return true;
		case R.id.add_team_member:
		case R.id.btn_add_team_member:
			Intent addTeamMember = new Intent(HomeScreen.this,
					AddTeamMember.class);
			startActivity(addTeamMember);
			return true;
		case R.id.create_sample_team:
			createSampleTeam();
			return true;

		case R.id.view_stats:
		case R.id.btn_stats:
			Intent stats = new Intent(HomeScreen.this, StatsHome.class);
			startActivity(stats);
			return true;
		case R.id.exit:
		case R.id.btn_exit:
			this.finish();
			return true;
		default:
			return false;
		}
	}

	private void createSampleTeam() {

		if (createTeams()) {
			createPlayers();
		}
	}

	long teamId1 = 0;
	long teamId2 = 0;

	public boolean createTeams() {

		CricketTeamDataSource db = new CricketTeamDataSource(this);

		List<CricketTeam> teams = db.GetAllTeams();
		if (teams.size() > 0)
			return false;

		CricketTeam team1 = new CricketTeam();
		team1.TeamName = "Team 1";

		db.createTeam(team1);

		teamId1 = team1.TeamId;
		team1.TeamName = "Team 2";
		db.createTeam(team1);
		teamId2 = team1.TeamId;
		return true;
	}

	String[] team1Players = { "10-5-Batsman1", "10-5-Batsman2", "10-5-Batsman3",
			"9-5-Batsman4", "9-5-Batsman5", "9-5-Batsman6", "7-7-AllRounder1",
			"7-7-AllRounder2", "7-7-AllRounder3", "7-7-AllRounder4",
			"7-7-AllRounder5", "5-8-Bowler1", "5-8-Bowler2", "5-8-Bowler3",
			"5-8-Bowler4", "5-8-Bowler5", "5-8-Bowler6" };

	String[] team2Players = { "10-5-Batsman1", "10-5-Batsman2", "10-5-Batsman3",
			"9-5-Batsman4", "9-5-Batsman5", "9-5-Batsman6", "7-7-AllRounder1",
			"7-7-AllRounder2", "7-7-AllRounder3", "7-7-AllRounder4",
			"7-7-AllRounder5", "5-8-Bowler1", "5-8-Bowler2", "5-8-Bowler3",
			"5-8-Bowler4", "5-8-Bowler5", "5-8-Bowler6" };

	public void createPlayers() {
		List<CricketPlayer> players = new ArrayList<CricketPlayer>();

		for (String teamMember : team1Players) {
			CricketPlayer player = new CricketPlayer();
			player.PlayerName = teamMember;
			player.TeamId = teamId1;
			players.add(player);
		}

		for (String teamMember : team2Players) {
			CricketPlayer player = new CricketPlayer();
			player.PlayerName = teamMember;
			player.TeamId = teamId2;
			players.add(player);
		}

		CricketPlayerDataSource db = new CricketPlayerDataSource(this);

		db.createPlayers(players);

	}

}
