package it.alcacoop.fourinaline.actors;

import java.util.Iterator;
import java.util.List;

import org.gojul.fourinaline.model.AlphaBeta;
import org.gojul.fourinaline.model.DefaultEvalScore;
import org.gojul.fourinaline.model.GameModel;
import org.gojul.fourinaline.model.GameModel.CellCoord;
import org.gojul.fourinaline.model.GameModel.GameStatus;

import it.alcacoop.fourinaline.FourInALine;
import it.alcacoop.fourinaline.fsm.FSM.Events;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Pool;

public class Board extends Group {

  private Table mask;
  private float twidth, theight, dim;
  private Image bbg;
  private Group checkersLayer;
  
  private GameModel gameModel;
  private AlphaBeta alphaBeta;
  
  private Pool<Checker> checkers;
  
  private int color;
  private boolean locked;
  
  int wx;
  int wy;
  int winLength;
  
  public Board(final int wx, final int wy, final int winLength, final float height) {
    super();
    this.wx = wx;
    this.wy = wy;
    this.winLength = winLength;
    
    checkers = new Pool<Checker>(wx*wy) {
      @Override
      protected Checker newObject() {
        return new Checker();
      }
    };
    
    NinePatch patch = null;
    TextureRegion r = FourInALine.Instance.atlas.findRegion("bbg");
    int[] splits = ((AtlasRegion)r).splits;
    patch = new NinePatch(r, splits[0], splits[1], splits[2], splits[3]);
    
    dim = (height-splits[2]-splits[3])/wy;
    
    bbg = new Image(patch);
    bbg.setWidth(dim*wx+splits[0]+splits[1]);
    bbg.setHeight(dim*wy+splits[2]+splits[3]);
    bbg.setPosition(0,0); 
    addActor(bbg);

    checkersLayer = new Group();
    checkersLayer.setWidth(dim*wx);
    checkersLayer.setHeight(dim*(wy+1));
    checkersLayer.setPosition(splits[0], splits[3]);
    addActor(checkersLayer);
    
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

    mask.addListener(new ClickListener(){
      @Override
      public void clicked(InputEvent event, float x, float y) {
        if ((!locked)&&(color==1)) {
          int cx = (int) Math.ceil((x/dim))-1;
          play(cx);
        }
      }
    });
  }
  
  
  public void initMatch(int who) {
    gameModel = new GameModel(wy, wx, winLength, who);
    System.out.println("START GAME: "+gameModel.getCurrentPlayer());
    alphaBeta = new AlphaBeta(new DefaultEvalScore(), 3, 1);
    
    locked = false;
    
    color = gameModel.getCurrentPlayer().hashCode();
    if (color==2) {
      int a = alphaBeta.getColumnIndex(gameModel, gameModel.getCurrentPlayer());
      play(a);
    }
    
  }
  
  
  public boolean play(int col) {
    int row=gameModel.getFreeRowIndexForColumn(col);
    if (row==-1) return false;
    
    locked = true;
    int dist = row+1;
    row = wy-row;
    
    gameModel.play(col, gameModel.getCurrentPlayer());
    
    Checker c = checkers.obtain();
    c.setColor(color);
    c.setWidth(dim);
    c.setHeight(dim);
    checkersLayer.addActor(c);
    c.setPosition(dim*col, checkersLayer.getHeight());
    
    c.addAction(
      Actions.sequence(
        Actions.moveTo(dim*col, dim*(row-1), dist*0.1f),
        Actions.run(new Runnable() {
          @Override
          public void run() {
            locked = false;
            if (color==1) color=2;
            else color = 1;
            if (gameModel.getGameStatus()==GameStatus.WON_STATUS) {
              System.out.println("PARTITA VINTA!");
              List<CellCoord> l = gameModel.getWinLine();
              Iterator<CellCoord> iterator = l.iterator();
              while (iterator.hasNext()) {
                System.out.println(iterator.next().getColIndex()+":"+iterator.next().getRowIndex());
              }
              locked = true;
              FourInALine.Instance.fsm.processEvent(Events.GAME_TERMINATED, 1);
            }
            else if (gameModel.getGameStatus()==GameStatus.TIE_STATUS) {
              System.out.println("PAREGGIO!");
              locked = true;
              FourInALine.Instance.fsm.processEvent(Events.GAME_TERMINATED, 0);
            }
            else if (gameModel.getCurrentPlayer().hashCode()==2) {
              int a = alphaBeta.getColumnIndex(gameModel, gameModel.getCurrentPlayer());
              play(a);
            }
          }
        })
    ));
    
    System.out.println(gameModel+" "+gameModel.getGameStatus());
    return true;
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
