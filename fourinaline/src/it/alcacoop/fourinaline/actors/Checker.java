package it.alcacoop.fourinaline.actors;

import it.alcacoop.fourinaline.FourInALine;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Checker extends Image implements Poolable {

  TextureRegion r1, r2;
  
  public Checker() {
    super();
    r1 = FourInALine.Instance.atlas.findRegion("CSW");
    r2 = FourInALine.Instance.atlas.findRegion("CSB");
    setDrawable(new TextureRegionDrawable(r1));
  }

  public void setColor(int color) {
    if (color==1)
      setDrawable(new TextureRegionDrawable(r1));
    if (color==2)
      setDrawable(new TextureRegionDrawable(r2));
  }

  @Override
  public void reset() {
    setColor(1, 1, 1, 1);
  }
}
