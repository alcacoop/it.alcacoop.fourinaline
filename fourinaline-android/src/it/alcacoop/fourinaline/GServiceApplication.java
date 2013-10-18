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
import android.content.Intent;

import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.achievement.OnAchievementUpdatedListener;
import com.google.android.gms.games.leaderboard.OnScoreSubmittedListener;
import com.google.android.gms.games.leaderboard.SubmitScoreResult;
import com.google.android.gms.games.multiplayer.Participant;

public abstract class GServiceApplication extends BaseGServiceApplication implements GServiceInterface {

  @Override
  public String gservicePendingNotificationAreaInvitation() {
    String tmp = invitationId;
    invitationId = "";
    return tmp;
  };

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
    _gserviceAcceptInvitation(invitationId);
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

}
