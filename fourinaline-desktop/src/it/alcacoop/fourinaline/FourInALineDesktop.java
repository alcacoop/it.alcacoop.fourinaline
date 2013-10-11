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
  }

  @Override
  public void openURL(String url) {
  }

  @Override
  public void openURL(String url, String fallback) {
  }

  @Override
  public void gserviceSignIn() {
  }

  @Override
  public boolean gserviceIsSignedIn() {
    return false;
  }

  @Override
  public void gserviceStartRoom() {
  }

  @Override
  public void gserviceAcceptInvitation(String invitationId) {
  }

  @Override
  public void gserviceSendReliableRealTimeMessage(String msg) {
  }

  @Override
  public void gserviceResetRoom() {
  }

  @Override
  public void gserviceOpenLeaderboards() {
  }

  @Override
  public void gserviceOpenAchievements() {
  }

  @Override
  public void gserviceSubmitRating(long score, String board_id) {
  }

  @Override
  public void gserviceUpdateAchievement(String achievement_id, int increment) {
  }

  @Override
  public void gserviceUnlockAchievement(String achiev_id) {
  }

  @Override
  public void gserviceUpdateState() {
  }

  @Override
  public void gserviceGetSigninDialog(int from) {
  }

  @Override
  public boolean isNetworkUp() {
    return false;
  }

  @Override
  public int getAppVersionCode() {
    return 0;
  }

  @Override
  public void showInterstitial() {
  }
}
