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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.Timer;
import java.util.TimerTask;

import it.alcacoop.fourinaline.actors.UIDialog;
import it.alcacoop.fourinaline.fsm.FSM.Events;
import it.alcacoop.fourinaline.fsm.FSM.States;
import it.alcacoop.fourinaline.gservice.GServiceClient;
import it.alcacoop.fourinaline.layers.GameScreen;
import it.alcacoop.fourinaline.logic.MatchState;
import it.alcacoop.fourinaline.utils.AppDataManager;
import it.alcacoop.fourinaline.utils.ELORatingManager;

@SuppressLint("NewApi")
public class MainActivity extends GServiceApplication implements NativeFunctions, OnEditorActionListener {
  private int appVersionCode;
  private AdView adView;
  private View chatBox;
  private InterstitialAd interstitial;
  private View gameView;
  private Timer adsTimer;
  private TimerTask adsTask;


  @SuppressLint("InflateParams")
  @SuppressWarnings("deprecation")
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
    //cfg.useGLSurfaceView20API18 = true;
    gameView = initializeForView(new FourInALine(this), cfg);

    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    RelativeLayout layout = new RelativeLayout(this);

    /** ADS INITIALIZATION **/
    PrivateDataManager.initData();
    PrivateDataManager.createBillingData(this);
    adView = new AdView(this);
    adView.setAdUnitId(PrivateDataManager.ads_id);

    if (isTablet(this))
      adView.setAdSize(AdSize.SMART_BANNER);
    else
      adView.setAdSize(AdSize.BANNER);
    adView.setVisibility(View.GONE);

    if (!isProVersion())
      adView.loadAd(new AdRequest.Builder().build());

    interstitial = new InterstitialAd(this);
    interstitial.setAdUnitId(PrivateDataManager.int_id);
    /** ADS INITIALIZATION **/

    RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
    adParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
    layout.addView(gameView);
    layout.addView(adView, adParams);

    LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    chatBox = inflater.inflate(R.layout.chat_box, null);
    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    chatBox.setVisibility(View.GONE);
    layout.addView(chatBox, params);

    /** CHATBOX DIMS **/
    Display display = getWindowManager().getDefaultDisplay();
    // rotation = display.getRotation();
    Point size = new Point();
    try {
      display.getSize(size);
    } catch (java.lang.NoSuchMethodError ignore) { // Older device
      size.x = display.getWidth();
      size.y = display.getHeight();
    }
    int width = size.x;
    View s1 = chatBox.findViewById(R.id.space1);
    View s2 = chatBox.findViewById(R.id.space2);
    View s3 = chatBox.findViewById(R.id.chat_content);
    ViewGroup.LayoutParams pars = s1.getLayoutParams();
    pars.width = Math.round(width * 0.15f) + 7;
    s1.setLayoutParams(pars);
    pars = s2.getLayoutParams();
    pars.width = Math.round(width * 0.15f) + 7;
    s2.setLayoutParams(pars);
    pars = s3.getLayoutParams();
    FourInALine.chatHeight = pars.height;
    pars.width = Math.round(width * 0.7f) - 14;
    s3.setLayoutParams(pars);
    EditText target = (EditText)chatBox.findViewById(R.id.message);
    target.setOnEditorActionListener(this);
    /** CHATBOX DIMS **/

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
              if ((!isProVersion()) && (!interstitial.isLoaded())) {
                interstitial.loadAd(new AdRequest.Builder().build());
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
      FourInALine.Instance.menuScreen.redrawMainMenu();
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
          adView.loadAd(new AdRequest.Builder().build());
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
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        synchronized (this) {
          if (interstitial.isLoaded()) {
            try {
              wait(50);
            } catch (InterruptedException e) {}
            interstitial.show();
          }
        }
      }
    });
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
    String s = new String(data);
    System.out.println("APPSTATE: RECEIVED DATA: " + s);
    AppDataManager.getInstance().loadState(data);
    ELORatingManager.getInstance().syncLeaderboards();
  }


  @Override
  byte[] onStateConflictBehaviour(byte[] localData, byte[] serverData) {
    return AppDataManager.getInstance().resolveConflict(localData, serverData);
  }

  @Override
  void onResetRoomBehaviour() {
    FourInALine.Instance.gameScreen.chatBox.hardHide();
  }


  @Override
  public void showChatBox() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        chatBox.setVisibility(View.VISIBLE);
      }
    });
  }


  @Override
  public void hideChatBox() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        EditText chat = (EditText)findViewById(R.id.message);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(chat.getWindowToken(), 0);
        chatBox.setVisibility(View.GONE);
      }
    });
  }


  @Override
  public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    sendMessage(null);
    return false;
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if ((FourInALine.Instance == null) || (FourInALine.Instance.currentScreen == null) || (FourInALine.Instance.getScreen() == null))
      return super.onKeyDown(keyCode, event);
    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
      adjustFocus();
      FourInALine.Instance.gameScreen.chatBox.hide();
    }
    return super.onKeyDown(keyCode, event);
  }

  public void clearMessage(View v) {
    EditText chat = (EditText)findViewById(R.id.message);
    chat.setText("");
  }

  public void sendMessage(View v) {
    EditText chat = (EditText)findViewById(R.id.message);
    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(chat.getWindowToken(), 0);
    Editable msg = chat.getText();
    if (msg.toString().length() > 0) {
      chat.setText("");
      FourInALine.Instance.appendChatMessage(msg.toString(), true);
    }
    adjustFocus();
  }

  private void adjustFocus() {
    gameView.setFocusableInTouchMode(true);
    gameView.requestFocus();
  }


  @Override
  public void onInvitationRemoved(String arg0) {}

}
