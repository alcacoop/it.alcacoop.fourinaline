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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import it.alcacoop.fourinaline.FourInALine;


public class SplashScreen extends BaseScreen implements Screen {

  private Stage stage;
  private final Image alca;


  public SplashScreen(String img) {
    //stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
    //stage.setViewport(FourInALine.Instance.resolution[0], FourInALine.Instance.resolution[1], false);

    stage = new Stage(new StretchViewport(FourInALine.Instance.resolution[0], FourInALine.Instance.resolution[1]));

    Texture r = new Texture(Gdx.files.internal(img));
    r.setFilter(TextureFilter.Linear, TextureFilter.Linear);

    alca = new Image(r);
    alca.setX((stage.getWidth() - alca.getWidth()) / 2);
    alca.setY((stage.getHeight() - alca.getHeight()) / 2);
    alca.setColor(1, 1, 1, 0);

    stage.addActor(alca);
  }


  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(1, 1, 1, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    stage.act(delta);
    stage.draw();
  }


  @Override
  public void resize(int width, int height) {
  }


  @Override
  public void show() {
    FourInALine.Instance.snd.stopBGMusic();
    Action r2 = Actions.run(new Runnable() {
      @Override
      public void run() {
        // TODO: GOTO MAIN MENU
        FourInALine.Instance.fsm.start();
      }
    });

    alca.addAction(Actions.sequence(Actions.delay(0.2f), Actions.fadeIn(0.8f), Actions.run(new Runnable() {
      @Override
      public void run() {
        // INIT ASSETS IF NEEDED
      }
    }), Actions.delay(1.5f), Actions.fadeOut(0.8f), Actions.delay(0.2f), r2));
  }

  @Override
  public void hide() {
  }

  @Override
  public void pause() {
  }


  @Override
  public void dispose() {
  }
}
