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

package it.alcacoop.fourinaline.logic;

import it.alcacoop.fourinaline.FourInALine;
import it.alcacoop.fourinaline.fsm.FSM.Events;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.gojul.fourinaline.model.AlphaBeta;
import org.gojul.fourinaline.model.GameModel;

import com.badlogic.gdx.Gdx;

public class AIExecutor {

  private static ExecutorService dispatchExecutor;

  static {
    dispatchExecutor = Executors.newSingleThreadExecutor();
  }


  public static void getBestColIndex(final AlphaBeta alphaBeta, final GameModel gameModel) {
    dispatchExecutor.execute(new Runnable() {
      @Override
      public void run() {
        final int col = alphaBeta.getColumnIndex(gameModel, gameModel.getCurrentPlayer());
        Gdx.app.postRunnable(new Runnable() {
          @Override
          public void run() {
            FourInALine.Instance.fsm.processEvent(Events.AI_EVALUETED, col);
          }
        });
      }
    });
  }
}
