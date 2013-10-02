package it.alcacoop.fourinaline;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class FourInALineDesktop {
  public static void main(String[] args) {
    LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
    cfg.title = "fourinaline";
    cfg.useGL20 = false;
    cfg.width = 800;
    cfg.height = 480;

    new LwjglApplication(new FourInALine(), cfg);
  }
}
