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
import it.alcacoop.fourinaline.actors.IconButton;
import it.alcacoop.fourinaline.actors.UIDialog;
import it.alcacoop.fourinaline.fsm.FSM.Events;
import it.alcacoop.fourinaline.fsm.FSM.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;


public class MenuScreen extends BaseScreen {

  private Table table, buttonGroup;
  private ImageButton scoreboards, achievements, gplus, twitter, facebook;

  public MenuScreen() {
    stage.addListener(new InputListener() {
      @Override
      public boolean keyDown(InputEvent event, int keycode) {
        if (Gdx.input.isKeyPressed(Keys.BACK) || Gdx.input.isKeyPressed(Keys.ESCAPE)) {
          if (UIDialog.isOpened())
            return false;
          FourInALine.Instance.snd.playButton();
          FourInALine.Instance.vibrate(80);
          UIDialog.getYesNoDialog(Events.LEAVE_GAME, "Really quit Four in a Line Mobile?");
        }
        return super.keyDown(event, keycode);
      }
    });


    table = new Table();
    table.setWidth(stage.getWidth() * 0.66f);
    table.setHeight(stage.getHeight() * 0.9f);

    float height = table.getHeight() / 7;
    float width = table.getWidth() / 2;
    float pad = height / 55;

    table.setPosition(-table.getWidth(), (stage.getHeight() - table.getHeight()) / 2);

    ClickListener cl = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        FourInALine.Instance.fsm.processEvent(Events.BUTTON_CLICKED, ((IconButton)event.getListenerActor()).getText().toString().toUpperCase());
      };
    };


    TextButtonStyle st = FourInALine.Instance.skin.get("mainmenu", TextButtonStyle.class);
    IconButton sp = new IconButton("Single Player", FourInALine.Instance.atlas.findRegion("sp"), st);
    sp.addListener(cl);
    IconButton tp = new IconButton("Two Players", FourInALine.Instance.atlas.findRegion("dp"), st);
    tp.addListener(cl);
    IconButton gp = new IconButton("Online Multiplayer", FourInALine.Instance.atlas.findRegion("gpl"), st);
    gp.addListener(cl);

    IconButton op = new IconButton("Options", FourInALine.Instance.atlas.findRegion("opt"), st);
    op.addListener(cl);

    IconButton ri = new IconButton("Rate it!", FourInALine.Instance.atlas.findRegion("str"), st);
    ri.addListener(cl);
    IconButton ab = new IconButton("About..", FourInALine.Instance.atlas.findRegion("abt"), st);
    ab.addListener(cl);

    table.add(new Image(FourInALine.Instance.atlas.findRegion("logo"))).expand().colspan(2);

    table.row().pad(pad);
    table.add().colspan(2).fill().expand();

    table.row().pad(pad);
    table.add(sp).fill().expandX().height(height).colspan(2);

    table.row().pad(pad);
    table.add(tp).fill().expandX().height(height).width(width);
    table.add(gp).fill().expandX().height(height).width(width);

    table.row();
    table.add().colspan(2).fill().expand();

    table.row().pad(pad);
    table.add(op).fill().expandX().height(height).colspan(2);


    table.row();
    table.add().colspan(2).fill().expand();

    table.row().pad(pad);
    table.add(ri).fill().expandX().height(height).width(width);
    table.add(ab).fill().expandX().height(height).width(width);

    if (true) { // TODO: isPremium
      IconButton ad = new IconButton("Remove Ads", FourInALine.Instance.atlas.findRegion("pro"), st);
      ad.addListener(cl);
      table.row().pad(pad);
      table.add(ad).fill().expandX().height(height).colspan(2);
    }

    table.row();
    table.add().colspan(2).fill().expand();


    scoreboards = new ImageButton(new TextureRegionDrawable(FourInALine.Instance.atlas.findRegion("leaderboards")));
    scoreboards.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        FourInALine.Instance.snd.playButton();
        FourInALine.Instance.vibrate(80);
        FourInALine.Instance.nativeFunctions.gserviceOpenLeaderboards();
      }
    });
    achievements = new ImageButton(new TextureRegionDrawable(FourInALine.Instance.atlas.findRegion("achievements")));
    achievements.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        FourInALine.Instance.snd.playButton();
        FourInALine.Instance.vibrate(80);
        FourInALine.Instance.nativeFunctions.gserviceOpenAchievements();
      }
    });

    gplus = new ImageButton(new TextureRegionDrawable(FourInALine.Instance.atlas.findRegion("gplus")));
    twitter = new ImageButton(new TextureRegionDrawable(FourInALine.Instance.atlas.findRegion("twitter")));
    facebook = new ImageButton(new TextureRegionDrawable(FourInALine.Instance.atlas.findRegion("facebook")));

    gplus.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        FourInALine.Instance.snd.playButton();
        FourInALine.Instance.vibrate(80);
        FourInALine.Instance.nativeFunctions.openURL("https://plus.google.com/105593457876935389170/posts");
      }
    });
    twitter.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        FourInALine.Instance.snd.playButton();
        FourInALine.Instance.vibrate(80);
        FourInALine.Instance.nativeFunctions.openURL("twitter://user?screen_name=alcamobile", "http://mobile.twitter.com/alcamobile");
      }
    });
    facebook.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        FourInALine.Instance.snd.playButton();
        FourInALine.Instance.vibrate(80);
        FourInALine.Instance.nativeFunctions.openURL("fb://page/229928153837363", "https://m.facebook.com/pages/Alca-Mobile/229928153837363");
      }
    });

    buttonGroup = new Table();
    buttonGroup.setWidth(gplus.getWidth());
    buttonGroup.setHeight(gplus.getHeight() * 6);
    buttonGroup.add(achievements).width(gplus.getWidth()).height(gplus.getHeight()).fill();
    buttonGroup.row().spaceTop(0);
    buttonGroup.add(scoreboards).width(gplus.getWidth()).height(gplus.getHeight()).fill();
    buttonGroup.row().spaceTop(gplus.getHeight() / 3);
    buttonGroup.add(gplus).width(gplus.getWidth()).height(gplus.getHeight()).fill();
    buttonGroup.row().spaceTop(0);
    buttonGroup.add(twitter).width(gplus.getWidth()).height(gplus.getHeight()).fill();
    buttonGroup.row().spaceTop(0);
    buttonGroup.add(facebook).width(facebook.getWidth()).height(facebook.getHeight()).fill();
    buttonGroup.setPosition(gplus.getWidth() / 2, -stage.getHeight());

    stage.addActor(table);
    stage.addActor(buttonGroup);
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(1, 1, 1, 1);
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    stage.act(delta);
    stage.draw();
  }


  @Override
  public void show() {
    super.show();
    Gdx.input.setInputProcessor(stage);
    Gdx.input.setCatchBackKey(true);
    buttonGroup.addAction(Actions.sequence(Actions.parallel(Actions.fadeIn(0.2f), Actions.moveTo(gplus.getWidth() / 4, (stage.getHeight() - buttonGroup.getHeight()) / 2, 0.2f))));
    table.addAction(Actions.sequence(
        Actions.parallel(Actions.fadeIn(animationTime), Actions.moveTo((stage.getWidth() - table.getWidth()) / 2, (stage.getHeight() - table.getHeight()) / 2, animationTime)),
        Actions.run(new Runnable() {
          @Override
          public void run() {
            if (FourInALine.Instance.fsm.previousState == States.CHECK_END_MATCH) {
              FourInALine.Instance.nativeFunctions.showInterstitial();
            }
          }
        })));
  }

  @Override
  public void fadeOut() {
    buttonGroup.addAction(Actions.sequence(Actions.parallel(Actions.fadeOut(0.2f), Actions.moveTo(gplus.getWidth() / 4, -stage.getHeight(), 0.2f))));
    table
        .addAction(Actions.sequence(Actions.parallel(Actions.fadeOut(animationTime), Actions.moveTo(-stage.getWidth(), (stage.getHeight() - table.getHeight()) / 2, animationTime))));
  }
}
