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

package it.alcacoop.fourinaline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {

  private Sound click, move, newMessage, gameEnd;
  private Music bgMusic;

  public SoundManager() {
    move = Gdx.audio.newSound(Gdx.files.internal("sounds/move1.wav"));
    click = Gdx.audio.newSound(Gdx.files.internal("sounds/move2.wav"));
    newMessage = Gdx.audio.newSound(Gdx.files.internal("sounds/newmessage.wav"));
    gameEnd = Gdx.audio.newSound(Gdx.files.internal("sounds/move1.wav"));

    bgMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/bg.mp3"));
    bgMusic.setVolume(0.25f);
    bgMusic.setLooping(true);
  }

  public boolean isBGPlaying() {
    return bgMusic.isPlaying();
  }

  public void playBGMusic() {
    if (FourInALine.Instance.optionPrefs.getString("MUSIC", "Yes").equals("Yes"))
      bgMusic.play();
    else
      bgMusic.stop();
  }


  public void playButton() {
    if (FourInALine.Instance.optionPrefs.getString("SOUND", "Yes").equals("Yes"))
      click.play();
  }

  public void playMove() {
    if (FourInALine.Instance.optionPrefs.getString("SOUND", "Yes").equals("Yes"))
      move.play();
  }

  public void playCameEnd() {
    if (FourInALine.Instance.optionPrefs.getString("SOUND", "Yes").equals("Yes"))
      gameEnd.play();
  }


  public void playMessage() {
    if (FourInALine.Instance.optionPrefs.getString("SOUND", "Yes").equals("Yes"))
      newMessage.play();
  }
}
