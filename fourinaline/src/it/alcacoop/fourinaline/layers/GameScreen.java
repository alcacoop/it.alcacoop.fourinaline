package it.alcacoop.fourinaline.layers;

import it.alcacoop.fourinaline.actors.Board;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;



public class GameScreen extends BaseScreen {

  private Board board;
  
  public GameScreen() {
    board = new Board(7, 6, 4, stage.getHeight()*0.85f);
    board.setPosition(-stage.getWidth(), (stage.getHeight()-board.getHeight())/2);
    stage.addActor(board);
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(1,1,1, 1);
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    stage.act(delta);
    stage.draw();
  }
 
  @Override
  public void initialize() {
    board.setPosition(-stage.getWidth(), (stage.getHeight()-board.getHeight())/2);
  }
  
  
  @Override
  public void show() {
    super.show();
    Gdx.input.setInputProcessor(stage);
    Gdx.input.setCatchBackKey(true);
    board.addAction(Actions.sequence(Actions.parallel(Actions.fadeIn(animationTime),Actions.moveTo((stage.getWidth()-board.getWidth())/2, (stage.getHeight()-board.getHeight())/2, animationTime))));
    board.initMatch(1);
  }
  
  @Override
  public void fadeOut() {
    board.addAction(Actions.sequence(Actions.parallel(Actions.fadeOut(animationTime),Actions.moveTo(-stage.getWidth(), (stage.getHeight()-board.getHeight())/2, animationTime))));
  }
}
