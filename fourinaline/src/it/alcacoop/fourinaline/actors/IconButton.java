package it.alcacoop.fourinaline.actors;

import it.alcacoop.fourinaline.FourInALine;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class IconButton extends Button {
  private final Label label;
  private TextButtonStyle style;
  private TextureRegion reg;
  private Random rnd;

  public IconButton(String text, TextureRegion icon, TextButtonStyle tl) {
    super(tl);
    this.style = tl;
    label = new Label(text, new LabelStyle(tl.font, tl.fontColor));
    label.setAlignment(Align.center);

    if (icon != null) {
      Image icon1 = new Image(icon);
      add(icon1).expandY();
    }

    add(label).expand().fill();

    if (icon != null) {
      Image icon2 = new Image(icon);
      add(icon2).expandY();
    }

    setWidth(getPrefWidth());
    setHeight(getPrefHeight());
    rnd = new Random();
  }

  public void setStyle(ButtonStyle style) {
    if (!(style instanceof TextButtonStyle))
      throw new IllegalArgumentException("style must be a TextButtonStyle.");
    super.setStyle(style);
    this.style = (TextButtonStyle)style;
    if (label != null) {
      TextButtonStyle textButtonStyle = (TextButtonStyle)style;
      LabelStyle labelStyle = label.getStyle();
      labelStyle.font = textButtonStyle.font;
      labelStyle.fontColor = textButtonStyle.fontColor;
      label.setStyle(labelStyle);
    }
  }


  @Override
  public void layout() {
    super.layout();
    int x = rnd.nextInt((int)(FourInALine.Instance.btntxt.getWidth() - getWidth()));
    int y = rnd.nextInt((int)(FourInALine.Instance.btntxt.getHeight() - getHeight()));
    reg = new TextureRegion(FourInALine.Instance.btntxt, x, y, Math.round(getWidth()), Math.round(getHeight()));
  }

  public TextButtonStyle getStyle() {
    return style;
  }

  private void baseDraw(SpriteBatch batch, float parentAlpha) {
    validate();

    Drawable background = null;
    if (isPressed() && !isDisabled()) {
      background = style.down == null ? style.up : style.down;
    } else {
      if (isDisabled() && style.disabled != null)
        background = style.disabled;
      else if (isChecked() && style.checked != null)
        background = (isOver() && style.checkedOver != null) ? style.checkedOver : style.checked;
      else if (isOver() && style.over != null)
        background = style.over;
      else background = style.up;
    }

    if (background != null) {
      Color color = getColor();
      batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
      background.draw(batch, getX(), getY(), getWidth(), getHeight());
    }

    // HERE TEXTURE!
    batch.setColor(1, 1, 1, 0.45f);
    batch.draw(reg, 0 + getX(), 0 + getY(), getWidth(), getHeight());
    batch.setColor(1, 1, 1, 1);
    drawChildren(batch, parentAlpha);
  }


  public void draw(SpriteBatch batch, float parentAlpha) {
    Color fontColor;
    if (isDisabled() && style.disabledFontColor != null)
      fontColor = style.disabledFontColor;
    else if (isPressed() && style.downFontColor != null)
      fontColor = style.downFontColor;
    else if (isChecked() && style.checkedFontColor != null)
      fontColor = (isOver() && style.checkedOverFontColor != null) ? style.checkedOverFontColor : style.checkedFontColor;
    else if (isOver() && style.overFontColor != null)
      fontColor = style.overFontColor;
    else fontColor = style.fontColor;
    if (fontColor != null)
      label.getStyle().fontColor = fontColor;
    baseDraw(batch, parentAlpha);
  }

  public Label getLabel() {
    return label;
  }

  public void setText(String text) {
    label.setText(text);
  }

  public CharSequence getText() {
    return label.getText();
  }

}
