package it.alcacoop.fourinaline.utils;

import it.alcacoop.fourinaline.FourInALine;
import it.alcacoop.fourinaline.logic.MatchState;


public class ELORatingManager {
  
  private final static String SINGLE_BOARD = "CgkItK3my54ZEAIQHA";
  private final static String MULTI_BOARD = "CgkItK3my54ZEAIQHQ";
  
  private final static double CONVERT_ADDENDUM = 1500.00;
  private double matchValue;

  private static ELORatingManager instance;
  private double currentRating = 0.00; // in ELO
  private double opponentRating = 0.00; // in ELO
  
  private ELORatingManager() {}
  
  public synchronized static ELORatingManager getInstance() {
    if (instance == null) instance = new ELORatingManager();
    return instance;
  }
  
  public void setRatings(double opponentRating) {
    if (MatchState.matchType == 2) {
      this.opponentRating = opponentRating + CONVERT_ADDENDUM;
      this.currentRating = Double.parseDouble(FourInALine.Instance.gameOptionPrefs.getString("MULTIBOARD", "0")) + CONVERT_ADDENDUM;
    } else if (MatchState.matchType == 0) {
      this.opponentRating = opponentRating;
      this.currentRating = Double.parseDouble(FourInALine.Instance.gameOptionPrefs.getString("SINGLEBOARD", "0")) + CONVERT_ADDENDUM;
    }
  }

  public void syncLeaderboards() {
    long score = (long)(Double.parseDouble(FourInALine.Instance.gameOptionPrefs.getString("MULTIBOARD", "0")) * 100);
    
    if (score>0)
      FourInALine.Instance.nativeFunctions.gserviceSubmitRating(score, MULTI_BOARD);
    
    score = (long)(Double.parseDouble(FourInALine.Instance.gameOptionPrefs.getString("SINGLEBOARD", "0")) * 100);
    if (score>0)
      FourInALine.Instance.nativeFunctions.gserviceSubmitRating(score, SINGLE_BOARD);
  }

  
  public void updateRating(int server, double increment) {
    FourInALine.Instance.gameOptionPrefs.flush();
    FourInALine.Instance.nativeFunctions.gserviceUpdateState();
  }
  
  public void updateRating(boolean youWin) {
    if (!youWin) return;
    
    int matchLevel = MatchState.nMatchTo;
    double wp = 1/(Math.pow(10, (Math.abs(currentRating - opponentRating) * Math.sqrt(matchLevel)/2000)) + 1);
    matchValue = 4*Math.sqrt(matchLevel);

    if (currentRating <= opponentRating) {
      currentRating += matchValue * (1-wp);
    }
    else {
      currentRating += matchValue * wp;
    }
    updatePreferences(Math.round((currentRating - CONVERT_ADDENDUM) * 100) / 100d );
    long score = (long)((currentRating - CONVERT_ADDENDUM) * 100);

    if (MatchState.matchType == 2) {
      FourInALine.Instance.nativeFunctions.gserviceSubmitRating(score, MULTI_BOARD);
    } else if (MatchState.matchType == 0) {
      FourInALine.Instance.nativeFunctions.gserviceSubmitRating(score, SINGLE_BOARD);
    }
  }
  
  private void updatePreferences(double newRating) {
    if (newRating<0) newRating=0.00; //FIX ON OLD RATING CALCULATOR
    if (MatchState.matchType == 3) {
      FourInALine.Instance.gameOptionPrefs.putString("MULTIBOARD", newRating + "");
    } else if (MatchState.matchType == 0) {
      FourInALine.Instance.gameOptionPrefs.putString("SINGLEBOARD", newRating + "");
    }
    
    FourInALine.Instance.gameOptionPrefs.flush();
    FourInALine.Instance.nativeFunctions.gserviceUpdateState();
  }

}
