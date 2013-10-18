package it.alcacoop.fourinaline.actors;

import it.alcacoop.fourinaline.FourInALine;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class PlayerBlock extends Table {

  private Label lName;
  private Label lScore;
  private Skin skin;
  private Checker checker;
  private AlphaAction fadeOut;

  public PlayerBlock() {
    super();
    skin = FourInALine.Instance.skin;
    setBackground(FourInALine.Instance.skin.getDrawable("sep"));
    lName = new Label("", skin);
    lScore = new Label("Score: 0", skin);
    checker = new Checker();
    checker.setColor(1, 1, 1, 1);
    float dim = lScore.getHeight() * 0.8f;
    checker.setWidth(dim);
    checker.setHeight(dim);

    Table t = new Table();
    t.add(lName).expandX().left();
    t.row();
    t.add(lScore).expandX().left();

    add(checker).left().padRight(checker.getWidth() / 16).pad(4);
    add(t).expandX().left().padRight(4);

    fadeOut = Actions.fadeOut(0.3f);
    lName.addAction(Actions.forever(Actions.sequence(fadeOut, Actions.fadeIn(0.3f))));
  }

  public void setName(String name) {
    lName.setText(name);
  }

  public String getName() {
    return lName.getText().toString();
  }

  public void setScore(int score) {
    lScore.setText("Score: " + score);
  }

  public void setColor(int c) {
    checker.setColor(c);
  }

  public void highlight(boolean status) {
    if (status) {
      fadeOut.setAlpha(0.0f);
    } else {
      fadeOut.setAlpha(1.0f);
      lName.setColor(1, 1, 1, 1);
    }
  }
}
