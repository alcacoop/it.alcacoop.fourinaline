package it.alcacoop.fourinaline.layers;

import it.alcacoop.fourinaline.FourInALine;
import it.alcacoop.fourinaline.actors.IconButton;
import it.alcacoop.fourinaline.actors.PlayerBlock;
import it.alcacoop.fourinaline.actors.UIDialog;
import it.alcacoop.fourinaline.fsm.FSM.Events;
import it.alcacoop.fourinaline.logic.MatchState;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class GameScreen extends BaseScreen {

  private Table table;
  private PlayerBlock players[];
  private Label nMatchTo;
  private IconButton leave, resign;

  public GameScreen() {
    players = new PlayerBlock[2];
    players[0] = new PlayerBlock();
    players[0].setName("YOU");
    players[0].setColor(1);
    players[1] = new PlayerBlock();
    players[1].setName("CPU (L" + MatchState.AILevel + ")");
    players[1].setColor(2);
    nMatchTo = new Label("", FourInALine.Instance.skin);

    ClickListener clBack = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        if (UIDialog.isOpened())
          return;
        UIDialog.getYesNoDialog(Events.LEAVE_MATCH, "Really leave current match?");
      };
    };

    ClickListener clResign = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        if (UIDialog.isOpened())
          return;
        UIDialog.getYesNoDialog(Events.RESIGN_GAME, "Really resign current game?");
      };
    };

    TextButtonStyle st = FourInALine.Instance.skin.get("button", TextButtonStyle.class);
    leave = new IconButton("", FourInALine.Instance.atlas.findRegion("back"), st, true, false, false);
    leave.addListener(clBack);
    resign = new IconButton("", FourInALine.Instance.atlas.findRegion("resign"), st, true, false, false);
    resign.addListener(clResign);

    table = new Table();
    table.setWidth(stage.getWidth() * 0.9f);
    table.setHeight(stage.getHeight() * 0.78f);
    table.debug();
    stage.addListener(new InputListener() {
      @Override
      public boolean keyDown(InputEvent event, int keycode) {
        if (Gdx.input.isKeyPressed(Keys.BACK) || Gdx.input.isKeyPressed(Keys.ESCAPE)) {
          if (UIDialog.isOpened())
            return false;
          UIDialog.getYesNoDialog(Events.LEAVE_MATCH, "Really leave current match?");
        }
        return super.keyDown(event, keycode);
      }
    });
    stage.addActor(table);
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(1, 1, 1, 1);
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    stage.act(delta);
    stage.draw();
    // Table.drawDebug(stage);
  }

  @Override
  public void initialize() {
    table.clear();

    nMatchTo.setText("MATCH TO: " + MatchState.nMatchTo);
    nMatchTo.setColor(136f / 255f, 74f / 255f, 36f / 255f, 1f);

    System.out.println(nMatchTo.getHeight());

    players[0].setScore(0);
    players[1].setScore(0);

    if (MatchState.matchType == 0) {
      players[0].setName("YOU");
      players[1].setName("CPU (L" + MatchState.AILevel + ")");
    } else if (MatchState.matchType == 1) {
      players[0].setName("PLAYER1");
      players[1].setName("PLAYER2");
    } else {
      players[0].setName("YOU");
      players[1].setName("OPPONENT");
    }


    Table tp = new Table();
    tp.debug();
    tp.setFillParent(false);
    tp.add(nMatchTo).expandX().colspan(2);

    tp.row();
    tp.add().height(table.getHeight() / 40).colspan(2);

    tp.row();
    tp.add(players[0]).left().fill().expandX().colspan(2);

    tp.row();
    tp.add().height(table.getHeight() / 22).colspan(2);

    tp.row();
    tp.add(players[1]).fill().left().expandX().colspan(2);
    tp.row();
    tp.add().expand().colspan(2);

    tp.row();
    tp.add(leave).expandX().fill().padLeft(table.getHeight() / 80).padRight(table.getHeight() / 80);
    tp.add(resign).expandX().fill().padLeft(table.getHeight() / 80).padRight(table.getHeight() / 80);

    tp.setBackground(FourInALine.Instance.skin.getDrawable("default-window"));

    table.add(tp).expand().fill().left();
    table.add(FourInALine.Instance.board);

    table.setPosition(-stage.getWidth(), (stage.getHeight() - table.getHeight()) / 2);
  }

  @Override
  public void show() {
    super.show();
    Gdx.input.setInputProcessor(stage);
    Gdx.input.setCatchBackKey(true);
    table.addAction(Actions.sequence(
        Actions.parallel(Actions.fadeIn(animationTime), Actions.moveTo((stage.getWidth() - table.getWidth()) / 2, (stage.getHeight() - table.getHeight()) / 2, animationTime)),
        Actions.run(new Runnable() {
          @Override
          public void run() {
            FourInALine.Instance.fsm.processEvent(Events.START_GAME, null);
          }
        })));
  }

  @Override
  public void fadeOut() {
    table
        .addAction(Actions.sequence(Actions.parallel(Actions.fadeOut(animationTime), Actions.moveTo(-stage.getWidth(), (stage.getHeight() - table.getHeight()) / 2, animationTime))));
  }

  public void highlightPlayer(int player) {
    if (player == 1) {
      players[0].highlight(true);
      players[1].highlight(false);
    } else if (player == 2) {
      players[0].highlight(false);
      players[1].highlight(true);
    } else {
      players[0].highlight(false);
      players[1].highlight(false);
    }
  }

  public void incScore(int player) {
    MatchState.anScore[player - 1]++;
    players[player - 1].setScore(MatchState.anScore[player - 1]);
  }
}
