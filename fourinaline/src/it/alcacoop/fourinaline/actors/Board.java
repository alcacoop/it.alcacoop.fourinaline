package it.alcacoop.fourinaline.actors;

import it.alcacoop.fourinaline.FourInALine;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class Board extends Group {

  private Table mask;
  private float twidth, theight, dim;
  private Image bbg;
  
  int wx;
  int wy;
  
  public Board(int _wx, int _wy, float _height) {
    super();
    wx = _wx;
    wy = _wy;
    
    NinePatch patch = null;
    TextureRegion r = FourInALine.Instance.atlas.findRegion("bbg");
    int[] splits = ((AtlasRegion)r).splits;
    patch = new NinePatch(r, splits[0], splits[1], splits[2], splits[3]);
    
    dim = (_height-splits[2]-splits[3])/wy;
    
    bbg = new Image(patch);
    bbg.setWidth(dim*wx+splits[0]+splits[1]);
    bbg.setHeight(dim*wy+splits[2]+splits[3]);
    bbg.setPosition(0,0); 
    addActor(bbg);
    
    Image p1 = new Image(FourInALine.Instance.atlas.findRegion("CSW"));
    p1.setWidth(dim);
    p1.setHeight(dim);
    Image p2 = new Image(FourInALine.Instance.atlas.findRegion("CSB"));
    p2.setWidth(dim);
    p2.setHeight(dim);
    Image p3 = new Image(FourInALine.Instance.atlas.findRegion("CSW"));
    p3.setWidth(dim);
    p3.setHeight(dim);
    addActor(p1);
    addActor(p2);
    addActor(p3);
    
    mask = new Table();
    twidth = dim*wx;
    theight = dim*wy;
    
    mask.setWidth(twidth);
    mask.setHeight(theight);
    mask.setPosition(splits[0], splits[3]);
    
    for (int i=0;i<wy;i++) {
      for (int j=0;j<wx;j++) {
        mask.add(new Image(FourInALine.Instance.atlas.findRegion("hole"))).width(dim).height(dim).expand().fill();
      }
      mask.row();
    }
    
    addActor(mask);
    
    
    p1.setPosition(mask.getX(), mask.getY());
    p2.setPosition(mask.getX()+dim, mask.getY());
    p3.setPosition(mask.getX()+dim, mask.getY()+dim);
    
  }
  
  @Override
  public float getWidth() {
    return bbg.getWidth();
  }
  
  @Override
  public float getHeight() {
    return bbg.getHeight();
  }

}
