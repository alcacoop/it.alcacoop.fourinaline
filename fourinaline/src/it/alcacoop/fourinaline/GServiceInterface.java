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

public interface GServiceInterface {
  public boolean gserviceIsSignedIn();
  public void gserviceStartRoom();
  public void gserviceAcceptInvitation(String invitationId);
  public void gserviceSendReliableRealTimeMessage(String msg);
  public void gserviceResetRoom();
  public void gserviceOpenLeaderboards();
  public void gserviceOpenAchievements();
  public void gserviceSubmitRating(long score, String board_id);
  public void gserviceUpdateAchievement(String achievement_id, int increment);
  public void gserviceUnlockAchievement(String achiev_id);
  public void gserviceUpdateState();
  public String gservicePendingNotificationAreaInvitation();

}
