package org.gojul.fourinaline.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.WeakHashMap;

import org.gojul.fourinaline.model.GameModel.GameStatus;
import org.gojul.fourinaline.model.GameModel.PlayerMark;


/**
 * An implementation of the alpha-beta algorithm for our purpose.
 * This implementation makes it possible to use any user-developed evaluation
 * algorithm that can be better than the one provided.<br/>
 * This algorithm implements a caching mechanism to improve the performance
 * of the AI player.
 * 
 * @author Julien Aubin
 */
public class AlphaBeta implements Serializable
{
  /**
   * The serial version UID.
   */
  final static long serialVersionUID = 1;
  
  /**
   * The cache initial capacity.
   */
  private final static int CACHE_INITIAL_CAPACITY = 5000;
  
  /**
   * The random which determines which column is to be played
   * when two columns have the same score.
   */
  private final static Random random = new SecureRandom();
  
  /**
   * The evaluation function.
   */
  private EvalScore evalScore;
  
  /**
   * The search deepness.
   */
  private int deepness;
  
  /**
   * The random factor.
   */
  private float randFactor;
  
  /**
   * The score cache.
   */
  private transient Map<String, Integer> scoreCache;
  
  
  /**
   * Constructor.
   * @param evalScoreFunction the evaluation function used.
   * @param deepnessSearch the search deepness.
   * @param randomFactor the random factor used when two possible plays
   * have the same score.
   * @throws NullPointerException if any of the method parameter is null.
   * @throws IllegalArgumentException if <code>deepnessSearch</code> is
   * inferior or equal to 0, or if <code>randomFactor</code> is not in the
   * [0, 1] range.
   */
  public AlphaBeta(final EvalScore evalScoreFunction, final int deepnessSearch, final float randomFactor)
    throws NullPointerException, IllegalArgumentException
  {
    if (evalScoreFunction == null)
      throw new NullPointerException();
    
    if (deepnessSearch <= 0)
      throw new IllegalArgumentException("deepnessSearch");
    
    if (randomFactor < 0.0f || randomFactor > 1.0f) 
      throw new IllegalArgumentException("randomFactor");
    
    evalScore = evalScoreFunction;
    deepness = deepnessSearch;
    randFactor = randomFactor; 
    scoreCache = new WeakHashMap<String, Integer>(CACHE_INITIAL_CAPACITY);
  }
  
  /**
   * Returns the index of the column to play, or -1 if there's no
   * more playable column.
   * @param gameModel the game model to consider.
   * @param playerMark the player mark to consider.
   * @return the index of the column to play, or -1 if there's no
   * more playable column.
   */
  public int getColumnIndex(final GameModel gameModel, final PlayerMark playerMark)
  {     
    Collection<Integer> possiblePlays = gameModel.getListOfPlayableColumns();
    
    int bestColumn = -1;
    int bestScore = -Integer.MAX_VALUE;
    
    GameModel tempModel = new GameModel(gameModel);
    // We iterate over the columns from the center
    // as this is the most interesting order for us.
    // This quirk improves greatly speed as the best
    // scores of the alpha beta algorithm are in
    // the middle columns.
    List<Integer> playOrder = new ArrayList<Integer>();
    int column = (tempModel.getColCount() - 1) / 2;
    for (int i = 1, len = tempModel.getColCount(); i <= len; i++) 
    {
      playOrder.add(column);
      column += (i % 2 == 1) ? i: -i;
    }
    //for (int i=0; i<tempModel.getColCount();i++)
      //playOrder.add(i);
    
    List<Integer> iterationOrder = new ArrayList<Integer>(playOrder);
    iterationOrder.retainAll(possiblePlays);
    
    HashMap<Integer, Integer> m = new HashMap<Integer, Integer>();
    List<Integer> scoresList = new ArrayList<Integer>(playOrder);
    for (Integer colIndex: iterationOrder)
    {
      tempModel.play(colIndex.intValue(), playerMark);
      String key = tempModel.toUniqueKey();
      int currentScore = 0;
      
      Integer currentScoreInt = scoreCache.get(key);
      
      if (currentScoreInt != null)
      {
        currentScore = currentScoreInt.intValue();
      }
      else
      {         
        // We build the key before performing the alpha-beta evaluation
        // becuase tempModel is mutable.
        currentScore = alphaBeta(playOrder, tempModel, playerMark, Integer.MIN_VALUE, -bestScore, 0);
        scoreCache.put(key, Integer.valueOf(currentScore));
      } 
      

      m.put(colIndex, currentScore);

      System.out.println("SCORE "+colIndex+": "+currentScore);
      
      tempModel.cancelLastPlay();
      
      if (currentScore > bestScore)
      {
        bestScore = currentScore;
        bestColumn = colIndex;
      }
      else if (currentScore == bestScore) {
        if (random.nextFloat() >= randFactor) {
          bestColumn = colIndex;
        }
      }
    }
    System.out.println("BEST COLUMN: "+bestColumn+"\n");

    // Taglio via i piu bassi
    Iterator it = m.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry pairs = (Map.Entry)it.next();
      if ((Integer)pairs.getValue() < -1000)
        it.remove();
      else scoresList.add((Integer)pairs.getValue());
    }

