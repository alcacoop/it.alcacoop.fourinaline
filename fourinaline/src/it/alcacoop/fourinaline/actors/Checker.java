package it.alcacoop.fourinaline.actors;

import it.alcacoop.fourinaline.FourInALine;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Checker extends Actor implements Poolable {

  private TextureRegion r1, r2;
  private Texture wood;
  private ShaderProgram shader;
  private Random rnd;
  private float rotation;
  private SpriteBatch sb;
  private FrameBuffer fbo;
  private TextureRegion reg;
  int color;

  public Checker() {
    super();
    r1 = FourInALine.Instance.atlas.findRegion("CSW");
    r2 = FourInALine.Instance.atlas.findRegion("CSB");

    shader = new ShaderProgram(Gdx.files.internal("shaders/vertex.s"), Gdx.files.internal("shaders/fragment2.s"));
    System.out.println(shader.isCompiled());
    System.out.println(shader.getLog());
    wood = FourInALine.Instance.wood;
    rnd = new Random();

    fbo = new FrameBuffer(Format.RGBA4444, r1.getRegionWidth(), r1.getRegionHeight(), true);
    sb = new SpriteBatch();
    Matrix4 matrix = new Matrix4();
    matrix.setToOrtho2D(0, 0, r1.getRegionWidth(), r1.getRegionHeight());
    sb.setProjectionMatrix(matrix);
    setColor(1);
  }

  @Override
  public void draw(SpriteBatch batch, float parentAlpha) {
    Color c = getColor();
    batch.setColor(c.r, c.g, c.b, c.a * parentAlpha);
    if (color == 1)
      batch.draw(r1, getX(), getY(), 0, 0, getWidth(), getHeight(), 1, 1, 0);
    else
      batch.draw(r2, getX(), getY(), 0, 0, getWidth(), getHeight(), 1, 1, 0);

    batch.setColor(1, 1, 1, 0.45f);
    batch.draw(reg, getX(), getY(), 0, 0, getWidth(), getHeight(), 1, 1, 0);
    batch.setColor(c.r, c.g, c.b, c.a * parentAlpha);
  }

  public void setColor(int color) {
    this.color = color;
    rotation = rnd.nextFloat() * 360;
    Sprite sprite;
    sprite = new Sprite(r1);
    sprite.setRotation(rotation);

    double a = (double)r1.getRegionWidth();
    double b = (double)wood.getWidth();
    float d = (float)(a / b);

    float x = (rnd.nextFloat() * d);
    float y = (rnd.nextFloat() * d);

    fbo.begin();
    Gdx.gl.glClearColor(1, 1, 1, 0);
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    sb.setShader(shader);
    shader.begin();
    sb.begin();
    shader.setUniformf("u_sx", x);
    shader.setUniformf("u_sy", y);
    Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE1);
    wood.bind();
    shader.setUniformi("u_wood", 1);
    Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE0);
    sprite.draw(sb);
    sb.end();
    shader.end();
    sb.setShader(null);
    fbo.end();
    reg = new TextureRegion(fbo.getColorBufferTexture());
  }

  @Override
  public void reset() {
    setColor(1, 1, 1, 1);
  }
}
