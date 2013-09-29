package it.alcacoop.fourinaline.actors;

import it.alcacoop.fourinaline.FourInALine;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Checker extends Group implements Poolable {

  private Image i1, i2, hi;
  
  public Checker() {
    i1 = new Image(FourInALine.Instance.atlas.findRegion("CSW"));
    i2 = new Image(FourInALine.Instance.atlas.findRegion("CSB"));
    hi = new Image(FourInALine.Instance.atlas.findRegion("hi"));
    addActor(i1);
    addActor(i2);
    addActor(hi);
    hi.addAction(Actions.forever(Actions.sequence(
      Actions.fadeOut(0.2f),
      Actions.fadeIn(0.2f)
    )));
  }

  public void setColor(int color) {
    hi.setVisible(false);
    if (color==1) {
      i1.setColor(1,1,1,1);
      i2.setColor(1,1,1,0);
    }
    if (color==2) {
      i1.setColor(1,1,1,0);
      i2.setColor(1,1,1,1);
    }
  }

  public void highlight() {
    hi.setVisible(true);
  }
  
  
  @Override
  public void setWidth(float width) {
    i1.setWidth(width);
    i2.setWidth(width);
    hi.setWidth(width);
  }
  
  @Override
  public void setHeight(float height) {
    i1.setHeight(height);
    i2.setHeight(height);
    hi.setHeight(height);
  }
  
  @Override
  public float getWidth() {
    return i1.getWidth();
  }
  
  @Override
  public float getHeight() {
    return i1.getHeight();
  }
  
  @Override
  public void reset() {
    i1.setColor(1,1,1,0);
    i2.setColor(1,1,1,0);
    hi.setVisible(false);
    setColor(1,1,1,1);
  }

}
