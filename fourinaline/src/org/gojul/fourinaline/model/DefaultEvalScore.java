/*
 * DefaultEvalScore.java
 *
 * Created: 2008/02/24
 *
 * Copyright (C) 2008 Julien Aubin
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.gojul.fourinaline.model;

import java.util.Collection;
import java.util.List;

import org.gojul.fourinaline.model.GameModel.CellCoord;
import org.gojul.fourinaline.model.GameModel.PlayerMark;

/**
 * The default evaluation function of the game for an AI player.<br/>
 * It is based on the works from Keith Pomakis, available 
 * <A href="http://www.pomakis.com/c4/connect_generic/c4.txt">there</A>.
 * 
 * @author Julien Aubin
 */
public final class DefaultEvalScore implements EvalScore
{
	
	/**
	 * The serial version UID.
	 */
	final static long serialVersionUID = 1;

	/**
	 * @see org.gojul.fourinaline.model.TestConnect.EvalScore#evaluate(org.gojul.fourinaline.model.GameModel, org.gojul.fourinaline.model.GameModel.PlayerMark)
	 */
	public int evaluate(final GameModel gameModel, final PlayerMark playerMark)
			throws NullPointerException
	{			
		if (gameModel == null || playerMark == null)
			throw new NullPointerException();
		
		return evaluateForOnePlayer(gameModel, playerMark) - evaluateForOnePlayer(gameModel, PlayerMark.getNextMark(playerMark));
	}
	
	/**
	 * Evaluates the score for the player which has for mark <code>playerMark</code>.
	 * See <A href="http://www.pomakis.com/c4/connect_generic/c4.txt">this document</A>
	 * for futher information.
	 * @param gameModel the game model to consider.
	 * @param playerMark the player mark to consider.
	 * @return the score for the player which has for mark <code>playerMark</code>.
	 */
	private int evaluateForOnePlayer(final GameModel gameModel, final PlayerMark playerMark)
	{
		int score = 0;
		
		Collection<List<CellCoord>> lines = gameModel.getAllLines();
		
		for (List<CellCoord> line: lines)
		{
			// We treat only once each line.
			// Since we treat all the cells, a line might be treated at
			// a maximum of four times if we do not check if it has already
			// been treated.
			int lineScore = evaluateLine(gameModel, playerMark, line); 
			score += lineScore;
		}
		
		return score;
	}
	
	/**
	 * Evaluates the value of the line <code>line</code> in the game
	 * model <code>gameModel</code> with the player mark <code>playerMark</code>.
	 * @param gameModel the game model.
	 * @param playerMark the player mark.
	 * @param line the line to consider.
	 * @return the value of the line <code>line</code> in the game
	 * model <code>gameModel</code> with the player mark <code>playerMark</code>.
	 */
	private int evaluateLine(final GameModel gameModel, final PlayerMark playerMark, final List<CellCoord> line)
	{	
		// Formula : score for a cell = 2^(number of cells of this color in the line) if the cell
		// still contains winning positions 0 otherwise.		
		int result = 1;
		
		List<PlayerMark> lineValues = gameModel.getValuesOfLine(line);
		
		for (int i = 0, len = line.size(); i < len && result != 0; i++)
		{
			PlayerMark markTest = lineValues.get(i);
			
			if (markTest != null)
			{
				if (markTest.equals(playerMark))
					result = 2 * result;
				else
					result = 0;
			}
		}
		
		return result;
	}
}
