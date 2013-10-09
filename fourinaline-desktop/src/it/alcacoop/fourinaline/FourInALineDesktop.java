package it.alcacoop.fourinaline;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class FourInALineDesktop implements NativeFunctions {
  private static FourInALineDesktop instance;
  public static void main(String[] args) {
    LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
    cfg.title = "fourinaline";
    cfg.useGL20 = true;
    cfg.width = 800;
    cfg.height = 480;
    instance = new FourInALineDesktop();

    new LwjglApplication(new FourInALine(instance), cfg);
  }

  @Override
  public void showAds(boolean show) {
    // TODO Auto-generated method stub

  }

  @Override
  public void openURL(String url) {
    // TODO Auto-generated method stub

  }

  @Override
  public void openURL(String url, String fallback) {
    // TODO Auto-generated method stub

  }

  @Override
  public String getDataDir() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void gserviceSignIn() {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean gserviceIsSignedIn() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void gserviceStartRoom() {
    // TODO Auto-generated method stub

  }

  @Override
  public void gserviceAcceptInvitation(String invitationId) {
    // TODO Auto-generated method stub

  }

  @Override
  public void gserviceSendReliableRealTimeMessage(String msg) {
    // TODO Auto-generated method stub

  }

  @Override
  public void gserviceResetRoom() {
    // TODO Auto-generated method stub

  }

  @Override
  public void gserviceOpenLeaderboards() {
    // TODO Auto-generated method stub

  }

  @Override
  public void gserviceOpenAchievements() {
    // TODO Auto-generated method stub

  }

  @Override
  public void gserviceSubmitRating(long score, String board_id) {
    // TODO Auto-generated method stub

  }

  @Override
  public void gserviceUpdateAchievement(String achievement_id, int increment) {
    // TODO Auto-generated method stub

  }

  @Override
  public void gserviceUnlockAchievement(String achiev_id) {
    // TODO Auto-generated method stub

  }

  @Override
  public void gserviceUpdateState() {
    // TODO Auto-generated method stub

  }

  @Override
  public void gserviceGetSigninDialog(int from) {
    // TODO Auto-generated method stub

  }
}
