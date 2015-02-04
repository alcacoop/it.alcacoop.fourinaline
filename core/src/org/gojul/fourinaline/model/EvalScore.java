package org.gojul.fourinaline.model;

import java.io.Serializable;

import org.gojul.fourinaline.model.GameModel.PlayerMark;

public interface EvalScore extends Serializable
{   
  
  /**
   * Returns the score of the game model <code>gameModel</code>
   * for the player mark <code>playerMark</code>.
   * @param gameModel the game model to consider.
   * @param playerMark the player mark to consider.
   * @return the score of the game model <code>gameModel</code>
   * for the player mark <code>playerMark</code>.
   * @throws NullPointerException if any of the method parameter
   * is null.
   */
  public int evaluate(final GameModel gameModel, final PlayerMark playerMark)
    throws NullPointerException;
}