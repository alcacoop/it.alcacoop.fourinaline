/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package it.alcacoop.fourinaline.actors;

import it.alcacoop.fourinaline.FourInALine;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;


public class RepeatedImage extends Widget {
  private Scaling scaling;
  private int align = Align.center;
  private float imageX, imageY, imageWidth, imageHeight;
  private Drawable drawable;
  private int nx, ny;
  private TextureRegion tile, wood;// , mask;


  public RepeatedImage(TextureRegion region, final float imageWidth, final float imageHeight, final int nx, final int ny) {
    setDrawable(new TextureRegionDrawable(region));
    this.scaling = Scaling.stretch;
    this.align = Align.center;
    this.nx = nx;
    this.ny = ny;
    setWidth(imageWidth);
    setHeight(imageHeight);
    tile = region;

    wood = new TextureRegion(FourInALine.Instance.wood, 0, 0, Math.round(imageWidth), Math.round(imageHeight));
    /*
    wood = FourInALine.Instance.atlas.findRegion("texture");
    Texture t = new Texture(128, 128, Format.RGBA8888);
    t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
    Pixmap p = new Pixmap(Gdx.files.internal("mdpi/hole.png"));
    t.draw(p, 0, 0);
    mask = new TextureRegion(t);
    */
  }


  public void layout() {
    if (drawable == null)
      return;

    float regionWidth = drawable.getMinWidth();
    float regionHeight = drawable.getMinHeight();
    float width = getWidth();
    float height = getHeight();

    Vector2 size = scaling.apply(regionWidth, regionHeight, width, height);
    imageWidth = size.x;
    imageHeight = size.y;

    if ((align & Align.left) != 0)
      imageX = 0;
    else if ((align & Align.right) != 0)
      imageX = (int)(width - imageWidth);
    else imageX = (int)(width / 2 - imageWidth / 2);

    if ((align & Align.top) != 0)
      imageY = (int)(height - imageHeight);
    else if ((align & Align.bottom) != 0)
      imageY = 0;
    else imageY = (int)(height / 2 - imageHeight / 2);
  }

  public void draw(SpriteBatch batch, float parentAlpha) {
    validate();
    Color color = getColor();
    batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);


    float xdim = imageWidth / nx;
    float ydim = imageHeight / ny;

    /*
    // SAVE ORIG Fns
    int s = batch.getBlendSrcFunc();
    int d = batch.getBlendDstFunc();
    */

    // BG
    if (drawable != null) {
      for (int x = 0; x < nx; x++)
        for (int y = 0; y < ny; y++) {
          batch.draw(tile, x * xdim + getX(), y * ydim + getY(), 0, 0, xdim, ydim, 1, 1, 0);
        }
    }
    batch.setColor(1, 1, 1, 0.3f);
    batch.draw(wood, getX(), getY());
    batch.setColor(1, 1, 1, 1);
    /*
    batch.flush();
    // MASK
    Gdx.gl20.glColorMask(true, true, true, true);
    batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);
    if (drawable != null) {
      for (int x = 0; x < nx; x++)
        for (int y = 0; y < ny; y++) {
          batch.draw(mask, x * xdim + getX(), y * ydim + getY(), 0, 0, xdim, ydim, 1, 1, 0);
        }
    }
    batch.flush();

    // TEXTURE
    Gdx.gl20.glColorMask(true, true, true, true);
    batch.setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_ONE_MINUS_DST_ALPHA);
    batch.draw(wood, 0 + getX(), 0 + getY(), getWidth(), getHeight());
    batch.flush();

    // RESTORE ORIG Fns
    batch.setBlendFunction(s, d);
    */
  }

  @Override
  public void setPosition(float x, float y) {
    setX(x);
    setY(y);
  }

  public void setDrawable(Drawable drawable) {
    if (drawable != null) {
      if (this.drawable == drawable)
        return;
      if (getPrefWidth() != drawable.getMinWidth() || getPrefHeight() != drawable.getMinHeight())
        invalidateHierarchy();
    } else {
      if (getPrefWidth() != 0 || getPrefHeight() != 0)
        invalidateHierarchy();
    }
    this.drawable = drawable;
  }

  public Drawable getDrawable() {
    return drawable;
  }

  public void setScaling(Scaling scaling) {
    if (scaling == null)
      throw new IllegalArgumentException("scaling cannot be null.");
    this.scaling = scaling;
  }

  public void setAlign(int align) {
    this.align = align;
  }

  public float getMinWidth() {
    return 0;
  }

  public float getMinHeight() {
    return 0;
  }

  public float getPrefWidth() {
    if (drawable != null)
      return drawable.getMinWidth();
    return 0;
  }

  public float getPrefHeight() {
    if (drawable != null)
      return drawable.getMinHeight();
    return 0;
  }

  public float getImageX() {
    return imageX;
  }

  public float getImageY() {
    return imageY;
  }

  public float getImageWidth() {
    return imageWidth;
  }

  public float getImageHeight() {
    return imageHeight;
  }
}
