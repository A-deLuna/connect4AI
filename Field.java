// Copyright 2015 theaigames.com (developers@theaigames.com)

//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at

//    http://www.apache.org/licenses/LICENSE-2.0

//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//
//  For the full copyright and license information, please view the LICENSE
//  file that was distributed with this source code.

package bot;
import java.util.List;
import java.util.ArrayList;
/**
 * Field class
 *
 * Field class that contains the field status data and various helper functions.
 *
 * @author Jim van Eeden <jim@starapple.nl>, Joost de Meij <joost@starapple.nl>
 */

public class Field {
  private int[][] mBoard;
  public int mCols = 0, mRows = 0;
  private String mLastError = "";
  public int mLastColumn = 0;
  private int winningId;

  public Field(int columns, int rows) {
    mBoard = new int[columns][rows];
    mCols = columns;
    mRows = rows;
    clearBoard();
  }
  
  public Field clone() {
    Field clone = new Field(mCols, mRows);
    
    int[][] boardCopy = new int[mCols][mRows];
    for (int i = 0; i < mCols; i++) {
      for (int j = 0; j < mRows; j++) {
        boardCopy[i][j] = mBoard[i][j];
      } 
    }

    clone.setBoard(boardCopy);

    return clone;
  }

  void setBoard(int [][] board) {
    this.mBoard = board;
  }

  /**
   * Sets the number of columns (this clears the board)
   * @param args : int cols
   */
  public void setColumns(int cols) {
    mCols = cols;
    mBoard = new int[mCols][mRows];
  }

  /**
   * Sets the number of rows (this clears the board)
   * @param args : int rows
   */
  public void setRows(int rows) {
    mRows = rows;
    mBoard = new int[mCols][mRows];
  }

  /**
   * Clear the board
   */
  public void clearBoard() {
    for (int x = 0; x < mCols; x++) {
      for (int y = 0; y < mRows; y++) {
        mBoard[x][y] = 0;
      }
    }
  }

  /**
   * Adds a disc to the board
   * @param args : command line arguments passed on running of application
   * @return : true if disc fits, otherwise false
   */
  public Boolean addDisc(int column, int disc) {
    mLastError = "";
    if (column < mCols) {
      for (int y = mRows-1; y >= 0; y--) { // From bottom column up
        if (mBoard[column][y] == 0) {
          mBoard[column][y] = disc;
          mLastColumn = column;
          return true;
        }
      }
      mLastError = "Column is full.";
    } else {
      mLastError = "Move out of bounds.";
    }
    return false;
  }

  /**
   * Initialise field from comma separated String
   * @param String :
   */
  public void parseFromString(String s) {
    s = s.replace(';', ',');
    String[] r = s.split(",");
    int counter = 0;
    for (int y = 0; y < mRows; y++) {
      for (int x = 0; x < mCols; x++) {
        mBoard[x][y] = Integer.parseInt(r[counter]);
        counter++;
      }
    }
  }

  /**
   * Returns the current piece on a given column and row
   * @param args : int column, int row
   * @return : int
   */
  public int getDisc(int column, int row) {
    return mBoard[column][row];
  }

  /**
   * Returns whether a slot is open at given column
   * @param args : int column
   * @return : Boolean
   */
  public Boolean isValidMove(int column) {
    return (mBoard[column][0] == 0);
  }

  public List<Integer> validMoves() {
    List<Integer> moves = new ArrayList<Integer>();
    for(int i = 0 ; i < mCols; i++) {
      if(isValidMove(i)) {
        moves.add(i);
      }
    }
    return moves;
  }

  public Boolean isWinningBoard() {
    // Horizontal
    for (int i = 0; i < mCols - 4; i++) {
      for (int j = 0; j < mRows; j++) {
        if ( mBoard[i][j] != 0 &&
            mBoard[i][j] == mBoard[i+1][j] &&
            mBoard[i+1][j] == mBoard[i+2][j] &&
            mBoard[i+2][j] == mBoard[i+3][j] )
        {
          winningId = mBoard[i][j];
          return true;
        }
      }
    }
    // Vertical
    for (int j = 0; j < mRows - 4; j++) {
      for (int i = 0; i < mCols; i++) {
        if ( mBoard[i][j] != 0 &&
            mBoard[i][j] == mBoard[i][j+1] &&
            mBoard[i][j+2] == mBoard[i][j+2] &&
            mBoard[i][j+3] == mBoard[i][j+3])
        {
          winningId = mBoard[i][j];
          return true;
        }
      }
    }
    if(mCols < 4 || mRows < 4) {
      return false;
    }
    // Diagonal like this \ and like this /
    for (int i = 0; i < mCols - 4; i++) {
      for (int j = 0; j < mRows - 4; j++) {
        if ( mBoard[i][j] != 0 &&
            (( mBoard[i][j+3] == mBoard[i+1][j+2] &&
              mBoard[i+1][j+2] == mBoard[i+2][j+1] &&
              mBoard[i+2][j+1] == mBoard[i+3][j+0]) ||
             ( mBoard[i][j] == mBoard[i+1][j+1] &&
              mBoard[i+1][j+1] == mBoard[i+2][j+2] &&
              mBoard[i+2][j+2] == mBoard[i+3][j+3]))
            )
        {
          winningId = mBoard[i][j];
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Returns reason why addDisc returns false
   * @param args :
   * @return : reason why addDisc returns false
   */
  public String getLastError() {
    return mLastError;
  }

  @Override
  /**
   * Creates comma separated String with every cell.
   * @param args :
   * @return : String
   */
  public String toString() {
    String r = "";
    int counter = 0;
    for (int y = 0; y < mRows; y++) {
      for (int x = 0; x < mCols; x++) {
        if (counter > 0) {
          r += ",";
        }
        r += mBoard[x][y];
        counter++;
      }
    }
    return r;
  }

  /**
   * Checks whether the field is full
   * @return : Returns true when field is full, otherwise returns false.
   */
  public boolean isFull() {
    for (int x = 0; x < mCols; x++)
      for (int y = 0; y < mRows; y++)
      if (mBoard[x][y] == 0)
        return false; // At least one cell is not filled
    // All cells are filled
    return true;
  }

  public boolean continueGame() {
    return !(isFull() || isWinningBoard());
  }

  public boolean idWon(int id) {
    return id == winningId;
  }

  /**
   * Checks whether the given column is full
   * @return : Returns true when given column is full, otherwise returns false.
   */
  public boolean isColumnFull(int column) {
    return (mBoard[column][0] != 0);
  }

  /**
   * @return : Returns the number of columns in the field.
   */
  public int getNrColumns() {
    return mCols;
  }

  /**
   * @return : Returns the number of rows in the field.
   */
  public int getNrRows() {
    return mRows;
  }
}
