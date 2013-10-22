/**
 ##################################################################
 #                     FOUR IN A LINE MOBILE                      #
 ##################################################################
 #                                                                #
 #  Authors: Domenico Martella - Francesco Valente                #
 #  E-mail: info@alcacoop.it                                      #
 #  Date:   18/10/2013                                            #
 #                                                                #
 ##################################################################
 #                                                                #
 #  Copyright (C) 2013   Alca Societa' Cooperativa                #
 #                                                                #
 #  This file is part of FOUR IN A LINE MOBILE.                   #
 #  FOUR IN A LINE MOBILE is free software: you can redistribute  # 
 #  it and/or modify it under the terms of the GNU General        #
 #  Public License as published by the Free Software Foundation,  #
 #  either version 3 of the License, or (at your option)          #
 #  any later version.                                            #
 #                                                                #
 #  FOUR IN A LINE MOBILE is distributed in the hope that it      #
 #  will be useful, but WITHOUT ANY WARRANTY; without even the    #
 #  implied warranty of MERCHANTABILITY or FITNESS FOR A          #
 #  PARTICULAR PURPOSE.  See the GNU General Public License       #
 #  for more details.                                             #
 #                                                                #
 #  You should have received a copy of the GNU General            #
 #  Public License v3 along with this program.                    #
 #  If not, see <http://http://www.gnu.org/licenses/>             #
 #                                                                #
 ##################################################################
 **/

package it.alcacoop.fourinaline;

import it.alcacoop.fourinaline.actors.UIDialog;
import it.alcacoop.fourinaline.fsm.FSM.Events;
import it.alcacoop.fourinaline.fsm.FSM.States;
import it.alcacoop.fourinaline.gservice.GServiceClient;
import it.alcacoop.fourinaline.layers.GameScreen;
import it.alcacoop.fourinaline.logic.MatchState;
import it.alcacoop.fourinaline.utils.AppDataManager;
import it.alcacoop.fourinaline.utils.ELORatingManager;

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

public class MainActivity extends GServiceApplication implements NativeFunctions {
  private int appVersionCode;
  private AdView adView;
  private InterstitialAd interstitial;
  private View gameView;
  private Timer adsTimer;
  private TimerTask adsTask;


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
    PrivateDataManager.initData();
    PrivateDataManager.createBillingData(this);
    if (isTablet(this))
      adView = new AdView(this, AdSize.IAB_BANNER, PrivateDataManager.ads_id);
    else
      adView = new AdView(this, AdSize.BANNER, PrivateDataManager.ads_id);
    adView.setVisibility(View.GONE);

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

  @Override
  protected void onDestroy() {
    super.onDestroy();
    PrivateDataManager.destroyBillingData();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == PrivateDataManager.RC_REQUEST) {
      if (resultCode != 10000) {
        if (isProVersion()) {
          adView.setVisibility(View.GONE);
          if (adsTimer != null) {
            adsTimer.cancel();
            adsTimer.purge();
            PrivateDataManager.destroyBillingData(); // Memory Optimization!
          }
        }
      } else { // ERROR!
        PrivateDataManager.destroyBillingData();
        PrivateDataManager.createBillingData(this);
      }
    } else
      super.onActivityResult(requestCode, resultCode, data);
  }


  @Override
  public boolean isProVersion() {
    return PrivateDataManager.msIsPremium;
  }

  @Override
  public void inAppBilling() {
    Intent myIntent = new Intent(this, PurchaseActivity.class);
    startActivityForResult(myIntent, PrivateDataManager.RC_REQUEST);
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
              wait(50);
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
  public boolean isNetworkUp() {
    ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    return activeNetworkInfo != null;
  }

  @Override
  public int getAppVersionCode() {
    return appVersionCode;
  }


  @Override
  boolean shouldShowInvitationDialog() {
    return (FourInALine.Instance.currentScreen instanceof GameScreen);
  }


  @Override
  void onRoomConnectedBehaviour() {
    MatchState.matchType = 2;
    FourInALine.Instance.fsm.state(States.GSERVICE);
  }


  @Override
  void onRTMessageReceivedBehaviour(String msg) {
    GServiceClient.getInstance().processReceivedMessage(msg);
  }


  @Override
  void onLeaveRoomBehaviour(int reason) {
    GServiceClient.getInstance().leaveRoom(reason);
  }


  @Override
  void onErrorBehaviour(String msg) {
    UIDialog.getFlashDialog(Events.NOOP, msg);
  }


  @Override
  void onStateLoadedBehaviour(byte[] data) {
    System.out.println("APPSTATE: RECEIVED DATA:" + data);
    AppDataManager.getInstance().loadState(data);
    ELORatingManager.getInstance().syncLeaderboards();
  }


  @Override
  byte[] onStateConflictBehaviour(byte[] localData, byte[] serverData) {
    return AppDataManager.getInstance().resolveConflict(localData, serverData);
  }
}
