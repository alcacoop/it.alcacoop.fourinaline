package it.alcacoop.fourinaline;

import it.alcacoop.fourinaline.actors.Board;
import it.alcacoop.fourinaline.fsm.FSM;
import it.alcacoop.fourinaline.layers.BaseScreen;
import it.alcacoop.fourinaline.layers.GameScreen;
import it.alcacoop.fourinaline.layers.MatchOptionsScreen;
import it.alcacoop.fourinaline.layers.MenuScreen;
import it.alcacoop.fourinaline.layers.OptionsScreen;

import java.util.Timer;
import java.util.TimerTask;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class FourInALine extends Game implements ApplicationListener {

  private Timer transitionTimer;
  private int resolutions[][] = { { 1280, 740 }, { 800, 480 }, { 480, 320 } };
  private String[] resname = { "mdpi", "mdpi", "mdpi" };
  public int resolution[];
  public int ss;

  public TextureAtlas atlas;
  public Skin skin;
  public BitmapFont font;
  public BaseScreen currentScreen;

  public MenuScreen menuScreen;
  public GameScreen gameScreen;
  public MatchOptionsScreen matchOptionsScreen;
  public OptionsScreen optionsScreen;

  public Texture wood, mask, btntxt;

  public Board board;

  public FSM fsm;

  public static FourInALine Instance;

  @Override
  public void create() {
    Instance = this;
    // CHECK SCREEN DIM AND SELECT CORRECT ATLAS
    int pWidth = Gdx.graphics.getWidth();
    if (pWidth <= 480)
      ss = 2;
    else if (pWidth <= 800)
      ss = 1;
    else
      ss = 0;
    resolution = resolutions[ss];
    atlas = new TextureAtlas(Gdx.files.internal(resname[ss] + "/pack.atlas"));
    skin = new Skin(Gdx.files.internal(resname[ss] + "/myskin.json"));
    font = new BitmapFont(Gdx.files.internal(resname[ss] + "/checker.fnt"), false);

    wood = new Texture(Gdx.files.internal(resname[ss] + "/texture.jpg"));
    mask = new Texture(Gdx.files.internal(resname[ss] + "/mask.png"));
    btntxt = new Texture(Gdx.files.internal(resname[ss] + "/btn_texture.jpg"));

    transitionTimer = new Timer();

    menuScreen = new MenuScreen();
    gameScreen = new GameScreen();
    matchOptionsScreen = new MatchOptionsScreen();
    optionsScreen = new OptionsScreen();

    board = new Board(7, 6, 4, gameScreen.getStage().getHeight() * 0.8f);
    board.setPosition(-gameScreen.getStage().getWidth(), (gameScreen.getStage().getHeight() - board.getHeight()) / 2);
    gameScreen.getStage().addActor(board);

    fsm = new FSM();
    fsm.start();
  }


  @Override
  public void setScreen(final Screen screen) {
    System.out.println("SET SCREEN");
    if (currentScreen != null) {
      ((BaseScreen)screen).initialize();
      currentScreen.fadeOut();
      TimerTask task = new TimerTask() {
        @Override
        public void run() {
          FourInALine.super.setScreen(screen);
        }
      };
      transitionTimer.schedule(task, (long)(currentScreen.animationTime * 1000));
    } else
      super.setScreen(screen);
    currentScreen = (BaseScreen)screen;
  }


  @Override
  public void pause() {
  }

  @Override
  public void resume() {
  }
}
