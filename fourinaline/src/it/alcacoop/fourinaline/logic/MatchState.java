package it.alcacoop.fourinaline.logic;

public class MatchState {
  
  public static int currentLevel = 1; //ALPHABETA DEEPNESS
  public static int fMove = 1; //TURNO (1=HUMAN, 2=AI)
  public static int nMatchTo = 1; 
  public static int[] anScore = {0,0}; //MATCH SCORES
  public static int matchType = 0; //0=SINGLE PLAYER, 1=TWO PLAYERS, 2=GSERVICE
  public static int winner = 0; //0=TIE, 1=HUMAN, 2=AI
  
}
