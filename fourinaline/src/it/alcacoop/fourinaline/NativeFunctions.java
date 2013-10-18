package it.alcacoop.fourinaline;


public interface NativeFunctions extends GServiceInterface {
  public void showAds(boolean show);
  public void showInterstitial();
  public void openURL(String url);
  public void openURL(String url, String fallback);

  // public void injectBGInstance();
  //
  // public void fibsSignin();
  // public void fibsRegistration();
  // public void hideChatBox();
  // public void showChatBox();
  // public void initEngine();
  //
  // public boolean isProVersion();
  // public void inAppBilling();
  //
  // public void hideProgressDialog();

  public boolean isNetworkUp();
  public int getAppVersionCode();
}