    // Ordinamento
    Collections.sort(scoresList);
    for (Entry<Integer, Integer> entry : m.entrySet()) {
      System.out.println("MAP SCORE RIDOTTA: " + entry.getKey() + " -> " + entry.getValue());
    }

    // Max/min
    int max = scoresList.get(scoresList.size() - 1);
    int min = scoresList.get(0);
    System.out.println("MAX: " + max);
    System.out.println("MIN: " + min);

    int window = Math.abs(max - min);
    float unit = window / m.size();
    System.out.println("WINDOW: " + window + "UNIT: " + unit);

    return bestColumn;
  }
  
  /**
   * Deserializes the AI game client in case of serialization.
   * @param in the input stream responsible of deserialization.
   * @throws IOException if an I/O error occurs while deserializing.
   * @throws ClassNotFoundException in case a class to be deserialized
   * is not found.
   */
  private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();
    scoreCache = new WeakHashMap<String, Integer>(CACHE_INITIAL_CAPACITY);
  }

  
  /**
   * Performs an alpha-beta algorithm over the game model <code>gameModel</code>,
   * with current player <code>playerMark</code>.
   * @param playOrder the play order in which we iterate over the board.
   * @param gameModel the game model to consider. 
   * @param playerMark the player mark to consider.
   * @param alpha the alpha value.
   * @param beta the beta value.
   * @param currentDeepness the deepness in the alpha-beta tree.
   * @return the score of each possibility of the alpha beta model.
   */
  private int alphaBeta(final List<Integer> playOrder, final GameModel gameModel, final PlayerMark playerMark, final int alpha, final int beta, final int currentDeepness)
  {   
    // Game won by the player.
    if (gameModel.getGameStatus() == GameStatus.WON_STATUS)
    {
      return Integer.MAX_VALUE - currentDeepness;
    }
    // Tie game.
    else if (gameModel.getGameStatus() == GameStatus.TIE_STATUS)
      return 0;
    // Maximum deepness.
    else if (currentDeepness >= deepness)
      return evalScore.evaluate(gameModel, playerMark);
    else
    {
      int bestScore = Integer.MIN_VALUE;
      
      PlayerMark tempMark = PlayerMark.getNextMark(playerMark);
      
      int alphaEval = alpha;
      
      Collection<Integer> possiblePlays = gameModel.getListOfPlayableColumns();
      List<Integer> iterationOrder = new ArrayList<Integer>(playOrder);
      iterationOrder.retainAll(possiblePlays);
      
      for (Integer colIndex: iterationOrder)
      {   
        // We avoid there multiple copies of the game model
        // which are unuseful in our case...
        gameModel.play(colIndex.intValue(), tempMark);
        
        // We cannot use the cache there since it would bring
        // erroneous results.
        int currentScore = alphaBeta(playOrder, gameModel, tempMark, -beta, -alphaEval, currentDeepness + 1);
        
        gameModel.cancelLastPlay();
        
        if (currentScore > bestScore)
        {
          bestScore = currentScore;
          
          if (bestScore > alphaEval)
          {
            alphaEval = bestScore;
            
            if (alphaEval > beta)
            {
              // What is good for the other player is bad for this one.
              return -bestScore;
            }
          }
        }
      }
      
      // What is good for the other player is bad for this one.
      return -bestScore;
    }
  }
}