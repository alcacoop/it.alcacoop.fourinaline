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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class BaseScreen implements Screen {

  protected Stage stage;
  protected Image bgImg;
  public float animationTime = 0.2f;
  private Image alca, top;
  private TextButton alcaBtn;

  public BaseScreen() {
    // STAGE DIM = SCREEN RES
    // stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
    // VIEWPORT DIM = VIRTUAL RES (ON SELECTED TEXTURE BASIS)
    // stage.setViewport(FourInALine.Instance.resolution[0], FourInALine.Instance.resolution[1], false);

    stage = new Stage(new StretchViewport(FourInALine.Instance.resolution[0], FourInALine.Instance.resolution[1]));

    TextureRegion bgRegion = FourInALine.Instance.atlas.findRegion("bg");
    bgImg = new Image(bgRegion);
    bgImg.setWidth(stage.getWidth());
    bgImg.setHeight(stage.getHeight());
    bgImg.setPosition(0, 0);


    NinePatch patch = null;
    TextureRegion r = FourInALine.Instance.atlas.findRegion("alca");
    int[] splits = ((AtlasRegion)r).splits;
    patch = new NinePatch(r, splits[0], splits[1], splits[2], splits[3]);

    alca = new Image(patch);
    alca.setWidth(stage.getWidth());
    alca.setPosition(0, 0);


    NinePatch patch2 = null;
    TextureRegion r2 = FourInALine.Instance.atlas.findRegion("topborder");
    splits = ((AtlasRegion)r2).splits;
    patch2 = new NinePatch(r2, splits[0], splits[1], splits[2], splits[3]);

    top = new Image(patch2);
    top.setWidth(stage.getWidth());
    top.setPosition(0, stage.getHeight() - top.getHeight());


    alcaBtn = new TextButton("", FourInALine.Instance.skin);
    alcaBtn.setWidth(alca.getHeight());
    alcaBtn.setHeight(alca.getHeight());
    alcaBtn.setX(stage.getWidth() - alcaBtn.getHeight());
    alcaBtn.setColor(0, 0, 0, 0);
    alcaBtn.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        FourInALine.Instance.nativeFunctions.openURL("market://search?q=pub:Alca Soc. Coop.");
      }
    });


    stage.addActor(bgImg);
    stage.addActor(alca);
    stage.addActor(top);
    stage.addActor(alcaBtn);
  }

  @Override
  public void resize(int width, int height) {
    bgImg.setWidth(stage.getWidth());
    bgImg.setHeight(stage.getHeight());
  }

  @Override
  public void show() {
    alca.setColor(1, 1, 1, 1);
    top.setColor(1, 1, 1, 1);
    stage.addActor(alcaBtn);
    if (!FourInALine.Instance.snd.isBGPlaying())
      FourInALine.Instance.snd.playBGMusic();
  }

  public void initialize() {
  }


  public Stage getStage() {
    return stage;
  }

  public void fadeOut() {
  }

  @Override
  public void render(float delta) {
  }

  @Override
  public void hide() {
  }

  @Override
  public void pause() {
  }

  @Override
  public void resume() {
  }

  @Override
  public void dispose() {
  }
}
