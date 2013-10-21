package it.alcacoop.fourinaline.utils;

import it.alcacoop.fourinaline.FourInALine;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.utils.Json;

public class AppDataManager {

  private HashMap<String, String> app_data;
  private static AppDataManager instance;
  
  public static AppDataManager getInstance() {
    if (instance==null)
      instance = new AppDataManager();
    return instance;
  }
  
  
  private AppDataManager() {
    app_data = new HashMap<String, String>();
  }
  
  public byte[] getBytes() {
    app_data.put("singleboard", FourInALine.Instance.gameOptionPrefs.getString("SINGLEBOARD", "0"));
    app_data.put("multiboard", FourInALine.Instance.gameOptionPrefs.getString("MULTIBOARD", "0"));

    app_data.put("sound", FourInALine.Instance.optionPrefs.getString("SOUND", "Yes"));
    app_data.put("sound", FourInALine.Instance.optionPrefs.getString("MUSIC", "Yes"));
    app_data.put("sound", FourInALine.Instance.optionPrefs.getString("VIBRATION", "Yes"));

    app_data.put("sound", FourInALine.Instance.matchOptionPrefs.getString("LEVEL", "Yes"));
    app_data.put("sound", FourInALine.Instance.matchOptionPrefs.getString("MATCHTO", "Yes"));
    app_data.put("sound", FourInALine.Instance.matchOptionPrefs.getString("VARIANT", "Yes"));

    app_data.put("opponents", AchievementsManager.getInstance().prefs.getString("OPPONENTS", "{}"));
    Json json = new Json();
    return json.toJson(app_data).getBytes();
  }


  @SuppressWarnings("unchecked")
  public void loadState(byte[] bytes) {
    Json json = new Json();
    if (bytes.length == 0)
      return;
    app_data = json.fromJson(HashMap.class, new String(bytes));
    if (app_data == null)
      return;
    savePrefs();
  }


  @SuppressWarnings("unchecked")
  public byte[] resolveConflict(byte[] local, byte[] remote) {
    Json jLocal = new Json();
    HashMap<String, String> hLocal = jLocal.fromJson(HashMap.class, new String(local));
    Json jRemote = new Json();
    HashMap<String, String> hRemote = jRemote.fromJson(HashMap.class, new String(remote));

    double single = Math.max(Double.parseDouble(hLocal.get("singleboard")), Double.parseDouble(hRemote.get("singleboard")));
    double multi = Math.max(Double.parseDouble(hLocal.get("multiboard")), Double.parseDouble(hRemote.get("multiboard")));

    app_data.put("singleboard", single + "");
    app_data.put("multiboard", multi + "");

    app_data.put("sound", hRemote.get("sound"));
    app_data.put("music", hRemote.get("music"));
    app_data.put("vibration", hRemote.get("vibration"));

    app_data.put("level", hRemote.get("level"));
    app_data.put("matchTo", hRemote.get("matchTo"));
    app_data.put("variant", hRemote.get("variant"));

    ArrayList<String> local_played_list = jLocal.fromJson(ArrayList.class, hLocal.get("opponents"));
    ArrayList<String> remote_played_list = jRemote.fromJson(ArrayList.class, hRemote.get("opponents"));
    if (local_played_list.size() >= remote_played_list.size())
      app_data.put("opponents", hLocal.get("opponents"));
    else app_data.put("opponents", hRemote.get("opponents"));

    savePrefs();
    return new Json().toJson(app_data).getBytes();
  }


  private void savePrefs() {
    System.out.println("APPSTATE: savePrefs");
    FourInALine.Instance.gameOptionPrefs.putString("SINGLEBOARD", app_data.get("singleboard"));
    FourInALine.Instance.gameOptionPrefs.putString("MULTIBOARD", app_data.get("multiboard"));

    FourInALine.Instance.optionPrefs.putString("SOUND", app_data.get("sound"));
    FourInALine.Instance.optionPrefs.putString("MUSIC", app_data.get("music"));
    FourInALine.Instance.optionPrefs.putString("VIBRATION", app_data.get("vibration"));

    FourInALine.Instance.matchOptionPrefs.putString("LEVEL", app_data.get("level"));
    FourInALine.Instance.matchOptionPrefs.putString("MATCHTO", app_data.get("matchTo"));
    FourInALine.Instance.matchOptionPrefs.putString("VARIANT", app_data.get("variant"));

    AchievementsManager.getInstance().prefs.putString("OPPONENTS", app_data.get("opponents"));

    FourInALine.Instance.gameOptionPrefs.flush();
    FourInALine.Instance.matchOptionPrefs.flush();
    FourInALine.Instance.optionPrefs.flush();
    AchievementsManager.getInstance().prefs.flush();
  }
}
