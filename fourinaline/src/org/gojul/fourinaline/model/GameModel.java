/*
 * GameModel.java
 *
 * Created: 2008/02/16
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The <code>GameModel</code> class contains the basic modelisation
 * of a 4 in a line game.<br/>
 * It supports a various number of lines, and a various number of cells.<br/>
 * It only supports two players. However it is optimized for the support of
 * alpha beta algorithm in order to improve the overall speed of an AI player. 
 * 
 * @author Julien Aubin
 */
public final class GameModel implements Serializable 
{

	/**
	 * The serial version UID.
	 */
	final static long serialVersionUID = 1;
	
	/**
	 * The <code>CellCoord</code> class represents a class
	 * of cell coordinates.
	 * 
	 * @author Julien Aubin
	 */
	public final static class CellCoord implements Serializable
	{
		/**
		 * The serial version UID.
		 */
		final static long serialVersionUID = 1;		
		
		/**
		 * The row index.
		 */
		private int rowIndex;
		
		/**
		 * The column index.
		 */
		private int colIndex;
		
		/**
		 * Constructor.
		 * @param row the row index.
		 * @param col the column index.
		 */
		public CellCoord(final int row, final int col)
		{
			rowIndex = row;
			colIndex = col;
		}
		
		/**
		 * Returns the row index.
		 * @return the row index.
		 */
		public int getRowIndex()
		{
			return rowIndex;
		}
		
		/**
		 * Returns the column index.
		 * @return the column index.
		 */
		public int getColIndex()
		{
			return colIndex;
		}

		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(final Object obj)
		{
			if (obj != null && obj instanceof CellCoord)
			{
				CellCoord test = (CellCoord) obj;
				
				return test.rowIndex == rowIndex && test.colIndex == colIndex;
			}
			else
				return false;
		}

		/**
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode()
		{			
			return rowIndex + colIndex;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{			
			return new StringBuffer("(").append(rowIndex).append(", ").append(colIndex).append(")").toString();
		}
	}
	
	/**
	 * The <code>GameModelException</code> class is the exception
	 * for all the game model errors.
	 * 
	 * @author Julien Aubin
	 *
	 */
	public final static class GameModelException extends RuntimeException
	{
		/**
		 * The serial version UID.
		 */
		final static long serialVersionUID = 1;
		
		/**
		 * Constructor.
		 */
		GameModelException()
		{
			super();
		}
		
		/**
		 * Constructor.
		 * @param message the mesage to display.
		 */
		GameModelException(final String message)
		{
			super(message);
		}
		
	}
	
	/**
	 * The <code>PlayerMark</code> symbol contains the mark  
	 * for a game player.
	 * 
	 * @author Julien Aubin
	 */
	public final static class PlayerMark implements Serializable
	{
		/**
		 * The serial version UID.
		 */
		final static long serialVersionUID = 1;
		
		/**
		 * The list of players.
		 */
		private final static List<PlayerMark> players = new ArrayList<PlayerMark>();
		
		/**
		 * The iterator upon the list of players.
		 */
		private static Iterator<PlayerMark> itPlayers = null;
		
		/**
		 * The mark value.
		 */
		private int markValue;
		
		/**
		 * Constructor.
		 * @param val the value of the player mark.
		 * @throws IllegalArgumentException if <code>val</code>
		 * is smaller or equal to 0 or greater or equal to <code>Character.MAX_VALUE - 'A'</code>.
		 */
		private PlayerMark(final int val) throws IllegalArgumentException
		{
			if (val <= Character.MIN_VALUE || val >= (Character.MAX_VALUE - 'A'))
			{
				throw new IllegalArgumentException();
			}
			
			players.add(this);
			
			markValue = val;
		}

		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(final Object obj)
		{
			
			if (obj != null && obj instanceof PlayerMark)
				return ((PlayerMark) obj).markValue == markValue;
			else
				return false;
		}

		/**
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() 
		{
			return markValue;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() 
		{			
			return String.valueOf(markValue);
		}
		
		/**
		 * Return the unique key representation
		 * of this mark.
		 * @return the unique key representation
		 * of this mark.
		 */
		public String toUniqueKey()
		{
			char c = (char) ('A' + markValue - 1);
			return String.valueOf(c);
		}
		
