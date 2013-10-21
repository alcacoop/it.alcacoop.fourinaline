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
import it.alcacoop.fourinaline.actors.FixedButtonGroup;
import it.alcacoop.fourinaline.actors.IconButton;
import it.alcacoop.fourinaline.fsm.FSM.Events;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class OptionsScreen extends BaseScreen {

  private final FixedButtonGroup sound;
  private final FixedButtonGroup music;
  private final FixedButtonGroup vibration;

  private IconButton back;
  private IconButton soundButtons[];
  private IconButton musicButtons[];
  private IconButton vibrationButtons[];
  private String _yesNo[] = { "Yes", "No" };

  private Label titleLabel;
  private Label soundLabel;
  private Label musicLabel;
  private Label vibrationLabel;

  private Table table;

  // private Group g;

  public OptionsScreen() {

    stage.addListener(new InputListener() {
      @Override
      public boolean keyDown(InputEvent event, int keycode) {
        if (Gdx.input.isKeyPressed(Keys.BACK) || Gdx.input.isKeyPressed(Keys.ESCAPE)) {
          FourInALine.Instance.snd.playButton();
          FourInALine.Instance.vibrate(80);
          savePrefs();
          FourInALine.Instance.fsm.processEvent(Events.BUTTON_CLICKED, "BACK");
        }
        return super.keyDown(event, keycode);
      }
    });

    ClickListener cls = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        FourInALine.Instance.snd.playButton();
        FourInALine.Instance.vibrate(80);
      };
    };


    titleLabel = new Label("OPTIONS", FourInALine.Instance.skin);
    soundLabel = new Label("Sound:", FourInALine.Instance.skin);
    musicLabel = new Label("Music:", FourInALine.Instance.skin);
    vibrationLabel = new Label("Vibration:", FourInALine.Instance.skin);

    TextButtonStyle ts = FourInALine.Instance.skin.get("toggle", TextButtonStyle.class);

    soundButtons = new IconButton[_yesNo.length];
    sound = new FixedButtonGroup();
    for (int i = 0; i < _yesNo.length; i++) {
      soundButtons[i] = new IconButton(_yesNo[i], null, ts);
      soundButtons[i].addListener(cls);
      sound.add(soundButtons[i]);
    }

    musicButtons = new IconButton[_yesNo.length];
    music = new FixedButtonGroup();
    for (int i = 0; i < _yesNo.length; i++) {
      musicButtons[i] = new IconButton(_yesNo[i], null, ts);
      musicButtons[i].addListener(cls);
      music.add(musicButtons[i]);
    }

    vibrationButtons = new IconButton[_yesNo.length];
    vibration = new FixedButtonGroup();
    for (int i = 0; i < _yesNo.length; i++) {
      vibrationButtons[i] = new IconButton(_yesNo[i], null, ts);
      vibrationButtons[i].addListener(cls);
      vibration.add(vibrationButtons[i]);
    }

    ClickListener cl = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        FourInALine.Instance.snd.playButton();
        FourInALine.Instance.vibrate(80);
        savePrefs();
        FourInALine.Instance.fsm.processEvent(Events.BUTTON_CLICKED, ((IconButton)event.getListenerActor()).getText().toString().toUpperCase());
      };
    };

    ts = FourInALine.Instance.skin.get("button", TextButtonStyle.class);
    back = new IconButton("BACK", FourInALine.Instance.atlas.findRegion("back"), ts, true, false, true);
    back.addListener(cl);
    initFromPrefs();
    table = new Table();
    stage.addActor(table);
  }


  public void initFromPrefs() {
    String sSound = FourInALine.Instance.optionPrefs.getString("SOUND", "Yes");
    sound.setChecked(sSound);
    String sMusic = FourInALine.Instance.optionPrefs.getString("MUSIC", "Yes");
    music.setChecked(sMusic);
    String sVibration = FourInALine.Instance.optionPrefs.getString("VIBRATION", "Yes");
    vibration.setChecked(sVibration);
  }


  public void savePrefs() {
    String sSound = ((IconButton)sound.getChecked()).getText().toString();
    FourInALine.Instance.optionPrefs.putString("SOUND", sSound);
    String sMusic = ((IconButton)music.getChecked()).getText().toString();
    FourInALine.Instance.optionPrefs.putString("MUSIC", sMusic);
    String sVibration = ((IconButton)vibration.getChecked()).getText().toString();
    FourInALine.Instance.optionPrefs.putString("VIBRATION", sVibration);
    FourInALine.Instance.optionPrefs.flush();
    FourInALine.Instance.nativeFunctions.gserviceUpdateState();
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

    table.row().pad(pad);
    table.add().expand();
    table.add(soundLabel).right();
    table.add(soundButtons[0]).height(height).width(width).fill().expandY();
    table.add(soundButtons[1]).height(height).width(width).fill().expandY();
    table.add().expand();

    table.row().pad(pad);
    table.add().expand();
    table.add(musicLabel).right();
    table.add(musicButtons[0]).expandY().fill().width(width).height(height);
    table.add(musicButtons[1]).expandY().fill().width(width).height(height);
    table.add().expand();

    table.row().pad(pad);
    table.add().expand();
    table.add(vibrationLabel).right();
    table.add(vibrationButtons[0]).expandY().fill().height(height).width(width);
    table.add(vibrationButtons[1]).expandY().fill().height(height).width(width);
    table.add().expand();

    table.row().pad(pad);
    table.add().expand().fill();

    table.row().pad(pad);
    Table tbl = new Table();
    tbl.add().expandX();
    tbl.add(back).width(width * 3 / 2).fill().expand();
    tbl.add().expandX();
    table.add(tbl).fill().expand().height(height).colspan(5);
  }
}
