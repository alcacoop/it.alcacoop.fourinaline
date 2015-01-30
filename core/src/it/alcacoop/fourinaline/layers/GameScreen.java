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

package it.alcacoop.fourinaline.layers;

import it.alcacoop.fourinaline.FourInALine;
import it.alcacoop.fourinaline.actors.ChatBox;
import it.alcacoop.fourinaline.actors.IconButton;
import it.alcacoop.fourinaline.actors.PlayerBlock;
import it.alcacoop.fourinaline.actors.UIDialog;
import it.alcacoop.fourinaline.fsm.FSM.Events;
import it.alcacoop.fourinaline.logic.MatchState;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class GameScreen extends BaseScreen {

  private Table table;
  private PlayerBlock players[];
  private Label nMatchTo, thinking;
  private IconButton leave, resign;

  public ChatBox chatBox;

  public GameScreen() {
    players = new PlayerBlock[2];
    players[0] = new PlayerBlock();
    players[0].setName("YOU");
    players[0].setColor(1);
    players[1] = new PlayerBlock();
    players[1].setName("CPU (L" + MatchState.AILevel + ")");
    players[1].setColor(2);
    nMatchTo = new Label("", FourInALine.Instance.skin);
    thinking = new Label("Thinking.. plase wait", FourInALine.Instance.skin);
    thinking.addAction(Actions.forever(Actions.sequence(Actions.fadeOut(0.25f), Actions.fadeIn(0.4f), Actions.delay(0.3f))));

    ClickListener clBack = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        if (UIDialog.isOpened())
          return;
        FourInALine.Instance.snd.playButton();
        FourInALine.Instance.vibrate(80);
        UIDialog.getYesNoDialog(Events.LEAVE_MATCH, "Really leave current match?");
      };
    };

    ClickListener clResign = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        if (UIDialog.isOpened())
          return;
        FourInALine.Instance.snd.playButton();
        FourInALine.Instance.vibrate(80);
        UIDialog.getYesNoDialog(Events.RESIGN_GAME, "Really resign current game?");
      };
    };

    TextButtonStyle st = FourInALine.Instance.skin.get("button", TextButtonStyle.class);
    leave = new IconButton("", FourInALine.Instance.atlas.findRegion("back"), st, true, false, false);
    leave.addListener(clBack);
    resign = new IconButton("", FourInALine.Instance.atlas.findRegion("resign"), st, true, false, false);
    resign.addListener(clResign);

    table = new Table();
    table.setWidth(stage.getWidth() * 0.9f);
    table.setHeight(stage.getHeight() * 0.78f);
    table.debug();
    stage.addListener(new InputListener() {
      @Override
      public boolean keyDown(InputEvent event, int keycode) {
        if (Gdx.input.isKeyPressed(Keys.BACK) || Gdx.input.isKeyPressed(Keys.ESCAPE)) {
          if (UIDialog.isOpened())
            return false;
          if (chatBox.visible) {
            chatBox.hide();
          } else {
            FourInALine.Instance.snd.playButton();
            FourInALine.Instance.vibrate(80);
            UIDialog.getYesNoDialog(Events.LEAVE_MATCH, "Really leave current match?");
          }
        }
        return super.keyDown(event, keycode);
      }
    });
    stage.addActor(table);
    chatBox = new ChatBox(stage);
    stage.addActor(chatBox);

    thinking.setPosition((stage.getWidth() - thinking.getWidth()) / 2, thinking.getHeight() * 0.5f);
    stage.addActor(thinking);
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(1, 1, 1, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    stage.act(delta);
    stage.draw();
  }


  public void showThinking(boolean show) {
    if (show) {
      if (MatchState.matchType == 0)
        thinking.setText("CPU is thinking.. please wait!");
      else
        thinking.setText("Opponent's turn.. please wait!");
      stage.addActor(thinking);
    } else {
      thinking.remove();
    }
  }

  @Override
  public void initialize() {
    table.clear();

    nMatchTo.setText("MATCH TO: " + MatchState.nMatchTo);
    nMatchTo.setColor(136f / 255f, 74f / 255f, 36f / 255f, 1f);

    System.out.println(nMatchTo.getHeight());

    players[0].setScore(0);
    players[1].setScore(0);

    if (MatchState.matchType == 0) {
      players[0].setName("YOU");
      players[1].setName("CPU (Lev" + MatchState.AILevel + ")");
    } else if (MatchState.matchType == 1) {
      players[0].setName("PLAYER1");
      players[1].setName("PLAYER2");
    } else {
      players[0].setName("YOU");
      players[1].setName("OPPONENT");
    }


    Table tp = new Table();
    tp.debug();
    tp.setFillParent(false);
    tp.add(nMatchTo).expandX().colspan(2);

    tp.row();
    tp.add().height(table.getHeight() / 40).colspan(2);

    tp.row();
    tp.add(players[0]).left().fill().expandX().colspan(2);

    tp.row();
    tp.add().height(table.getHeight() / 22).colspan(2);

    tp.row();
    tp.add(players[1]).fill().left().expandX().colspan(2);
    tp.row();
    tp.add().expand().colspan(2);

    tp.row();
    tp.add(leave).expandX().fill().padLeft(table.getHeight() / 80).padRight(table.getHeight() / 80);
    tp.add(resign).expandX().fill().padLeft(table.getHeight() / 80).padRight(table.getHeight() / 80);

    tp.setBackground(FourInALine.Instance.skin.getDrawable("default-window"));

    table.add(tp).expand().fill().left();
    table.add(FourInALine.Instance.board);

    table.setPosition(-stage.getWidth(), (stage.getHeight() - table.getHeight()) / 2);

    if (MatchState.matchType == 2) {
      chatBox.reset();
    } else {
      chatBox.setVisible(false);
      chatBox.hardHide();
    }
  }

  @Override
  public void show() {
    super.show();
    Gdx.input.setInputProcessor(stage);
    Gdx.input.setCatchBackKey(true);
    table.addAction(Actions.sequence(
        Actions.parallel(Actions.fadeIn(animationTime), Actions.moveTo((stage.getWidth() - table.getWidth()) / 2, (stage.getHeight() - table.getHeight()) / 2, animationTime)),
        Actions.run(new Runnable() {
          @Override
          public void run() {
            FourInALine.Instance.fsm.processEvent(Events.START_GAME, null);
            FourInALine.Instance.nativeFunctions.showAds(true);
          }
        })));
  }

  @Override
  public void fadeOut() {
    FourInALine.Instance.nativeFunctions.showAds(false);
    table
        .addAction(Actions.sequence(Actions.parallel(Actions.fadeOut(animationTime), Actions.moveTo(-stage.getWidth(), (stage.getHeight() - table.getHeight()) / 2, animationTime))));
  }

  public void highlightPlayer(int player) {
    if (player == 1) {
      players[0].highlight(true);
      players[1].highlight(false);
    } else if (player == 2) {
      players[0].highlight(false);
      players[1].highlight(true);
    } else {
      players[0].highlight(false);
      players[1].highlight(false);
    }
  }

  public void incScore(int player) {
    MatchState.anScore[player - 1]++;
    players[player - 1].setScore(MatchState.anScore[player - 1]);
  }

  public String getPlayerName(int id) {
    return players[id - 1].getName();
  }
}
