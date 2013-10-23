/**
 ##################################################################
 #                     FOUR IN A LINE MOBILE                      #
 ##################################################################
 #                                                                #
 #  Authors: Domenico Martella - Francesco Valente                #
 #  E-mail: info@alcacoop.it                                      #
 #  Date:   18/10/2013                                            #
 #                                                                #
 ##################################################################
 #                                                                #
 #  Copyright (C) 2013   Alca Societa' Cooperativa                #
 #                                                                #
 #  This file is part of FOUR IN A LINE MOBILE.                   #
 #  FOUR IN A LINE MOBILE is free software: you can redistribute  # 
 #  it and/or modify it under the terms of the GNU General        #
 #  Public License as published by the Free Software Foundation,  #
 #  either version 3 of the License, or (at your option)          #
 #  any later version.                                            #
 #                                                                #
 #  FOUR IN A LINE MOBILE is distributed in the hope that it      #
 #  will be useful, but WITHOUT ANY WARRANTY; without even the    #
 #  implied warranty of MERCHANTABILITY or FITNESS FOR A          #
 #  PARTICULAR PURPOSE.  See the GNU General Public License       #
 #  for more details.                                             #
 #                                                                #
 #  You should have received a copy of the GNU General            #
 #  Public License v3 along with this program.                    #
 #  If not, see <http://http://www.gnu.org/licenses/>             #
 #                                                                #
 ##################################################################
 **/

package it.alcacoop.fourinaline.actors;

import it.alcacoop.fourinaline.FourInALine;
import it.alcacoop.fourinaline.fsm.FSM.Events;
import it.alcacoop.fourinaline.fsm.FSM.States;
import it.alcacoop.fourinaline.gservice.GServiceClient;
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

