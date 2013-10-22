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

package it.alcacoop.fourinaline;

import it.alcacoop.fourinaline.actors.Board;
import it.alcacoop.fourinaline.fsm.FSM;
import it.alcacoop.fourinaline.layers.BaseScreen;
import it.alcacoop.fourinaline.layers.GameScreen;
import it.alcacoop.fourinaline.layers.MatchOptionsScreen;
import it.alcacoop.fourinaline.layers.MenuScreen;
import it.alcacoop.fourinaline.layers.OptionsScreen;
import it.alcacoop.fourinaline.layers.SplashScreen;

import java.util.Timer;
import java.util.TimerTask;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class FourInALine extends Game implements ApplicationListener {

  private Timer transitionTimer;
  private int resolutions[][] = { { 1280, 740 }, { 800, 480 }, { 480, 320 } };
  private String[] resname = { "hdpi", "mdpi", "ldpi" };
  public int resolution[];
  public int ss;

  public TextureAtlas atlas;
  public Skin skin;
  public BitmapFont font;
  public BaseScreen currentScreen;

  public MenuScreen menuScreen;
  public GameScreen gameScreen;
  public MatchOptionsScreen matchOptionsScreen;
  public OptionsScreen optionsScreen;

  public SoundManager snd;
  public NativeFunctions nativeFunctions;

  public Texture wood, mask, btntxt;

  public Board board;

  public FSM fsm;

  public Preferences optionPrefs, matchOptionPrefs, gameOptionPrefs;

  public static FourInALine Instance;

  public FourInALine(NativeFunctions n) {
    nativeFunctions = n;
  }

  @Override
  public void create() {
    Instance = this;
    optionPrefs = Gdx.app.getPreferences("Options");
    matchOptionPrefs = Gdx.app.getPreferences("MatchOptions");
    gameOptionPrefs = Gdx.app.getPreferences("GameOptions");
    snd = new SoundManager();

    // CHECK SCREEN DIM AND SELECT CORRECT ATLAS
    int pWidth = Gdx.graphics.getWidth();
    if (pWidth <= 480)
      ss = 2;
    else if (pWidth <= 800)
      ss = 1;
    else
      ss = 0;
    resolution = resolutions[ss];
    atlas = new TextureAtlas(Gdx.files.internal(resname[ss] + "/pack.atlas"));
    skin = new Skin(Gdx.files.internal(resname[ss] + "/myskin.json"));
    font = new BitmapFont(Gdx.files.internal(resname[ss] + "/checker.fnt"), false);
    font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);


    wood = new Texture(Gdx.files.internal(resname[ss] + "/texture.jpg"));
    mask = new Texture(Gdx.files.internal(resname[ss] + "/mask.png"));
    btntxt = new Texture(Gdx.files.internal(resname[ss] + "/btn_texture.jpg"));

    transitionTimer = new Timer();

    menuScreen = new MenuScreen();
    gameScreen = new GameScreen();
    matchOptionsScreen = new MatchOptionsScreen();
    optionsScreen = new OptionsScreen();

    board = new Board(7, 6, 4, gameScreen.getStage().getHeight() * 0.76f);
    board.setPosition(-gameScreen.getStage().getWidth(), (gameScreen.getStage().getHeight() - board.getHeight()) / 2);
    gameScreen.getStage().addActor(board);

    fsm = new FSM();
    setScreen(new SplashScreen(resname[ss] + "/alca.png"));
  }

  @Override
  public void setScreen(final Screen screen) {
    System.out.println("SET SCREEN");
    if (currentScreen != null) {
      ((BaseScreen)screen).initialize();
      currentScreen.fadeOut();
      TimerTask task = new TimerTask() {
        @Override
        public void run() {
          FourInALine.super.setScreen(screen);
        }
      };
      transitionTimer.schedule(task, (long)(currentScreen.animationTime * 1000));
    } else
      super.setScreen(screen);
    currentScreen = (BaseScreen)screen;
  }


  @Override
  public void pause() {
  }

  @Override
  public void resume() {
  }

  public String getResName() {
    return resname[ss];
  }

  public void vibrate(int millisecs) {
    if (optionPrefs.getString("VIBRATION", "Yes").equals("Yes"))
      Gdx.input.vibrate(millisecs);
  }
}
