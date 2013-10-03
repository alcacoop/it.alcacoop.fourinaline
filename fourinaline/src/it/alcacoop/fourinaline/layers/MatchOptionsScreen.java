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
import it.alcacoop.fourinaline.actors.IconButton;
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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class MatchOptionsScreen extends BaseScreen {

  private Preferences prefs;

  private final FixedButtonGroup level;
  private final FixedButtonGroup matchTo;
  private final FixedButtonGroup gametype;

  private String _levels[] = { "Beginner", "Casual", "Intermediate", "Advanced", "Expert" };
  private IconButton levelButtons[];

  private String _matchTo[] = { "1", "3", "5", "7" };
  private IconButton matchToButtons[];

  private String _yesNo[] = { "Yes", "No" };

  private String _gametype[] = { "7x6x4 (Standard)", "9x7x5 (Bigger)" };
  private IconButton gameTypeButtons[];

  private Label difficultyLabel;
  private Table table;
  private Label gameTypeLabel;
  private IconButton back;
  private IconButton play;
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

    levelButtons = new IconButton[_levels.length];
    level = new FixedButtonGroup();
    for (int i = 0; i < _levels.length; i++) {
      levelButtons[i] = new IconButton(_levels[i], null, ts);
      levelButtons[i].addListener(cls);
      level.add(levelButtons[i]);
    }

    matchToButtons = new IconButton[_matchTo.length];
    matchTo = new FixedButtonGroup();
    for (int i = 0; i < _matchTo.length; i++) {
      matchToButtons[i] = new IconButton(_matchTo[i], null, ts);
      matchToButtons[i].addListener(cls);
      matchTo.add(matchToButtons[i]);
    }

    gameTypeButtons = new IconButton[_gametype.length];
    gametype = new FixedButtonGroup();
    for (int i = 0; i < _yesNo.length; i++) {
      gameTypeButtons[i] = new IconButton(_gametype[i], null, ts);
      gameTypeButtons[i].addListener(cls);
      gametype.add(gameTypeButtons[i]);
    }

    ClickListener cl = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        savePrefs();
        FourInALine.Instance.fsm.processEvent(Events.BUTTON_CLICKED, ((IconButton)event.getListenerActor()).getText().toString().toUpperCase());
      };
    };

    ts = FourInALine.Instance.skin.get("button", TextButtonStyle.class);
    play = new IconButton("PLAY", null, ts);
    play.addListener(cl);
    back = new IconButton("BACK", null, ts);
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
    String sLevel = ((IconButton)level.getChecked()).getText().toString();
    prefs.putString("LEVEL", sLevel);
    String sMatchTo = ((IconButton)matchTo.getChecked()).getText().toString();
    prefs.putString("MATCHTO", sMatchTo);
    String sGameType = ((IconButton)gametype.getChecked()).getText().toString();
    prefs.putString("VARIANT", sGameType);

    prefs.flush();

    for (int i = 0; i < _levels.length; i++) {
      if (_levels[i].equals(sLevel))
        MatchState.gameLevel = (i + 1);
    }

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
    table.clear();
    table.setHeight(stage.getHeight() * 0.9f);
    table.setWidth(stage.getWidth() * 0.9f);
    table.setPosition(-stage.getWidth(), (stage.getHeight() - table.getHeight()) / 2);

    float height = table.getHeight() / 7;
    float width = table.getWidth() / 5;
    float pad = height / 55;


    table.add(titleLabel).colspan(9);

    table.row().pad(pad);
    table.add().expand().fill();

    if (MatchState.matchType == 0) {
      table.row().pad(pad);
      table.add(difficultyLabel).right();
      table.add(levelButtons[0]).height(height).width(width).fill().expand();
      table.add(levelButtons[1]).height(height).width(width).fill().expand();
      table.add(levelButtons[2]).height(height).width(width).fill().expand();
      table.add(levelButtons[3]).height(height).width(width).fill().expand();
      table.row().pad(pad);
      table.add();
      table.add(levelButtons[4]).height(height).width(width).fill().expand();

      table.row().pad(pad);
      table.add().expand().fill();
    }


    table.row().pad(pad);
    table.add(playToLabel).right();
    table.add(matchToButtons[0]).expand().fill().width(width).height(height);
    table.add(matchToButtons[1]).expand().fill().width(width).height(height);
    table.add(matchToButtons[2]).expand().fill().width(width).height(height);
    table.add(matchToButtons[3]).expand().fill().width(width).height(height);

    table.row().pad(pad);
    table.add().expand().fill();


    table.row().pad(pad);
    table.add(gameTypeLabel).right();
    table.add(gameTypeButtons[0]).expand().fill().colspan(2).height(height);
    table.add(gameTypeButtons[1]).expand().fill().colspan(2).height(height);
    table.add();

    table.row().pad(pad);
    table.add().expand().fill();

    table.row().pad(pad);
    Table tbl = new Table();
    tbl.add().width(width / 3);
    tbl.add(back).width(width * 3 / 2).fill().expand();
    tbl.add().width(width / 3);
    tbl.add(play).width(width * 3 / 2).fill().expand();
    tbl.add().width(width / 3);
    table.add(tbl).fill().expand().height(height).colspan(5);
  }
}
