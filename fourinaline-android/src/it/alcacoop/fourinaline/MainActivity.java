package it.alcacoop.fourinaline;

import it.alcacoop.fourinaline.gservice.GServiceClient;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.ads.InterstitialAd;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.achievement.OnAchievementUpdatedListener;
import com.google.android.gms.games.leaderboard.OnScoreSubmittedListener;
import com.google.android.gms.games.leaderboard.SubmitScoreResult;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;

public class MainActivity extends GServiceApplication implements NativeFunctions {
  private int appVersionCode;
  private AdView adView;
  private InterstitialAd interstitial;
  private View gameView;
  private Timer adsTimer;
  private TimerTask adsTask;


  private class PrivateDataManager {
    static final String ads_id = "";
    static final String int_id = "";
  }


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
    cfg.useGL20 = true;
    gameView = initializeForView(new FourInALine(this), cfg);

    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    RelativeLayout layout = new RelativeLayout(this);

    /** ADS INITIALIZATION **/
    // PrivateDataManager.initData();
    if (isTablet(this))
      adView = new AdView(this, AdSize.IAB_BANNER, PrivateDataManager.ads_id);
    else
      adView = new AdView(this, AdSize.BANNER, PrivateDataManager.ads_id);
    adView.setVisibility(View.VISIBLE);

    if (!isProVersion())
      adView.loadAd(new AdRequest());
    interstitial = new InterstitialAd(this, PrivateDataManager.int_id);
    /** ADS INITIALIZATION **/

    RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
    adParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
    layout.addView(gameView);
    layout.addView(adView, adParams);

    setContentView(layout);
  }


  @Override
  protected void onResume() {
    super.onResume();
    if (!isProVersion()) {
      adsTimer = new Timer();
      adsTask = new TimerTask() {
        @Override
        public void run() {
          runOnUiThread(new Runnable() {
            public void run() {
              if ((!isProVersion()) && (!interstitial.isReady())) {
                interstitial.loadAd(new AdRequest());
              }
            }
          });
        }
      };
      adsTimer.schedule(adsTask, 0, 15000);
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (adsTimer != null) {
      adsTimer.cancel();
      adsTimer.purge();
    }
  }


  /*
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
    
  }
  */


  public boolean isProVersion() {
    return false;
  }

  private boolean isTablet(Context context) {
    boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
    boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
    return (xlarge || large);
  }

  @Override
  public void showAds(final boolean show) {
    if (isProVersion())
      return;
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (show) {
          adView.loadAd(new AdRequest());
          adView.setVisibility(View.VISIBLE);
        } else {
          adView.setVisibility(View.GONE);
        }
      }
    });
  }

  @Override
  public void showInterstitial() {
    if (isProVersion())
      return;
    if (interstitial.isReady()) {
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          synchronized (this) {
            try {
              wait(500);
            } catch (InterruptedException e) {
            }
            interstitial.show();
          }
        }
      });
    }
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
    gserviceResetRoom();
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
    _gserviceResetRoom();
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
    if (achievement_id == null || achievement_id.equals("") || achievement_id == "")
      return;
    if (!prefs.getBoolean("ALREADY_SIGNEDIN", false) || (!gHelper.isSignedIn()))
      return;
    gHelper.getGamesClient().incrementAchievementImmediate(new OnAchievementUpdatedListener() {

      @Override
      public void onAchievementUpdated(int statusCode, String achievement_id) {
      }
    }, achievement_id, increment);
  }

  @Override
  public void gserviceUnlockAchievement(String achievement_id) {
    if (achievement_id == null || achievement_id.equals("") || achievement_id == "")
      return;
    if (!prefs.getBoolean("ALREADY_SIGNEDIN", false) || (!gHelper.isSignedIn()))
      return;
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
