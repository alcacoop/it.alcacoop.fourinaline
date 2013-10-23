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
import it.alcacoop.fourinaline.fsm.FSM;
import it.alcacoop.fourinaline.fsm.FSM.Events;
import it.alcacoop.fourinaline.logic.MatchState;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;


public final class UIDialog extends Table {

  private Table t1, t2, t3;
  private IconButton bContinue;
  private IconButton bYes;
  private IconButton bNo;
  private IconButton bCancel;
  private IconButton bExport;

  private Label label;
  private Drawable background;
  private static ClickListener cl;


  private static UIDialog instance;

  private FSM.Events evt;
  private boolean quitWindow = false;
  private boolean leaveWindow = false;
  private boolean visible = false;
  private static float alpha = 0.95f;

  static {
    instance = new UIDialog();
    instance.setSkin(FourInALine.Instance.skin);
  }

  private UIDialog() {
    cl = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        // FourInALine.Instance.snd.playMoveStart(); //TODO
        final String s;
        if (event.getTarget() instanceof Label) {
          s = ((Label)event.getTarget()).getText().toString().toUpperCase();
        } else {
          s = ((IconButton)event.getTarget()).getText().toString().toUpperCase();
        }

        FourInALine.Instance.snd.playButton();
        FourInALine.Instance.vibrate(80);

        instance.addAction(Actions.sequence(Actions.fadeOut(0.3f), Actions.run(new Runnable() {
          @Override
          public void run() {
            instance.remove();
            if (leaveWindow) {
              FourInALine.Instance.fsm.processEvent(instance.evt, s);
              return;
            }
            boolean ret = s.equals("YES") || s.equals("OK");
            if ((instance.quitWindow) && (ret)) {
              Gdx.app.exit();
            } else {
              FourInALine.Instance.fsm.processEvent(instance.evt, ret);
            }
            visible = false;
          }
        })));
      };
    };

    label = new Label("", FourInALine.Instance.skin);

    TextButtonStyle tl = FourInALine.Instance.skin.get("button", TextButtonStyle.class);

    bYes = new IconButton("Yes", null, tl);
    bYes.addListener(cl);
    bNo = new IconButton("No", null, tl);
    bNo.addListener(cl);
    bContinue = new IconButton("Ok", null, tl);
    bContinue.addListener(cl);
    bCancel = new IconButton("Cancel", null, tl);
    bCancel.addListener(cl);

    background = FourInALine.Instance.skin.getDrawable("default-window");
    setBackground(background);

    t1 = new Table();
    t1.setFillParent(true);
    t1.add(label).fill().expand().center();

    t2 = new Table();
    t2.setFillParent(true);
    t2.add().colspan(2).expand();
    t2.add(bContinue).fill().expand();
    t2.add().colspan(2).expand();

    t3 = new Table();
    t3.setFillParent(true);
    t3.add().expand();
    t3.add(bNo).fill().expand();
    t3.add().expand();
    t3.add(bYes).fill().expand();
    t3.add().expand();

    setColor(1, 1, 1, 0);

  }

  private void setText(String t) {
    label.setText(t);
  }


  public static void getYesNoDialog(FSM.Events evt, String text) {
    Stage stage = FourInALine.Instance.currentScreen.getStage();
    instance.visible = true;
    instance.quitWindow = false;
    instance.leaveWindow = false;
    instance.evt = evt;
    instance.remove();
    instance.setText(text);

    float height = stage.getHeight() * 0.4f;
    float width = stage.getWidth() * 0.78f;

    instance.clear();
    instance.setWidth(width);
    instance.setHeight(height);
    instance.setX((stage.getWidth() - width) / 2);
    instance.setY((stage.getHeight() - height) / 2);

    instance.row().padTop(width / 25);
    instance.add(instance.label).colspan(5).expand().align(Align.center);

    instance.row().pad(width / 25);
    instance.add();
    instance.add(instance.bNo).fill().expand().height(height * 0.25f).width(width / 4);
    instance.add();
    instance.add(instance.bYes).fill().expand().height(height * 0.25f).width(width / 4);
    instance.add();

    stage.addActor(instance);
    instance.setY(stage.getHeight());
    instance.addAction(Actions.sequence(Actions.parallel(Actions.color(new Color(1, 1, 1, alpha), 0.2f),
        Actions.moveTo((stage.getWidth() - width) / 2, (stage.getHeight() - height) / 2, 0.2f))));
  }

  public static void getContinueDialog(FSM.Events evt, String text) {
    getContinueDialog(evt, text, UIDialog.alpha);
  }

  public static void getContinueDialog(FSM.Events evt, String text, float alpha) {
    Stage stage = FourInALine.Instance.currentScreen.getStage();
    instance.visible = true;
    instance.quitWindow = false;
    instance.leaveWindow = false;
    instance.evt = evt;
    instance.remove();
    instance.setText(text);

    float height = stage.getHeight() * 0.4f;
    float width = stage.getWidth() * 0.6f;

    instance.clear();
    instance.setWidth(width);
    instance.setHeight(height);
    instance.setX((stage.getWidth() - width) / 2);
    instance.setY((stage.getHeight() - height) / 2);

    instance.row().padTop(width / 25);
    instance.add(instance.label).colspan(3).expand().align(Align.center);

    instance.row().pad(width / 25);
    instance.add();
    instance.add(instance.bContinue).fill().expand().height(height * 0.25f).width(width / 4);
    instance.add();

    stage.addActor(instance);
    instance.setY(stage.getHeight());
    instance.addAction(Actions.sequence(Actions.parallel(Actions.color(new Color(1, 1, 1, alpha), 0.2f),
        Actions.moveTo((stage.getWidth() - width) / 2, (stage.getHeight() - height) / 2, 0.2f))));
  }


  public static void getEndGameDialog(FSM.Events evt, String text, String text1, String score1, String score2) {
    Stage stage = FourInALine.Instance.currentScreen.getStage();
    instance.visible = true;
    instance.quitWindow = false;
    instance.leaveWindow = false;
    instance.evt = evt;
    instance.remove();

    float height = stage.getHeight() * 0.6f;
    float width = stage.getWidth() * 0.6f;

    instance.clear();
    instance.setWidth(width);
    instance.setHeight(height);
    instance.setX((stage.getWidth() - width) / 2);
    instance.setY((stage.getHeight() - height) / 2);

    instance.row().padTop(width / 25);
    instance.add().expand();
    instance.add(text1).colspan(2).expand().align(Align.center);
    instance.add().expand();

    instance.row();
    instance.add().expand();
    instance.add("Overall Score " + text).colspan(2).expand().align(Align.center);
    instance.add().expand();
    instance.row();
    instance.add().expand();
    instance.add(score1).expand().align(Align.center);
    instance.add(score2).expand().align(Align.center);
    instance.add().expand();

    Table t1 = new Table();
    t1.row().expand().fill();
    t1.add();
    t1.add(instance.bContinue).colspan(2).fill().expand().height(height * 0.15f).width(width / 3);
    if ((MatchState.anScore[0] >= MatchState.nMatchTo || MatchState.anScore[1] >= MatchState.nMatchTo) && (MatchState.matchType == 0)) {
      t1.add();
      t1.add(instance.bExport).colspan(2).fill().expand().height(height * 0.15f).width(width / 3);
    }
    t1.add();
    instance.row();
    instance.add(t1).colspan(4).fill().padBottom(width / 25);

    stage.addActor(instance);
    instance.setY(stage.getHeight());
    instance.addAction(Actions.sequence(Actions.parallel(Actions.color(new Color(1, 1, 1, alpha), 0.2f),
        Actions.moveTo((stage.getWidth() - width) / 2, (stage.getHeight() - height) / 2, 0.2f))));
  }


  public static void getFlashDialog(FSM.Events evt, String text) {
    getFlashDialog(evt, text, 1.5f);
  }

  public static void getFlashDialog(FSM.Events evt, String text, float waitTime) {
    Stage stage = FourInALine.Instance.currentScreen.getStage();
    instance.visible = true;
    instance.quitWindow = false;
    instance.leaveWindow = false;
    instance.evt = evt;
    instance.remove();
    instance.setText(text);

    float height = stage.getHeight() * 0.3f;
    float width = stage.getWidth() * 0.75f;

    instance.clear();
    instance.setWidth(width);
    instance.setHeight(height);
    instance.setX((stage.getWidth() - width) / 2);
    instance.setY((stage.getHeight() - height) / 2);

    instance.add(instance.label).expand().align(Align.center);

    stage.addActor(instance);
    instance.addAction(Actions.sequence(Actions.alpha(alpha, 0.3f), Actions.delay(waitTime), Actions.fadeOut(0.3f), Actions.run(new Runnable() {
      @Override
      public void run() {
        instance.remove();
        FourInALine.Instance.fsm.processEvent(instance.evt, true);
      }
    })));
  }


  public static void getQuitDialog() {
    Stage stage = FourInALine.Instance.currentScreen.getStage();
    instance.visible = true;
    instance.quitWindow = true;
    instance.leaveWindow = false;
    instance.remove();
    instance.setText("Really quit the game?");

    float height = stage.getHeight() * 0.4f;
    float width = stage.getWidth() * 0.5f;

    instance.clear();
    instance.setWidth(width);
    instance.setHeight(height);
    instance.setX((stage.getWidth() - width) / 2);
    instance.setY((stage.getHeight() - height) / 2);

    instance.row().padTop(width / 25);
    instance.add(instance.label).colspan(5).expand().align(Align.center);

    instance.row().pad(width / 25);
    instance.add();
    instance.add(instance.bNo).fill().expand().height(height * 0.25f).width(width / 4);
    instance.add();
    instance.add(instance.bYes).fill().expand().height(height * 0.25f).width(width / 4);
    instance.add();

    stage.addActor(instance);
    instance.setY(stage.getHeight());
    instance.addAction(Actions.sequence(Actions.parallel(Actions.color(new Color(1, 1, 1, alpha), 0.2f),
        Actions.moveTo((stage.getWidth() - width) / 2, (stage.getHeight() - height) / 2, 0.2f))));
  }


  public static void getLeaveDialog(FSM.Events evt) {
    Stage stage = FourInALine.Instance.currentScreen.getStage();
    instance.visible = true;
    instance.quitWindow = false;
    instance.leaveWindow = true;
    instance.evt = evt;
    instance.remove();

    instance.setText("You are leaving current match.");

    float height = stage.getHeight() * 0.45f;
    float width = stage.getWidth() * 0.6f;

    instance.clear();
    instance.setWidth(width);
    instance.setHeight(height);
    instance.setX((stage.getWidth() - width) / 2);
    instance.setY((stage.getHeight() - height) / 2);

    instance.row().padTop(width / 25);
    instance.add(instance.label).colspan(7).expand().align(Align.center);
    instance.row().padTop(width / 45);
    instance.add(new Label("Do you want to save it?", FourInALine.Instance.skin)).colspan(7).expand().align(Align.center);

    instance.row().padTop(width / 25);
    instance.add();
    instance.add(instance.bYes).fill().expand().height(height * 0.25f).width(width / 4.5f);
    instance.add();
    instance.add(instance.bNo).fill().expand().height(height * 0.25f).width(width / 4.5f);
    instance.add();
    instance.add(instance.bCancel).fill().expand().height(height * 0.25f).width(width / 4.5f);
    instance.add();

    instance.row().padBottom(width / 35);
    instance.add();


    stage.addActor(instance);
    instance.setY(stage.getHeight());
    instance.addAction(Actions.sequence(Actions.parallel(Actions.color(new Color(1, 1, 1, alpha), 0.2f),
        Actions.moveTo((stage.getWidth() - width) / 2, (stage.getHeight() - height) / 2, 0.2f))));
  }


  public static void getAboutDialog() {
    Stage stage = FourInALine.Instance.currentScreen.getStage();
    instance.visible = true;
    instance.evt = Events.NOOP;
    instance.quitWindow = false;
    instance.leaveWindow = false;
    instance.remove();

    final String gnuBgLink = "http://code.google.com/p/fourinaline";
    final String gplLink = "http://www.gnu.org/licenses/gpl.html";
    final String githubLink1 = "https://github.com/alcacoop/it.alcacoop.fourinaline";
    final String wikipediaLink = "http://en.wikipedia.org/wiki/Connect_Four";

    Table t = new Table();
    t.add(new Label("ABOUT FOUR IN A LINE MOBILE", FourInALine.Instance.skin)).expand();
    t.row();
    t.add(new Label(" ", FourInALine.Instance.skin)).fill().expand();
    t.row();
    t.add(new Label("\"Four in a Line Mobile!\" is based on FourInALine", FourInALine.Instance.skin));
    Label link1 = new Label(gnuBgLink, FourInALine.Instance.skin);
    link1.addListener(new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        FourInALine.Instance.snd.playMove();
        FourInALine.Instance.nativeFunctions.openURL(gnuBgLink);
      };
    });
    t.row();
    t.add(link1);
    t.row();
    t.add(new Label(" ", FourInALine.Instance.skin)).fill().expand();
    t.row();
    t.add(new Label("Its source code is released under a GPLv3 License", FourInALine.Instance.skin));
    Label link2 = new Label(gplLink, FourInALine.Instance.skin);
    link2.addListener(new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        FourInALine.Instance.snd.playMove();
        FourInALine.Instance.nativeFunctions.openURL(gplLink);
      };
    });
    t.row();
    t.add(link2);
    t.row();
    t.add(new Label(" ", FourInALine.Instance.skin)).fill().expand();
    t.row();
    t.add(new Label("and is available on GitHub at:", FourInALine.Instance.skin));
    Label link3 = new Label(githubLink1, FourInALine.Instance.skin);
    link3.addListener(new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        FourInALine.Instance.snd.playMove();
        FourInALine.Instance.nativeFunctions.openURL(githubLink1);
      };
    });

    t.row();
    t.add(link3);

    t.row();
    t.add(new Label(" ", FourInALine.Instance.skin)).fill().expand();
    t.row();
    t.add(new Label("You can find a detailed description of game rules on Wikipedia:", FourInALine.Instance.skin));
    Label link5 = new Label(wikipediaLink, FourInALine.Instance.skin);
    link5.addListener(new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        FourInALine.Instance.snd.playMove();
        FourInALine.Instance.nativeFunctions.openURL(wikipediaLink);
      };
    });
    t.row();
    t.add(link5);
    t.row();
    t.add(new Label(" ", FourInALine.Instance.skin)).fill().expand();
    t.row();
    t.add(new Label("If you enjoy our game, support us rating on the Play Store", FourInALine.Instance.skin));
    t.row();
    t.add(new Label(" ", FourInALine.Instance.skin)).fill().expand();
    t.row();
    t.add(new Label("Copyright 2013 - Alca Soc. Coop.", FourInALine.Instance.skin));


    ScrollPane sc = new ScrollPane(t, FourInALine.Instance.skin);
    sc.setFadeScrollBars(false);
    sc.setOverscroll(false, false);

    float height = stage.getHeight() * 0.85f;
    float width = stage.getWidth() * 0.95f;

    instance.clear();
    instance.row().padTop(width / 25);
    instance.add(sc).colspan(3).expand().fill().align(Align.center).padTop(width / 25).padLeft(width / 35).padRight(width / 35);

    instance.row().pad(width / 25);
    instance.add();
    instance.add(instance.bContinue).fill().expand().height(height * 0.15f).width(width / 4);
    instance.add();

    instance.setWidth(width);
    instance.setHeight(height);
    instance.setX((stage.getWidth() - width) / 2);

    stage.addActor(instance);
    instance.setY(stage.getHeight());
    instance.addAction(
        Actions.sequence(Actions.parallel(Actions.color(new Color(1, 1, 1, alpha), 0.2f), Actions.moveTo((stage.getWidth() - width) / 2, (stage.getHeight() - height) / 2, 0.2f))));
  }


  public static boolean isOpened() {
    return instance.hasParent();
  }


  public static void setButtonsStyle(String b) {
  }


  public Actor hit(float x, float y, boolean touchable) {
    Actor hit = super.hit(x, y, touchable);
    if (visible) {
      if (hit != null)
        return hit;
      else {
        return this;
      }

    } else {
      return hit;
    }
  }
}
