package it.alcacoop.fourinaline.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ParticleEffectActor extends Actor {

  ParticleEffect effect;
  
  public ParticleEffectActor() {
    effect = new ParticleEffect();
    System.out.println(Gdx.files.internal("effects/fire.p"));
    effect.load(Gdx.files.internal("effects/fire.p"), Gdx.files.internal("effects"));
  }

  public void draw(SpriteBatch batch, float parentAlpha) {
     effect.draw(batch);
  }

  public void act(float delta) {
     super.act(delta);
     effect.update(delta);
     effect.start();
  }

  @Override
  public void setPosition(float x, float y) {
    effect.reset();
    effect.setPosition(x, y);
  }
  
  public ParticleEffect getEffect() {
     return effect;
  }
}
