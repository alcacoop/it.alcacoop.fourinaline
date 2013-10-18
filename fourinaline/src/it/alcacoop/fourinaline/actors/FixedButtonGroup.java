package it.alcacoop.fourinaline.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;

public class FixedButtonGroup extends ButtonGroup {

  @Override
  public void setChecked(String text) {
    if (text == null) throw new IllegalArgumentException("text cannot be null.");
    for (int i = 0, n = getButtons().size; i < n; i++) {
      Button button = getButtons().get(i);
      if (button instanceof IconButton && text.equals((String)((IconButton)button).getText().toString())) {
        button.setChecked(true);
        return;
      }   
    }   
  }
  
}
