package sss.cricket.scorer.ui;

import sss.cricket.scorer.database.CommonDataSource;
import sss.cricket.scorer.database.CricketMatch;
import sss.cricket.scorer.database.CricketMatchDataSource;
import sss.cricket.scorer.database.CricketTeam;
import sss.cricket.scorer.database.MatchType;

import sss.cricket.scorer.ui.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class ViewMatch extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_match);
		fillAllMatches();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.view_match, menu);
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

	CricketMatch selectedMatch;
	int deleteClick = 0;

	private void fillAllMatches() {
		Spinner team1 = (Spinner) findViewById(R.id.spinner_all_matches);

		CricketMatchDataSource database = new CricketMatchDataSource(this);

		final CricketMatch[] allMatches = database.getAllMatches();

		ArrayAdapter<CricketMatch> adapter = new ArrayAdapter<CricketMatch>(
				this, android.R.layout.simple_spinner_item, allMatches);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		team1.setAdapter(adapter);

		team1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				CricketMatch d = allMatches[position];
				deleteClick = 0;
				selectedMatch = d;
				final TextView message = (TextView) findViewById(R.id.view_match_message);
				message.setText("");

			}

			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}

	public void delete_match_click(View v) {

		if (selectedMatch != null) {

			deleteClick++;

			final TextView message = (TextView) findViewById(R.id.view_match_message);
			message.setTextColor(Color.RED);

			if (deleteClick == 3) {

				deleteClick = 0;
				selectedMatch.deleteMatch(this);
				selectedMatch = null;
				message.setText("Selected Match sucessfully deleted");
				fillAllMatches();

			} else if (deleteClick == 1) {

				message.setText("Press delete button 2 times to delete the match");

			} else if (deleteClick == 2) {
				message.setText("Press delete button 1 more time to delete the match");
			}
		}

	}

	public void viewMatchDetails(View v) {

		if (selectedMatch != null) {

			CommonDataSource database = new CommonDataSource(this);
			MatchType matchType = database.getMatchTypeById(selectedMatch
					.getMatchTypeId());

			selectedMatch.setMatchType(matchType);
			final TextView message = (TextView) findViewById(R.id.view_match_message);
			message.setText("");

			CricketTeam team1 = new CricketTeam();
			CricketTeam team2 = new CricketTeam();

			team1.Select(selectedMatch.getTeam1(), this);
			team2.Select(selectedMatch.getTeam2(), this);

			team1.fillPlayers(this);
			team2.fillPlayers(this);

			selectedMatch.selectAllInningsByMatch(this);
						
			ScoreSheetDataStore.scoreSheetLoaded = false;
			ScoreSheetDataStore.context = this;
			ScoreSheetDataStore.Team1 = team1;
			ScoreSheetDataStore.Team2 = team2;
			ScoreSheetDataStore.fillPlayerDictionary();
			ScoreSheetDataStore.Match = selectedMatch;

			ScoreSheetDataStore.setCurrentInnings();

			ScoreSheetDataStore.fillPlayers(
					ScoreSheetDataStore.getBattingTeam().TeamId, this);

			Intent scoreSheet = new Intent(this, FullScoreBoard.class);
			startActivity(scoreSheet);
		}

	}

}
