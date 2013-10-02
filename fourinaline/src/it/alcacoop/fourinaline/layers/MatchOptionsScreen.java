/*
 ##################################################################
 #                     GNU BACKGAMMON MOBILE                      #
 ##################################################################
 #                                                                #
 #  Authors: Domenico Martella - Davide Saurino                   #
 #  E-mail: info@alcacoop.it                                      #
 #  Date:   19/12/2012                                            #
 #                                                                #
 ##################################################################
 #                                                                #
 #  Copyright (C) 2012   Alca Societa' Cooperativa                #
 #                                                                #
 #  This file is part of GNU BACKGAMMON MOBILE.                   #
 #  GNU BACKGAMMON MOBILE is free software: you can redistribute  # 
 #  it and/or modify it under the terms of the GNU General        #
 #  Public License as published by the Free Software Foundation,  #
 #  either version 3 of the License, or (at your option)          #
 #  any later version.                                            #
 #                                                                #
 #  GNU BACKGAMMON MOBILE is distributed in the hope that it      #
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
*/

package it.alcacoop.fourinaline.layers;


import it.alcacoop.fourinaline.FourInALine;
import it.alcacoop.fourinaline.actors.FixedButtonGroup;
import it.alcacoop.fourinaline.fsm.FSM.Events;
import it.alcacoop.fourinaline.logic.MatchState;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class MatchOptionsScreen extends BaseScreen {

  private Preferences prefs;

  private final FixedButtonGroup level;
  private final FixedButtonGroup matchTo;
  private final FixedButtonGroup gametype;

  private String _levels[] = { "Beginner", "Casual", "Intermediate", "Advanced", "Expert" };
  private TextButton levelButtons[];

  private String _matchTo[] = { "1", "3", "5", "7" };
  private TextButton matchToButtons[];

  private String _yesNo[] = { "Yes", "No" };
  private TextButton doublingButtons[];
  private TextButton crawfordButtons[];

  private String _gametype[] = { "7x6x4 (Standard)", "9x7x5" };
  private TextButton gameTypeButtons[];

  private Label difficultyLabel;
  private Table table;
  private Label doublingLabel;
  private Label crawfordLabel;
  private Label gameTypeLabel;
  private TextButton back;
  private TextButton play;
  private Label playToLabel;
  private Label titleLabel;

  // private Group g;

  public MatchOptionsScreen() {

    prefs = Gdx.app.getPreferences("MatchOptions");

    stage.addListener(new InputListener() {
      @Override
      public boolean keyDown(InputEvent event, int keycode) {
        if (Gdx.input.isKeyPressed(Keys.BACK) || Gdx.input.isKeyPressed(Keys.ESCAPE)) {
          // if (UIDialog.isOpened())
          // return false;
          savePrefs();
          FourInALine.Instance.fsm.processEvent(Events.BUTTON_CLICKED, "BACK");
        }
        return super.keyDown(event, keycode);
      }
    });

    ClickListener cls = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        // GnuBackgammon.Instance.snd.playMoveStart();
      };
    };

    titleLabel = new Label("MATCH SETTINGS", FourInALine.Instance.skin);
    difficultyLabel = new Label("Difficulty:", FourInALine.Instance.skin);
    playToLabel = new Label("Match to:", FourInALine.Instance.skin);
    gameTypeLabel = new Label("Variant:", FourInALine.Instance.skin);

    TextButtonStyle ts = FourInALine.Instance.skin.get("toggle", TextButtonStyle.class);
    levelButtons = new TextButton[_levels.length];
    level = new FixedButtonGroup();
    for (int i = 0; i < _levels.length; i++) {
      levelButtons[i] = new TextButton(_levels[i], ts);
      levelButtons[i].addListener(cls);
      level.add(levelButtons[i]);
    }

    matchToButtons = new TextButton[_matchTo.length];
    matchTo = new FixedButtonGroup();
    for (int i = 0; i < _matchTo.length; i++) {
      matchToButtons[i] = new TextButton(_matchTo[i], ts);
      matchToButtons[i].addListener(cls);
      matchTo.add(matchToButtons[i]);
    }

    gameTypeButtons = new TextButton[_gametype.length];
    gametype = new FixedButtonGroup();
    for (int i = 0; i < _yesNo.length; i++) {
      gameTypeButtons[i] = new TextButton(_gametype[i], ts);
      gameTypeButtons[i].addListener(cls);
      gametype.add(gameTypeButtons[i]);
    }

    ClickListener cl = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        savePrefs();
        FourInALine.Instance.fsm.processEvent(Events.BUTTON_CLICKED, ((TextButton)event.getListenerActor()).getText().toString().toUpperCase());
      };
    };
    play = new TextButton("PLAY", FourInALine.Instance.skin);
    play.addListener(cl);
    back = new TextButton("BACK", FourInALine.Instance.skin);
    back.addListener(cl);
    initFromPrefs();
    table = new Table();
    stage.addActor(table);
  }


  public void initFromPrefs() {
    String sLevel = prefs.getString("LEVEL", "Beginner");
    level.setChecked(sLevel);
    String sMatchTo = prefs.getString("MATCHTO", "1");
    matchTo.setChecked(sMatchTo);
    String sVariant = prefs.getString("VARIANT", "7x6x4 (Standard)");
    gametype.setChecked(sVariant);
  }


  public void savePrefs() {
    String sLevel = ((TextButton)level.getChecked()).getText().toString();
    prefs.putString("LEVEL", sLevel);
    String sMatchTo = ((TextButton)matchTo.getChecked()).getText().toString();
    prefs.putString("MATCHTO", sMatchTo);
    String sGameType = ((TextButton)gametype.getChecked()).getText().toString();
    prefs.putString("VARIANT", sGameType);

    prefs.flush();

    MatchState.gameLevel = Integer.parseInt(sLevel);
    MatchState.nMatchTo = Integer.parseInt(sMatchTo);
    MatchState.anScore[0] = 0;
    MatchState.anScore[1] = 0;
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0.1f, 0.45f, 0.08f, 1);
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    stage.act(delta);
    stage.draw();
  }


  @Override
  public void initialize() {
    initTable();
    table.setColor(1, 1, 1, 0);
  }


  @Override
  public void show() {
    super.show();
    Gdx.input.setInputProcessor(stage);
    Gdx.input.setCatchBackKey(true);
    table.addAction(Actions.sequence(Actions.parallel(Actions.fadeIn(animationTime),
        Actions.moveTo((stage.getWidth() - table.getWidth()) / 2, (stage.getHeight() - table.getHeight()) / 2, animationTime))));
  }


  @Override
  public void fadeOut() {
    table
        .addAction(Actions.sequence(Actions.parallel(Actions.fadeOut(animationTime), Actions.moveTo(-stage.getWidth(), (stage.getHeight() - table.getHeight()) / 2, animationTime))));
  }


  @Override
  public void resume() {
    Gdx.graphics.requestRendering();
  }

  public void initTable() {
    // float height = stage.getWidth()/15;
    table.clear();

    table.setWidth(stage.getWidth() * 0.9f);
    table.setHeight(stage.getHeight() * 0.9f);
    table.setX(-stage.getWidth());
    table.setY((stage.getHeight() - table.getHeight()) / 2);

    float width = table.getWidth() / 9f;

    table.add(titleLabel).colspan(9);

    if (MatchState.matchType == 0) {
      table.row();
      table.add().expand().fill();
      table.row();
      table.add().expand().fill();
      table.row();
      table.add(difficultyLabel).right().spaceRight(6);
      table.add(levelButtons[0]).expand().fill().colspan(2).width(2 * width);
      table.add(levelButtons[1]).expand().fill().colspan(2).width(2 * width);
      table.add(levelButtons[2]).expand().fill().colspan(2).width(2 * width);
      table.add(levelButtons[3]).expand().fill().colspan(2).width(2 * width);
      table.row();
      table.add();
      table.add(levelButtons[4]).expand().fill().colspan(2).width(2 * width);
      table.add(levelButtons[5]).expand().fill().colspan(2).width(2 * width);
      table.add(levelButtons[6]).expand().fill().colspan(2).width(2 * width);
      table.add(levelButtons[7]).expand().fill().colspan(2).width(2 * width);
    }

    table.row();
    table.add().expand().fill();
    table.row();
    table.add().expand().fill();

    table.row();
    table.add(playToLabel).right().spaceRight(6);
    table.add(matchToButtons[0]).expand().fill().width(width);
    table.add(matchToButtons[1]).expand().fill().width(width);
    table.add(matchToButtons[2]).expand().fill().width(width);
    table.add(matchToButtons[3]).expand().fill().width(width);
    table.add(matchToButtons[4]).expand().fill().width(width);
    table.add(matchToButtons[5]).expand().fill().width(width);
    table.add(matchToButtons[6]).expand().fill().width(width);
    table.add(matchToButtons[7]).expand().fill().width(width);

    table.row();
    table.add().expand().fill();

    table.row();
    table.add(doublingLabel).right().spaceRight(6);
    table.add(doublingButtons[0]).expand().fill().width(width);
    table.add(doublingButtons[1]).expand().fill().width(width);
    table.add();
    table.add(crawfordLabel).right().colspan(2).spaceRight(6);
    table.add(crawfordButtons[0]).expand().fill().width(width);
    table.add(crawfordButtons[1]).expand().fill().width(width);
    table.add();

    table.row();
    table.add().expand().fill();

    table.row();
    table.add(gameTypeLabel).right().spaceRight(6);
    table.add(gameTypeButtons[0]).expand().fill().colspan(2).width(2 * width);
    table.add(gameTypeButtons[1]).expand().fill().colspan(2).width(2 * width);
    table.add().colspan(6);
    table.add();

    table.row();
    table.add().expand().fill();
    table.row();
    table.add().expand().fill();

    table.row();
    table.add().colspan(2);
    table.add(back).fill().expand().colspan(2);
    table.add();
    table.add(play).fill().expand().colspan(2);
    table.add().colspan(2);

    table.row();
    table.add().expand().fill();
  }
}
