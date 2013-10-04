package it.alcacoop.fourinaline.actors;

import it.alcacoop.fourinaline.FourInALine;
import it.alcacoop.fourinaline.fsm.FSM.Events;
import it.alcacoop.fourinaline.fsm.FSM.States;
import it.alcacoop.fourinaline.logic.AIExecutor;
import it.alcacoop.fourinaline.logic.MatchState;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.gojul.fourinaline.model.AlphaBeta;
import org.gojul.fourinaline.model.DefaultEvalScore;
import org.gojul.fourinaline.model.GameModel;
import org.gojul.fourinaline.model.GameModel.CellCoord;
import org.gojul.fourinaline.model.GameModel.GameStatus;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Pool;

public class Board extends Group {

  private int dim;
  private Image bbg;
  private BoardImage boardImage;
  private Group checkersLayer;

  private GameModel gameModel;
  private AlphaBeta alphaBeta;

  private Pool<Checker> checkers;
  private ParticleEffectActor[] effects;

  private boolean locked;
  private HashMap<CellCoord, Checker> usedCheckers;

  private int wx;
  private int wy;
  private int winLength;

  public Board(final int wx, final int wy, final int winLength, final float height) {
    super();
    this.wx = wx;
    this.wy = wy;
    this.winLength = winLength;

    checkers = new Pool<Checker>(wx * wy) {
      @Override
      protected Checker newObject() {
        return new Checker();
      }
    };

    usedCheckers = new HashMap<GameModel.CellCoord, Checker>();

    NinePatch patch = null;
    TextureRegion r = FourInALine.Instance.atlas.findRegion("bbg");
    int[] splits = ((AtlasRegion)r).splits;
    patch = new NinePatch(r, splits[0], splits[1], splits[2], splits[3]);

    dim = Math.round((height - splits[2] - splits[3]) / wy);

    bbg = new Image(patch);
    bbg.setWidth(dim * wx + splits[0] + splits[1]);
    bbg.setHeight(dim * wy + splits[2] + splits[3]);
    bbg.setPosition(0, 0);
    addActor(bbg);

    checkersLayer = new Group();
    checkersLayer.setWidth(dim * wx);
    checkersLayer.setHeight(dim * (wy + 1));
    checkersLayer.setPosition(splits[0], splits[3]);
    addActor(checkersLayer);

    boardImage = new BoardImage(wx * dim, wy * dim, wx, wy);
    addActor(boardImage);
    boardImage.setPosition((getWidth() - boardImage.getWidth()) / 2, (getHeight() - boardImage.getHeight()) / 2);

    effects = new ParticleEffectActor[winLength];
    for (int i = 0; i < winLength; i++) {
      effects[i] = new ParticleEffectActor();
      effects[i].setVisible(false);
      addActor(effects[i]);
    }

    boardImage.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        if (MatchState.winner >= 0) {
          FourInALine.Instance.fsm.state(States.CHECK_END_MATCH);
          FourInALine.Instance.fsm.processEvent(Events.GAME_TERMINATED, MatchState.winner);
        } else {
          if (!locked) {
            int cx = (int)Math.ceil((x / dim)) - 1;
            FourInALine.Instance.fsm.processEvent(Events.CLICKED_COL, cx);
          }
        }
      }
    });

  }


  public boolean play(int col) {
    int row = gameModel.getFreeRowIndexForColumn(col);
    if (row == -1)
      return false;
    locked = true;

    MatchState.mCount++;

    gameModel.play(col, gameModel.getCurrentPlayer());

    Checker checker = checkers.obtain();
    checker.setColor(MatchState.currentPlayer);
    checker.setWidth(dim);
    checker.setHeight(dim);
    checkersLayer.addActor(checker);
    checker.setPosition(dim * col, checkersLayer.getHeight());

    CellCoord cc = new CellCoord(col, row);
    usedCheckers.put(cc, checker);
    checker.addAction(Actions.sequence(Actions.moveTo(dim * col, dim * (wy - row - 1), 0.3f), Actions.run(new Runnable() {
      @Override
      public void run() {
        moveEnd();
      }
    })));
    return true;
  }


  public void moveEnd() {
    if (MatchState.currentPlayer == 1)
      MatchState.currentPlayer = 2;
    else MatchState.currentPlayer = 1;
    System.out.println("Livello: " + MatchState.currentAILevel);
    locked = false;
    // -1=CONTINUE, 0=TIE, 1=WON1, 2=WON2

    if (gameModel.getGameStatus() != GameStatus.CONTINUE_STATUS) {
      locked = true;
      if (gameModel.getGameStatus() == GameStatus.WON_STATUS) {
        System.out.println("PARTITA VINTA!");
        highlightWinLine();
        MatchState.winner = gameModel.getCurrentPlayer().hashCode();
        System.out.println("THE WINNER IS: " + MatchState.winner);
      } else if (gameModel.getGameStatus() == GameStatus.TIE_STATUS) {
        System.out.println("PAREGGIO!");
        MatchState.winner = 0;
      }
    } else {
      locked = false;
      if ((MatchState.mCount > 5) && (MatchState.currentAILevel != MatchState.AILevel)) {
        alphaBeta = new AlphaBeta(new DefaultEvalScore(), MatchState.AILevel, 0.5f);
        MatchState.currentAILevel = MatchState.AILevel;
      }
    }
    FourInALine.Instance.fsm.processEvent(Events.MOVE_END, MatchState.winner);
  }


  public void playAI() {
    AIExecutor.getBestColIndex(alphaBeta, gameModel);
  }

  public void initMatch(int who) {
    gameModel = new GameModel(wy, wx, winLength, who);
    System.out.println("START GAME: " + gameModel.getCurrentPlayer());
    alphaBeta = new AlphaBeta(new DefaultEvalScore(), MatchState.currentAILevel, 0.5f);
    MatchState.winner = -1;
    locked = false;
    MatchState.currentPlayer = gameModel.getCurrentPlayer().hashCode();
  }


  public void reset() {
    if (usedCheckers.size() == 0) {
      FourInALine.Instance.fsm.processEvent(Events.BOARD_RESETTED, 0);
    } else {
      Iterator<Entry<CellCoord, Checker>> iter = usedCheckers.entrySet().iterator();
      while (iter.hasNext()) {
        Entry<CellCoord, Checker> entry = iter.next();
        final Checker c = entry.getValue();
        final boolean hasNext = iter.hasNext();
        c.addAction(Actions.sequence(Actions.fadeOut(0.4f), Actions.run(new Runnable() {
          @Override
          public void run() {
            c.remove();
            checkers.free(c);
            if ((usedCheckers.size() == 0) && (!hasNext))
              FourInALine.Instance.fsm.processEvent(Events.BOARD_RESETTED, 0);
          }
        })));
        iter.remove();
      }
      for (int i = 0; i < 4; i++)
        effects[i].setVisible(false);
    }
  }


  public void highlightWinLine() {
    for (int c = 0; c < gameModel.getWinLine().size(); c++) {
      int row = wy - gameModel.getWinLine().get(c).getRowIndex() - 1;
      int col = gameModel.getWinLine().get(c).getColIndex();
      float x = boardImage.getWidth() / wx * col + boardImage.getX() + dim / 2;
      float y = boardImage.getHeight() / wy * row + boardImage.getY() + dim / 2;
      effects[c].setPosition(x, y);
      effects[c].setVisible(true);
    }
  }


  @Override
  public float getWidth() {
    return bbg.getWidth();
  }

  @Override
  public float getHeight() {
    return bbg.getHeight();
  }

}
