package it.alcacoop.fourinaline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {

  private Sound click, move, newMessage, gameEnd;
  private Preferences prefs;

  public SoundManager() {
    prefs = Gdx.app.getPreferences("Options");

    move = Gdx.audio.newSound(Gdx.files.internal("sounds/move1.wav"));
    click = Gdx.audio.newSound(Gdx.files.internal("sounds/move2.wav"));
    newMessage = Gdx.audio.newSound(Gdx.files.internal("sounds/newmessage.wav"));
    gameEnd = Gdx.audio.newSound(Gdx.files.internal("sounds/move1.wav"));
  }

  public void playButton() {
    if (prefs.getString("SOUND", "Yes").equals("Yes"))
      click.play();
  }

  public void playMove() {
    if (prefs.getString("SOUND", "Yes").equals("Yes"))
      move.play();
  }

  public void playCameEnd() {
    if (prefs.getString("SOUND", "Yes").equals("Yes"))
      gameEnd.play();
  }


  public void playMessage() {
    if (prefs.getString("SOUND", "Yes").equals("Yes"))
      newMessage.play();
  }
}
