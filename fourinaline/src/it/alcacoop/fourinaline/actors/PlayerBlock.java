package it.alcacoop.fourinaline.actors;

import it.alcacoop.fourinaline.FourInALine;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class PlayerBlock extends Table {

  private Label lName;
  private Label lScore;
  private Skin skin;
  private Checker checker;
  private Action high;

  public PlayerBlock() {
    super();
    skin = FourInALine.Instance.skin;
    setBackground(skin.getDrawable("default-window"));
    lName = new Label("", skin);
    lScore = new Label("Score: 0", skin);
    checker = new Checker();
    checker.setColor(1, 1, 1, 1);
    float dim = lScore.getHeight() * 0.7f;
    checker.setWidth(dim);
    checker.setHeight(dim);

    add(checker).left().padRight(10);
    add(lName).expandX().left();
    row();
    add(lScore).colspan(2).expandX().left();
  }

  public void setName(String name) {
    lName.setText(name);
  }

  public void setScore(int score) {
    lScore.setText("Score: " + score);
  }

  public void setColor(int c) {
    checker.setColor(c);
  }

  public void highlight(boolean status) {
    if (status) {
      high = Actions.forever(Actions.sequence(Actions.fadeOut(0.3f), Actions.fadeIn(0.3f)));
      lName.addAction(high);
    } else {
      lName.removeAction(high);
      lName.setColor(1, 1, 1, 1);
    }
  }
}