		/**
		 * Returns the next player.
		 * @return the next player.
		 */
		private final static PlayerMark getNextPlayer()
		{	
		  /*
			if (itPlayers == null || !itPlayers.hasNext())
				itPlayers = players.iterator();
			*/
			PlayerMark result = itPlayers.next();
			
			return result;
		}
		
		/**
		 * Returns an iterator over the player mark list. This iterator
		 * cannot modify the player mark list.
		 * @return an iterator over the player mark list. This iterator
		 * cannot modify the player mark list.
		 */
		public final static Iterator<PlayerMark> getPlayerIterator()
		{
			return Collections.unmodifiableCollection(players).iterator();
		}
		
		/**
		 * Returns the mark that follows the player mark <code>playerMark</code>.
		 * @param playerMark the player mark to consider.
		 * @return the mark that follows the player mark <code>playerMark</code>.
		 * @throws NullPointerException if <code>playerMark</code> is null.
		 */
		public final static PlayerMark getNextMark(final PlayerMark playerMark)
			throws NullPointerException
		{
			if (playerMark == null)
				throw new NullPointerException();
			
			int index = players.indexOf(playerMark);
			
			index = (index + 1) % players.size();
			
			return players.get(index);
		}
		
		/**
		 * Returns the number of known player marks.
		 * @return the number of known player marks.
		 */
		public final static int getNumberOfPlayerMarks()
		{
			return players.size();
		}
		
		/**
		 * The player A mark.
		 */
		//public final static PlayerMark PLAYER_A_MARK = new PlayerMark(1);
		
		/**
		 * The player B mark.
		 */
		//public final static PlayerMark PLAYER_B_MARK = new PlayerMark(2);
		
	}
	
	/**
	 * The game status class represents the current game status of the game.
	 * 
	 * @author Julien Aubin
	 */
	public final static class GameStatus implements Serializable
	{
		/**
		 * The serial version UID.
		 */
		final static long serialVersionUID = 1;
		
		/**
		 * The game status text.
		 */
		private String text;
		
		/**
		 * Constructor.
		 * @param statusText the status text.
		 */
		private GameStatus(final String statusText)
		{
			text = statusText;
		}

		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(final Object obj) 
		{
			
			if (obj != null && obj instanceof GameStatus)
				return ((GameStatus) obj).text.equals(text);
			else
				return false;
		}

		/**
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() 
		{
			return text.hashCode();
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() 
		{
			return text;
		}
		
		/**
		 * The continue status.
		 */
		public final static GameStatus CONTINUE_STATUS = new GameStatus("Continue");
		
		/**
		 * The tie status.
		 */
		public final static GameStatus TIE_STATUS = new GameStatus("Tie");
		
		/**
		 * The won status.
		 */
		public final static GameStatus WON_STATUS = new GameStatus("Won");
	}
	
	/**
	 * This class represents a step of the game.
	 *
	 * @author Julien Aubin
	 */
	private final static class PlayStep implements Serializable {
		
		/**
		 * The class serial version UID.
		 */
		final static long serialVersionUID = 1L;
		
		/**
		 * The played column index.
		 */
		private int colIndex;
		
		/**
		 * The played status.
		 */
		private GameStatus currentStatus;
		
		/**
		 * The current player mark.
		 */
		private PlayerMark currentMark;
		
		/**
		 * Constructor.
		 * @param col the played column index.
		 * @param status the played status.
		 * @param mark the played mark.
		 * @throws NullPointerException if any of the method parameter is null.
		 */
		public PlayStep(final int col, final GameStatus status, final PlayerMark mark)
			throws NullPointerException {
			
			if (status == null || mark == null)
				throw new NullPointerException();
			
			colIndex = col;
			currentStatus = status;
			currentMark = mark;
		}
		
		/**
		 * Return played the column index.
		 * @return the played column index.
		 */
		public int getColIndex() {
			return colIndex;
		}
		
		/**
		 * Return the game status at the time of the start of the turn.
		 * @return the game status at the time of the start of the turn.
		 */
		public GameStatus getGameStatus() {
			return currentStatus;
		}
		
		/**
		 * Return the mark at the start of the turn.
		 * @return the mark at the start of the turn.
		 */
		public PlayerMark getPlayerMark() {
			return currentMark;
		}
	}
	
