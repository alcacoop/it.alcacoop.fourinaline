package it.alcacoop.fourinaline.layers;

import it.alcacoop.fourinaline.actors.Board;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;



public class TestScreen extends BaseScreen {

  public TestScreen() {
    
    Board b = new Board(7, 6, 4, stage.getHeight()*0.85f);
    b.setPosition((stage.getWidth()-b.getWidth())/2, (stage.getHeight()-b.getHeight())/2); 
    stage.addActor(b);
    b.initMatch(1);
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(1,1,1, 1);
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    stage.act(delta);
    stage.draw();
  }
 
  @Override
  public void show() {
    super.show();
    Gdx.input.setInputProcessor(stage);
    Gdx.input.setCatchBackKey(true);
  }
  
  @Override
  public void fadeOut() {
    //table.addAction(MyActions.sequence(Actions.parallel(Actions.fadeOut(animationTime),Actions.moveTo(-stage.getWidth(), (stage.getHeight()-table.getHeight())/2, animationTime))));
  }
}
