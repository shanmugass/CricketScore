package sss.cricket.scorer.ui;

import sss.cricket.scorer.database.CricketPlayer;
import sss.cricket.scorer.database.CricketPlayerDataSource;
import sss.cricket.scorer.ui.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class ViewTeamMember extends Activity {

	CricketPlayer player;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_team_member);
		player = ScoreSheetDataStore.PlayerToView;

		fillPlayer(player);
	}

	private void fillPlayer(CricketPlayer player) {
		final EditText et_playerName = (EditText) findViewById(R.id.playerName);
		final SeekBar sb_battingSkill = (SeekBar) findViewById(R.id.battingSkill);
		final SeekBar sb_bowlingSkill = (SeekBar) findViewById(R.id.BowlingSkill);

		et_playerName.setText(player.PlayerName);
		sb_battingSkill.setProgress(player.getBattingSkill());
		sb_bowlingSkill.setProgress(player.getBowlingSkill());

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.view_team_member, menu);
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

	@SuppressWarnings({ "deprecation", "unused" })
	private void showCustomDialog(int id, String message, String title) {
		htmlMessage = message;
		dialogTitle = title;
		removeDialog(id);
		showDialog(id);
	}

	public void updateTeamMemberClick(View v) {
		final EditText et_playerName = (EditText) findViewById(R.id.playerName);

		String playerName = et_playerName.getText().toString();

		if (playerName.length() > 2) {

			final SeekBar sb_battingSkill = (SeekBar) findViewById(R.id.battingSkill);
			final SeekBar sb_bowlingSkill = (SeekBar) findViewById(R.id.BowlingSkill);
			player.PlayerName = playerName;
			player.setBattingSkill(sb_battingSkill.getProgress());
			player.setBowlingSkill(sb_bowlingSkill.getProgress());

			CricketPlayerDataSource database = new CricketPlayerDataSource(this);
			database.updatePlayer(player);

		}
	}

}
