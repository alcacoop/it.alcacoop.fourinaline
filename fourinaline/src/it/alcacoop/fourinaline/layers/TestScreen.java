package it.alcacoop.fourinaline.layers;

import it.alcacoop.fourinaline.actors.Board;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;



public class TestScreen extends BaseScreen {

  public TestScreen() {
    
    Board b = new Board(5, 4, stage.getHeight()*0.9f);
    b.setPosition((stage.getWidth()-b.getWidth())/2, (stage.getHeight()-b.getHeight())/2); 
    stage.addActor(b);
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(1,1,1, 1);
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    stage.act(delta);
    stage.draw();
  }
  
  @Override
  public void fadeOut() {
    //table.addAction(MyActions.sequence(Actions.parallel(Actions.fadeOut(animationTime),Actions.moveTo(-stage.getWidth(), (stage.getHeight()-table.getHeight())/2, animationTime))));
  }
}
