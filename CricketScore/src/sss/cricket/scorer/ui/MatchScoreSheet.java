package sss.cricket.scorer.ui;

import java.util.List;
import sss.cricket.scorer.database.BallByBall;
import sss.cricket.scorer.database.BatsmanScore;
import sss.cricket.scorer.database.CricketMatchDataSource;
import sss.cricket.scorer.database.CricketPlayer;
import sss.cricket.scorer.database.CricketTeam;
import sss.cricket.scorer.database.WicketFall;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

public class MatchScoreSheet extends Activity implements
		OnSeekBarChangeListener {

	CricketPlayer tempBatsman1;
	CricketPlayer tempBatsman2;
	CricketPlayer tempBowler;
	WicketFall wicketfall;
	long wicketfallFeilderId;
	Boolean intialScoreAdded = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ScoreSheetDataStore.context = this;

		setContentView(R.layout.match_score_sheet);

		ScoreSheetDataStore.scoreSheetLoaded = true;
		setTeamNames();
		Reset();
		fullReset();

	}

	@Override
	public void onResume() {
		super.onResume();
		if (ScoreSheetDataStore.Batsman1 != null
				&& ScoreSheetDataStore.Batsman2 != null) {
			fillBatsMan();
		}
		if (ScoreSheetDataStore.Bowler != null) {
			fillBowler();
		}
	}

	private void setTeamNames() {

		CricketTeam batting = ScoreSheetDataStore.getBattingTeam();
		CricketTeam bowling = ScoreSheetDataStore.getBowlingTeam();

		final TextView battingTeam = (TextView) findViewById(R.id.batting_team1_label);
		battingTeam.setText(batting.TeamName);

		final TextView bowlingTeam = (TextView) findViewById(R.id.batting_team2_label);
		bowlingTeam.setText(bowling.TeamName);

		if (batting.Players.size() > 2) {
			if (!ScoreSheetDataStore.isOversCompleted()) {
				showCustomDialog(DialogWindowType.SelectOpeningBatsmans);
			}
		}

		int currentInningsid = ScoreSheetDataStore.getCurrentInningsId();
		showSecondInningsRow(false);
		if (currentInningsid != 1) {

			if (currentInningsid == 2) {

				String bowlingTeamScore = ScoreSheetDataStore
						.getFirstInningsScore(bowling.TeamId);

				final TextView txtbowlingTeamScore = (TextView) findViewById(R.id.bowling_team_score);
				txtbowlingTeamScore.setText(bowlingTeamScore);
			} else {
				String bowlingTeamScore = ScoreSheetDataStore
						.getSecondInningsScore(bowling.TeamId);

				final TextView txtbowlingTeamScore = (TextView) findViewById(R.id.bowling_team_score);
				txtbowlingTeamScore.setText(bowlingTeamScore);
			}

			if (ScoreSheetDataStore.hasStartedSecondInnings()) {
				showSecondInningsRow(true);
				final TextView batting2Score = (TextView) findViewById(R.id.batting_team_2nd_score);
				String strbatting2Score = ScoreSheetDataStore
						.getFirstInningsScore(batting.TeamId);
				batting2Score.setText(strbatting2Score);

				final TextView bowling2Score = (TextView) findViewById(R.id.bowling_team_2nd_score);
				String strbowling2Score = ScoreSheetDataStore
						.getFirstInningsScore(bowling.TeamId);
				bowling2Score.setText(strbowling2Score);

			}
		}

	}

	public void showSecondInningsRow(Boolean show) {

		final LinearLayout row2 = (LinearLayout) findViewById(R.id.innings_2_row);

		if (show) {
			row2.setVisibility(View.VISIBLE);
			row2.setLayoutParams(new TableRow.LayoutParams(
					TableRow.LayoutParams.MATCH_PARENT,
					TableRow.LayoutParams.WRAP_CONTENT));
		} else {
			row2.setVisibility(View.INVISIBLE);
			row2.setLayoutParams(new TableRow.LayoutParams(1, 1));
		}

	}

	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {

		case DialogWindowType.ExtrasDialog:
			LayoutInflater extrasFactory = LayoutInflater
					.from(MatchScoreSheet.this);
			final View extrasWindow = extrasFactory.inflate(
					R.layout.dialog_extras, null);

			populateExtrasDialog(extrasWindow);

			return new AlertDialog.Builder(this)
					.setIcon(1)
					.setTitle("Extras Details")
					.setView(extrasWindow)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									saveExtras(extrasWindow);

								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									Reset();
								}
							}).create();

		case DialogWindowType.SelectOpeningBatsmans: // Populate Bats man
		default:
			LayoutInflater factory = LayoutInflater.from(MatchScoreSheet.this);
			final View changeBatsmanWindow = factory.inflate(
					R.layout.dialog_change_batsman, null);

			populateBatsmansForDialog(changeBatsmanWindow);

			return new AlertDialog.Builder(this)
					.setIcon(1)
					.setTitle("Please select the batsmans")
					.setView(changeBatsmanWindow)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									ScoreSheetDataStore.OnStrikeBatsmanId = tempBatsman1.PlayerId;

									tempBatsman1.initializeBattingScore();
									tempBatsman2.initializeBattingScore();

									ScoreSheetDataStore.Batsman1 = tempBatsman1;
									ScoreSheetDataStore.Batsman2 = tempBatsman2;

									fillBatsMan();

									showCustomDialog(DialogWindowType.SelectBowlerWithNoCancel);
								}

							}).create();
		case DialogWindowType.SelectBowler: // bowler change
			LayoutInflater bowlerfactory = LayoutInflater
					.from(MatchScoreSheet.this);
			final View changeBowlerWindow = bowlerfactory.inflate(
					R.layout.dialog_change_player, null);

			populateBowlersForDialog(changeBowlerWindow);

			return new AlertDialog.Builder(this)
					.setIcon(1)
					.setTitle("Please select the bowler")
					.setView(changeBowlerWindow)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									tempBowler.initializeBowlerScore();
									ScoreSheetDataStore.Bowler = tempBowler;
									fillBowler();

									/* User clicked OK so do some stuff */
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									/* User clicked OK so do some stuff */
								}
							}).create();
		case DialogWindowType.SelectBatsman1: // batsman1 change
			LayoutInflater batsman1Factory = LayoutInflater
					.from(MatchScoreSheet.this);
			final View batsman1Window = batsman1Factory.inflate(
					R.layout.dialog_change_player, null);

			populateBatsman1ChangeDialog(batsman1Window);

			return new AlertDialog.Builder(this)
					.setIcon(1)
					.setTitle(
							"Please select the new batsman in place of "
									+ ScoreSheetDataStore.Batsman1.PlayerName)
					.setView(batsman1Window)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									tempBatsman1.initializeBattingScore();
									ScoreSheetDataStore.Batsman1 = tempBatsman1;

									CheckBox cb_batsmanOnstrike = (CheckBox) batsman1Window
											.findViewById(R.id.cb_batsmanOnstrike);

									if (cb_batsmanOnstrike.isChecked()) {
										ScoreSheetDataStore.OnStrikeBatsmanId = ScoreSheetDataStore.Batsman1.PlayerId;
									} else {

										ScoreSheetDataStore.OnStrikeBatsmanId = ScoreSheetDataStore.Batsman2.PlayerId;
									}

									ScoreSheetDataStore
											.checkOnStikeBatsman(tempBatsman1.PlayerId);

									fillBatsMan();

									/* User clicked OK so do some stuff */
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									/* User clicked OK so do some stuff */
								}
							}).create();
		case DialogWindowType.SelectBatsman2: // batsman2 change
			LayoutInflater batsman2Factory = LayoutInflater
					.from(MatchScoreSheet.this);
			final View batsman2Window = batsman2Factory.inflate(
					R.layout.dialog_change_player, null);

			populateBatsman2ChangeDialog(batsman2Window);

			return new AlertDialog.Builder(this)
					.setIcon(1)
					.setTitle(
							"Please select the new batsman in place of "
									+ ScoreSheetDataStore.Batsman2.PlayerName)
					.setView(batsman2Window)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									tempBatsman2.initializeBattingScore();
									ScoreSheetDataStore.Batsman2 = tempBatsman2;

									CheckBox cb_batsmanOnstrike = (CheckBox) batsman2Window
											.findViewById(R.id.cb_batsmanOnstrike);

									if (cb_batsmanOnstrike.isChecked()) {
										ScoreSheetDataStore.OnStrikeBatsmanId = ScoreSheetDataStore.Batsman2.PlayerId;
									} else {

										ScoreSheetDataStore.OnStrikeBatsmanId = ScoreSheetDataStore.Batsman1.PlayerId;
									}

									ScoreSheetDataStore
											.checkOnStikeBatsman(tempBatsman2.PlayerId);
									fillBatsMan();

									/* User clicked OK so do some stuff */
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									/* User clicked OK so do some stuff */
								}
							}).create();

		case DialogWindowType.WicketFall: // Wicket fall
			LayoutInflater wicketfallFactory = LayoutInflater
					.from(MatchScoreSheet.this);
			final View wicketfallWindow = wicketfallFactory.inflate(
					R.layout.dialog_wicket_fall, null);

			populateWicketfallDialog(wicketfallWindow);

			return new AlertDialog.Builder(this)
					.setIcon(1)
					.setTitle("Wicket fall details.")
					.setView(wicketfallWindow)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									fillWicketFallFromUi(wicketfallWindow);

								}

							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

								}
							}).create();

		case DialogWindowType.SelectBowlerWithNoCancel: // bowler change with no
														// cancel
			LayoutInflater bowlerfactory1 = LayoutInflater
					.from(MatchScoreSheet.this);
			final View changeBowlerWindow1 = bowlerfactory1.inflate(
					R.layout.dialog_change_player, null);

			populateBowlersForDialog(changeBowlerWindow1);

			return new AlertDialog.Builder(this)
					.setIcon(1)
					.setTitle("Please select the bowler")
					.setView(changeBowlerWindow1)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									tempBowler.initializeBowlerScore();
									ScoreSheetDataStore.Bowler = tempBowler;
									fillBowler();

									/* User clicked OK so do some stuff */
								}
							}).create();
		case DialogWindowType.SelectBatsman1WithNoCancel: // batsman1 change
															// with no cancel
			LayoutInflater batsman1Factory1 = LayoutInflater
					.from(MatchScoreSheet.this);
			final View batsman1Window1 = batsman1Factory1.inflate(
					R.layout.dialog_change_player, null);

			populateBatsman1ChangeDialog(batsman1Window1);

			return new AlertDialog.Builder(this)
					.setIcon(1)
					.setTitle(
							"Please select the new batsman in place of "
									+ ScoreSheetDataStore.Batsman1.PlayerName)
					.setView(batsman1Window1)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									tempBatsman1.initializeBattingScore();
									ScoreSheetDataStore.Batsman1 = tempBatsman1;

									CheckBox cb_batsmanOnstrike = (CheckBox) batsman1Window1
											.findViewById(R.id.cb_batsmanOnstrike);

									if (cb_batsmanOnstrike.isChecked()) {
										ScoreSheetDataStore.OnStrikeBatsmanId = ScoreSheetDataStore.Batsman1.PlayerId;

									} else {

										ScoreSheetDataStore.OnStrikeBatsmanId = ScoreSheetDataStore.Batsman2.PlayerId;
									}

									ScoreSheetDataStore
											.checkOnStikeBatsman(tempBatsman1.PlayerId);

									fillBatsMan();

									/* User clicked OK so do some stuff */
								}
							}).create();
		case DialogWindowType.SelectBatsman2WithNoCancel: // batsman2 change
															// with no cancel
			LayoutInflater batsman2Factory1 = LayoutInflater
					.from(MatchScoreSheet.this);
			final View batsman2Window1 = batsman2Factory1.inflate(
					R.layout.dialog_change_player, null);

			populateBatsman2ChangeDialog(batsman2Window1);

			return new AlertDialog.Builder(this)
					.setIcon(1)
					.setTitle(
							"Please select the new batsman in place of "
									+ ScoreSheetDataStore.Batsman2.PlayerName)
					.setView(batsman2Window1)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									tempBatsman2.initializeBattingScore();
									ScoreSheetDataStore.Batsman2 = tempBatsman2;

									CheckBox cb_batsmanOnstrike = (CheckBox) batsman2Window1
											.findViewById(R.id.cb_batsmanOnstrike);

									if (cb_batsmanOnstrike.isChecked()) {
										ScoreSheetDataStore.OnStrikeBatsmanId = ScoreSheetDataStore.Batsman2.PlayerId;

									} else {

										ScoreSheetDataStore.OnStrikeBatsmanId = ScoreSheetDataStore.Batsman1.PlayerId;
									}
									ScoreSheetDataStore
											.checkOnStikeBatsman(tempBatsman2.PlayerId);
									fillBatsMan();

									/* User clicked OK so do some stuff */
								}
							}).create();

		case DialogWindowType.ChangeAllocatedOvers:
			LayoutInflater changeAllocatedOvers = LayoutInflater
					.from(MatchScoreSheet.this);
			final View changeAllocatedWindow = changeAllocatedOvers.inflate(
					R.layout.dialog_change_over, null);

			populateChangeOverDialog(changeAllocatedWindow);

			return new AlertDialog.Builder(this)
					.setIcon(1)
					.setTitle("Change Allocated Overs.")
					.setView(changeAllocatedWindow)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									SeekBar seekbar = (SeekBar) changeAllocatedWindow
											.findViewById(R.id.sb_overs);
									int changedOves = seekbar.getProgress();
									int bowledOvers = ScoreSheetDataStore.currentInnings
											.getBowledOvers();

									if (bowledOvers > changedOves) {
										changedOves = bowledOvers;
									}
									ScoreSheetDataStore.currentInnings
											.setAllocatedOvers(changedOves);

									ScoreSheetDataStore.currentInnings
											.saveOvers();

									Reset();
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									/* User clicked OK so do some stuff */
								}
							}).create();

		case DialogWindowType.CompleteMatch:
			LayoutInflater completeMatch = LayoutInflater
					.from(MatchScoreSheet.this);
			final View completeMatchWindow = completeMatch.inflate(
					R.layout.dialog_complete_match, null);

			populateCompleteMatchDialog(completeMatchWindow);

			return new AlertDialog.Builder(this)
					.setIcon(1)
					.setTitle("Are you sure to complete the match?")
					.setView(completeMatchWindow)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									getMatchCompleteDetails(completeMatchWindow);
									showFullScoreCard();
								}

							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									/* User clicked OK so do some stuff */
								}
							}).create();
		}

	}

	private void showFullScoreCard() {
		Intent scoreBoard = new Intent(this, FullScoreBoard.class);
		scoreBoard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(scoreBoard);
		this.finish();
	}

	private void getMatchCompleteDetails(final View completeMatchWindow) {
		final EditText summary = (EditText) completeMatchWindow
				.findViewById(R.id.et_match_description);

		String matchSummaryDetails = summary.getText().toString();

		final RadioButton rb_team1 = (RadioButton) completeMatchWindow
				.findViewById(R.id.rb_WonByTeam1);

		if (rb_team1.isChecked()) {
			saveMatchCompletionDetails(ScoreSheetDataStore.Team1.TeamId, 1,
					matchSummaryDetails);
			return;
		}

		final RadioButton rb_team2 = (RadioButton) completeMatchWindow
				.findViewById(R.id.rb_WonByTeam2);

		if (rb_team2.isChecked()) {
			saveMatchCompletionDetails(ScoreSheetDataStore.Team2.TeamId, 1,
					matchSummaryDetails);
			return;
		}

		final RadioButton rb_matchDrawn = (RadioButton) completeMatchWindow
				.findViewById(R.id.rb_MatchDrawn);

		if (rb_matchDrawn.isChecked()) {
			saveMatchCompletionDetails(0, 2, matchSummaryDetails);
			return;
		}

		final RadioButton rb_MatchTied = (RadioButton) completeMatchWindow
				.findViewById(R.id.rb_MatchTied);

		if (rb_MatchTied.isChecked()) {
			saveMatchCompletionDetails(0, 3, matchSummaryDetails);
		}

		final RadioButton rb_matchAbandon = (RadioButton) completeMatchWindow
				.findViewById(R.id.rb_Abandon);

		if (rb_matchAbandon.isChecked()) {
			saveMatchCompletionDetails(0, 4, matchSummaryDetails);
		}
	}

	private void saveMatchCompletionDetails(long winningTeamId, int result,
			String matchSummary) {

		ScoreSheetDataStore.Match.setWonBy(winningTeamId);
		ScoreSheetDataStore.Match.setMatchResult(result);
		ScoreSheetDataStore.Match.setMatchDescription(matchSummary);
		CricketMatchDataSource database = new CricketMatchDataSource(this);
		database.completeMatch(ScoreSheetDataStore.Match);

	}

	private void populateCompleteMatchDialog(View completeMatchWindow) {

		final EditText summary = (EditText) completeMatchWindow
				.findViewById(R.id.et_match_description);

		final RadioButton rb_team1 = (RadioButton) completeMatchWindow
				.findViewById(R.id.rb_WonByTeam1);

		final RadioButton rb_team2 = (RadioButton) completeMatchWindow
				.findViewById(R.id.rb_WonByTeam2);

		rb_team1.setText("Won by " + ScoreSheetDataStore.Team1.TeamName);
		rb_team2.setText("Won by " + ScoreSheetDataStore.Team2.TeamName);

		long winningTeamId = ScoreSheetDataStore.Match.getWinningTeam();

		if (winningTeamId == ScoreSheetDataStore.Team1.TeamId) {
			rb_team1.setChecked(true);
			summary.setText(ScoreSheetDataStore.Team1.TeamName
					+ " Won the match.");
		} else {
			rb_team2.setChecked(true);
			summary.setText(ScoreSheetDataStore.Team2.TeamName
					+ " Won the match.");
		}

	}

	private void populateChangeOverDialog(View changeAllocatedWindow) {

		SeekBar seekbar = (SeekBar) changeAllocatedWindow
				.findViewById(R.id.sb_overs);

		final TextView tv_allocated_overs = (TextView) changeAllocatedWindow
				.findViewById(R.id.tv_allocated_overs);

		int overs = ScoreSheetDataStore.currentInnings.getAllocatedOvers();

		tv_allocated_overs.setText("Allocated Overs - " + overs);

		seekbar.setProgress(overs);

		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar arg0, int progress,
					boolean arg2) {
				tv_allocated_overs.setText("Allocated Overs - " + progress);
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {

			}
		});
	}

	private void saveExtras(final View extrasWindow) {
		CheckBox chk_Wide = (CheckBox) extrasWindow.findViewById(R.id.chk_wide);

		CheckBox chk_NoBall = (CheckBox) extrasWindow.findViewById(R.id.chk_nb);

		CheckBox chk_bye = (CheckBox) extrasWindow.findViewById(R.id.chk_bye);

		CheckBox chk_legBye = (CheckBox) extrasWindow
				.findViewById(R.id.chk_bye);

		isWideClick = chk_Wide.isChecked();
		isNoBallClick = chk_NoBall.isChecked();
		isByeClick = chk_bye.isChecked();
		isLegByeClick = chk_legBye.isChecked();

		SeekBar seekbar = (SeekBar) extrasWindow
				.findViewById(R.id.wicket_runs_SeekBar1);

		clickedValue = seekbar.getProgress();

		saveClick(null);
	}

	private void populateExtrasDialog(final View extrasWindow) {

		if (isWideClick) {
			CheckBox chk_Wide = (CheckBox) extrasWindow
					.findViewById(R.id.chk_wide);
			chk_Wide.setChecked(true);
		}

		if (isNoBallClick) {
			CheckBox chk_NoBall = (CheckBox) extrasWindow
					.findViewById(R.id.chk_nb);
			chk_NoBall.setChecked(true);
		}

		if (isByeClick) {
			CheckBox chk_bye = (CheckBox) extrasWindow
					.findViewById(R.id.chk_bye);
			chk_bye.setChecked(true);
		}

		if (isLegByeClick) {
			CheckBox chk_legBye = (CheckBox) extrasWindow
					.findViewById(R.id.chk_bye);
			chk_legBye.setChecked(true);
		}

		SeekBar seekbar = (SeekBar) extrasWindow
				.findViewById(R.id.wicket_runs_SeekBar1);

		final TextView runsLabel1 = (TextView) extrasWindow
				.findViewById(R.id.extras_plus_run);

		if (isWideClick) {
			runsLabel1.setText("Wide + 0 Runs");
		} else if (isNoBallClick) {
			runsLabel1.setText("No Ball + 0 Runs");
		} else {
			runsLabel1.setText(" 0 Runs");
		}
		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar arg0, int progress,
					boolean arg2) {

				if (isWideClick) {
					runsLabel1.setText("Wide + " + progress + " Runs");
				} else if (isNoBallClick) {
					runsLabel1.setText("No Ball + " + progress + " Runs");
				} else {
					runsLabel1.setText(" " + progress + " Runs");
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {

			}
		});
	}

	private void fillWicketFallFromUi(final View wicketfallWindow) {

		wicketfall = new WicketFall();

		RadioButton rb_player1 = (RadioButton) wicketfallWindow
				.findViewById(R.id.wicketfall_player1);

		CheckBox chk_Wide = (CheckBox) wicketfallWindow
				.findViewById(R.id.chk_wide);

		CheckBox chk_NoBall = (CheckBox) wicketfallWindow
				.findViewById(R.id.chk_nb);

		CheckBox chk_bye = (CheckBox) wicketfallWindow
				.findViewById(R.id.chk_bye);

		CheckBox chk_legBye = (CheckBox) wicketfallWindow
				.findViewById(R.id.chk_bye);

		isWideClick = chk_Wide.isChecked();
		isNoBallClick = chk_NoBall.isChecked();
		isByeClick = chk_bye.isChecked();
		isLegByeClick = chk_legBye.isChecked();

		SeekBar seekbar = (SeekBar) wicketfallWindow
				.findViewById(R.id.wicket_runs_SeekBar1);

		clickedValue = seekbar.getProgress();

		if (rb_player1.isChecked()) {
			wicketfall
					.setBatsmanId(ScoreSheetDataStore.getOnStikeBatsman().PlayerId);
		} else {
			wicketfall
					.setBatsmanId(ScoreSheetDataStore.getNonStikeBatsman().PlayerId);
		}

		Spinner wicketType = (Spinner) wicketfallWindow
				.findViewById(R.id.wicket_type);

		int wicketTypeId = wicketType.getSelectedItemPosition() + 1;
		wicketfall.setWicketType(wicketTypeId);
		wicketfall.setBowlerId(ScoreSheetDataStore.getBowler().PlayerId);

		if (wicketTypeId != 2 && wicketTypeId != 5) {

			wicketfall.setFielderId(wicketfallFeilderId);
		}

		isWicketClick = true;

		saveClick(null);
	}

	private void fillBatsMan() {

		CricketPlayer player1 = ScoreSheetDataStore.Batsman1;
		CricketPlayer player2 = ScoreSheetDataStore.Batsman2;

		final ImageView batimage1 = (ImageView) findViewById(R.id.batsmanImage1);
		final ImageView batimage2 = (ImageView) findViewById(R.id.batsmanImage2);

		final TextView bat1 = (TextView) findViewById(R.id.batting_player1);
		final TextView bat2 = (TextView) findViewById(R.id.batting_player2);

		final TextView bat1Score = (TextView) findViewById(R.id.batting_player1_score);
		final TextView bat2Score = (TextView) findViewById(R.id.batting_player2_score);

		final TextView bat1Balls = (TextView) findViewById(R.id.batting_player1_balls);
		final TextView bat2Balls = (TextView) findViewById(R.id.batting_player2_balls);

		final TextView bat1rr = (TextView) findViewById(R.id.batting_player1_rr);
		final TextView bat2rr = (TextView) findViewById(R.id.batting_player2_rr);

		if (player1 == null || player2 == null) {
			bat1.setText("");
			bat2.setText("");

			bat1Score.setText("0");
			bat2Score.setText("0");

			bat1Balls.setText("0");

			bat1Balls.setText("0");

			bat1rr.setText("0");
			bat2rr.setText("0");
			return;
		}

		if (player1.PlayerId == ScoreSheetDataStore.OnStrikeBatsmanId) {
			batimage1.setVisibility(View.VISIBLE);
			batimage2.setVisibility(View.INVISIBLE);

		} else {
			batimage2.setVisibility(View.VISIBLE);
			batimage1.setVisibility(View.INVISIBLE);

		}

		bat1.setText(player1.PlayerName);
		bat2.setText(player2.PlayerName);

		BatsmanScore batsman1Score = player1.getBatsmanScore();
		BatsmanScore batsman2Score = player2.getBatsmanScore();

		bat1Score.setText("" + batsman1Score.getScoredRuns());
		bat2Score.setText("" + batsman2Score.getScoredRuns());

		bat1Balls.setText("" + batsman1Score.getBallsPlayed());
		bat2Balls.setText("" + batsman2Score.getBallsPlayed());

		bat1rr.setText("" + batsman1Score.getStrikeRate());
		bat2rr.setText("" + batsman2Score.getStrikeRate());

	}

	private void populateWicketfallDialog(final View wikcetfallWindow) {

		RadioButton rb_player1 = (RadioButton) wikcetfallWindow
				.findViewById(R.id.wicketfall_player1);
		RadioButton rb_player2 = (RadioButton) wikcetfallWindow
				.findViewById(R.id.wicketfall_player2);

		rb_player1.setText(ScoreSheetDataStore.getOnStikeBatsman().PlayerName);
		rb_player2.setText(ScoreSheetDataStore.getNonStikeBatsman().PlayerName);

		rb_player1.setChecked(true);

		Spinner sp_team1Players = (Spinner) wikcetfallWindow
				.findViewById(R.id.wicket_fielder);

		final CricketPlayer[] team1Players = ScoreSheetDataStore
				.getBowlers(true);

		ArrayAdapter<CricketPlayer> adapter = new ArrayAdapter<CricketPlayer>(
				this, android.R.layout.simple_spinner_item, team1Players);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		sp_team1Players.setAdapter(adapter);

		sp_team1Players
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						CricketPlayer d = team1Players[position];
						wicketfallFeilderId = d.PlayerId;
					}

					public void onNothingSelected(AdapterView<?> parent) {

					}
				});

		final TextView runsLabel1 = (TextView) wikcetfallWindow
				.findViewById(R.id.wicket_plus_run);

		SeekBar seekbar = (SeekBar) wikcetfallWindow
				.findViewById(R.id.wicket_runs_SeekBar1);
		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar arg0, int progress,
					boolean arg2) {

				runsLabel1.setText("Wicket + " + progress + " Runs");
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {

			}
		});

	}

	private void fillBowler() {

		CricketPlayer bowler = ScoreSheetDataStore.getBowler();

		final TextView battingTeam = (TextView) findViewById(R.id.bowler);

		if (bowler == null) {
			battingTeam.setText("");
			return;
		}

		battingTeam.setText(bowler.getBowlerStats());

		enableUndoButton(ScoreSheetDataStore.hasRecordedActions());

	}

	private void populateBowlersForDialog(final View changeBowlerWindow) {
		Spinner sp_team1Players = (Spinner) changeBowlerWindow
				.findViewById(R.id.spinner_team1);

		final CricketPlayer[] team1Players = ScoreSheetDataStore
				.getBowlers(false);

		CheckBox cb_batsmanOnstrike = (CheckBox) changeBowlerWindow
				.findViewById(R.id.cb_batsmanOnstrike);

		cb_batsmanOnstrike.setVisibility(View.INVISIBLE);

		ArrayAdapter<CricketPlayer> adapter = new ArrayAdapter<CricketPlayer>(
				this, android.R.layout.simple_spinner_item, team1Players);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		sp_team1Players.setAdapter(adapter);

		sp_team1Players
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						CricketPlayer d = team1Players[position];
						tempBowler = d;
					}

					public void onNothingSelected(AdapterView<?> parent) {

					}
				});
	}

	private void populateBatsman2ChangeDialog(final View changeBatsmanWindow) {

		Spinner sp_team2Players = (Spinner) changeBatsmanWindow
				.findViewById(R.id.spinner_team1);

		final CricketPlayer[] team1Players = ScoreSheetDataStore.getBatsmans();

		ArrayAdapter<CricketPlayer> adapter = new ArrayAdapter<CricketPlayer>(
				this, android.R.layout.simple_spinner_item, team1Players);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		if (ScoreSheetDataStore.OnStrikeBatsmanId == ScoreSheetDataStore.Batsman2.PlayerId) {
			CheckBox cb_batsmanOnstrike = (CheckBox) changeBatsmanWindow
					.findViewById(R.id.cb_batsmanOnstrike);

			cb_batsmanOnstrike.setChecked(true);
		}

		sp_team2Players.setAdapter(adapter);

		sp_team2Players
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						CricketPlayer d = team1Players[position];
						tempBatsman2 = d;
					}

					public void onNothingSelected(AdapterView<?> parent) {

					}
				});
	}

	private void populateBatsman1ChangeDialog(final View changeBatsmanWindow) {
		Spinner sp_team1Players = (Spinner) changeBatsmanWindow
				.findViewById(R.id.spinner_team1);

		final CricketPlayer[] team1Players = ScoreSheetDataStore.getBatsmans();

		ArrayAdapter<CricketPlayer> adapter = new ArrayAdapter<CricketPlayer>(
				this, android.R.layout.simple_spinner_item, team1Players);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		sp_team1Players.setAdapter(adapter);

		if (ScoreSheetDataStore.OnStrikeBatsmanId == ScoreSheetDataStore.Batsman1.PlayerId) {
			CheckBox cb_batsmanOnstrike = (CheckBox) changeBatsmanWindow
					.findViewById(R.id.cb_batsmanOnstrike);

			cb_batsmanOnstrike.setChecked(true);
		}

		sp_team1Players
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						CricketPlayer d = team1Players[position];
						tempBatsman1 = d;
					}

					public void onNothingSelected(AdapterView<?> parent) {

					}
				});
	}

	private void populateBatsmansForDialog(final View changeBatsmanWindow) {
		Spinner sp_team1Players = (Spinner) changeBatsmanWindow
				.findViewById(R.id.spinner_team1);

		final CricketPlayer[] team1Players = ScoreSheetDataStore.getBatsmans();

		ArrayAdapter<CricketPlayer> adapter = new ArrayAdapter<CricketPlayer>(
				this, android.R.layout.simple_spinner_item, team1Players);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		sp_team1Players.setAdapter(adapter);

		sp_team1Players
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						CricketPlayer d = team1Players[position];
						tempBatsman1 = d;
					}

					public void onNothingSelected(AdapterView<?> parent) {

					}
				});

		Spinner sp_team2Players = (Spinner) changeBatsmanWindow
				.findViewById(R.id.spinner_team2);

		sp_team2Players.setAdapter(adapter);

		sp_team2Players
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						CricketPlayer d = team1Players[position];
						tempBatsman2 = d;
					}

					public void onNothingSelected(AdapterView<?> parent) {

					}
				});

		sp_team2Players.setSelection(2);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.match_score_sheet, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.exit:
			this.finish();
			return true;

		case R.id.menu_add_user:
			Intent teamMember = new Intent(this, AddTeamMember.class);
			startActivity(teamMember);
			return true;
		case R.id.menu_full_score_board:
			Intent scoreBoard = new Intent(this, FullScoreBoard.class);
			scoreBoard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(scoreBoard);
			return true;
		case R.id.changeBatsman1:
			showCustomDialog(DialogWindowType.SelectBatsman1);
			return true;
		case R.id.changeBatsman2:
			showCustomDialog(DialogWindowType.SelectBatsman2);
			return true;
		case R.id.changeBowler:
			showCustomDialog(DialogWindowType.SelectBowler);
			return true;
		case R.id.changeStricker:
			ScoreSheetDataStore.switchBatsman();
			fillBatsMan();
			return true;
		case R.id.SwitchInnings:
			switchInnings(ScoreSheetDataStore.getBowlingTeam().TeamId);
			return true;
		case R.id.enforceFollowOn:
			if (ScoreSheetDataStore.getCurrentInningsId() == 2) {
				switchInnings(ScoreSheetDataStore.getBattingTeam().TeamId);
			}
			return true;
		case R.id.changeOvers:
			showCustomDialog(DialogWindowType.ChangeAllocatedOvers);
			return true;
		case R.id.completeMatch:
			showCustomDialog(DialogWindowType.CompleteMatch);
			return true;
		default:
			return false;
		}
	}

	public void show_stats(View v) {

		CricketPlayer playerToView = null;
		switch (v.getId()) {
		case R.id.batting_player1:
			playerToView = ScoreSheetDataStore.Batsman1;
			break;
		case R.id.batting_player2:
			playerToView = ScoreSheetDataStore.Batsman2;
			break;
		case R.id.bowler:
			playerToView = ScoreSheetDataStore.Bowler;
			break;
		}

		if (playerToView != null) {
			Intent viewTeamMember = new Intent(this, ViewTeamMember.class);
			ScoreSheetDataStore.PlayerToView = playerToView;
			startActivity(viewTeamMember);
		}
	}

	private void switchInnings(long teamId) {
		// TODO Include single match innings logic
		int inningsId = ScoreSheetDataStore.getCurrentInningsId();
		if (inningsId != 4) {
			ScoreSheetDataStore.createNewInnings(teamId, this);
			setTeamNames();
			fullReset();
		}
	}

	int clickedValue = 0;

	boolean isWicketClick = false;
	boolean isWideClick = false;
	boolean isNoBallClick = false;
	boolean isByeClick = false;
	boolean isLegByeClick = false;

	public void autoScoreClick(View v) {

		switch (v.getId()) {

		case R.id.btn_run0:
			clickedValue = 0;
			break;
		case R.id.btn_run1:
			clickedValue = 1;
			break;
		case R.id.btn_run2:
			clickedValue = 2;
			break;
		case R.id.btn_run3:
			clickedValue = 3;
			break;
		case R.id.btn_run4:
			clickedValue = 4;
			break;

		case R.id.btn_run6:
			clickedValue = 6;
			break;
		}

		saveClick(v);
	}

	public void wideClick(View v) {
		isWideClick = true;
		saveClick(v);
	}

	public void wicketClick(View v) {
		showCustomDialog(DialogWindowType.WicketFall);
	}

	public void noBallClick(View v) {
		isNoBallClick = true;
		showCustomDialog(DialogWindowType.ExtrasDialog);
	}

	public void othersClick(View v) {
		showCustomDialog(DialogWindowType.ExtrasDialog);
	}

	public void saveClick(View v) {

		isWicketClick = (wicketfall != null);
		Boolean isOverChange = ScoreSheetDataStore.recordScore(clickedValue,
				isWideClick, isNoBallClick, isByeClick, isWicketClick,
				isLegByeClick, wicketfall);

		if (!ScoreSheetDataStore.isOversCompleted()) {
			if (isOverChange) {
				showCustomDialog(DialogWindowType.SelectBowlerWithNoCancel);
			}

			// New Wicket
			if (isWicketClick) {

				long batsmanId = wicketfall.getBatsmanId();

				if (ScoreSheetDataStore.Batsman1.PlayerId == batsmanId) {
					ScoreSheetDataStore.Batsman1
							.setWicketFallDetails(wicketfall);
					showCustomDialog(DialogWindowType.SelectBatsman1WithNoCancel);

				} else {
					ScoreSheetDataStore.Batsman2
							.setWicketFallDetails(wicketfall);
					showCustomDialog(DialogWindowType.SelectBatsman2WithNoCancel);
				}

			}
		}

		fullReset();
	}

	private void fullReset() {
		final TextView tv_score = (TextView) findViewById(R.id.batting_team1_score);

		tv_score.setText(ScoreSheetDataStore.getPlayingTeamScore());

		calculateRunRate();
		Reset();
		PopulateLastTenBalls();
		fillBatsMan();
		fillBowler();
	}

	private void PopulateLastTenBalls() {

		final LinearLayout ll_overs = (LinearLayout) findViewById(R.id.ll_oversdetails);

		final LinearLayout ll_runs = (LinearLayout) findViewById(R.id.ll_rundetails);

		ll_overs.removeAllViews();
		ll_runs.removeAllViews();

		List<BallByBall> allBalls = ScoreSheetDataStore.getLastTenBallDetails();
		for (BallByBall ball : allBalls) {

			LayoutParams lparams = new LayoutParams(0,
					LayoutParams.WRAP_CONTENT, 0.083f);

			TextView over = new TextView(this);
			over.setLayoutParams(lparams);
			over.setBackgroundColor(Color.RED);
			over.setText(ball.getOver());
			over.setTextSize(19f);

			ll_overs.addView(over);

			LayoutParams lparams1 = new LayoutParams(0,
					LayoutParams.WRAP_CONTENT, 0.083f);
			TextView run = new TextView(this);
			run.setLayoutParams(lparams1);
			run.setText(ball.getBallDetails());
			run.setTextSize(19f);
			ll_runs.addView(run);

		}

	}

	private void calculateRunRate() {

		final TextView tv_runRate = (TextView) findViewById(R.id.batting_summary);

		tv_runRate.setText(Html.fromHtml(ScoreSheetDataStore.getMatchStats()));
	}

	private void Reset() {
		isWicketClick = false;
		isWideClick = false;
		isNoBallClick = false;
		isByeClick = false;
		isLegByeClick = false;
		clickedValue = 0;
		wicketfallFeilderId = 0;
		wicketfall = null;

		LinearLayout scoreControls = (LinearLayout) findViewById(R.id.ll_scoringControls);

		if (ScoreSheetDataStore.isOversCompleted()) {
			scoreControls.setVisibility(View.INVISIBLE);
		} else {
			scoreControls.setVisibility(View.VISIBLE);
		}

	}

	private void enableUndoButton(Boolean isEnabled) {
		Button save = (Button) findViewById(R.id.btn_undo);
		save.setEnabled(isEnabled);
	}

	public void clearClick(View v) {
		Reset();
	}

	public void undoClick(View v) {

		ScoreSheetDataStore.removeLastRecordedAction();

		fullReset();

	}

	@SuppressWarnings("deprecation")
	private void showCustomDialog(int id) {
		removeDialog(id);
		showDialog(id);
	}

	public void changeBatsmanClick(View v) {

		if (!ScoreSheetDataStore.isOversCompleted()) {
			if (v.getId() == R.id.changeBatsman1) {
				showCustomDialog(DialogWindowType.SelectBatsman1);
			} else {
				showCustomDialog(DialogWindowType.SelectBatsman2);
			}
		}

	}

	public void changeOnstrikebatsman(View v) {
		if (!ScoreSheetDataStore.isOversCompleted()) {
			ScoreSheetDataStore.switchBatsman();
			fillBatsMan();
		}
	}

	public void changeBowlerClick(View v) {
		if (!ScoreSheetDataStore.isOversCompleted()) {
			showCustomDialog(DialogWindowType.SelectBowler);
		}
	}

	public void onStartTrackingTouch(SeekBar arg0) {

	}

	public void onStopTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {

	}

}