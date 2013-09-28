package it.alcacoop.fourinaline.layers;

import it.alcacoop.fourinaline.FourInALine;
import it.alcacoop.fourinaline.fsm.FSM.Events;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;



public class MenuScreen extends BaseScreen {

  private Table table;
  
  public MenuScreen() {
    table = new Table();
    table.setWidth(stage.getWidth()*0.9f);
    table.setHeight(stage.getHeight()*0.9f);
    table.setPosition((stage.getWidth()-table.getWidth())/2, (stage.getHeight()-table.getHeight())/2);
    
    TextButton sp = new TextButton("Single Play", FourInALine.Instance.skin);
    sp.addListener(new ClickListener(){
      @Override
      public void clicked(InputEvent event, float x, float y) {
        FourInALine.Instance.fsm.processEvent(Events.BUTTON_CLICKED, null);
      }
    });
    table.add(sp);
    
    stage.addActor(table);
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
    table.addAction(Actions.sequence(Actions.parallel(Actions.fadeIn(animationTime),Actions.moveTo((stage.getWidth()-table.getWidth())/2, (stage.getHeight()-table.getHeight())/2, animationTime))));
  }
  
  @Override
  public void fadeOut() {
    System.out.println("FOUT");
    table.addAction(Actions.sequence(Actions.parallel(Actions.fadeOut(animationTime),Actions.moveTo(-stage.getWidth(), (stage.getHeight()-table.getHeight())/2, animationTime))));
  }
}
