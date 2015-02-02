/**
 ##################################################################
 #                     FOUR IN A LINE MOBILE                      #
 ##################################################################
 #                                                                #
 #  Authors: Domenico Martella - Francesco Valente                #
 #  E-mail: info@alcacoop.it                                      #
 #  Date:   18/10/2013                                            #
 #                                                                #
 ##################################################################
 #                                                                #
 #  Copyright (C) 2013   Alca Societa' Cooperativa                #
 #                                                                #
 #  This file is part of FOUR IN A LINE MOBILE.                   #
 #  FOUR IN A LINE MOBILE is free software: you can redistribute  # 
 #  it and/or modify it under the terms of the GNU General        #
 #  Public License as published by the Free Software Foundation,  #
 #  either version 3 of the License, or (at your option)          #
 #  any later version.                                            #
 #                                                                #
 #  FOUR IN A LINE MOBILE is distributed in the hope that it      #
 #  will be useful, but WITHOUT ANY WARRANTY; without even the    #
 #  implied warranty of MERCHANTABILITY or FITNESS FOR A          #
 #  PARTICULAR PURPOSE.  See the GNU General Public License       #
 #  for more details.                                             #
 #                                                                #
 #  You should have received a copy of the GNU General            #
 #  Public License v3 along with this program.                    #
 #  If not, see <http://http://www.gnu.org/licenses/>             #
 #                                                                #
 ##################################################################
**/

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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;

import it.alcacoop.fourinaline.FourInALine;


public class BoardImage extends Widget {
  private Scaling scaling;
  private int align = Align.center;
  private float imageX, imageY, imageWidth, imageHeight;
  private Drawable drawable;
  private int nx, ny;
  private int xdim;
  private int ydim;
  private TextureRegion tile, mask;
  private Texture wood;
  private ShaderProgram shader;
  private FrameBuffer fbo1, fbo2;


  public BoardImage(final float imageWidth, final float imageHeight, final int nx, final int ny) {
    setDrawable(new TextureRegionDrawable(FourInALine.Instance.atlas.findRegion("hole")));
    this.scaling = Scaling.stretch;
    this.align = Align.center;
    this.nx = nx;
    this.ny = ny;
    xdim = (int)imageWidth / nx;
    ydim = (int)imageHeight / ny;
    setWidth((int)imageWidth);
    setHeight((int)imageHeight);
    tile = FourInALine.Instance.atlas.findRegion("hole");

    wood = FourInALine.Instance.wood;
    mask = new TextureRegion(FourInALine.Instance.mask);

    shader = new ShaderProgram(Gdx.files.internal("shaders/vertex.s"), Gdx.files.internal("shaders/fragment.s"));
    System.out.println(shader.isCompiled());
    System.out.println(shader.getLog());
  }

  @Override
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
    else
      imageX = (int)(width / 2 - imageWidth / 2);

    if ((align & Align.top) != 0)
      imageY = (int)(height - imageHeight);
    else if ((align & Align.bottom) != 0)
      imageY = 0;
    else
      imageY = (int)(height / 2 - imageHeight / 2);

    if (fbo1 == null) {
      fbo1 = new FrameBuffer(Format.RGBA8888, (int)getWidth(), (int)getHeight(), false);
      fbo2 = new FrameBuffer(Format.RGBA8888, (int)getWidth(), (int)getHeight(), false);
    }

    SpriteBatch sb = new SpriteBatch();
    Matrix4 matrix = new Matrix4();
    matrix.setToOrtho2D(0, 0, getWidth(), getHeight()); // here is the actual size you want
    sb.setProjectionMatrix(matrix);

    fbo1.begin();
    sb.begin();
    Gdx.gl.glClearColor(1, 1, 1, 0);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    if (drawable != null) {
      for (int x = 0; x < nx; x++)
        for (int y = 0; y < ny; y++) {
          sb.draw(mask, x * xdim, y * ydim, 0, 0, xdim, ydim, 1, 1, 0);
        }
    }
    sb.end();
    fbo1.end();

    fbo2.begin();
    sb.setShader(shader);
    sb.begin();
    Gdx.gl.glClearColor(1, 1, 1, 0);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE1);
    wood.bind();
    shader.setUniformi("u_wood", 1);

    Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE0);
    sb.draw(fbo1.getColorBufferTexture(), 0, 0, getWidth(), getHeight());
    sb.end();
    shader.end();
    sb.setShader(null);
    fbo2.end();
  }

  @Override
  public void draw(Batch batch, float parentAlpha) {
    validate();
    Color color = getColor();
    batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

    // BG
    if (drawable != null) {
      for (int x = 0; x < nx; x++)
        for (int y = 0; y < ny; y++) {
          batch.draw(tile, x * xdim + getX(), y * ydim + getY(), 0, 0, xdim, ydim, 1, 1, 0);
        }
    }
    batch.setColor(1, 1, 1, 1);
    batch.draw(fbo2.getColorBufferTexture(), getX(), getY());
    batch.setColor(1, 1, 1, 1);
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
