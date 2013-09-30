package it.alcacoop.fourinaline.layers;

import it.alcacoop.fourinaline.actors.Board;
import it.alcacoop.fourinaline.logic.MatchState;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;



public class GameScreen extends BaseScreen {

  public Board board;

  public GameScreen() {
    board = new Board(7, 6, 4, stage.getHeight() * 0.85f);
    board.setPosition(-stage.getWidth(), (stage.getHeight() - board.getHeight()) / 2);
    stage.addActor(board);
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
    board.setPosition(-stage.getWidth(), (stage.getHeight() - board.getHeight()) / 2);
  }


  @Override
  public void show() {
    super.show();
    Gdx.input.setInputProcessor(stage);
    Gdx.input.setCatchBackKey(true);
    board.addAction(Actions.sequence(Actions.parallel(Actions.fadeIn(animationTime),Actions.moveTo((stage.getWidth()-board.getWidth())/2, (stage.getHeight()-board.getHeight())/2, animationTime))));

    int[] a = {1, 2};
    if (MatchState.gamesIntoMatch == 1)
      MatchState.whoStart = new Random().nextInt(2)+1;
    else {
      MatchState.whoStart = ((MatchState.whoStart-1) == 0) ? a[1] : a[0];
    }
    MatchState.nMatchTo = 3; //TODO: leggere da preferences
    MatchState.gameLevel = 1; //TODO: leggere da preferences
    board.initMatch(MatchState.whoStart);
  }
  
  @Override
  public void fadeOut() {
    board
        .addAction(Actions.sequence(Actions.parallel(Actions.fadeOut(animationTime), Actions.moveTo(-stage.getWidth(), (stage.getHeight() - board.getHeight()) / 2, animationTime))));
  }
}
