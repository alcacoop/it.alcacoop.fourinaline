package it.alcacoop.fourinaline.actors;

import it.alcacoop.fourinaline.FourInALine;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Checker extends Group implements Poolable {

  Image i1, i2;
  
  public Checker() {
    i1 = new Image(FourInALine.Instance.atlas.findRegion("CSW"));
    i2 = new Image(FourInALine.Instance.atlas.findRegion("CSB"));
    
    addActor(i1);
    addActor(i2);
  }

  public void setColor(int color) {
    System.out.println("COLOR: "+color);
    if (color==1) {
      i1.setColor(1,1,1,1);
      i2.setColor(1,1,1,0);
    }
    if (color==2) {
      i1.setColor(1,1,1,0);
      i2.setColor(1,1,1,1);
    }
  }

  
  @Override
  public void setWidth(float width) {
    i1.setWidth(width);
    i2.setWidth(width);
  }
  
  @Override
  public void setHeight(float height) {
    i1.setHeight(height);
    i2.setHeight(height);
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
  }

}
