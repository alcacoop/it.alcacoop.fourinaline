package it.alcacoop.fourinaline.utils;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.alcacoop.fourinaline.FourInALine;
import it.alcacoop.fourinaline.logic.MatchState;

public class AchievementsManager {

  public static final Map<String, String> achievMap;
  static
  {
    achievMap = new HashMap<String, String>();

    // 7x6x4 (Standard variant)
    achievMap.put("STANDARD_BEGINNER", "CgkItK3my54ZEAIQAQ"); // 5 games at level BEGINNER
    achievMap.put("STANDARD_CASUAL", "CgkItK3my54ZEAIQAg"); // 5 games at level CASUAL
    achievMap.put("STANDARD_INTERMEDIATE", "CgkItK3my54ZEAIQAw"); // 5 games at level INTERMEDIATE
    achievMap.put("STANDARD_ADVANCED", "CgkItK3my54ZEAIQBA"); // 5 games at level ADVANCED
    achievMap.put("STANDARD_EXPERT", "CgkItK3my54ZEAIQBQ"); // 5 games at level EXPERT

    // 9x7x5 (Bigger variant)
    achievMap.put("BIGGER_BEGINNER", "CgkItK3my54ZEAIQBg"); // 5 games at level BEGINNER
    achievMap.put("BIGGER_CASUAL", "CgkItK3my54ZEAIQBw"); // 5 games at level CASUAL
    achievMap.put("BIGGER_INTERMEDIATE", "CgkItK3my54ZEAIQCA"); // 5 games at level INTERMEDIATE
    achievMap.put("BIGGER_ADVANCED", "CgkItK3my54ZEAIQCQ"); // 5 games at level ADVANCED
    achievMap.put("BIGGER_EXPERT", "CgkItK3my54ZEAIQCg"); // 5 games at level EXPERT

    // matchTo=5 7x6x4 (Standard variant)
    achievMap.put("STANDARD_TOURNAMENT_NOVICE", "CgkItK3my54ZEAIQCw"); // 5 point match at level BEGINNER
    achievMap.put("STANDARD_TOURNAMENT_CASUAL", "CgkItK3my54ZEAIQDA"); // 5 point match at level CASUAL
    achievMap.put("STANDARD_TOURNAMENT_LEADER", "CgkItK3my54ZEAIQDQ"); // 5 point match at level INTERMEDIATE
    achievMap.put("STANDARD_TOURNAMENT_STAR", "CgkItK3my54ZEAIQDg"); // 5 point match at level ADVANCED
    achievMap.put("STANDARD_BIG_BOSS_OF_TOURNAMENT", "CgkItK3my54ZEAIQDw"); // 5 point match at level EXPERT

    // matchTo=5 9x7x5 (Bigger variant)
    achievMap.put("BIGGER_TOURNAMENT_NOVICE", "CgkItK3my54ZEAIQEA"); // 5 point match at level BEGINNER
    achievMap.put("BIGGER_TOURNAMENT_EXPERT", "CgkItK3my54ZEAIQEQ"); // 5 point match at level CASUAL
    achievMap.put("BIGGER_TOURNAMENT_LEADER", "CgkItK3my54ZEAIQEg"); // 5 point match at level INTERMEDIATE
    achievMap.put("BIGGER_TOURNAMENT_STAR", "CgkItK3my54ZEAIQEw"); // 5 point match at level ADVANCED
    achievMap.put("BIGGER_BIG_BOSS_OF_TOURNAMENT", "CgkItK3my54ZEAIQFA"); // 5 point match at level EXPERT

    // multiplayer
    achievMap.put("SOCIAL_NEWBIE", "CgkItK3my54ZEAIQFQ"); // invite 5 different
    achievMap.put("SOCIAL_PROUD", "CgkItK3my54ZEAIQFg"); // invite 10 different
    achievMap.put("SOCIAL_ADDICTED", "CgkItK3my54ZEAIQFw"); // invite 20 different

    achievMap.put("MULTIPLAYER_TURTLE", "CgkItK3my54ZEAIQGA"); // 3 games
    achievMap.put("MULTIPLAYER_RABBIT", "CgkItK3my54ZEAIQGQ"); // 5 games
    achievMap.put("MULTIPLAYER_DOBERMANN", "CgkItK3my54ZEAIQGg"); // 10 games
    achievMap.put("MULTIPLAYER_TIGER", "CgkItK3my54ZEAIQGw"); // 20 games
  }

  public Preferences prefs;
  private static AchievementsManager instance;
  public static ArrayList<String> opponents_played;