import com.badlogic.gdx.Gdx;
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
  private NinePatch patch;
  private Group checkersLayer;

  private GameModel gameModel;
  private AlphaBeta alphaBeta;

  private Pool<Checker> checkers;
  private ParticleEffectActor[] effects;

  private boolean locked;
  private HashMap<CellCoord, Checker> usedCheckers;

  private int splits[];

  private int wx;
  private int wy;
  private int winLength;
  private float height;

  public Board(final int wx, final int wy, final int winLength, final float height) {
    super();
    this.wx = wx;
    this.wy = wy;
    this.winLength = winLength;
    this.height = height;

    checkers = new Pool<Checker>(wx * wy) {
      @Override
      protected Checker newObject() {
        return new Checker();
      }
    };

    usedCheckers = new HashMap<GameModel.CellCoord, Checker>();

    TextureRegion r = FourInALine.Instance.atlas.findRegion("bbg");
    splits = ((AtlasRegion)r).splits;
    patch = new NinePatch(r, splits[0], splits[1], splits[2], splits[3]);

    effects = new ParticleEffectActor[6];
    for (int i = 0; i < 6; i++) {
      effects[i] = new ParticleEffectActor();
      effects[i].setVisible(false);
    }

    setBoardDim(wx, wy, winLength);
  }

  private void setVariant() {
    String variant = "";
    if (MatchState.matchType != 2)
      variant = Gdx.app.getPreferences("MatchOptions").getString("VARIANT", "7x6x4 (Standard)");
    else
      variant = "7x6x4 (Standard)";
    if (variant.equals("7x6x4 (Standard)") && (wx != 7)) {
      setBoardDim(7, 6, 4);
      System.out.println("STANDARD!!!");
    } else if (variant.equals("9x7x5 (Bigger)") && (wx != 9)) {
      setBoardDim(9, 7, 5);
      System.out.println("BIGGER!!!");
    }

  }

  private void setBoardDim(int wx, int wy, int winLength) {
    if (bbg != null)
      bbg.remove();
    if (boardImage != null)
      boardImage.remove();
    if (checkersLayer != null)
      checkersLayer.remove();

    for (int i = 0; i < 6; i++)
      effects[i].remove();

    this.wx = wx;
    this.wy = wy;
    this.winLength = winLength;

    dim = Math.round((height - splits[2] - splits[3]) / wy);

    checkersLayer = new Group();
    checkersLayer.setWidth(dim * wx);
    checkersLayer.setHeight(dim * (wy + 1));
    checkersLayer.setPosition(splits[0], splits[3]);
    addActor(checkersLayer);

    bbg = new Image(patch);
    bbg.setWidth(dim * wx + splits[0] + splits[1]);
    bbg.setHeight(dim * wy + splits[2] + splits[3]);
    bbg.setPosition(0, 0);
    addActor(bbg);

    boardImage = new BoardImage(wx * dim, wy * dim, wx, wy);
    addActor(boardImage);
    boardImage.setPosition((getWidth() - boardImage.getWidth()) / 2, (getHeight() - boardImage.getHeight()) / 2);

    for (int i = 0; i < winLength; i++)
      addActor(effects[i]);

    boardImage.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        if (MatchState.winner == -1) {
          if (!locked) {
            int cx = (int)Math.ceil((x / dim)) - 1;
            FourInALine.Instance.fsm.processEvent(Events.CLICKED_COL, cx);
          }
        }
      }
    });
  }


  public boolean play(final int col) {
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

    float to = dim * (wy - row - 1);
    float delta = checker.getY() - to; // delta/time = v; => delta/v = time;
    float time = delta / (getHeight() * 4f);
    System.out.println("GETY: " + boardImage.getHeight());

    checker.addAction(Actions.sequence(Actions.moveTo(checker.getX(), to, time), Actions.run(new Runnable() {
      @Override
      public void run() {
        moveEnd(col);
      }
    })));
    return true;
  }

  public void moveEnd(int col) {
    FourInALine.Instance.snd.playMove();
    FourInALine.Instance.vibrate(120);
    if (MatchState.currentPlayer == 1)
      MatchState.currentPlayer = 2;
    else
      MatchState.currentPlayer = 1;
    System.out.println("Livello: " + MatchState.AILevel);
    locked = false;
    // -1=CONTINUE, 0=TIE, 1=WON1, 2=WON2

    if (gameModel.getGameStatus() != GameStatus.CONTINUE_STATUS) {
      if ((MatchState.currentPlayer == 2) && (MatchState.matchType == 2))
        GServiceClient.getInstance().sendMessage("6 " + col);
      locked = true;
      FourInALine.Instance.fsm.state(States.CHECK_END_MATCH);
      if (gameModel.getGameStatus() == GameStatus.WON_STATUS) {
        System.out.println("PARTITA VINTA!");
        highlightWinLine();
        MatchState.winner = gameModel.getCurrentPlayer().hashCode();
        System.out.println("THE WINNER IS: " + MatchState.winner);
        FourInALine.Instance.gameScreen.incScore(MatchState.winner);
        if ((MatchState.anScore[0] < MatchState.nMatchTo) && (MatchState.anScore[1] < MatchState.nMatchTo)) {
          UIDialog.getContinueDialog(Events.GAME_TERMINATED, FourInALine.Instance.gameScreen.getPlayerName(MatchState.winner) + " won a game!", 0.8f);
        } else {
          UIDialog.getContinueDialog(Events.GAME_TERMINATED, FourInALine.Instance.gameScreen.getPlayerName(MatchState.winner) + " won the match!", 0.8f);
        }
      } else if (gameModel.getGameStatus() == GameStatus.TIE_STATUS) {
        System.out.println("PAREGGIO!");
        MatchState.winner = 0;
        UIDialog.getContinueDialog(Events.GAME_TERMINATED, "Game tied!", 0.8f);
      }
    } else {
      locked = false;
      FourInALine.Instance.fsm.processEvent(Events.MOVE_END, col);
    }
  }


  public void playAI() {
    AIExecutor.getBestColIndex(alphaBeta, gameModel);
  }

  public void initMatch(int who) {
    setVariant();
    gameModel = new GameModel(wy, wx, winLength, who);
    System.out.println("START GAME: " + gameModel.getCurrentPlayer());
    int deepness = 3;
    if (MatchState.AILevel == 5)
      deepness = 4;
    alphaBeta = new AlphaBeta(new DefaultEvalScore(), deepness, 0.5f, MatchState.AILevel);
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
      for (int i = 0; i < winLength; i++)
        effects[i].setVisible(false);
    }
  }


  public void highlightWinLine() {
    FourInALine.Instance.snd.playGameEnd();
    for (int c = 0; c < gameModel.getWinLine().size(); c++) {
      int row = wy - gameModel.getWinLine().get(c).getRowIndex() - 1;
      int col = gameModel.getWinLine().get(c).getColIndex();
      float x = boardImage.getWidth() / wx * col + boardImage.getX() + dim / 2;
      float y = boardImage.getHeight() / wy * row + boardImage.getY() + dim / 2;
      effects[c].setPosition(x, y);
      effects[c].setVisible(true);
    }
    FourInALine.Instance.gameScreen.highlightPlayer(0);
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
