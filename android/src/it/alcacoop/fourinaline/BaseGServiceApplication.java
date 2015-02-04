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

import it.alcacoop.fourinaline.gservice.GServiceClient;
import it.alcacoop.fourinaline.util.GServiceGameHelper;
import it.alcacoop.fourinaline.utils.AchievementsManager;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.google.android.gms.appstate.AppStateManager;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMultiplayer;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;

@SuppressLint("InflateParams")
public abstract class BaseGServiceApplication extends AndroidApplication
    implements GServiceGameHelper.GameHelperListener, RealTimeMessageReceivedListener, RoomStatusUpdateListener,
    RoomUpdateListener, OnInvitationReceivedListener, RealTimeMultiplayer.ReliableMessageSentCallback
{


  protected Preferences prefs;
  protected GServiceGameHelper gHelper;
  protected static int APP_DATA_KEY = 0;
  protected boolean gConnecting = false;

  protected String mRoomId = null;
  protected String mMyId = null;
  protected ArrayList<Participant> mParticipants = null;
  protected boolean meSentInvitation;

  ProgressDialog mProgressDialog = null;
  protected static int RC_SELECT_PLAYERS = 6000;
  protected static int RC_WAITING_ROOM = 6001;
  protected static int RC_LEADERBOARD = 6002;
  protected static int RC_ACHIEVEMENTS = 6003;

  protected static int FROM_ACHIEVEMENTS = 1;
  protected static int FROM_SCOREBOARDS = 2;

  protected String invitationId = "";

  abstract boolean shouldShowInvitationDialog();

  abstract void onRoomConnectedBehaviour();

  abstract void onLeaveRoomBehaviour(int reason);

  abstract void onRTMessageReceivedBehaviour(String msg);

  abstract void onErrorBehaviour(String msg);

  abstract void onStateLoadedBehaviour(byte[] data);

  abstract byte[] onStateConflictBehaviour(byte[] localData, byte[] serverData);

  abstract void onResetRoomBehaviour();

  public void onStateConflict(AppStateManager.StateConflictResult conflictResult) {
    AppStateManager.resolve(gHelper.getApiClient(), conflictResult.getStateKey(), conflictResult.getResolvedVersion(),
        onStateConflictBehaviour(conflictResult.getLocalData(), conflictResult.getServerData()));
  }

  public void onStateLoaded(int statusCode, int stateKey, byte[] data) {

    if (statusCode == GServiceClient.STATUS_OK) { // OK
      onStateLoadedBehaviour(data);
    }
  }

  @Override
  public void onRealTimeMessageSent(int statusCode, int token, String recipientParticipantId) {
    if (statusCode != GServiceClient.STATUS_OK) {
      onLeaveRoomBehaviour(GServiceClient.STATUS_NETWORK_ERROR_OPERATION_FAILED);
    }
  }


  @Override
  public void onInvitationReceived(Invitation invitation) {
    if (shouldShowInvitationDialog()) {
      Games.RealTimeMultiplayer.declineInvitation(gHelper.getApiClient(), invitation.getInvitationId());
      return;
    }
    gserviceInvitationReceived(invitation.getInviter().getIconImageUri(), invitation.getInviter().getDisplayName(), invitation.getInvitationId());
  }


  @Override
  public void onJoinedRoom(int arg0, Room room) {
    if (room == null) {
      hideProgressDialog();
      onErrorBehaviour("Invalid invitation");
    } else {
      updateRoom(room);
      gConnecting = true;
    }
  }

  @Override
  public void onLeftRoom(int arg0, String arg1) {
    System.out.println("---> P2P LEFT ROOM");
  }

  @Override
  public void onRoomConnected(int arg0, Room room) {
    System.out.println("GSERVICE onRoomConnected");
    hideProgressDialog();
    updateRoom(room);
    onRoomConnectedBehaviour();
    gConnecting = false;
  }

  @Override
  public void onRoomCreated(int statusCode, Room room) {
    if (statusCode != GServiceClient.STATUS_OK) {
      hideProgressDialog();
      onErrorBehaviour("Unknown error");
      return;
    }
    mRoomId = room.getRoomId();
    meSentInvitation = true;
    Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(gHelper.getApiClient(), room, Integer.MAX_VALUE);
    startActivityForResult(i, RC_WAITING_ROOM);
  }

  @Override
  public void onConnectedToRoom(Room room) {
    mParticipants = room.getParticipants();
    mMyId = room.getParticipantId(Games.Players.getCurrentPlayerId(gHelper.getApiClient()));
    updateRoom(room);
    String opponent_player_id;

    SecureRandom rdm = new SecureRandom();
    String sRdm = new BigInteger(130, rdm).toString(32);

    if (mParticipants.get(0).getParticipantId() == mMyId) {
      if (mParticipants.get(1).getPlayer() == null)
        opponent_player_id = sRdm;
      else
        opponent_player_id = mParticipants.get(1).getPlayer().getPlayerId();
    } else {
      if (mParticipants.get(0).getPlayer() == null)
        opponent_player_id = sRdm;
      else
        opponent_player_id = mParticipants.get(0).getPlayer().getPlayerId();
    }
    if (meSentInvitation)
      AchievementsManager.getInstance().checkSocialAchievements(opponent_player_id);
  }


  @Override
  public void onDisconnectedFromRoom(Room room) {
    System.out.println("---> P2P DISCONNECTED FROM ROOM");
  }

  @Override
  public void onPeerDeclined(Room room, List<String> arg1) {
    updateRoom(room);
  }

  @Override
  public void onPeerInvitedToRoom(Room room, List<String> arg1) {
    updateRoom(room);
  }

  @Override
  public void onPeerJoined(Room room, List<String> arg1) {
    updateRoom(room);
  }

  @Override
  public void onPeerLeft(Room room, List<String> arg1) {
    System.out.println("---> P2P PEER LEFT");
    if (gConnecting) {
      hideProgressDialog();
      _gserviceResetRoom();
      // UIDialog.getFlashDialog(Events.NOOP, "Error: peer left the room");
      updateRoom(room);
    }
  }

  @Override
  public void onPeersConnected(Room room, List<String> arg1) {
    updateRoom(room);
  }


  @Override
  public void onPeersDisconnected(Room room, List<String> arg1) {
    System.out.println("---> P2P PEER DISCONNECTED");
    onLeaveRoomBehaviour(GServiceClient.STATUS_OK);
    updateRoom(room);
  }

  @Override
  public void onRoomAutoMatching(Room room) {
    updateRoom(room);
  }

  @Override
  public void onRoomConnecting(Room room) {
    updateRoom(room);
  }

  @Override
  public void onRealTimeMessageReceived(RealTimeMessage rtm) {
    byte[] buf = rtm.getMessageData();
    String s = new String(buf);
    System.out.println("GSERVICE RECEIVED: " + s);
    onRTMessageReceivedBehaviour(s);
  }

  @Override
  public void onSignInFailed() {}

  @Override
  public void onSignInSucceeded() {
    prefs.putBoolean("ALREADY_SIGNEDIN", true);
    prefs.flush();
    Games.Invitations.registerInvitationListener(gHelper.getApiClient(), this);

    System.out.println("===> LOADING APPSTATE");
    AppStateManager.load(gHelper.getApiClient(), APP_DATA_KEY).setResultCallback(
        new ResultCallback<AppStateManager.StateResult>() {
          @Override
          public void onResult(AppStateManager.StateResult result) {
            AppStateManager.StateConflictResult conflictResult = result.getConflictResult();
            AppStateManager.StateLoadedResult loadedResult = result.getLoadedResult();
            if (loadedResult != null) {
              onStateLoaded(loadedResult.getStatus().getStatusCode(), loadedResult.getStateKey(), loadedResult.getLocalData());
            } else if (conflictResult != null) {
              onStateConflict(conflictResult);
            }
          }
        });


    if (gHelper.hasInvitation()) {
      invitationId = gHelper.getInvitationId();
    }
  }

  public void gserviceInvitationReceived(final Uri imagesrc, final String username, final String invitationId) {
    final AlertDialog.Builder alert = new AlertDialog.Builder(this);
    final LayoutInflater inflater = this.getLayoutInflater();

    runOnUiThread(new Runnable() {

      @Override
      public void run() {
        final View myView = inflater.inflate(R.layout.dialog_invitation, null);
        alert.setView(myView).setTitle("Invitation received").setCancelable(false).setNegativeButton("Decline", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            Games.RealTimeMultiplayer.declineInvitation(gHelper.getApiClient(), invitationId);
          }
        });
        alert.setPositiveButton("Accept", null);

        final AlertDialog d = alert.create();
        d.setOnShowListener(new DialogInterface.OnShowListener() {
          @Override
          public void onShow(DialogInterface arg0) {
            ImageManager im = ImageManager.create(getApplicationContext());
            im.loadImage(((ImageView)myView.findViewById(R.id.image)), imagesrc);
            TextView tv = (TextView)myView.findViewById(R.id.text);
            tv.setText(username + " wants to play with you...");
            tv.setFocusable(true);
            tv.setFocusableInTouchMode(true);
            tv.requestFocus();
            Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
            b.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                _gserviceAcceptInvitation(invitationId);
                d.dismiss();
              }
            });
          }
        });
        d.show();
      }
    });
  }


  public void _gserviceAcceptInvitation(String invitationId) {
    RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(this);
    roomConfigBuilder.setInvitationIdToAccept(invitationId);
    roomConfigBuilder.setMessageReceivedListener(this);
    roomConfigBuilder.setRoomStatusUpdateListener(this);
    _gserviceResetRoom();
    Games.RealTimeMultiplayer.join(gHelper.getApiClient(), roomConfigBuilder.build());

    showProgressDialog();
  }


  private void updateRoom(Room room) {
    System.out.println("---> P2P UPDATE ROOM");
    if (room != null) {
      mRoomId = room.getRoomId();
      mParticipants = room.getParticipants();
    }
  }

  public void showProgressDialog() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (mProgressDialog == null) {
          if (BaseGServiceApplication.this == null)
            return;
          mProgressDialog = new ProgressDialog(BaseGServiceApplication.this) {
            int clickCount = 0;

            @Override
            public void dismiss() {
              super.dismiss();
              clickCount = 0;
            }

            @Override
            public boolean onKeyDown(int keyCode, KeyEvent event) {
              clickCount++;
              if (clickCount == 7) {
                _gserviceResetRoom();
                // TODO: SevenTimesCancelBehaviour for future use
                // FourInALine.Instance.fsm.state(States.MAIN_MENU);
                dismiss();
              }
              return false;
            }
          };
        }
        mProgressDialog.setMessage("Please wait..");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
      }
    });
  }

  public void hideProgressDialog() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (mProgressDialog != null) {
          mProgressDialog.dismiss();
          mProgressDialog = null;
        }
      }
    });
  }

  @Override
  public void onP2PConnected(String arg0) {
    System.out.println("---> P2P CONNECTED");
  }


  @Override
  public void onP2PDisconnected(String arg0) {
    System.out.println("---> P2P DISCONNECTED");
  }


  @Override
  protected void onStart() {
    super.onStart();
    prefs = Gdx.app.getPreferences("GameOptions");
    gHelper = new GServiceGameHelper(this, GServiceGameHelper.CLIENT_APPSTATE | GServiceGameHelper.CLIENT_GAMES);
    gHelper.setup(this);

    ActivityManager actvityManager = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE);
    List<RunningTaskInfo> taskInfos = actvityManager.getRunningTasks(3);
    for (RunningTaskInfo runningTaskInfo : taskInfos) {
      if (runningTaskInfo.baseActivity.getPackageName().contains("gms")) {
        gserviceSignIn();
        break;
      }
    }
    gHelper.onStart(this);
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (mRoomId != null) {
      onLeaveRoomBehaviour(GServiceClient.STATUS_REAL_TIME_INACTIVE_ROOM);
    }
    gHelper.onStop();
  }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == RC_SELECT_PLAYERS) {
      if (resultCode == RESULT_OK) {
        Bundle autoMatchCriteria = null;
        int minAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
        int maxAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
        if (minAutoMatchPlayers > 0 || maxAutoMatchPlayers > 0) {
          autoMatchCriteria = RoomConfig.createAutoMatchCriteria(minAutoMatchPlayers, maxAutoMatchPlayers, 0);
        }
        final ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);
        // create the room
        RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(this);
        rtmConfigBuilder.addPlayersToInvite(invitees);
        rtmConfigBuilder.setMessageReceivedListener(this);
        rtmConfigBuilder.setRoomStatusUpdateListener(this);
        if (autoMatchCriteria != null) {
          rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
        }
        _gserviceResetRoom();
        Games.RealTimeMultiplayer.create(gHelper.getApiClient(), rtmConfigBuilder.build());
      } else {
        hideProgressDialog();
      }
    } else if (requestCode == RC_WAITING_ROOM) {
      if (resultCode != RESULT_OK) {
        _gserviceResetRoom();
        hideProgressDialog();
      }
    } else {
      super.onActivityResult(requestCode, resultCode, data);
      gHelper.onActivityResult(requestCode, resultCode, data);
    }
  }

  public void _gserviceResetRoom() {
    onResetRoomBehaviour();
    gConnecting = false;
    meSentInvitation = false;
    if (mRoomId != null) {
      Games.RealTimeMultiplayer.leave(gHelper.getApiClient(), this, mRoomId);
      mRoomId = null;
    }
  }


  protected void gserviceSignIn() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        gHelper.beginUserInitiatedSignIn();
      }
    });
  }

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

  protected void trySignIn(final int from) {
    if ((from == FROM_ACHIEVEMENTS) || (from == FROM_SCOREBOARDS)) {
      gHelper.setListener(new GServiceGameHelper.GameHelperListener() {
        @Override
        public void onSignInSucceeded() {
          gHelper.setListener(BaseGServiceApplication.this);
          // MainActivity.this.onSignInSucceeded();
          if (from == FROM_ACHIEVEMENTS)
            startActivityForResult(Games.Achievements.getAchievementsIntent(gHelper.getApiClient()), RC_ACHIEVEMENTS);
          else
            startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(gHelper.getApiClient()), RC_LEADERBOARD);
        }

        @Override
        public void onSignInFailed() {
          onErrorBehaviour("Login error");
        }
      });
    }
    gserviceSignIn();
  }

}
