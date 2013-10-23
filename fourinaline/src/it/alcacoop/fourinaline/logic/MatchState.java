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

public class MatchState {

  public static int mCount = 0; // contatore mosse effettuate

  public static int AILevel = 1; // livello scelto dal giocatore
  public static int whoStart = 2; // 1=PLAYER1, 2=AI|REMOTE

  public static int nMatchTo = 1;
  public static boolean firstGame = true; // numero di game giocati in un match

  public static int currentPlayer = 1; // giocatore corrente 1=self 2=opponent

  public static int[] anScore = { 0, 0 }; // MATCH SCORES
  public static int matchType = 0; // -1=SIMULATOR, 0=SINGLE PLAYER, 1=TWO PLAYERS, 2=GSERVICE
  public static int winner = -1; // -1= partina in corso, 0=TIE, 1=HUMAN, 2=AI
}