  @SuppressWarnings("unchecked")
  private AchievementsManager() {
    prefs = Gdx.app.getPreferences("Achievemnts");
    String currentString = prefs.getString("OPPONENTS", "{}");
    Json json = new Json();
    if (currentString.equals("{}"))
      opponents_played = new ArrayList<String>();
    else
      opponents_played = json.fromJson(ArrayList.class, currentString);
  }

  public static synchronized AchievementsManager getInstance() {
    if (instance == null) instance = new AchievementsManager();
    return instance;
  }

  public void checkAchievements(boolean youWin) {
    switch (MatchState.matchType) {
    case 0:
      // Single player
      checkSinglePlayerAchievements(youWin);
      break;
      case 2:
      // Gservice
      checkMultiplayerAchievements(youWin);
    default:
      break;
    }
  }
  
  public void checkSocialAchievements(String opponent_player_id) {
    if (!opponents_played.contains(opponent_player_id)) {
      opponents_played.add(opponent_player_id);
      
      FourInALine.Instance.nativeFunctions.gserviceUpdateAchievement(achievMap.get("SOCIAL_NEWBIE"), 1);
      FourInALine.Instance.nativeFunctions.gserviceUpdateAchievement(achievMap.get("SOCIAL_PROUD"), 1);
      FourInALine.Instance.nativeFunctions.gserviceUpdateAchievement(achievMap.get("SOCIAL_ADDICTED"), 1);
      
      Json json = new Json();
      prefs.putString("OPPONENTS", json.toJson(opponents_played));
      prefs.flush();
      FourInALine.Instance.nativeFunctions.gserviceUpdateState();
    }
  }

  /**
   * START PRIVATE METHODS
   */
  private void checkSinglePlayerAchievements(boolean youWin) {
    if (!youWin) return;

    switch (MatchState.nMatchTo) {
      case 5:
        FourInALine.Instance.nativeFunctions.gserviceUpdateAchievement(getSingleAchievementByGameVariant(), 1);
        if (MatchState.anScore[0] >= MatchState.nMatchTo) {
          FourInALine.Instance.nativeFunctions.gserviceUnlockAchievement(getTournamentAchievementByGameVariant());
        }
        break;
      default:
        FourInALine.Instance.nativeFunctions.gserviceUpdateAchievement(getSingleAchievementByGameVariant(), 1);
        break;
    }
  }
  
  private void checkMultiplayerAchievements(boolean youWin) {
    if (!youWin) return;

    FourInALine.Instance.nativeFunctions.gserviceUpdateAchievement(achievMap.get("MULTIPLAYER_TURTLE"), 1);
    FourInALine.Instance.nativeFunctions.gserviceUpdateAchievement(achievMap.get("MULTIPLAYER_RABBIT"), 1);
    FourInALine.Instance.nativeFunctions.gserviceUpdateAchievement(achievMap.get("MULTIPLAYER_DOBERMANN"), 1);
    FourInALine.Instance.nativeFunctions.gserviceUpdateAchievement(achievMap.get("MULTIPLAYER_TIGER"), 1);
  }

  
  
  private String getSingleAchievementByGameVariant() {
    String prefix = "STANDARD_";
    if (Gdx.app.getPreferences("MatchOptions").getString("VARIANT", "7x6x4 (Standard)").equals("9x7x5 (Bigger)"))
      prefix = "BIGGER_";

    String id = "";
    int level = MatchState.AILevel;
    switch (level) {
      case 1:
        id = achievMap.get(prefix + "BEGINNER");
        break;
      case 2:
        id = achievMap.get(prefix + "CASUAL");
        break;
      case 3:
        id = achievMap.get(prefix + "INTERMEDIATE");
        break;
      case 4:
        id = achievMap.get(prefix + "ADVANCED");
        break;
      case 5:
        id = achievMap.get(prefix + "EXPERT");
        break;
      default:
        break;
    }
    return id;
  }
  
  private String getTournamentAchievementByGameVariant() {
    String prefix = "STANDARD_";
    if (Gdx.app.getPreferences("MatchOptions").getString("VARIANT", "7x6x4 (Standard)").equals("9x7x5 (Bigger)"))
      prefix = "BIGGER_";

    String id = "";
    int level = MatchState.AILevel;
    switch (level) {
      case 1:
        id = achievMap.get(prefix + "TOURNAMENT_NOVICE");
        break;
      case 2:
        id = achievMap.get(prefix + "TOURNAMENT_CASUAL");
        break;
      case 3:
        id = achievMap.get(prefix + "TOURNAMENT_LEADER");
        break;
      case 4:
        id = achievMap.get(prefix + "TOURNAMENT_STAR");
        break;
      case 5:
        id = achievMap.get(prefix + "BIG_BOSS_OF_TOURNAMENT");
        break;
      default:
        break;
    }
    return id;
  }

}
