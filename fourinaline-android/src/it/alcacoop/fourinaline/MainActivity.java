package it.alcacoop.fourinaline;

import it.alcacoop.fourinaline.actors.UIDialog;
import it.alcacoop.fourinaline.fsm.FSM.Events;
import it.alcacoop.fourinaline.gservice.GServiceClient;
import it.alcacoop.fourinaline.util.GServiceGameHelper;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.achievement.OnAchievementUpdatedListener;
import com.google.android.gms.games.leaderboard.OnScoreSubmittedListener;
import com.google.android.gms.games.leaderboard.SubmitScoreResult;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;

public class MainActivity extends GServiceApplication implements OnEditorActionListener, SensorEventListener, NativeFunctions {
  private int appVersionCode;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
    cfg.useGL20 = true;

    initialize(new FourInALine(this), cfg);

    prefs = Gdx.app.getPreferences("GameOptions");
    gHelper = new GServiceGameHelper(this, false);
    gHelper.setup(this, GServiceGameHelper.CLIENT_APPSTATE | GServiceGameHelper.CLIENT_GAMES);
    // gserviceSignIn();
    ActivityManager actvityManager = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE);
    List<RunningTaskInfo> taskInfos = actvityManager.getRunningTasks(3);
    for (RunningTaskInfo runningTaskInfo : taskInfos) {
      if (runningTaskInfo.baseActivity.getPackageName().contains("gms")) {
        gserviceSignIn();
        break;
      }
    }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onSensorChanged(SensorEvent event) {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    // if (requestCode == PrivateDataManager.RC_REQUEST) {
    // if (resultCode != 10000) {
    // if (isProVersion()) {
    // adView.setVisibility(View.GONE);
    // if (adsTimer != null) {
    // adsTimer.cancel();
    // adsTimer.purge();
    // PrivateDataManager.destroyBillingData(); // Memory Optimization!
    // }
    // GnuBackgammon.Instance.menuScreen.redraw();
    // }
    // } else { // ERROR!
    // System.out.println("BILLING: 10000");
    // PrivateDataManager.destroyBillingData();
    // PrivateDataManager.createBillingData(this);
    // }
    // } else
    System.out.println("GSERVICE:------ activityResult requestCode:" + requestCode);
    if (requestCode == RC_SELECT_PLAYERS) {
      if (resultCode == RESULT_OK) {
        Bundle autoMatchCriteria = null;
        int minAutoMatchPlayers = data.getIntExtra(GamesClient.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
        int maxAutoMatchPlayers = data.getIntExtra(GamesClient.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
        if (minAutoMatchPlayers > 0 || maxAutoMatchPlayers > 0) {
          autoMatchCriteria = RoomConfig.createAutoMatchCriteria(minAutoMatchPlayers, maxAutoMatchPlayers, 0);
        }
        final ArrayList<String> invitees = data.getStringArrayListExtra(GamesClient.EXTRA_PLAYERS);
        // create the room
        RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(this);
        rtmConfigBuilder.addPlayersToInvite(invitees);
        rtmConfigBuilder.setMessageReceivedListener(this);
        rtmConfigBuilder.setRoomStatusUpdateListener(this);
        if (autoMatchCriteria != null) {
          rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
        }
        gHelper.getGamesClient().createRoom(rtmConfigBuilder.build());
      } else {
        hideProgressDialog();
      }
    } else if (requestCode == RC_WAITING_ROOM) {
      if (resultCode != RESULT_OK) {
        gserviceResetRoom();
        hideProgressDialog();
      }
    } else {
      super.onActivityResult(requestCode, resultCode, data);
      gHelper.onActivityResult(requestCode, resultCode, data);
    }
  }

  @Override
  public void showAds(final boolean show) {
    // if (isProVersion())
    // return;
    // runOnUiThread(new Runnable() {
    // @Override
    // public void run() {
    // if (show) {
    // adView.loadAd(new AdRequest());
    // adView.setVisibility(View.VISIBLE);
    // } else {
    // adView.setVisibility(View.GONE);
    // }
    // }
    // });
  }

  @Override
  public void openURL(String url) {
    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    startActivityForResult(myIntent, 1000);
  }

  @Override
  public void openURL(String url, String fallback) {
    try {
      Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
      startActivityForResult(myIntent, 1000);
    } catch (Exception e) {
      Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fallback));
      startActivityForResult(myIntent, 1000);
    }
  }

  @Override
  public String getDataDir() {
    // return data_dir;
    return null;
  }

  @Override
  public void gserviceSignIn() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        gHelper.beginUserInitiatedSignIn();
      }
    });
  }

  @Override
  public boolean gserviceIsSignedIn() {
    return gHelper.isSignedIn();
  }

  @Override
  public void gserviceStartRoom() {
    if (gHelper.getGamesClient().isConnected()) {
      showProgressDialog();
      Intent intent = gHelper.getGamesClient().getSelectPlayersIntent(1, 1);
      startActivityForResult(intent, RC_SELECT_PLAYERS);
    } else {
      gserviceGetSigninDialog(-1);
    }
  }

  @Override
  public void gserviceAcceptInvitation(String invitationId) {
    RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(MainActivity.this);
    roomConfigBuilder.setInvitationIdToAccept(invitationId);
    roomConfigBuilder.setMessageReceivedListener(MainActivity.this);
    roomConfigBuilder.setRoomStatusUpdateListener(MainActivity.this);
    gHelper.getGamesClient().joinRoom(roomConfigBuilder.build());
    showProgressDialog();
  }

  @Override
  public void gserviceSendReliableRealTimeMessage(String msg) {
    if ((mRoomId == null) || (mRoomId == "")) {
      GServiceClient.getInstance().leaveRoom(GamesClient.STATUS_NETWORK_ERROR_OPERATION_FAILED);
    } else {
      for (Participant p : mParticipants) {
        if (p.getParticipantId().equals(mMyId))
          continue;
        if (p.getStatus() != Participant.STATUS_JOINED) {
          continue;
        }

        gHelper.getGamesClient().sendReliableRealTimeMessage(this, msg.getBytes(), mRoomId, p.getParticipantId()); // .sendReliableRealTimeMessage(this, msg.getBytes(), mRoomId,
                                                                                                                   // p.getParticipantId());
      }
    }
  }

  @Override
  public void gserviceResetRoom() {
    // FourInALine.Instance.gameScreen.chatBox.hardHide();
    gConnecting = false;
    meSentInvitation = false;
    if (mRoomId != null) {
      gHelper.getGamesClient().leaveRoom(this, mRoomId);
      mRoomId = null;
    }
  }

  @Override
  public void gserviceOpenLeaderboards() {
    if (gserviceIsSignedIn()) {
      startActivityForResult(gHelper.getGamesClient().getAllLeaderboardsIntent(), RC_LEADERBOARD);
    } else {
      gserviceGetSigninDialog(FROM_SCOREBOARDS);
    }
  }

  @Override
  public void gserviceOpenAchievements() {
    if (gserviceIsSignedIn()) {
      startActivityForResult(gHelper.getGamesClient().getAchievementsIntent(), RC_ACHIEVEMENTS);
    } else {
      gserviceGetSigninDialog(FROM_ACHIEVEMENTS);
    }
  }

  @Override
  public void gserviceSubmitRating(long score, String board_id) {
    if (!prefs.getBoolean("ALREADY_SIGNEDIN", false))
      return;
    gHelper.getGamesClient().submitScoreImmediate(new OnScoreSubmittedListener() {

      @Override
      public void onScoreSubmitted(int arg0, SubmitScoreResult arg1) {
      }
    }, board_id, score);

  }

  @Override
  public void gserviceUpdateAchievement(String achievement_id, int increment) {
    if (achievement_id == null || achievement_id.equals("") || achievement_id=="") return;
    if (!prefs.getBoolean("ALREADY_SIGNEDIN", false) || (!gHelper.isSignedIn())) return;
    gHelper.getGamesClient().incrementAchievementImmediate(new OnAchievementUpdatedListener() {

      @Override
      public void onAchievementUpdated(int statusCode, String achievement_id) {
      }
    }, achievement_id, increment);
  }

  @Override
  public void gserviceUnlockAchievement(String achievement_id) {
    if (achievement_id == null || achievement_id.equals("") || achievement_id=="") return;
    if (!prefs.getBoolean("ALREADY_SIGNEDIN", false) || (!gHelper.isSignedIn())) return;
    gHelper.getGamesClient().unlockAchievementImmediate(new OnAchievementUpdatedListener() {

      @Override
      public void onAchievementUpdated(int statusCode, String arg1) {
      }
    }, achievement_id);
  }

  @Override
  public void gserviceUpdateState() {
    // if (gHelper.isSignedIn()) {
    // // deleteAppState();
    // gHelper.getAppStateClient().updateState(APP_DATA_KEY, AppDataManager.getInstance().getBytes());
    // }
  }

  /*  
  private void deleteAppState() {
    if (gHelper.isSignedIn()) {
      gHelper.getAppStateClient().deleteState(new OnStateDeletedListener() {

        @Override
        public void onStateDeleted(int arg0, int arg1) {
          System.out.println("GSERVICE STATE DELETED");
        }
      }, APP_DATA_KEY);
    }
  }
  */

  @Override
  public void gserviceGetSigninDialog(final int from) {
    final AlertDialog.Builder alert = new AlertDialog.Builder(this);
    final LayoutInflater inflater = this.getLayoutInflater();

    runOnUiThread(new Runnable() {
      @SuppressLint("NewApi")
      @Override
      public void run() {
        final View myView = inflater.inflate(R.layout.dialog_gplus, null);
        alert.setView(myView).setTitle("Signin").setCancelable(true);
        final AlertDialog d = alert.create();
        d.setOnShowListener(new DialogInterface.OnShowListener() {
          @Override
          public void onShow(DialogInterface arg0) {
            String msg = "";
            TextView v = (TextView)d.findViewById(R.id.login_text);
            if (prefs.getBoolean("ALREADY_SIGNEDIN", false)) {
              msg = "Please sign in on Google Play Game Services to enable this feature";
            } else {
              msg = "Please sign in, Google will ask you to accept requested permissions and configure " + "sharing settings up to two times. This may take few minutes..";
            }
            v.setText(msg);
            com.google.android.gms.common.SignInButton b = (com.google.android.gms.common.SignInButton)d.findViewById(R.id.sign_in_button);
            b.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                d.dismiss();
                trySignIn(from);
              }
            });
          }
        });
        d.show();
      }
    });
  }

  public void trySignIn(final int from) {
    if ((from == FROM_ACHIEVEMENTS) || (from == FROM_SCOREBOARDS)) {
      gHelper.setListener(new GServiceGameHelper.GameHelperListener() {
        @Override
        public void onSignInSucceeded() {
          gHelper.setListener(MainActivity.this);
          MainActivity.this.onSignInSucceeded();
          if (from == FROM_ACHIEVEMENTS)
            startActivityForResult(gHelper.getGamesClient().getAchievementsIntent(), RC_ACHIEVEMENTS);
          else startActivityForResult(gHelper.getGamesClient().getAllLeaderboardsIntent(), RC_LEADERBOARD);
        }

        @Override
        public void onSignInFailed() {
          UIDialog.getFlashDialog(Events.NOOP, "Login error");
        }
      });
    }
    gserviceSignIn();
  }

  @Override
  public boolean isNetworkUp() {
    ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    return activeNetworkInfo != null;
  }

  @Override
  public int getAppVersionCode() {
    return appVersionCode;
  }

}
