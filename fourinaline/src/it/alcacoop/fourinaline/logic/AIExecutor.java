package it.alcacoop.fourinaline.logic;

import it.alcacoop.fourinaline.FourInALine;
import it.alcacoop.fourinaline.fsm.FSM.Events;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.gojul.fourinaline.model.AlphaBeta;
import org.gojul.fourinaline.model.GameModel;

import com.badlogic.gdx.Gdx;

public class AIExecutor {

  private static ExecutorService dispatchExecutor;

  static {
    dispatchExecutor = Executors.newSingleThreadExecutor();
  }


  public static void getBestColIndex(final AlphaBeta alphaBeta, final GameModel gameModel) {
    dispatchExecutor.execute(new Runnable() {
      @Override
      public void run() {
        final int col = alphaBeta.getColumnIndex(gameModel, gameModel.getCurrentPlayer());
        Gdx.app.postRunnable(new Runnable() {
          @Override
          public void run() {
            FourInALine.Instance.fsm.processEvent(Events.AI_EVALUETED, col);
          }
        });
      }
    });
  }
}
