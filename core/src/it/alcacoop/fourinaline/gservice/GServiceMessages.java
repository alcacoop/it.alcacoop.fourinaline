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

package it.alcacoop.fourinaline.gservice;

public interface GServiceMessages {
  final static int GSERVICE_CONNECTED = 1;
  final static int GSERVICE_READY = 2;
  final static int GSERVICE_HANDSHAKE = 3;
  final static int GSERVICE_MOVE = 6;
  final static int GSERVICE_BOARD = 7;
  final static int GSERVICE_INIT_RATING = 8;
  
  final static int GSERVICE_PING = 70;
  final static int GSERVICE_CHATMSG = 90;
  final static int GSERVICE_ABANDON = 97;
  final static int GSERVICE_ERROR = 98;
  final static int GSERVICE_BYE = 99;
}
