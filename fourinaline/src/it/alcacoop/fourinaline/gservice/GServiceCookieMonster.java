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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Pattern;


public class GServiceCookieMonster implements GServiceMessages {
  private final static boolean DEBUG = false;

  private class CookieDough {
    public Pattern regex = null;
    public int message = 0;
  }

  private LinkedList<CookieDough> numericBatch;
  
  public GServiceCookieMonster() {
    prepareBatches();
  }
  
  public int fIBSCookie(String message) {
    int result = 0;
    Iterator<GServiceCookieMonster.CookieDough> iter;
    CookieDough ptr = null;

    iter = numericBatch.iterator();
    while (iter.hasNext()) {
      ptr = iter.next();
      if (ptr.regex.matcher(message).find()) {
        result = ptr.message;
        break;
      }
    }

    if (result == 0) return(0);
    
    String[] ss = ptr.regex.split(message, 2);
    if (ss.length > 1 && ss[1].length() > 0) {
      if (DEBUG) {
        System.out.println("cookie = " + result);
        System.out.println("message = '" + message + "'");
        System.out.println("Leftover = '" + ss[1] + "'");
      }
    }
    return(result);
  }



  LinkedList<CookieDough> currentBatchBuild;
  private void addDough(int msg, String re) {
    CookieDough newDough = new CookieDough();
    newDough.regex = Pattern.compile(re);
    newDough.message = msg;
    currentBatchBuild.add(newDough);
  }


  private void prepareBatches() {
    currentBatchBuild = new LinkedList<CookieDough>();
    addDough(GSERVICE_CONNECTED, "^"+GSERVICE_CONNECTED+"$");
    addDough(GSERVICE_READY, "^"+GSERVICE_READY+"$");
    addDough(GSERVICE_INIT_RATING, "^"+GSERVICE_INIT_RATING+" ");
    addDough(GSERVICE_HANDSHAKE, "^"+GSERVICE_HANDSHAKE+" ");
    addDough(GSERVICE_MOVE, "^"+GSERVICE_MOVE+" ");
    addDough(GSERVICE_BOARD, "^"+GSERVICE_BOARD+" ");
    addDough(GSERVICE_PING, "^"+GSERVICE_PING+" ");
    addDough(GSERVICE_CHATMSG, "^"+GSERVICE_CHATMSG+" ");
    addDough(GSERVICE_ABANDON, "^"+GSERVICE_ABANDON+" ");
    addDough(GSERVICE_ERROR, "^"+GSERVICE_ERROR+"$");
    addDough(GSERVICE_BYE, "^"+GSERVICE_BYE+"$");
    this.numericBatch = this.currentBatchBuild;
  }
}
