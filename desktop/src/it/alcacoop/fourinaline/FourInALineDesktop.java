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

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class FourInALineDesktop implements NativeFunctions {
  private static FourInALineDesktop instance;

  public static void main(String[] args) {
    LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
    cfg.title = "fourinaline";
    cfg.useGL20 = true;
    cfg.width = 800;
    cfg.height = 480;
    instance = new FourInALineDesktop();

    new LwjglApplication(new FourInALine(instance), cfg);
  }

  @Override
  public void showAds(boolean show) {
  }

  @Override
  public void openURL(String url) {
  }

  @Override
  public void openURL(String url, String fallback) {
  }


  @Override
  public boolean gserviceIsSignedIn() {
    return false;
  }

  @Override
  public void gserviceStartRoom() {
  }

  @Override
  public void gserviceAcceptInvitation(String invitationId) {
  }

  @Override
  public void gserviceSendReliableRealTimeMessage(String msg) {
  }

  @Override
  public void gserviceResetRoom() {
  }

  @Override
  public void gserviceOpenLeaderboards() {
  }

  @Override
  public void gserviceOpenAchievements() {
  }

  @Override
  public void gserviceSubmitRating(long score, String board_id) {
  }

  @Override
  public void gserviceUpdateAchievement(String achievement_id, int increment) {
  }

  @Override
  public void gserviceUnlockAchievement(String achiev_id) {
  }

  @Override
  public void gserviceUpdateState() {
  }

  @Override
  public boolean isNetworkUp() {
    return false;
  }

  @Override
  public int getAppVersionCode() {
    return 0;
  }

  @Override
  public void showInterstitial() {
  }

  @Override
  public boolean isProVersion() {
    return true;
  }

  @Override
  public void inAppBilling() {
  }

  @Override
  public String gservicePendingNotificationAreaInvitation() {
    return "";
  }

  @Override
  public void hideChatBox() {
  }

  @Override
  public void showChatBox() {
  }

}