	/**
	 * The game tab, indexed by rows and then by column index.
	 * The (0, 0) coordinate represent the (top, left) cell.
	 */
	private PlayerMark[][] gameTab;
	
	/**
	 * The line length required in order to win.
	 */
	private int winLineLength;
	
	/**
	 * The currentPlayer.
	 */
	private PlayerMark currentPlayer;
	
	/**
	 * The game status.
	 */
	private GameStatus gameStatus;
	
	/**
	 * The win line.
	 */
	private List<CellCoord> winLine;
	
	/**
	 * The play history.
	 */
	private List<PlayStep> playHistory;
	
	/**
	 * The set of all lines of the game model.
	 */
	private Set<List<CellCoord>> lines; 
	
	/**
	 * The map of line positions that are considered as
	 * possible victory lines. This is a cache mechanism in order
	 * to improve the performance, especially with AI algorithm.<br/>
	 * It maps a cell to the list of lines to which it belongs.<br/>
	 * This map is shared among all the instances of a gamemodel that
	 * share the same parameters, since it's never written with a
	 * game model own data.<br/>
	 * However it's thread safe.
	 */
	private Map<CellCoord, Set<List<CellCoord>>> winLinesMap;
	
	
	public GameModel(final int rows, final int cols, final int winLength, int firstPlayer) {
    PlayerMark.players.clear();
    if (firstPlayer==1) {
      new PlayerMark(1);
      new PlayerMark(2);
    } else {
      new PlayerMark(2);
      new PlayerMark(1);
    }
    PlayerMark.itPlayers = PlayerMark.players.iterator();
    create(rows, cols, winLength);
  }
	
	
	/**
	 * Constructor.
	 */
	public GameModel()
	{
		this(6, 7, 4, 1);
	}
	
	
	/**
	 * Constructor.
	 * @param rows the number of rows.
	 * @param cols the number of columns.
	 * @param winLength the number of cells to get in order to have
	 * a winning line.
	 * @throws IllegalArgumentException if any of the parameters is smaller
	 * or equal to 0, or if the number of cells for a winning line is 
	 * greater than <code>Math.min(rows, cols)</code>, or if <code>winLength</code>
	 * is smaller or equal to 2.
	 */
	public void create(final int rows, final int cols, final int winLength)
	   throws IllegalArgumentException
	{
		if (rows <= 0 || cols <= 0)
			throw new IllegalArgumentException("Illegal dimenstions. Rows : " + rows + " - Columns : " + cols);
		
		if (winLength <= 2 || winLength > Math.min(rows, cols))
			throw new IllegalArgumentException("Illegal length of line in order to win. Rows : " + rows + " - Columns : " + cols + " - Length to win : " + winLength);
		
		gameTab = new PlayerMark[rows][cols];
		winLineLength = winLength;
		currentPlayer = PlayerMark.getNextPlayer();
		gameStatus = GameStatus.CONTINUE_STATUS;
		winLinesMap = new ConcurrentHashMap<CellCoord, Set<List<CellCoord>>>();
		winLine = null;
		playHistory = new LinkedList<PlayStep>();
		
		lines = new HashSet<List<CellCoord>>();
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				lines.addAll(getAllLines(i, j));
			}
		}
	}
	
	/**
	 * Constructor.
	 * @param gameModel the game model to copy.
	 * @throws NullPointerException if <code>gameModel</code> is null.
	 */
	public GameModel(final GameModel gameModel) throws NullPointerException
	{
		if (gameModel == null)
			throw new NullPointerException();
		
		winLineLength = gameModel.winLineLength;
		currentPlayer = gameModel.currentPlayer;
		gameStatus = gameModel.gameStatus;
		// Here it is safe to copy the win line map - nothing particular
		// to a given instance of a game model is written here. 
		winLinesMap = gameModel.winLinesMap;
		lines = gameModel.lines;
		
		// Here it is safe to copy the win line, since it is readonly.
		winLine = gameModel.winLine;
		
		gameTab = new PlayerMark[gameModel.gameTab.length][];
			
		for (int i = 0; i < gameModel.gameTab.length; i++)
		{
			gameTab[i] = new PlayerMark[gameModel.gameTab[i].length];
				
			for (int j = 0; j < gameModel.gameTab[i].length; j++)
				gameTab[i][j] = gameModel.gameTab[i][j];
		}
		playHistory = new LinkedList<PlayStep>(gameModel.playHistory);
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj)
	{
		if (obj != null && obj instanceof GameModel)
		{
			// No test performed winLinesMap since it should be
			// the same for all the instances of GameModel which
			// have the same dimension.
			GameModel model = (GameModel) obj;
			
			boolean result = winLineLength == model.winLineLength
				&& currentPlayer.equals(model.currentPlayer)
				&& gameStatus.equals(model.gameStatus);
			
			if (result)
			{
				result = gameTab.length == model.gameTab.length
					&& gameTab[0].length == model.gameTab[0].length;
				
				if (result)
				{
					for (int i = 0; i < gameTab.length && result; i++)
					{
						for (int j = 0; j < gameTab[i].length && result; j++)
						{
							if ((gameTab[i][j] == null && model.gameTab[i][j] != null)
									|| (gameTab[i][j] != null && !gameTab[i][j].equals(model.gameTab[i][j])))
								result = false;
									
						}
					}
				}
			}
			
			return result;
		}
		else
			return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return winLinesMap.hashCode();
	}

	/**
	 * Returns the current player.
	 * @return the current player.
	 */
	public PlayerMark getCurrentPlayer()
	{
		return currentPlayer;
	}
	
	/**
	 * Returns the game status.
	 * @return the game status.
	 */
	public GameStatus getGameStatus()
	{
		return gameStatus;
	}
	
	/**
	 * Returns the number of cells to align in order to win.
	 * @return the number of cells to align in order to win.
	 */
	public int getWinLineLength()
	{
		return winLineLength;
	}
	
	/**
	 * Returns the row count of this game model.
	 * @return the row count of this game model.
	 */
	public int getRowCount()
	{
		return gameTab.length;
	}
	
	/**
	 * Returns the column count of this game model.
	 * @return the column count of this game model.
	 */
	public int getColCount()
	{
		return gameTab[0].length;
	}
	
	/**
	 * Returns the mark of the cell at coordinates <code>rowIndex, colIndex</code>.
	 * @param rowIndex the row index. 
	 * @param colIndex the column index.
	 * @return the mark of the cell at coordinates <code>rowIndex, colIndex</code>.
	 * @throws ArrayIndexOutOfBoundsException if <code>rowIndex</code> is strictly
	 * smaller than 0 or greater or equal to the number of rows, or if <code>colIndex</code>
	 * is strictly smaller than 0 or greater or equal to the number of columns.
	 */
	public PlayerMark getCell(final int rowIndex, final int colIndex) throws ArrayIndexOutOfBoundsException
	{
		if (isOutOfBounds(rowIndex, colIndex))
			throw new ArrayIndexOutOfBoundsException(new CellCoord(rowIndex, colIndex).toString());
		
		return gameTab[rowIndex][colIndex];
	}
	
	/**
	 * Returns the mark of the cell at coordinates represented by <code>cellCoord</code>.
	 * @param cellCoord the cell coordinates.
	 * @return the mark of the cell at coordinates represented by <code>cellCoord</code>.
	 * @throws NullPointerException if <code>cellCoord</code> is null.
	 * @throws ArrayIndexOutOfBoundsException if <code>cellCoord</code> is out of the
	 * game tab bounds.
	 */
	public PlayerMark getCell(final CellCoord cellCoord) throws NullPointerException, ArrayIndexOutOfBoundsException
	{
		if (cellCoord == null)
			throw new NullPointerException();
		
		return getCell(cellCoord.getRowIndex(), cellCoord.getColIndex());
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() 
	{
		
		final String LINE_SEP = System.getProperty("line.separator");
		
		StringBuilder sbContent = new StringBuilder();
		
		for (int i = 0; i < gameTab.length; i++)
		{
			for (int j = 0; j < gameTab[i].length; j++)
			{
				String mark = null;
				
				if (gameTab[i][j] != null)
					mark = gameTab[i][j].toString();
				else
					mark = "0";
				
				sbContent.append(mark);
				
				if (j < gameTab[i].length - 1)
					sbContent.append(" ");
				else
					sbContent.append(LINE_SEP);
			}
		}
		
		return sbContent.toString();
		
	}
	
	/**
	 * Return the unique string representation
	 * of this game model.<br/>
	 * The string representation is provided
	 * under a compressed form.
	 * @return the unique string representation
	 * of this game model.
	 */
	public String toUniqueKey()
	{
		StringBuilder result = new StringBuilder();
		
		int nbEmptyCells = 0;
		
		for (int i = 0; i < gameTab.length; i++)
		{
			for (int j = 0; j < gameTab[i].length; j++)
			{
				if (gameTab[i][j] != null)
				{
					if (nbEmptyCells > 0)
						result.append(nbEmptyCells);
					nbEmptyCells = 0;
					result.append(gameTab[i][j].toUniqueKey());
				}
				else
				{
					nbEmptyCells++;
				}
			}
		}
		
		if (nbEmptyCells > 0)
			result.append(nbEmptyCells);
		
		return result.toString();
	}
	
	/**
	 * Returns the set of playable columns.
	 * @return the list of playable columns.
	 */
	public Collection<Integer> getListOfPlayableColumns()
	{
		Set<Integer> result = new TreeSet<Integer>();
		
		if (!gameStatus.equals(GameStatus.CONTINUE_STATUS))
			return result;
		
		for (int i = 0; i < gameTab[0].length; i++)
			if (gameTab[0][i] == null)
				result.add(Integer.valueOf(i));
		
		return result;
	}
	
	/**
	 * Perform a play at the column number <code>colIndex</code>, with
	 * player mark <code>playerMark</code>.<br/>
	 * Gives the turn to the next player, and updates the game status
	 * if necessary.
	 * @param colIndex the column index where the player wants to play.
	 * @param playerMark the player mark.
	 * @throws GameModelException if this is not the player turn, if
	 * the game is over or if the column number <code>colIndex</code>
	 * is not playable.
	 */ 
	public void play(final int colIndex, final PlayerMark playerMark)
	   throws GameModelException
	{
		Collection<Integer> playableColumns = getListOfPlayableColumns();
		
		if (!playableColumns.contains(Integer.valueOf(colIndex)))
			throw new GameModelException("The column number " + colIndex + " is not included in the list of playable columns " + playableColumns);
		
		if (!playerMark.equals(currentPlayer))
			throw new GameModelException("This is not the turn of player " + playerMark + ". Current turn : " + currentPlayer);
		
		gameTab[getFreeRowIndexForColumn(colIndex)][colIndex] = playerMark;
		
		playHistory.add(new PlayStep(colIndex, gameStatus, currentPlayer));
		
		updateGameStatus(colIndex);
		
		if (gameStatus.equals(GameStatus.CONTINUE_STATUS))
		{
			currentPlayer = PlayerMark.getNextMark(currentPlayer);
		}
	}
	
	/**
	 * Cancel the last play in the play history.
	 * @throws GameModelException if there's no more more
	 * play to remove.
	 */
	public void cancelLastPlay() throws GameModelException {
		if (playHistory.isEmpty())
			throw new GameModelException();
		
		PlayStep lastStep = playHistory.remove(playHistory.size() - 1);
		
		gameStatus = lastStep.getGameStatus();
		currentPlayer = lastStep.getPlayerMark();
		int colIndex = lastStep.getColIndex();
		gameTab[getFreeRowIndexForColumn(colIndex) + 1][colIndex] = null;
	}
	
	/**
	 * Returns the first free row index for the column which has for
	 * index <code>colIndex</code>, i.e. the row index of the first
	 * free cell of the column, or -1 if there's not such a free cell.
	 * @param colIndex the column index to test.
	 * @return the first free row index for the column which has for
	 * index <code>colIndex</code>, i.e. the row index of the first
	 * free cell of the column, or -1 if there's not such a free cell.
	 * @throws ArrayIndexOutOfBoundsException if <code>colIndex</code>
	 * is strictly smaller than 0, or greater or equal to the number of
	 * columns of the game tab.
	 */
	public int getFreeRowIndexForColumn(final int colIndex) throws ArrayIndexOutOfBoundsException
	{
		if (colIndex < 0 || colIndex >= gameTab[0].length)
			throw new ArrayIndexOutOfBoundsException(colIndex);
		
		int rowResult = -1;
		
		boolean occupiedCellFound = false;
		
		for (int i = 0; i < gameTab.length && !occupiedCellFound; i++)
		{
			if (gameTab[i][colIndex] != null)
			{
				occupiedCellFound = true;
				rowResult = i - 1;
			}
		}
		
		if (!occupiedCellFound)
			rowResult = gameTab.length - 1;
		
		return rowResult;
	}
	
	/**
	 * Returns the set of horizontal lines that cover the
	 * cell at coordinates <code>row, col</code>.
	 * @param row the row index.
	 * @param col the column index.
	 * @return the set of horizonal lines that cover the cell
	 * at coordinates <code>row, col</code>.
	 */
	private Set<List<CellCoord>> getHorizontalLines(final int row, final int col)
	{
		// Computes the min, max columns that contain the column number col.
		// We must ensure that he column number col is included in all the lines,
		// which explains the "+1"
		int minColIndex = Math.max(0, col - winLineLength + 1);		
		int maxColIndex = Math.min(col, gameTab[0].length - winLineLength);
		
		Set<List<CellCoord>> result = new HashSet<List<CellCoord>>();
		
		// Here we use a <= symbol because the extreme safe coordinates
		// are already computed.
		for (int i = minColIndex; i <= maxColIndex; i++)
		{
			List<CellCoord> line = new ArrayList<CellCoord>();
			
			for (int j = 0; j < winLineLength; j++)
				line.add(new CellCoord(row, i + j));
			
			result.add(Collections.unmodifiableList(line));
		}
		
		return result;
	}
	
	/**
	 * Returns the set of vertical lines that cover the cell
	 * at coordinates <code>row, col</code>.
	 * @param row the row index.
	 * @param col the column index.
	 * @return the set of vertical lines that cover the cell
	 * at coordinates <code>row, col</code>.
	 */
	private Set<List<CellCoord>> getVerticalLines(final int row, final int col)
	{
		// Computes the min, max rows that contain the row number row.
		// We must ensure that he row number row is included in all the columns,
		// which explains the "+1"
		int minRowIndex = Math.max(0, row - winLineLength + 1);
		int maxRowIndex = Math.min(row, gameTab.length - winLineLength);
		
		// Here we use a <= symbol because the extreme safe coordinates
		// are already computed.
		Set<List<CellCoord>> result = new HashSet<List<CellCoord>>();
		
		for (int i = minRowIndex; i <= maxRowIndex; i++)
		{
			List<CellCoord> line = new ArrayList<CellCoord>();
			
			for (int j = 0; j < winLineLength; j++)
				line.add(new CellCoord(i + j, col));
			
			result.add(Collections.unmodifiableList(line));
		}
		
		return result;
	}
	
	/**
	 * Returns true if the coordinate <code>row, col</code> is out of bounds,
	 * false elsewhere.
	 * @param row the row index.
	 * @param col the column index.
	 * @return true if the coordinate <code>row, col</code> is out of bounds,
	 * false elsewhere.
	 */
	private boolean isOutOfBounds(final int row, final int col)
	{
		return row < 0 || row >= gameTab.length || col < 0 || col >= gameTab[0].length;
	}
	
	/**
	 * Returns the set of down diagonals that cover the cell
	 * at coodinates <code>row, col</code>. Down diagonals
	 * go from (top, left) to (bottom, right) coordinates.
	 * @param row the row index.
	 * @param col the column index.
	 * @return the set of down diagonals that cover the cell
	 * at coodinates <code>row, col</code>.
	 */
	private Set<List<CellCoord>> getDownDiagonals(final int row, final int col)
	{
		// The algorithem is pretty simple : just consists in computing all
		// the diagonals of line winLineLength that would contain the cell of coords (row, col)
		// in an infinite grid, and then churning out all the ones that
		// contain out of bounds cells.
		
		// Computes the extremities of the diagonals that contain the cell
		// of coord (row, col)
		int minColIndex = col - winLineLength + 1;		
		int minRowIndex = row - winLineLength + 1;
		
		Set<List<CellCoord>> result = new HashSet<List<CellCoord>>();
		
		// Here we use the <= and >= symbol because all the possible
		// diagonals contain the cell of coords (row, col)
		for (int rowIndex = minRowIndex, colIndex = minColIndex; rowIndex <= row && colIndex <= col; rowIndex++, colIndex++)
		{
			List<CellCoord> line = new ArrayList<CellCoord>();
			
			boolean outOfBoundsDetected = false;
			
			// Computing diagonals is much more tricky since we evolve
			// simulataneously on two different dimensions.
			// That's why we must always detect if we're out of bounds or not.
			for (int i = 0; i < winLineLength && !outOfBoundsDetected; i++)
			{
				int cellRow = rowIndex + i;
				int cellCol = colIndex + i;
				
				if (!isOutOfBounds(cellRow, cellCol))
					line.add(new CellCoord(cellRow, cellCol));
				else
					outOfBoundsDetected = true;
			}
			
			if (!outOfBoundsDetected)
				result.add(Collections.unmodifiableList(line));
		}
		
		return result;
	}
	
	/**
	 * Returns the set of up diagonals that cover the cell
	 * at coodinates <code>row, col</code>. Up diagonals
	 * go from (bottom, left) to (top, right) coordinates.
	 * @param row the row index.
	 * @param col the column index.
	 * @return the set of down diagonals that cover the cell
	 * at coodinates <code>row, col</code>.
	 */
	private Set<List<CellCoord>> getUpDiagonals(final int row, final int col)
	{
		// The algorithem is pretty simple : just consists in computing all
		// the diagonals of line winLineLength that would contain the cell of coords (row, col)
		// in an infinite grid, and then churning out all the ones that
		// contain out of bounds cells.
		
		// Computes the extremities of the diagonals that contain the cell
		// of coord (row, col)		
		int maxColIndex = col + winLineLength - 1;
		int minRowIndex = row - winLineLength + 1;
		
		Set<List<CellCoord>> result = new HashSet<List<CellCoord>>();
		
		// Here we use the <= and >= symbol because all the possible
		// diagonals contain the cell of coords (row, col)
		for (int rowIndex = minRowIndex, colIndex = maxColIndex; rowIndex <= row && colIndex >= col; rowIndex++, colIndex--)
		{
			List<CellCoord> line = new ArrayList<CellCoord>();
			
			boolean outOfBoundsDetected = false;
			
			// Computing diagonals is much more tricky since we evolve
			// simulataneously on two different dimensions.
			// That's why we must always detect if we're out of bounds or not.
			for (int i = 0; i < winLineLength && !outOfBoundsDetected; i++)
			{
				int cellRow = rowIndex + i;
				int cellCol = colIndex - i;
				
				if (!isOutOfBounds(cellRow, cellCol))
					line.add(new CellCoord(cellRow, cellCol));
				else
					outOfBoundsDetected = true;
			}
			
			if (!outOfBoundsDetected)
				result.add(Collections.unmodifiableList(line));
		}
		
		return result;
	}
	
	/**
	 * Returns the list of all the lines of this game
	 * model. Each line appears only once in the returned
	 * set.
	 * @return the list of all the lines of this game
	 * model.
	 */
	public Set<List<CellCoord>> getAllLines() {
		return Collections.unmodifiableSet(lines);
	}
	
	/**
	 * Returns the list of all the values for the line <code>line</code>.
	 * @param line the line for which we want to get all the values.
	 * @return the list of all the values for the line <code>line</code>.
	 * @throws NullPointerException if <code>line</code> is null.
	 * @throws ArrayIndexOutOfBoundsException if any of the coordinates
	 * pointed out by <code>line</code> is null.
	 */
	public List<PlayerMark> getValuesOfLine(final List<CellCoord> line) throws NullPointerException, ArrayIndexOutOfBoundsException {
		if (line == null)
			throw new NullPointerException();
		
		// It's very easy to know if a line is valid, since
		// we know all the possible lines of the game.
		if (!lines.contains(line))
			throw new ArrayIndexOutOfBoundsException();
		
		List<PlayerMark> result = new ArrayList<PlayerMark>(line.size());
		
		for (CellCoord coord: line) {
			result.add(gameTab[coord.getRowIndex()][coord.getColIndex()]);
		}
		
		return result;
	}
	
	/**
	 * Returns the list of all the possible lines for coordinates <code>row, col</code>.
	 * @param row the row index.
	 * @param col the column index.
	 * @return the list of all the possible lines that are possible
	 * for coordinates <code>row, col</code>.
	 * @throws ArrayIndexOutOfBoundsException if <code>row</code> is strictly
	 * smaller than 0 or greater or equal to the number of rows, or if <code>col</code>
	 * is strictly smaller than 0 or greater or equal to the number of columns.
	 */
	public Collection<List<CellCoord>> getAllLines(final int row, final int col)
	   throws ArrayIndexOutOfBoundsException
	{
		if (isOutOfBounds(row, col))
			throw new ArrayIndexOutOfBoundsException(new CellCoord(row, col).toString());
		
		return getAllLines(new CellCoord(row, col));
	}
	
	/**
	 * Returns the list of all the possible lines for coordinates <code>row, col</code>.
	 * @param cellCoord the coordinates to consider.
	 * @return the list of all the possible lines for coordinates <code>row, col</code>.
	 * @throws NullPointerException if <code>cellCoord</code> is null.
	 * @throws ArrayIndexOutOfBoundsException if <code>cellCoord</code> is out of bounds.
	 */
	public Collection<List<CellCoord>> getAllLines(final CellCoord cellCoord)
	   throws NullPointerException, ArrayIndexOutOfBoundsException
	{
		if (cellCoord == null)
			throw new NullPointerException();
		
		Set<List<CellCoord>> result = winLinesMap.get(cellCoord);
		
		if (result == null)
		{
			int row = cellCoord.rowIndex;
			int col = cellCoord.colIndex;
		
			result = getHorizontalLines(row, col);
			result.addAll(getVerticalLines(row, col));
			result.addAll(getDownDiagonals(row, col));
			result.addAll(getUpDiagonals(row, col));
			
			winLinesMap.put(cellCoord, Collections.unmodifiableSet(result));
		}
		
		return result;
	}
	
	/**
	 * Returns the winning line, sorted so that the cell
	 * which has for index <code>i</code> in the line is contiguous
	 * to the cell which has for index <code>i + 1</code>.
	 * @return the winning line, or null if the game is not won.
	 */
	public List<CellCoord> getWinLine()
	{
		return winLine;
	}
	
	/**
	 * Returns true if the game is won, false otherwise.
	 * @param colIndex the index of the last played column.
	 * @return true if the game is won, false otherwise.
	 */
	private boolean isGameWon(final int colIndex)
	{
		// Here we just have to determine if the game
		// has been won around the last cell played. The
		// other ones have not been updated, so they're not
		// interesting for us.
		boolean isWon = false;
		
		Set<List<CellCoord>> encounteredLines = new HashSet<List<CellCoord>>();

		int rowIndex = getFreeRowIndexForColumn(colIndex) + 1;
		PlayerMark markTest = gameTab[rowIndex][colIndex]; 
		
		// We only consider occupied cells here.
		if (markTest != null)
		{
			Collection<List<CellCoord>> lines = getAllLines(rowIndex, colIndex);
			
			Iterator<List<CellCoord>> it = lines.iterator();
			
			while (it.hasNext() && !isWon)
			{
				List<CellCoord> line = it.next();
				
				if (!encounteredLines.contains(line))
				{
					encounteredLines.add(line);
										
					boolean isAllEqual = true;
					
					Iterator<CellCoord> itCoord = line.iterator();
					
					// A line is considered as correct if all its
					// marks are not null and equal to each other.
					while (itCoord.hasNext() && isAllEqual)
					{
						PlayerMark mark = getCell(itCoord.next());
						
						if (mark != null)
							isAllEqual = mark.equals(markTest);
						else
							isAllEqual = false;
					}
					
					isWon = isAllEqual;
					
					if (isWon)
						winLine = Collections.unmodifiableList(line);
				}
			}
		}
		
		return isWon;
	}
	
	/**
	 * Updates the game status.
	 * @param colIndex the index of the last played column.
	 */
	private void updateGameStatus(final int colIndex)
	{
		if (isGameWon(colIndex))
			gameStatus = GameStatus.WON_STATUS;
		else
		{
			// In case nothing is playable, the game is tie
			if (getListOfPlayableColumns().isEmpty())
				gameStatus = GameStatus.TIE_STATUS;
		}
	}
}