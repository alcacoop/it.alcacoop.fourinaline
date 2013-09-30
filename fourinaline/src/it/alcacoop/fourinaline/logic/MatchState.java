package it.alcacoop.fourinaline.logic;

public class MatchState {

  public static int mCount = 0; // contatore mosse effettuate

  public static int defaultStartLevel = 1; // livello di partenza
  public static int gameLevel = 1; // livello scelto dal giocatore
  public static int currentLevel = 1; // livello corrente, in generale
                                      // currentLevel=gameLevel
  public static int whoStart = 1;

  public static int nMatchTo = 1;
  public static int gamesIntoMatch = 1; // numero di game giocati in un match

  public static int currentPlayer = 1; // giocatore corrente 1=self 2=opponent

  public static int[] anScore = {0,0}; //MATCH SCORES
  public static int matchType = 0; //0=SINGLE PLAYER, 1=TWO PLAYERS, 2=GSERVICE
  public static int winner = 0; //0=TIE, 1=HUMAN, 2=AI
}
