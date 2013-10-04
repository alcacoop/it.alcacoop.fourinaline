package it.alcacoop.fourinaline.layers;

import it.alcacoop.fourinaline.FourInALine;
import it.alcacoop.fourinaline.actors.UIDialog;
import it.alcacoop.fourinaline.fsm.FSM.Events;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;


public class GameScreen extends BaseScreen {

  public GameScreen() {
    stage.addListener(new InputListener() {
      @Override
      public boolean keyDown(InputEvent event, int keycode) {
        if (Gdx.input.isKeyPressed(Keys.BACK) || Gdx.input.isKeyPressed(Keys.ESCAPE)) {
          if (UIDialog.isOpened())
            return false;
          UIDialog.getYesNoDialog(Events.LEAVE_MATCH, "Really leve current match?");
        }
        return super.keyDown(event, keycode);
      }
    });
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(1, 1, 1, 1);
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    stage.act(delta);
    stage.draw();
  }

  @Override
  public void initialize() {
    FourInALine.Instance.board.setPosition(-stage.getWidth(), (stage.getHeight() - FourInALine.Instance.board.getHeight()) / 2);
  }


  @Override
  public void show() {
    super.show();
    Gdx.input.setInputProcessor(stage);
    Gdx.input.setCatchBackKey(true);
    FourInALine.Instance.board.addAction(Actions.sequence(
        Actions.parallel(Actions.fadeIn(animationTime),
            Actions.moveTo((stage.getWidth() - FourInALine.Instance.board.getWidth()) / 2, (stage.getHeight() - FourInALine.Instance.board.getHeight()) / 2, animationTime)),
        Actions.run(new Runnable() {
          @Override
          public void run() {
            FourInALine.Instance.fsm.processEvent(Events.START_GAME, null);
          }
        })));
  }

  @Override
  public void fadeOut() {
    FourInALine.Instance.board.addAction(Actions.sequence(Actions.parallel(Actions.fadeOut(animationTime),
        Actions.moveTo(-stage.getWidth(), (stage.getHeight() - FourInALine.Instance.board.getHeight()) / 2, animationTime))));
  }
}
