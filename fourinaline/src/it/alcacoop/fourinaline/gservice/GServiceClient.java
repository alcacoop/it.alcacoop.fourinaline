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


import it.alcacoop.fourinaline.FourInALine;
import it.alcacoop.fourinaline.fsm.FSM.Events;
import it.alcacoop.fourinaline.fsm.FSM.States;

import java.util.concurrent.ArrayBlockingQueue;


public class GServiceClient implements GServiceMessages {

  public static GServiceClient instance;
  public GServiceNetHandler queue;
  public GServiceCookieMonster coockieMonster;
  public ArrayBlockingQueue<String> sendQueue;
  private Thread sendThread;


  private GServiceClient() {
    queue = new GServiceNetHandler();
    coockieMonster = new GServiceCookieMonster();
    sendQueue = new ArrayBlockingQueue<String>(20);

    sendThread = new Thread() {
      @Override
      public void run() {
        while (true) {
          try {
            synchronized (sendThread) {
              wait(230);
            }
            String msg = sendQueue.take();
            FourInALine.Instance.nativeFunctions.gserviceSendReliableRealTimeMessage(msg);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    };
    sendThread.start();
  }

  public static GServiceClient getInstance() {
    if (instance == null)
      instance = new GServiceClient();
    return instance;
  }


  public void connect() {
  }


  public void processReceivedMessage(String s) {
    int coockie = coockieMonster.getCookie(s);
    switch (coockie) {
      case GSERVICE_READY:
        queue.post(Events.GSERVICE_READY, null);
        // FourInALine.Instance.fsm.processEvent(Events.GSERVICE_READY, null);
        break;
      case GSERVICE_INIT_RATING:
        String chunks[] = s.split(" ");
        queue.post(Events.GSERVICE_INIT_RATING, Double.parseDouble(chunks[1]));
        break;
      case GSERVICE_HANDSHAKE:
        chunks = s.split(" ");
        queue.post(Events.GSERVICE_HANDSHAKE, Long.parseLong(chunks[1]));
        break;
      case GSERVICE_MOVE:
        chunks = s.split(" ");
        int col = Integer.parseInt(chunks[1]);
        queue.post(Events.GSERVICE_MOVES, col);
        break;
      case GSERVICE_CHATMSG:
        s = s.replace("90 ", "");
        FourInALine.Instance.fsm.processEvent(Events.GSERVICE_CHATMSG, s);
        break;
      case GSERVICE_ABANDON:
        chunks = s.split(" ");
        queue.reset();
        FourInALine.Instance.fsm.state(States.LOCAL_TURN);
        FourInALine.Instance.fsm.processEvent(Events.OPPONENT_LEAVE_OR_RESIGN, Integer.parseInt(chunks[1]));
        break;
      case GSERVICE_PING:
      case GSERVICE_ERROR:
        break;
      case GSERVICE_BYE:
        FourInALine.Instance.fsm.processEvent(Events.GSERVICE_BYE, null);
        break;
    }
  }


  public synchronized void sendMessage(String msg) {
    try {
      sendQueue.put(msg);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }


  public final static int STATUS_OK = 0;
  public final static int STATUS_NETWORK_ERROR_OPERATION_FAILED = 6;
  public final static int STATUS_REAL_TIME_INACTIVE_ROOM = 7005;

  public void leaveRoom(int code) {
    // FourInALine.Instance.nativeFunctions.gserviceResetRoom();
    switch (code) {
      case STATUS_OK:
        // opponent disconnected
        FourInALine.Instance.fsm.processEvent(Events.GSERVICE_ERROR, 0);
        break;
      case STATUS_NETWORK_ERROR_OPERATION_FAILED:
        // you disconnected
        FourInALine.Instance.fsm.processEvent(Events.GSERVICE_ERROR, 1);
        break;
      case STATUS_REAL_TIME_INACTIVE_ROOM:
        // activity stopped
        FourInALine.Instance.fsm.processEvent(Events.GSERVICE_ERROR, 2);
        break;
      default:
        FourInALine.Instance.fsm.processEvent(Events.GSERVICE_BYE, null);
        break;
    }
  }
}
