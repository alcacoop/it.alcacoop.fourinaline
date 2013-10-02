package it.alcacoop.fourinaline.layers;

import it.alcacoop.fourinaline.FourInALine;
import it.alcacoop.fourinaline.actors.IconButton;
import it.alcacoop.fourinaline.fsm.FSM.Events;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class MenuScreen extends BaseScreen {

  private Table table;

  public MenuScreen() {
    table = new Table();
    table.setWidth(stage.getWidth() * 0.7f);
    table.setHeight(stage.getHeight() * 0.9f);

    float height = table.getHeight() / 7;
    float width = table.getWidth() / 2;
    float pad = height / 55;

    table.setPosition((stage.getWidth() - table.getWidth()) / 2, (stage.getHeight() - table.getHeight()) / 2);

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

    stage.addActor(table);
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
    table.addAction(Actions.sequence(Actions.parallel(Actions.fadeIn(animationTime),
        Actions.moveTo((stage.getWidth() - table.getWidth()) / 2, (stage.getHeight() - table.getHeight()) / 2, animationTime))));
  }

  @Override
  public void fadeOut() {
    table
        .addAction(Actions.sequence(Actions.parallel(Actions.fadeOut(animationTime), Actions.moveTo(-stage.getWidth(), (stage.getHeight() - table.getHeight()) / 2, animationTime))));
  }
}
