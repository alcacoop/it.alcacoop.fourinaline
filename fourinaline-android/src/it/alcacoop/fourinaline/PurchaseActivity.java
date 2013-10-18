package it.alcacoop.fourinaline;

import it.alcacoop.fourinaline.billingutils.IabHelper;
import it.alcacoop.fourinaline.billingutils.IabResult;
import it.alcacoop.fourinaline.billingutils.Purchase;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;


public class PurchaseActivity extends Activity {


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.purchase_activity);
  }


  private void purchase(String sku) {
    IabHelper h = PrivateDataManager.getHelper();
    int ret = h.launchPurchaseFlow(this, sku, PrivateDataManager.RC_REQUEST, mPurchaseFinishedListener, PrivateDataManager.verifyCode);
    if (ret == 0) {
      System.out.println("BILLING: RET=" + ret);
      _toast("Application error: transaction not completed");
      this.setResult(10000);
      finish();
    }
  }


  // User clicked the "Upgrade to Premium" button.
  public void onUpgradeClicked(View v) {
    purchase(PrivateDataManager.SKU_NOADS);
  }


  // User clicked the "Upgrade to Premium" button.
  public void onDonateClicked(View v) {
    purchase(PrivateDataManager.SKU_DONATE);
  }


  // Callback for when a purchase is finished
  IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
    public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
      if (result.isFailure()) {
        // toast("An error occurred: " + result);
        return;
      }
      if (!PrivateDataManager.verifyDeveloperPayload(purchase)) {
        // toast("Error purchasing. Authenticity verification failed.");
        return;
      }
      // Log.d(TAG, "Purchase successful.");
      if (purchase.getSku().equals(PrivateDataManager.SKU_NOADS)) {
        alert("Your request has been successfully processed and Ads will be removed.\n\nThanks for your purchase!");
        PrivateDataManager.msIsPremium = true;
      }
      if (purchase.getSku().equals(PrivateDataManager.SKU_DONATE)) {
        alert("Your request has been successfully processed and Ads will be removed.\n\nThanks for your donation!");
        PrivateDataManager.msIsPremium = true;
      }
    }
  };


  private void alert(String message) {
    AlertDialog.Builder bld = new AlertDialog.Builder(this);
    bld.setMessage(message);
    bld.setNeutralButton("OK", new OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        finish();
      }
    });
    bld.create().show();
  }


  private void _toast(String message) {
    Toast t = Toast.makeText(this, message, Toast.LENGTH_LONG);
    t.show();
  }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (!PrivateDataManager.getHelper().handleActivityResult(requestCode, resultCode, data)) {
      super.onActivityResult(requestCode, resultCode, data);
    }
  }

}
